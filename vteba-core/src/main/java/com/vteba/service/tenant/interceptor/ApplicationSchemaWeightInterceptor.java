package com.vteba.service.tenant.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vteba.service.tenant.SchemaWeightHolder;
import com.vteba.service.tenant.annotation.Application;

/**
 * Parse annotation Application, get the app schema weight,
 * and then put it into the current ThreadLocal. for the next
 * locate database.
 * @author yinlei
 * @date 2013-8-16
 */
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
