package com.vteba.cluster.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vteba.cluster.user.ClusterUser;
import com.vteba.cluster.user.UserUtils;
import com.vteba.utils.common.PropUtils;

/**
 * 用户登录权限过滤器，一些资源必须登录才能访问。公共资源直接放行。
 * @author yinlei 
 * @since 2013-12-1 15:16
 */
public class UserAuthFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthFilter.class);
    
    private Set<String> urlSets = new HashSet<String>();
    private static final Pattern IMAGE = Pattern.compile("^/images/[a-zA-Z0-9_-.]+.(jpg|jpeg|gif|png|bmp)$");
    private static final Pattern CSS = Pattern.compile("^/css/[a-zA-Z0-9_-.]+.css$");//\\w+
    private static final Pattern JS = Pattern.compile("^/js/[a-zA-Z0-9_-.]+.js$");
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        urlSets.add("index.jsp");
        urlSets.add("index.html");
        urlSets.add("index.htm");
        urlSets.add("favicon.ico");
        
        String filters = PropUtils.get("filter.url");
        if (filters != null) {
            String[] urls = StringUtils.split(filters, ",");
            urlSets.addAll(Arrays.asList(urls));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                             ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String path = httpServletRequest.getServletPath();
        if (urlSets.contains(path)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("公开资源[{}]，放行。", path);
            }
            chain.doFilter(request, response);
        } else {
            if (matches(path)) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("公开静态资源[{}]，放行。", path);
                }
                chain.doFilter(request, response);
            } else {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("受限资源[{}]，需要登录。", path);
                }
                ClusterUser user = UserUtils.getUser();
                if (user == null) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("用户未登录，重定向到登录页面。", path);
                    }
                    request.getRequestDispatcher(PropUtils.get("userLogin.url")).forward(request, response);
                }
            }
        }
        
    }

    private boolean matches(String path) {
        return IMAGE.matcher(path).matches() || CSS.matcher(path).matches() || JS.matcher(path).matches();
    }
    
    @Override
    public void destroy() {
        
    }

}
