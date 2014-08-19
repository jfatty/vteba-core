package com.vteba.tx.hibernate.listener;

import java.util.Date;

import org.hibernate.event.internal.DefaultSaveOrUpdateEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;

import com.vteba.common.model.Ast;

public class SaveOrUpdateEventListener extends DefaultSaveOrUpdateEventListener {

    private static final long serialVersionUID = 1L;

    public SaveOrUpdateEventListener() {

    }

    @Override
    public void onSaveOrUpdate(SaveOrUpdateEvent event) {
        Object object = event.getObject();
        if (object instanceof Ast) {
            Ast ast = (Ast) object;
            ast.setMtime(new Date());
        }
        super.onSaveOrUpdate(event);
    }

}
