package com.vteba.tx.hibernate.listener;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.event.internal.DefaultUpdateEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;

import com.vteba.common.model.Ast;

public class UpdateEventListener extends DefaultUpdateEventListener {

	private static final long serialVersionUID = 1L;

	public UpdateEventListener() {
		
	}
	//拦截这个方法也是可以的。注册事件的时候，可以自己选择注册成update，或者save，或者saveOrUpdate
//	@Override
//    public void onSaveOrUpdate(SaveOrUpdateEvent event) {
//	    Object object = event.getObject();
//        if (object instanceof Ast) {
//            Ast ast = (Ast) object;
//            ast.setMtime(new Date());
//        }
//        super.onSaveOrUpdate(event);
//    }

    @Override
	protected Serializable performSaveOrUpdate(SaveOrUpdateEvent event) {
		Object object = event.getObject();
		if (object instanceof Ast) {
		    Ast ast = (Ast) object;
		    ast.setMtime(new Date());
		}
		return super.performSaveOrUpdate(event);
	}

}
