package com.vteba.service.scheduling.quartz;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.quartz.JobDetailAwareTrigger;
import org.springframework.scheduling.quartz.SimpleTriggerBean;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.vteba.utils.date.DateUtils;

/**
 * A Spring {@link FactoryBean} for creating a Quartz {@link org.quartz.CronTrigger}
 * instance, supporting bean-style usage for trigger configuration.
 *
 * <p>{@code CronTrigger(Impl)} itself is already a JavaBean but lacks sensible defaults.
 * This class uses the Spring bean name as job name, the Quartz default group ("DEFAULT")
 * as job group, the current time as start time, and indefinite repetition, if not specified.
 *
 * <p>This class will also register the trigger with the job name and group of
 * a given {@link org.quartz.JobDetail}. This allows {@link SchedulerFactoryBean}
 * to automatically register a trigger for the corresponding JobDetail,
 * instead of registering the JobDetail separately.
 *
 * <p>Cron表达式的定时任务，启动时间就直接按照server时间来计算。redis服务会保存每个任务的执行结果状态2个小时。
 * 这样即使有时间差 也不会导致重复执行。
 * 
 * @author Juergen Hoeller
 * @author 尹雷
 * @since 3.1
 * @see #setName
 * @see #setGroup
 * @see #setStartDelay
 * @see #setJobDetail
 * @see SchedulerFactoryBean#setTriggers
 * @see SchedulerFactoryBean#setJobDetails
 * @see SimpleTriggerBean
 */
public class CronTriggerFactoryBean implements FactoryBean<CronTrigger>, BeanNameAware, InitializingBean {

    /** Constants for the CronTrigger class */
    private static final Constants constants = new Constants(CronTrigger.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(CronTriggerFactoryBean.class);
    
    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    private String name;

    private String group;

    private JobDetail jobDetail;

    private JobDataMap jobDataMap = new JobDataMap();

    private Date startTime;

    private long startDelay = 0;

    private String cronExpression;

    private TimeZone timeZone;

    private String calendarName;

    private int priority;

    private int misfireInstruction;

    private String description;

    private String beanName;

    private CronTrigger cronTrigger;


    /**
     * Specify the trigger's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Specify the trigger's group.
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Set the JobDetail that this trigger should be associated with.
     */
    public void setJobDetail(JobDetail jobDetail) {
        this.jobDetail = jobDetail;
    }

    /**
     * Set the trigger's JobDataMap.
     * @see #setJobDataAsMap
     */
    public void setJobDataMap(JobDataMap jobDataMap) {
        this.jobDataMap = jobDataMap;
    }

    /**
     * Return the trigger's JobDataMap.
     */
    public JobDataMap getJobDataMap() {
        return this.jobDataMap;
    }

    /**
     * Register objects in the JobDataMap via a given Map.
     * <p>These objects will be available to this Trigger only,
     * in contrast to objects in the JobDetail's data map.
     * @param jobDataAsMap Map with String keys and any objects as values
     * (for example Spring-managed beans)
     * @see org.springframework.scheduling.quartz.JobDetailBean#setJobDataAsMap
     */
    public void setJobDataAsMap(Map<String, ?> jobDataAsMap) {
        this.jobDataMap.putAll(jobDataAsMap);
    }

    /**
     * Set a specific start time for the trigger.
     * <p>Note that a dynamically computed {@link #setStartDelay} specification
     * overrides a static timestamp set here.
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Set the start delay in milliseconds.
     * <p>The start delay is added to the current system time (when the bean starts)
     * to control the start time of the trigger.
     */
    public void setStartDelay(long startDelay) {
        Assert.isTrue(startDelay >= 0, "Start delay cannot be negative");
        this.startDelay = startDelay;
    }

    /**
     * Specify the cron expression for this trigger.
     */
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    /**
     * Specify the time zone for this trigger's cron expression.
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Associate a specific calendar with this cron trigger.
     */
    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    /**
     * Specify the priority of this trigger.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Specify a misfire instruction for this trigger.
     */
    public void setMisfireInstruction(int misfireInstruction) {
        this.misfireInstruction = misfireInstruction;
    }

    /**
     * Set the misfire instruction via the name of the corresponding
     * constant in the {@link org.quartz.CronTrigger} class.
     * Default is {@code MISFIRE_INSTRUCTION_SMART_POLICY}.
     * @see org.quartz.CronTrigger#MISFIRE_INSTRUCTION_FIRE_ONCE_NOW
     * @see org.quartz.CronTrigger#MISFIRE_INSTRUCTION_DO_NOTHING
     * @see org.quartz.Trigger#MISFIRE_INSTRUCTION_SMART_POLICY
     */
    public void setMisfireInstructionName(String constantName) {
        this.misfireInstruction = constants.asNumber(constantName).intValue();
    }

    /**
     * Associate a textual description with this trigger.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * 获取redis系统的时间
     * @return unix时间戳
     */
    public Long redisTime() {
        return redisTemplate.execute(new RedisCallback<Long>() {

            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.time();
            }
        });
    }

    @Override
    public void afterPropertiesSet() {
        if (this.name == null) {
            this.name = this.beanName;
        }
        if (this.group == null) {
            this.group = Scheduler.DEFAULT_GROUP;
        }
        if (this.jobDetail != null) {
            this.jobDataMap.put(JobDetailAwareTrigger.JOB_DETAIL_KEY, this.jobDetail);
        }
        // 看集群中的其他节点是否已经启动，并设置该job的开始时间，如果有重用。否则获取redis server系统时间
//        Long redisTime = redisTemplate.opsForValue().get(group + name);
//        Long redisDate = redisTime();
//        if (redisTime == null || redisTime < redisDate) {// 还没有设置启动时间，或者是过去的时间
//            redisTime = redisTime() + this.startDelay;
//            redisTemplate.opsForValue().set(group + name, redisTime);// 重新设置启动时间
//            if (LOGGER.isInfoEnabled()) {
//                LOGGER.info("将定时任务[{}]的开始时间设为：[{}]。", name, DateUtils.toDateString(redisTime));
//            }
//        } else {
//            if (LOGGER.isInfoEnabled()) {
//                LOGGER.info("集群中已有其他节点将定时任务[{}]的开始时间设为：[{}]。", name, DateUtils.toDateString(redisTime));
//            }
//        }
//        this.startTime = new Date(redisTime);
        
        if (this.startDelay > 0 || this.startTime == null) {
            this.startTime = new Date(System.currentTimeMillis() + this.startDelay);
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("设置CronJobDetail的开始时间是[{}].", DateUtils.toDateString(startTime));
        }
        
        if (this.timeZone == null) {
            this.timeZone = TimeZone.getDefault();
        }

        Class<?> cronTriggerClass;
        Method jobKeyMethod;
        try {
            cronTriggerClass = getClass().getClassLoader().loadClass("org.quartz.impl.triggers.CronTriggerImpl");
            jobKeyMethod = JobDetail.class.getMethod("getKey");
        }
        catch (ClassNotFoundException ex) {
            cronTriggerClass = CronTrigger.class;
            jobKeyMethod = null;
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Incompatible Quartz version");
        }
        BeanWrapper bw = new BeanWrapperImpl(cronTriggerClass);
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("name", this.name);
        pvs.add("group", this.group);
        if (jobKeyMethod != null) {
            pvs.add("jobKey", ReflectionUtils.invokeMethod(jobKeyMethod, this.jobDetail));
        }

        pvs.add("jobDataMap", this.jobDataMap);
        pvs.add("startTime", this.startTime);
        pvs.add("cronExpression", this.cronExpression);
        pvs.add("timeZone", this.timeZone);
        pvs.add("calendarName", this.calendarName);
        pvs.add("priority", this.priority);
        pvs.add("misfireInstruction", this.misfireInstruction);
        pvs.add("description", this.description);
        bw.setPropertyValues(pvs);
        this.cronTrigger = (CronTrigger) bw.getWrappedInstance();
    }


    @Override
    public CronTrigger getObject() {
        return this.cronTrigger;
    }

    @Override
    public Class<?> getObjectType() {
        return CronTrigger.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
    
}

