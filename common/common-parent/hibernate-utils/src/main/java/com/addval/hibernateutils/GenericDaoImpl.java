/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.hibernateutils;

import java.io.Serializable;
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GenericDaoImpl extends HibernateDaoSupport implements GenericDao {

	/**
	 * Create a HibernateTemplate for the given SessionFactory.
	 * Only invoked if populating the DAO with a SessionFactory reference!
	 * <p>Can be overridden in subclasses to provide a HibernateTemplate instance
	 * with different configuration, or a custom HibernateTemplate subclass.
	 * @param sessionFactory the Hibernate SessionFactory to create a HibernateTemplate for
	 * @return the new HibernateTemplate instance
	 * @see #setSessionFactory
	 */
	protected HibernateTemplate createHibernateTemplate(SessionFactory sessionFactory) {
		//System.out.println("GenericDaoImpl:createHibernateTemplate");
		HibernateTemplate hibernateTemplate = new HibernateTemplate(sessionFactory);
		hibernateTemplate.setAlwaysUseNewSession(true);
		return hibernateTemplate;
	}

	public Serializable create(Object newInstance) {
		return getHibernateTemplate().save(newInstance);
	}

	public Serializable create(Object newInstance, String entityName) {
		return getHibernateTemplate().save(entityName, newInstance);
	}

	public Object read(Serializable id, Class c, boolean lock) {
		if (lock){
			return (Object) getHibernateTemplate().load(c, id, LockMode.UPGRADE);
		}
		else{
			return (Object) getHibernateTemplate().load(c, id);
		}
	}

	public Object read(Serializable id, String entityName, boolean lock) {
		if (lock){
			return (Object) getHibernateTemplate().load(entityName, id, LockMode.UPGRADE);
		}
		else{
			return (Object) getHibernateTemplate().load(entityName, id);
		}
	}

	public void merge(Object transientObject) {
		//System.out.println("Calling merge");
		getHibernateTemplate().merge(transientObject);
		//System.out.println("Finished merge");
	}

	public void merge(Object transientObject, String entityName) {
		//System.out.println("Calling merge");
		getHibernateTemplate().merge(entityName, transientObject);
		//System.out.println("Finished merge");
	}

	public void update(Object transientObject) {
		//System.out.println("Calling update");
		getHibernateTemplate().update(transientObject);
		//System.out.println("Finished update");
	}

	public void update(Object transientObject, String entityName) {
		//System.out.println("Calling update");
		getHibernateTemplate().update(entityName, transientObject);
		//System.out.println("Finished update");
	}

	public void delete(Object persistentObject) {
		//System.out.println("Calling delete");
		getHibernateTemplate().delete(persistentObject);
		//System.out.println("Finished delete");
	}

	public void delete(Object persistentObject, String entityName) {
		getHibernateTemplate().delete(entityName, persistentObject);
	}

	public List findAll(Class c) {
		return getHibernateTemplate().loadAll(c);
	}

	public List findByExample(Object obj) {
		return getHibernateTemplate().findByExample(obj);
	}

	public List findByExample(Object obj, String entityName) {
		return getHibernateTemplate().findByExample(entityName, obj);
	}

	public List findByCriteria(DetachedCriteria criteria, int firstResult, int maxResult) {
		return getHibernateTemplate().findByCriteria(criteria, firstResult, maxResult);
	}

	public void flush() {
		getHibernateTemplate().flush();
	}

	public void clear() {
		getHibernateTemplate().clear();
	}

	public HibernateTemplate getTemplate() {
		return getHibernateTemplate();
	}

}
