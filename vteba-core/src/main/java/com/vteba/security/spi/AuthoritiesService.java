package com.vteba.security.spi;

import java.util.List;

/**
 * 权限service接口。
 * @author yinlei
 * @date 2012-4-20
 */
public interface AuthoritiesService {
	/**
	 * 获得系统所有的权限名 spring security use
	 * @return List<String> 系统所有的权限名
	 */
	public List<String> getAllAuthorities();
	
	/**
	 * 根据权限名，获得该权限下的资源 spring security use
	 * @param authName 权限名
	 * @return 资源URL list
	 */
	public List<String> getResourceByAuthName(String authName);
	
	/**
	 * 根据权限名，获得该权限下的资源（方法路径，含包名），spring security use
	 * @param authName 权限名
	 * @return （资源列表）方法名列表
	 */
	public List<String> getMethodByAuthName(String authName);
}

