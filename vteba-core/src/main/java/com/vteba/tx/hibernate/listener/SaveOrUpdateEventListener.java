package com.vteba.tx.hibernate.listener;

import org.hibernate.event.internal.DefaultSaveOrUpdateEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;

public class SaveOrUpdateEventListener extends DefaultSaveOrUpdateEventListener {

	private static final long serialVersionUID = 1L;

	public SaveOrUpdateEventListener() {
		
	}

	@Override
	public void onSaveOrUpdate(SaveOrUpdateEvent event) {
		event.getObject();
		super.onSaveOrUpdate(event);
	}

}
