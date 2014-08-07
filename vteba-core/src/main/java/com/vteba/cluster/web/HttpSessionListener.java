package com.vteba.cluster.web;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vteba.cluster.consts.ClusterConst;
import com.vteba.cluster.user.UserUtils;

/**
 * 登录用户，HttpSession过期Listener（监听器）。HttpSession过期将删除集群中用户信息。
 * @author yinlei 
 * @since 2013-12-1 14:06
 */
public class HttpSessionListener implements javax.servlet.http.HttpSessionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSessionListener.class);
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // do nothing
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession httpSession = se.getSession();
        String userId = (String) httpSession.getAttribute(ClusterConst.USER_ACCOUNT);
        if (userId == null) {
            LOGGER.warn("HttpSession用没有用户信息，可能无法同步集群中用户登录信息。");
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("HttpSession失效，SessionId=[{}]，将删除集群中用户=[{}]。", httpSession.getId(), userId);
        }
        boolean result = UserUtils.deleteUser(userId);
        if (result) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("删除集群中用户=[{}]成功。", userId);
            }
        } else {
            LOGGER.warn("删除集群中用户=[{}]失败，可能导致用户信息失效。", userId);
        }
    }

}
