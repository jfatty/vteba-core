package com.vteba.tx.hibernate.interceptor;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import com.vteba.common.model.Ast;

public class DefaultInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if (entity instanceof Ast) {
            int i = 0;
            for (String name : propertyNames) {
                if (name.equals("ctime")) {
                    state[i] = new Date();
                } else if (name.equals("mtime")) {
                    state[i] = new Date();
                } else if (name.equals("datasource")) {
                    state[i] = "OK";
                }
                i++;
            }
            return true;
        }
        return false;
    }
}
