package com.techmania.hibernateutils;

import org.hibernate.Session;

public class HibernateConnectionManager {
	private HibernateConnectionManager() {
		// No invocation
	}

	public static Session makeConnection() {
		return HibernateUtils.getSessionFactory().getCurrentSession();
	}

	public static void closeConnection() {
		HibernateUtils.getSessionFactory().close();
	}
}
