package com.addval.hibernateutils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.addval.utils.CacheException;
import com.addval.utils.CacheMgr;
import com.addval.utils.DefaultNamedCacheInitializer;
import com.addval.utils.LogMgr;

public class ObjectCacheInitializer extends DefaultNamedCacheInitializer implements Comparator {

	protected static transient final Logger _logger = LogMgr.getLogger(ObjectCacheInitializer.class);
	private GenericDao _dao;

	public void setGenericDao(GenericDao aDAO) {
		_dao = aDAO;
	}

	public GenericDao getGenericDao() {
		return _dao;
	}

	public ObjectCacheInitializer() {
	}

	public ObjectCacheInitializer(GenericDao dao, String objectName, String objectType, String keyName) {
		setGenericDao(dao);
		setObjectName(objectName);
		setObjectType(objectType);
		setKeyName(keyName);
	}

	public Object populateData(CacheMgr cache, String objectName, boolean refreshFlag) throws CacheException {
		Object obj = null;
		try {
			obj = getInstanceForClassName(getObjectType());
		}
		catch (Throwable e) {
			throw new CacheException(e);
		}

		List objects = getGenericDao().findAll(obj.getClass());

		if (getStoreAsList()) {

			// check if it should be sorted
			if (getSortName() != null) {

				try {
					Collections.sort(objects, this);
				}
				catch (Exception e) {
					_logger.error("Error sorting cache object: " + objectName, e);
					throw new CacheException(e);
				}
			}
			else if (getComparator() != null) {
				try {
					Collections.sort(objects, getComparator());
				}
				catch (Exception e) {
					_logger.error("Error sorting cache object: " + objectName, e);
					throw new CacheException(e);
				}
			}

			return objects;

		}
		else {
			Hashtable objMap = new Hashtable();

			try {
				for (int i = 0; i < objects.size(); i++) {
					Object theObj = (Object) objects.get(i);
					Object theKey = BeanUtils.getProperty(theObj, getKeyName());
					objMap.put(theKey, theObj);
				}
			}
			catch (Exception e) {
				_logger.error("Error converting cache object into hashtable: " + objectName, e);
				throw new CacheException(e);
			}

			return objMap;
		}

	}

	private Object getInstanceForClassName(String objectType) throws Throwable {
		// Construct a new instance of the specified class
		Class clazz = null;
		try {
			if (clazz == null) {
				Thread t = Thread.currentThread();
				ClassLoader cl = t.getContextClassLoader();
				clazz = cl.loadClass(objectType);
			}
			Object obj = (Object) clazz.newInstance();
			return (obj);
		}
		catch (Throwable t) {
			_logger.error("Unable to instantiate " + objectType);
			throw t;
		}
	}

	public int compare(Object obj1, Object obj2) {
		try {
			Comparable key1 = (Comparable) BeanUtils.getProperty(obj1, getSortName());
			Comparable key2 = (Comparable) BeanUtils.getProperty(obj2, getSortName());

			if ((key1 != null) && (key2 != null))
				return key1.compareTo(key2);
			else
				return 0;

		}
		catch (Exception e) {
			_logger.error("Comparator error ", e);
			return 0;
		}

	}

	public boolean equals(Object obj1, Object obj2) {
		return (compare(obj1, obj2) == 0);
	}

}
