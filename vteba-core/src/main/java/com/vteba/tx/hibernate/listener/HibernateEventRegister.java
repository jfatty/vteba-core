package com.vteba.tx.hibernate.listener;

import javax.annotation.PostConstruct;

import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;

public class HibernateEventRegister {

	@Autowired
	private SessionFactory sessionFactory;

	@PostConstruct
	public void registerListeners() {
		EventListenerRegistry registry = ((SessionFactoryImpl) sessionFactory)
				.getServiceRegistry().getService(EventListenerRegistry.class);
		registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(
				null);
//		registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(
//				logListener);
//		registry.getEventListenerGroup(EventType.POST_DELETE).appendListener(
//				logListener);
	}
}
