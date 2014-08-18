package com.vteba.tx.hibernate.listener;

import java.io.Serializable;

import org.hibernate.event.internal.DefaultUpdateEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;

public class UpdateEventListener extends DefaultUpdateEventListener {

	private static final long serialVersionUID = 1L;

	public UpdateEventListener() {
		
	}

	@Override
	protected Serializable performSaveOrUpdate(SaveOrUpdateEvent event) {
		
		return super.performSaveOrUpdate(event);
	}

}
