package com.vteba.service.tenant;

/**
 * Schema Context Holder。持有当前线程绑定的数据库schema。
 * @author yinlei
 * @date 2012-08-15
 */
public class SchemaHolder {
	
	private static ThreadLocal<String> schemaLocal = new ThreadLocal<String>();
	
	public static void putSchema(String schema) {
		schemaLocal.set(schema);
	}
	
	public static String getSchema() {
		return schemaLocal.get();
	}
}
