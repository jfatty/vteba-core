package com.vteba.tx.hibernate.listener;

import java.util.Date;

import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;

import com.vteba.common.model.Ast;

/**
 * 更新事件监听器
 * @author yinlei 
 * @since 2013-8-18
 */
public class UpdateListener implements PreUpdateEventListener {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof Ast) {
            Ast ast = (Ast) entity;
            ast.setMtime(new Date());
            return true;
        }
        return false;
    }

}
