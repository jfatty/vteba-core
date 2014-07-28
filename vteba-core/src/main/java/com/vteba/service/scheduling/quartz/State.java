package com.vteba.service.scheduling.quartz;

/**
 * Quartz job任务状态
 * @author 尹雷 
 * @since 2014-7-25 14:30
 */
public class State {
    /**
     * 未执行
     */
    public static final Long UN = 1L;
    /**
     * 执行中
     */
    public static final Long RUN = 2L;
    /**
     * 执行成功
     */
    public static final Long OK = 3L;
    /**
     * 执行错误
     */
    public static final Long ERR = 4L;
    
    /**
     * 保存任务状态的hash表的key前缀。
     */
    public static final String HASH = "task_hash_";
    
    /**
     * 定时任务名前缀
     */
    public static final String TASK = "q_task_";
    
    /**
     * 定时任务，名义时间key前缀
     */
    public static final String NAMING = "naming_";
    
    /**
     * 定时任务，实际时间key前缀
     */
    public static final String ACTUAL = "actual_";
}
