package com.vteba.service.tenant.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vteba.service.tenant.SchemaWeightHolder;
import com.vteba.service.tenant.annotation.Application;

/**
 * 解析注解@Application。回去当前应用的名字，然后根据应用的配置，回去改应用
 * 连接的分区数据库，如果有权重，根据权重获取对应的数据库schema
 * @author yinlei
 * @date 2013-8-16
 */
@Deprecated
public class ApplicationSchemaWeightInterceptor implements MethodInterceptor{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationSchemaWeightInterceptor.class);

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object object = invocation.getThis();//获得被拦截的目标对象
		//boolean isSchema = object.getClass().isAnnotationPresent(Schema.class);// 拦截类，这种方式是可以的，接口会是null
		Class<?> proxyClass = object.getClass();
		Class<?>[] classes = proxyClass.getInterfaces();// 因为我们知道，代理的就是MyBatis的Mapper借口
		if (classes != null) {
			Class<?> clazz = classes[0];
			Application app = clazz.getAnnotation(Application.class);
			if (app != null) {
				String name = app.name();
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("ApplicationSchemaWeight，APP是：[{}]。", name);
				}
				SchemaWeightHolder.putSchema(name);
			}
		}
		return invocation.proceed();
	}

}
