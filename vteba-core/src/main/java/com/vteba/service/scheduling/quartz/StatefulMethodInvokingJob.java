package com.vteba.service.scheduling.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

/**
 * <p>扩展MethodInvokingJob，使用 PersistJobDataAfterExecution and/or DisallowConcurrentExecution
 * 这两个注解标注的类，Quartz会减产jobs是否是有状态的，如果有，Quartz不会让他们互相干扰。
 * 
 * <p>Quartz checks whether or not jobs are stateful and if so,
 * won't let jobs interfere with each other.
 * 
 * @author 尹雷
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class StatefulMethodInvokingJob extends MethodInvokingJob {

    // No implementation, just an addition of the tag interface StatefulJob
    // in order to allow stateful method invoking jobs.
}
