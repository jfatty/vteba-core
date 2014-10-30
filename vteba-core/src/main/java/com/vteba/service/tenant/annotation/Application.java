package com.vteba.service.tenant.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示一个Service或者Dao是哪个应用的，为后期做动态分库做准备。
 * @author yinlei
 * @date 2013-10-29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Application {
	/**
	 * 应用的名字，唯一标识
	 * @return 应用的名
	 */
	public String name();
	
	/**
	 * 是否放行，不拦截应用
	 * @return true是，false否
	 */
	public boolean pass() default false;
}
