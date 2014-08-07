package com.vteba.cluster.user.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.vteba.cluster.consts.ClusterConst;
import com.vteba.cluster.user.ClusterUser;
import com.vteba.cluster.user.UserUtils;
import com.vteba.cluster.user.service.spi.ClusterUserService;
import com.vteba.utils.web.RequestContextHolder;

/**
 * 基于redis的集群用户service参考实现
 * @author yinlei 
 * @since 2013-12-1 14:14
 */
public class DefaultClusterServiceImpl implements ClusterUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClusterServiceImpl.class);
    
    @Autowired
    private RedisTemplate<String, ClusterUser> redisTemplate;
    
    @Override
    public ClusterUser getUser() {
        return getUser(ClusterConst.USER_ACCOUNT + UserUtils.getUserId());
    }

    @Override
    public ClusterUser getUser(String userId) {
        return redisTemplate.opsForValue().get(ClusterConst.USER_ACCOUNT + userId);
    }

    @Override
    public boolean putUser(ClusterUser user) {
        try {
            redisTemplate.opsForValue().set(ClusterConst.USER_ACCOUNT + user.getUserId(), user);
            // 将用户信息放到session中，HttpSessionListener失效用户使用。
            RequestContextHolder.getSession().setAttribute(ClusterConst.USER_ACCOUNT, user.getUserId());
        } catch (Exception e) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("向集群中新增用户[{}]出错。", user.getUserId());
            }
            return false;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("向集群中新增用户[{}]成功。", user.getUserId());
        }
        return true;
    }

    @Override
    public boolean updateUser(ClusterUser user) {
        return putUser(user);
    }

    @Override
    public boolean deleteUser(String userId) {
        try {
            redisTemplate.delete(ClusterConst.USER_ACCOUNT + userId);
        } catch (Exception e) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("删除集群中用户[{}]出错。", userId);
            }
            return false;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("删除集群中用户[{}]成功。", userId);
        }
        return true;
    }

}
