package com.vteba.service.scheduling.quartz;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.quartz.JobMethodInvocationFailedException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ReflectionUtils;

/**
 * {@link org.springframework.beans.factory.FactoryBean} that exposes a
 * {@link org.quartz.JobDetail} object which delegates job execution to a
 * specified (static or non-static) method. Avoids the need for implementing
 * a one-line Quartz Job that just invokes an existing service method on a
 * Spring-managed target bean.
 *
 * <p>Inherits common configuration properties from the {@link MethodInvoker}
 * base class, such as {@link #setTargetObject "targetObject"} and
 * {@link #setTargetMethod "targetMethod"}, adding support for lookup of the target
 * bean by name through the {@link #setTargetBeanName "targetBeanName"} property
 * (as alternative to specifying a "targetObject" directly, allowing for
 * non-singleton target objects).
 *
 * <p>Supports both concurrently running jobs and non-currently running
 * jobs through the "concurrent" property. Jobs created by this
 * MethodInvokingJobDetailFactoryBean are by default volatile and durable
 * (according to Quartz terminology).
 *
 * <p><b>NOTE: JobDetails created via this FactoryBean are <i>not</i>
 * serializable and thus not suitable for persistent job stores.</b>
 * You need to implement your own Quartz Job as a thin wrapper for each case
 * where you want a persistent job to delegate to a specific service method.
 *
 * <p>Compatible with Quartz 1.8 as well as Quartz 2.0-2.2, as of Spring 4.0.
 * <b>Note:</b> Quartz 1.x support is deprecated - please upgrade to Quartz 2.0+.<br>
 * 
 * <p>扩展了原有的功能，通过Redis实现集群，任务在执行前，会检查任务的状态。<br>
 * 1、如果已经执行成功，则不会执行。<br>
 * 2、如果未执行，才会执行，同时修改任务的状态为执行中，任务完成，改为任务执行成功。<br>
 * 3、如果任务执行异常，也重新执行，修改任务状态为执行中，如果执行成功，更改任务的状态为执行成功。否则状态仍然改回异常状态。<br>
 * @author Juergen Hoeller
 * @author Alef Arendsen
 * @author 尹雷
 * @since 18.02.2004
 * @see #setTargetBeanName
 * @see #setTargetObject
 * @see #setTargetMethod
 * @see #setConcurrent
 */
public class MethodInvokingJobDetailFactoryBean extends ArgumentConvertingMethodInvoker
        implements FactoryBean<JobDetail>, BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, InitializingBean {

    private static RedisTemplate<String, Long> redisTemplate;
    
    private static Class<?> jobDetailImplClass;
    private static Method setResultMethod;

    static {
        try {
            jobDetailImplClass = Class.forName("org.quartz.impl.JobDetailImpl");
        }
        catch (ClassNotFoundException ex) {
            jobDetailImplClass = null;
        }
        try {
            Class<?> jobExecutionContextClass =
                    QuartzJobBean.class.getClassLoader().loadClass("org.quartz.JobExecutionContext");
            setResultMethod = jobExecutionContextClass.getMethod("setResult", Object.class);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Incompatible Quartz API: " + ex);
        }
    }


    private String name;

    private String group = Scheduler.DEFAULT_GROUP;

    private boolean concurrent = true;

    private String targetBeanName;

//    private String[] jobListenerNames;

    private String beanName;

    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    private BeanFactory beanFactory;

    private JobDetail jobDetail;


    /**
     * Set the name of the job.
     * <p>Default is the bean name of this FactoryBean.
     * @see org.quartz.JobDetail#setName
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the group of the job.
     * <p>Default is the default group of the Scheduler.
     * @see org.quartz.JobDetail#setGroup
     * @see org.quartz.Scheduler#DEFAULT_GROUP
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Specify whether or not multiple jobs should be run in a concurrent
     * fashion. The behavior when one does not want concurrent jobs to be
     * executed is realized through adding the {StatefulJob} interface.
     * More information on stateful versus stateless jobs can be found
     * <a href="http://www.quartz-scheduler.org/documentation/quartz-2.1.x/tutorials/tutorial-lesson-03">here</a>.
     * <p>The default setting is to run jobs concurrently.
     */
    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    /**
     * Set the name of the target bean in the Spring BeanFactory.
     * <p>This is an alternative to specifying {@link #setTargetObject "targetObject"},
     * allowing for non-singleton beans to be invoked. Note that specified
     * "targetObject" and {@link #setTargetClass "targetClass"} values will
     * override the corresponding effect of this "targetBeanName" setting
     * (i.e. statically pre-define the bean type or even the bean object).
     */
    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

//    /**
//     * Set a list of JobListener names for this job, referring to
//     * non-global JobListeners registered with the Scheduler.
//     * <p>A JobListener name always refers to the name returned
//     * by the JobListener implementation.
//     * @see SchedulerFactoryBean#setJobListeners
//     * @see org.quartz.JobListener#getName
//     * @deprecated as of Spring 4.0, since it only works on Quartz 1.x
//     */
//    @Deprecated
//    public void setJobListenerNames(String... names) {
//        this.jobListenerNames = names;
//    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    protected Class<?> resolveClassName(String className) throws ClassNotFoundException {
        return ClassUtils.forName(className, this.beanClassLoader);
    }


    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() throws ClassNotFoundException, NoSuchMethodException {
        prepare();

        // Use specific name if given, else fall back to bean name.
        String name = (this.name != null ? this.name : this.beanName);

        // Consider the concurrent flag to choose between stateful and stateless job.
        Class<?> jobClass = (this.concurrent ? MethodInvokingJob.class : StatefulMethodInvokingJob.class);

        // Build JobDetail instance.
        if (jobDetailImplClass != null) {
            // Using Quartz 2.0 JobDetailImpl class...
            this.jobDetail = (JobDetail) BeanUtils.instantiate(jobDetailImplClass);
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this.jobDetail);
            bw.setPropertyValue("name", name);
            bw.setPropertyValue("group", this.group);
            bw.setPropertyValue("jobClass", jobClass);
            bw.setPropertyValue("durability", true);
            ((JobDataMap) bw.getPropertyValue("jobDataMap")).put("methodInvoker", this);
        }
        else {
            throw new IllegalStateException("不支持Quartz 1， 请升级到Quartz 2.x。 ");
            // Using Quartz 1.x JobDetail class...
//            this.jobDetail = new JobDetail(name, this.group, jobClass);
//            this.jobDetail.setVolatility(true);
//            this.jobDetail.setDurability(true);
//            this.jobDetail.getJobDataMap().put("methodInvoker", this);
        }

        // quartz 2.x没有全局的Listener
        // Register job listener names.
//        if (this.jobListenerNames != null) {
//            for (String jobListenerName : this.jobListenerNames) {
//                if (jobDetailImplClass != null) {
//                    throw new IllegalStateException("Non-global JobListeners not supported on Quartz 2 - " +
//                            "manually register a Matcher against the Quartz ListenerManager instead");
//                }
//                this.jobDetail.addJobListener(jobListenerName);
//            }
//        }
        redisTemplate = beanFactory.getBean("redisTemplate", RedisTemplate.class);
        postProcessJobDetail(this.jobDetail);
    }

    /**
     * Callback for post-processing the JobDetail to be exposed by this FactoryBean.
     * <p>The default implementation is empty. Can be overridden in subclasses.
     * @param jobDetail the JobDetail prepared by this FactoryBean
     */
    protected void postProcessJobDetail(JobDetail jobDetail) {
    }


    /**
     * Overridden to support the {@link #setTargetBeanName "targetBeanName"} feature.
     */
    @Override
    public Class<?> getTargetClass() {
        Class<?> targetClass = super.getTargetClass();
        if (targetClass == null && this.targetBeanName != null) {
            Assert.state(this.beanFactory != null, "BeanFactory must be set when using 'targetBeanName'");
            targetClass = this.beanFactory.getType(this.targetBeanName);
        }
        return targetClass;
    }

    /**
     * Overridden to support the {@link #setTargetBeanName "targetBeanName"} feature.
     */
    @Override
    public Object getTargetObject() {
        Object targetObject = super.getTargetObject();
        if (targetObject == null && this.targetBeanName != null) {
            Assert.state(this.beanFactory != null, "BeanFactory must be set when using 'targetBeanName'");
            targetObject = this.beanFactory.getBean(this.targetBeanName);
        }
        return targetObject;
    }


    @Override
    public JobDetail getObject() {
        return this.jobDetail;
    }

    @Override
    public Class<? extends JobDetail> getObjectType() {
        return (this.jobDetail != null ? this.jobDetail.getClass() : JobDetail.class);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }


    /**
     * Quartz Job implementation that invokes a specified method.
     * Automatically applied by MethodInvokingJobDetailFactoryBean.
     */
    public static class MethodInvokingJob extends QuartzJobBean {

        protected static final Logger logger = LoggerFactory.getLogger(MethodInvokingJob.class);

        private MethodInvoker methodInvoker;

        /**
         * Set the MethodInvoker to use.
         */
        public void setMethodInvoker(MethodInvoker methodInvoker) {
            this.methodInvoker = methodInvoker;
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
        
        /**
         * Invoke the method via the MethodInvoker.
         */
        @Override
        protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
            try {
                JobDetail jobDetail = context.getJobDetail();
                String name = jobDetail.getKey().getName();
                String jobKey = State.TASK + name;
                String table = State.HASH + name;
                
                Date currentDate = context.getScheduledFireTime();
                Date nextFireDate = context.getNextFireTime();
                
                long scheduledDate = currentDate.getTime();
                long nextDate = nextFireDate.getTime();
                
                String currentJobName = jobKey + scheduledDate;
                String nextJobName = jobKey + nextDate;
                
                // 抓取任务，同时将任务标示为执行中
                Long status = redisTemplate.opsForValue().getAndSet(currentJobName, State.RUN);
                
                if (status == null || status == State.UN) {// 未执行，将执行
                    if (logger.isInfoEnabled()) {
                        logger.info("计划在[{}]执行的任务，正在执行中，下次执行时间是[{}]。", currentDate, nextFireDate);
                    }
                    ReflectionUtils.invokeMethod(setResultMethod, context, this.methodInvoker.invoke());
                    if (logger.isInfoEnabled()) {
                        logger.info("计划在[{}]执行的任务，执行成功，下次执行时间是[{}]。", currentDate, nextFireDate);
                    }
                    // 执行成功，将下一次任务表示为未执行
                    redisTemplate.opsForValue().set(nextJobName, State.UN);
                    // 将本次任务标示为执行成功
                    hashOps().put(table , currentJobName, State.OK);
                } else if (status == State.RUN) {// 正在执行，日志记录一下，跳过
                    if (logger.isInfoEnabled()) {
                        logger.info("计划在[{}]执行的任务，正在执行中，跳过。下次执行时间是[{}]。", currentDate, nextFireDate);
                    }
                } else if (status == State.OK) {// 成功，逃过（理论上，这个其实是不会发生的）
                    if (logger.isInfoEnabled()) {
                        logger.info("计划在[{}]执行的任务，已被其他节点成功执行，跳过。下次执行时间是[{}]。", currentDate, nextFireDate);
                    }
                } else if (status == State.ERR) {// 任务异常，尝试执行
                    if (logger.isWarnEnabled()) {
                        logger.warn("计划在[{}]执行的任务，运行异常，尝试执行一次。下次执行时间是[{}]。", currentDate, nextFireDate);
                    }
                    ReflectionUtils.invokeMethod(setResultMethod, context, this.methodInvoker.invoke());
                    redisTemplate.opsForValue().set(nextJobName, State.UN);
                    hashOps().put(table, currentJobName, State.OK);
                    if (logger.isWarnEnabled()) {
                        logger.warn("计划在[{}]执行的任务，运行异常，尝试执行成功。下次执行时间是[{}]。", currentDate, nextFireDate);
                    }
                    
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
    }


    /**
     * <p>扩展MethodInvokingJob，使用 PersistJobDataAfterExecution and/or DisallowConcurrentExecution
     * 这两个注解标注的类，Quartz会减产jobs是否是有状态的，如果有，Quartz不会让他们互相干扰。
     * 
     * <p>Quartz checks whether or not jobs are stateful and if so,
     * won't let jobs interfere with each other.
     */
    @PersistJobDataAfterExecution
    @DisallowConcurrentExecution
    public static class StatefulMethodInvokingJob extends MethodInvokingJob {

        // No implementation, just an addition of the tag interface StatefulJob
        // in order to allow stateful method invoking jobs.
    }

}

