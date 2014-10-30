package com.vteba.service.tenant.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vteba.service.tenant.SchemaWeightHolder;
import com.vteba.service.tenant.annotation.Schema;

/**
 * Parse annotation schema, get the jta schema, and then put it into the current ThreadLocal.
 * @author yinlei
 * date 2012-8-16 下午9:33:59
 */
public class DetermineSchemaInterceptor implements MethodInterceptor{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DetermineSchemaInterceptor.class);

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object object = invocation.getThis();//获得被拦截的目标对象
		//boolean isSchema = object.getClass().isAnnotationPresent(Schema.class);// 拦截类，这种方式是可以的
		Class<?> proxyClass = object.getClass();
		Class<?>[] classes = proxyClass.getInterfaces();// 因为我们知道，代理的就是MyBatis的Mapper借口
		if (classes != null) {
			Class<?> clazz = classes[0];
			//boolean isSchema = clazz.isAnnotationPresent(Schema.class);// 不用判断了，这个方法底层调用的也是getAnnotation
			Schema schema = clazz.getAnnotation(Schema.class);
			if (schema != null) {
				String schemaName = schema.schemaName();
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("Schema拦截器打印的，Schema是：[{}]，线程name是：[{}]。", schemaName, Thread.currentThread().getName());
				}
				SchemaWeightHolder.putSchema(schemaName);
			}
		}
		return invocation.proceed();
	}

}
