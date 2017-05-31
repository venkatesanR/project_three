package com.techmania.hibernateutils;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public class AbstractDatabaseManager implements TransactionPhases {

	public <Q, T> List<T> getList(Q queryParam, Class clazz) {
		return null;
	}

	public <Q, T> T get(Q queryParam, Class clazz) {
		return null;
	}

	public <T> void save(T input) {
		Session session = null;
		try {
			session = HibernateConnectionManager.makeConnection();
			session.beginTransaction();
			session.save(input);
			session.getTransaction().commit();
		} catch (Throwable root) {
			throw new HibernateException("Error while doing AbstractDatabaseManager > save", root);
		} finally {
			if (session != null) {
				HibernateConnectionManager.closeConnection();
			}
		}
	}

	public <T> boolean update(T input) {
		Session session = HibernateConnectionManager.makeConnection();
		session.beginTransaction();
		session.update(input);
		session.getTransaction().commit();
		HibernateConnectionManager.closeConnection();
		return false;
	}

}
