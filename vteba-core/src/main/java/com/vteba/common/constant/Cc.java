package com.vteba.common.constant;

/**
 * 缓存常量类。
 * @author yinlei
 * 2013-4-4 下午4:43:46
 */
public final class Cc {
	public static final class Subject {
		public static final String TREE = "c_subject_tree";
	}
	
	public static final class Resources {
		public static final String JSON = "c_resources_json";
	}
	
	public static final class Auth {
		public static final String JSON = "c_auth_json";
	}
	
	public static void main(String[] aa) {
		System.err.println(Cc.Subject.class.getName());
	}
}
