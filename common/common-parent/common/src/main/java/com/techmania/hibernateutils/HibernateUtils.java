package com.techmania.hibernateutils;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.techmania.utils.LogMgr;

public class HibernateUtils {
	private static final Logger LOGGER = LogMgr.getLogger(HibernateUtils.class);
	private static final SessionFactory sessionFactory;

	private HibernateUtils() {
		// No invocation please
	}
	static {
		try {
			sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
		} catch (Throwable ex) {
			LOGGER.debug("********************** Initial SessionFactory creation failed.", ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
