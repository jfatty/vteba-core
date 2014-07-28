package com.vteba.service.scheduling.quartz;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.quartz.JobMethodInvocationFailedException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ReflectionUtils;

import com.vteba.utils.date.DateUtils;

/**
 * Quartz Job implementation that invokes a specified method.
 * Automatically applied by MethodInvokingJobDetailFactoryBean.
 * 
 * <p>通用的JobDetailBean。所有的Job任务调用都经过该Bean。通过反射实现。
 * 该类是无状态的。
 * @author 尹雷
 */
public class MethodInvokingJob extends QuartzJobBean {

    protected static final Logger logger = LoggerFactory.getLogger(MethodInvokingJob.class);
    private RedisTemplate<String, Long> redisTemplate;
    private MethodInvoker methodInvoker;
    
    private static Method setResultMethod;

    static {
        try {
            Class<?> jobExecutionContextClass =
                    QuartzJobBean.class.getClassLoader().loadClass("org.quartz.JobExecutionContext");
            setResultMethod = jobExecutionContextClass.getMethod("setResult", Object.class);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Incompatible Quartz API: " + ex);
        }
        
    }

    /**
     * Set the MethodInvoker to use.
     */
    public void setMethodInvoker(MethodInvoker methodInvoker) {
        this.methodInvoker = methodInvoker;
    }

    public void setRedisTemplate(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public HashOperations<String, String, Long> hashOps() {
        return redisTemplate.opsForHash();
    }
    
    public Long redisTime() {
        return redisTemplate.execute(new RedisCallback<Long>() {

            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.time();
            }
        });
    }
    
    public ValueOperations<String, Long> valueOps() {
        return redisTemplate.opsForValue();
    }
    
    /**
     * Invoke the method via the MethodInvoker.
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            Trigger trigger = context.getTrigger();// trigger详细信息
            String name = trigger.getKey().getName();// 定时任务名
            String group = trigger.getKey().getGroup();// 定时任务组
            
            String key = group + "_" + name;
            
            String jobKey = State.TASK + key;// 定时任务前缀
            String table = State.HASH + key;// 定时任务历史记录表前缀
            
            Date currentDate = context.getScheduledFireTime();// 本次执行时间
            Date nextFireDate = context.getNextFireTime();// 下次执行时间
            
            long curDate = currentDate.getTime();
            long nextDate = nextFireDate.getTime();
            
            String namingTimeKey = "naming_" + key;
            if (logger.isInfoEnabled()) {
                logger.info("定时任务名义（server）时间key：[{}]。", namingTimeKey);
            }
            String actualTimeKey = "actual_" + key;
            if (logger.isInfoEnabled()) {
                logger.info("定时任务实际（redis）时间key：[{}]。", actualTimeKey);
            }
            
            Long actualTime = valueOps().get(actualTimeKey);
            Long namingTime = valueOps().get(namingTimeKey);
            
            long redisTime = redisTime();
            
            long timeDiff = redisTime - actualTime;// 实际上过去了多长时间
            
            long interval = nextDate - curDate;// 时间间隔
            
            long count = timeDiff / interval;// 任务从集群中第一台节点 开始执行，一共执行过多少次了
            if (logger.isInfoEnabled()) {
                logger.info("距离集群中第一台节点开始执行此任务，一共执行过[{}]次了", count);
            }
            long mod = timeDiff % interval;// 整数次后，又过去了多长时间（小于一次的时间间隔）
            
            String currentJobName;// 用来在集群中查询本次定时任务的key
            String nextJobName;// 设置下次定时任务的key
            if (mod > 0) {
                currentJobName = jobKey + (namingTime + count * interval);
                nextJobName = jobKey + (namingTime + (count + 1) * interval);
            } else {
                currentJobName = jobKey + (namingTime + count * interval);
                nextJobName = jobKey + (namingTime + (count + 1) * interval);
            }
            if (logger.isInfoEnabled()) {
                logger.info("本次将去抓取任务[{}]", currentJobName);
            }
            // 抓取任务，同时将任务标示为执行中
            Long status = valueOps().getAndSet(currentJobName, State.RUN);
            
            if (status == null || status == State.UN) {// 未执行，将执行
                log(1, key, curDate, nextDate);
                ReflectionUtils.invokeMethod(setResultMethod, context, this.methodInvoker.invoke());
                log(2, key, curDate, nextDate);
                // 执行成功，将下一次任务表示为未执行
                valueOps().set(nextJobName, State.UN);
                // 将本次任务标示为执行成功
                hashOps().put(table , currentJobName, State.OK);
            } else if (status == State.RUN) {// 正在执行，日志记录一下，跳过
                log(3, key, curDate, nextDate);
            } else if (status == State.OK) {// 成功，逃过（理论上，这个其实是不会发生的）
                log(4, key, curDate, nextDate);
            } else if (status == State.ERR) {// 任务异常，尝试执行
                log(5, key, curDate, nextDate);
                ReflectionUtils.invokeMethod(setResultMethod, context, this.methodInvoker.invoke());
                valueOps().set(nextJobName, State.UN);
                hashOps().put(table, currentJobName, State.OK);
                log(6, key, curDate, nextDate);
            } else {// 未知任务状态，记录日志，跳过
                if (logger.isWarnEnabled()) {
                    logger.warn("未知任务状态[{}]，跳过。", status);
                }
            }
            
            
        }
        catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof JobExecutionException) {
                // -> JobExecutionException, to be logged at info level by Quartz
                throw (JobExecutionException) ex.getTargetException();
            }
            else {
                // -> "unhandled exception", to be logged at error level by Quartz
                throw new JobMethodInvocationFailedException(this.methodInvoker, ex.getTargetException());
            }
        }
        catch (Exception ex) {
            // -> "unhandled exception", to be logged at error level by Quartz
            throw new JobMethodInvocationFailedException(this.methodInvoker, ex);
        }
    }
    
    /**
     * 打印日志
     * @param type 1正在执行中、2执行成功、3正在执行中，跳过、4已被其他节点成功执行，跳过、5运行异常，尝试执行一次、
     * 6运行异常，尝试执行成功
     * @param key 任务id
     * @param curDate 现在的时间戳
     * @param nextDate 下一次时间戳
     */
    public void log(int type, String key, long curDate, long nextDate) {
        if (logger.isInfoEnabled()) {
            String info = null;
            if (type == 1) {
                info = "正在执行中";
            } else if (type == 2) {
                info = "执行成功";
            } else if (type == 3) {
                info = "正在执行中，跳过";
            } else if (type == 4) {
                info = "已被其他节点成功执行，跳过";
            } else if (type == 5) {
                info = "运行异常，尝试执行一次";
            } else if (type == 6) {
                info = "运行异常，尝试执行成功";
            }
            logger.info("计划在[{}]执行的任务[{}]，[{}]，下次执行时间是[{}]。", 
                        DateUtils.toDateString(curDate),
                        key,
                        info,
                        DateUtils.toDateString(nextDate));
        }
    }
}