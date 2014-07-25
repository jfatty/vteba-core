package com.vteba.service.scheduling.quartz;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.JobDetailAwareTrigger;
import org.springframework.scheduling.quartz.SimpleTriggerBean;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * A Spring {@link FactoryBean} for creating a Quartz {@link org.quartz.SimpleTrigger}
 * instance, supporting bean-style usage for trigger configuration.
 *
 * <p>{@code SimpleTrigger(Impl)} itself is already a JavaBean but lacks sensible defaults.
 * This class uses the Spring bean name as job name, the Quartz default group ("DEFAULT")
 * as job group, the current time as start time, and indefinite repetition, if not specified.
 *
 * <p>This class will also register the trigger with the job name and group of
 * a given {@link org.quartz.JobDetail}. This allows {@link SchedulerFactoryBean}
 * to automatically register a trigger for the corresponding JobDetail,
 * instead of registering the JobDetail separately.
 *
 * <p><b>NOTE:</b> This FactoryBean works against both Quartz 1.x and Quartz 2.x,
 * in contrast to the older {@link SimpleTriggerBean} class.
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
 * @see CronTriggerBean
 */
public class SimpleTriggerFactoryBean implements FactoryBean<SimpleTrigger>, BeanNameAware, InitializingBean, BeanFactoryAware {

    /** Constants for the SimpleTrigger class */
    private static final Constants constants = new Constants(SimpleTrigger.class);

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    private String name;

    private String group;

    private JobDetail jobDetail;

    private JobDataMap jobDataMap = new JobDataMap();

    private Date startTime;

    private long startDelay;

    private long repeatInterval;

    private int repeatCount = -1;

    private int priority;

    private int misfireInstruction;

    private String description;

    private String beanName;

    private SimpleTrigger simpleTrigger;


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
     * @see #setStartTime
     */
    public void setStartDelay(long startDelay) {
        Assert.isTrue(startDelay >= 0, "Start delay cannot be negative");
        this.startDelay = startDelay;
    }

    /**
     * Specify the interval between execution times of this trigger.
     */
    public void setRepeatInterval(long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    /**
     * Specify the number of times this trigger is supposed to fire.
     * <p>Default is to repeat indefinitely.
     */
    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
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
     * constant in the {@link org.quartz.SimpleTrigger} class.
     * Default is {@code MISFIRE_INSTRUCTION_SMART_POLICY}.
     * @see org.quartz.SimpleTrigger#MISFIRE_INSTRUCTION_FIRE_NOW
     * @see org.quartz.SimpleTrigger#MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT
     * @see org.quartz.SimpleTrigger#MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT
     * @see org.quartz.SimpleTrigger#MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT
     * @see org.quartz.SimpleTrigger#MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT
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
        Long redisTime = redisTemplate.opsForValue().get(group + name);
        if (redisTime == null || redisTime == 0L) {// 还没有设置启动时间
            redisTime = redisTime() + this.startDelay;
            redisTemplate.opsForValue().set(group + name, redisTime);// 重新设置启动时间
        }
        this.startTime = new Date(redisTime);
        
        // 忽略设置的开始时间
//        if (this.startDelay > 0 || this.startTime == null) {
//            // 以redis server的时间为开始，统一各应用server
//            this.startTime = new Date(redisTime + this.startDelay);
//            redisTemplate.opsForValue().set(name, redisTime);
//        }

        /*
        SimpleTriggerImpl sti = new SimpleTriggerImpl();
        sti.setName(this.name);
        sti.setGroup(this.group);
        sti.setJobKey(this.jobDetail.getKey());
        sti.setJobDataMap(this.jobDataMap);
        sti.setStartTime(this.startTime);
        sti.setRepeatInterval(this.repeatInterval);
        sti.setRepeatCount(this.repeatCount);
        sti.setPriority(this.priority);
        sti.setMisfireInstruction(this.misfireInstruction);
        cti.setDescription(this.description);
        this.simpleTrigger = sti;
        */

        Class<?> simpleTriggerClass;
        Method jobKeyMethod;
        try {
            simpleTriggerClass = getClass().getClassLoader().loadClass("org.quartz.impl.triggers.SimpleTriggerImpl");
            jobKeyMethod = JobDetail.class.getMethod("getKey");
        }
        catch (ClassNotFoundException ex) {
            simpleTriggerClass = SimpleTrigger.class;
            jobKeyMethod = null;
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Incompatible Quartz version");
        }
        BeanWrapper bw = new BeanWrapperImpl(simpleTriggerClass);
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.add("name", this.name);
        pvs.add("group", this.group);
        if (jobKeyMethod != null) {
            pvs.add("jobKey", ReflectionUtils.invokeMethod(jobKeyMethod, this.jobDetail));
        }
        // 这个Quartz 1.x的
//        else {
//            pvs.add("jobName", this.jobDetail.getName());
//            pvs.add("jobGroup", this.jobDetail.getGroup());
//        }
        pvs.add("jobDataMap", this.jobDataMap);
        pvs.add("startTime", this.startTime);
        pvs.add("repeatInterval", this.repeatInterval);
        pvs.add("repeatCount", this.repeatCount);
        pvs.add("priority", this.priority);
        pvs.add("misfireInstruction", this.misfireInstruction);
        pvs.add("description", this.description);
        bw.setPropertyValues(pvs);
        this.simpleTrigger = (SimpleTrigger) bw.getWrappedInstance();
    }


    @Override
    public SimpleTrigger getObject() {
        return this.simpleTrigger;
    }

    @Override
    public Class<?> getObjectType() {
        return SimpleTrigger.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        //this.beanFactory = beanFactory;
    }

}

