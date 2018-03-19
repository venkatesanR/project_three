/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.hibernateutils;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.HibernateTemplate;

public interface GenericDao {

	/**
	 * Persist the newInstance object into database
	 */
	java.io.Serializable create(Object newInstance);

	/**
	 * Persist the newInstance object into database, as the specified entityName
	 */
	java.io.Serializable create(Object newInstance, String entityName);



	/**
	 * Retrieve an object that was previously persisted to the database using
	 * the indicated id as primary key, as the specified entityName.
	 */
	Object read(java.io.Serializable id, Class c, boolean lock);

	/**
	 * Retrieve an object that was previously persisted to the database using
	 * the indicated id as primary key, as the specified entityName.
	 */
	Object read(java.io.Serializable id, String entityName, boolean lock);



	/**
	 * Save changes made to a persistent object.
	 */
	void update(Object transientObject);

	/**
	 * Save changes made to a persistent object, as the specified entityName.
	 */
	void update(Object transientObject, String entityName);



	/**
	 * Merge the changes made the specified object onto the  persisted version of the object.
	 */
	void merge(Object transientObject);

	/**
	 * Merge the changes made the specified object onto the  persisted version of the object,
	 * as the specified entityName.
	 */
	void merge(Object transientObject, String entityName);



	/**
	 * Remove the object that is persisted in the database.
	 */
	void delete(Object persistentObject);

	/**
	 * Remove the object that is persisted in the database, as the specified entityName.
	 */
	void delete(Object persistentObject, String entityName);



	/**
	 * Read and return all persisted instances of the entity class.
	 *
	 * (Note: there is no HibernateTemplate) support for String entityName for this operation.)
	 */
	List findAll(Class c);


	/**
	 * Do a query to find and return all persisted object instances that match the specified object.
	 */
	List findByExample(Object aExample);

	/**
	 * Do a query to find and return all persisted object instances that match the specified object
	 * as the specified entityName.
	 */
	List findByExample(Object aExample, String entityName);



	/**
	 * Do a query to find and return all persisted object instances, based on the specified
	 * Hibernate criteria object.
	 *
	 * (Note: there is no HibernateTemplate) support for String entityName for this operation.)
	 */
	List findByCriteria(DetachedCriteria criteria, int firstResult, int maxResult);



	void flush();

	void clear();

	HibernateTemplate getTemplate();
}
