package com.vteba.service.tenant;

/**
 * Schema权重持有者，根据配置的权重，获取某一应用本次db操作对应的数据库schema
 * @author yinlei
 * @date 2013-10-29
 */
public class SchemaWeightHolder {
	private static ThreadLocal<String> weighThreadLocal = new ThreadLocal<String>();
	
	public static void putSchema(String schema) {
		weighThreadLocal.set(schema);
	}
	
	public static String getSchema() {
		return weighThreadLocal.get();
	}
}
