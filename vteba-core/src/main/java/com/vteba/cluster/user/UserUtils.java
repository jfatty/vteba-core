package com.vteba.cluster.user;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vteba.cluster.consts.ClusterConst;
import com.vteba.cluster.user.service.spi.ClusterUserService;
import com.vteba.service.context.spring.ApplicationContextHolder;
import com.vteba.utils.web.ServletUtils;

/**
 * 用户工具类，获取当前用户，同步用户等等。
 * @author yinlei 
 * @since 2013-12-1 13:56
 */
public class UserUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserUtils.class);
    
    private static final ClusterUserService clusterUserService;
    static {
        clusterUserService = ApplicationContextHolder.getBean(ClusterUserService.class);
    }
    
    public static boolean deleteUser(String userId) {
        return clusterUserService.deleteUser(userId);
    }
    
    public static ClusterUser getUser() {
        return clusterUserService.getUser();
    }
    
    public static ClusterUser getUser(String userId) {
        return clusterUserService.getUser(userId);
    }
    
    public static void addUser(ClusterUser user) {
        clusterUserService.putUser(user);
    }
    
    public static void updateUser(ClusterUser user) {
        clusterUserService.updateUser(user);
    }
    public static String getUserId() {
        String userId = null;
        Cookie[] cookies = getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie != null && cookie.getName().equals(ClusterConst.USER_ACCOUNT)) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }
        if (userId == null) {
            LOGGER.error("没有从cookie中获取到用户账号信息。");
        }
        return userId;
    }
    
    public static String getAuthCode() {
        String authCode = null;
        Cookie[] cookies = getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie != null && cookie.getName().equals(ClusterConst.AUTH_CODE)) {
                    authCode = cookie.getValue();
                    break;
                }
            }
        }
        if (authCode == null) {
            LOGGER.error("没有从cookie中获取到验证码信息。");
        }
        return authCode;
    }
    
    private static Cookie[] getCookies() {
        Cookie[] cookies = null;
        try {
            cookies = ServletUtils.getRequest().getCookies();
        } catch (Exception e) {
            LOGGER.error("从当前线程获取HttpServletRequest失败。", e.getMessage());
        }
        if (cookies == null) {
            LOGGER.error("cookie为空，无法获取信息。");
        }
        return null;
    }
}
