package com.vteba.service.tenant.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多租户使用，multi-tenant use, based schema。
 * 对于不进行分区表的，使用这个注解来指定他查询的schema。
 * @author yinlei
 * @date 2012-8-20
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Schema {
	
	/**
	 * Database Schema Name.
	 * @return schema name
	 */
	public String name() default "skmbw";
	
	/**
	 * tenant id
	 * @return tenant id
	 */
	//public abstract String tenantIdentifier();
}
