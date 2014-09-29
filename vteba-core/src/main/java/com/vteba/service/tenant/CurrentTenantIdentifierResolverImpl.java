package com.vteba.service.tenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 当前租户ID处理器
 * @author yinlei 
 * date 2012-8-13 下午9:39:58
 */
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

	@Override
	public String resolveCurrentTenantIdentifier() {
		String tenantId = null;
		
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
//            	UserDetails user = (UserDetails) authentication.getPrincipal();
//            	tenantId = user.getTenantIdentifier();
            }
        } else {
            return null;
        }
        //String schema = SchemaContextHolder.getSchema();
        
        //System.out.println("返回多租户ID时，tenantId："+ tenantId +"，Schema是：" + schema + "，线程是：" + Thread.currentThread());
        //tenantId = "biziliDataSource";//btm
        return tenantId;
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}

}
