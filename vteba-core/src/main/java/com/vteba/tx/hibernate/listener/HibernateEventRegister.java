package com.vteba.tx.hibernate.listener;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;

public class HibernateEventRegister {
	
	public HibernateEventRegister() {
		
	}
	
	@Inject
	private SessionFactory sessionFactory;
	
	@Inject
	private UpdateEventListener updateEventListener;

	@PostConstruct
	public void registerListeners() {
	    SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) sessionFactory;
	    ServiceRegistry serviceRegistry = sessionFactoryImpl.getServiceRegistry();
		EventListenerRegistry registry = serviceRegistry.getService(EventListenerRegistry.class);
		registry.getEventListenerGroup(EventType.UPDATE).appendListener(updateEventListener);
//		registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(logListener);
	}
}
