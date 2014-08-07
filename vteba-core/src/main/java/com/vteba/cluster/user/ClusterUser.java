package com.vteba.cluster.user;

import java.io.Serializable;

/**
 * 集群用户抽象接口
 * @author yinlei 
 * @since 2013-12-1
 */
public interface ClusterUser extends Serializable {
    public String getUserId();
    
    public void setUserId(String userId);
    
    public String getUserAccount();
    
    public void setUserAccount(String userAccount);
    
    public Object getUserInfo();
    
    public void setUserInfo(Object userInfo);
}
