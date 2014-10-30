package com.vteba.service.tenant;

/**
 * Schema Context Holder。持有当前线程绑定的数据库schema。
 * Schema权重持有者，根据配置的权重，获取某一应用本次db操作对应的数据库schema.
 * @author yinlei
 * @date 2012-08-15
 */
public class SchemaWeightHolder {
	
	private static ThreadLocal<String> schemaLocal = new ThreadLocal<String>();
	
	public static void putSchema(String schema) {
		schemaLocal.set(schema);
	}
	
	public static String getSchema() {
		return schemaLocal.get();
	}
}
