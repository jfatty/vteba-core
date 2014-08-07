package com.vteba.cluster.user.service.spi;

import com.vteba.cluster.user.ClusterUser;

/**
 * 集群用户service操作
 * @author yinlei 
 * @since 2013-12-1 14:12
 */
public interface ClusterUserService {
    
    /**
     * 将用户信息放入集群中
     * @param user 用户信息
     * @return true成功，false失败
     */
    public boolean putUser(ClusterUser user);
    
    /**
     * 从当前线程获取用户账号，然后从集群中查询用户信息。如果没有经过登录，
     * 那么用户账号没有绑定到当前线程，是获取不到用户账号的，也就获取不到用户信息。
     * 例如，定时任务中，纯的后台业务中，都无法获取用户信息，请使用
     * {@link #getUser(String)}，该方法来获取。
     * @return 用户信息
     */
    public ClusterUser getUser();
    
    /**
     * 根据用户id获取集群中用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    public ClusterUser getUser(String userId);
    
    /**
     * 更新集群中用户信息。新的信息将覆盖原来的信息。
     * @param user 新的用户信息
     * @return true成功，false失败
     */
    public boolean updateUser(ClusterUser user);
    
    /**
     * 根据用户ID删除集群中用户信息
     * @param userId 用户信息
     * @return true成功，false失败
     */
    public boolean deleteUser(String userId);
}
