package com.addval.udfutils;

import java.util.Collections; 
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.addval.utils.LogMgr;
import com.addval.hibernateutils.GenericDao;

public class UdfDefnInitializer {

	protected static transient final Logger _logger = LogMgr.getLogger(UdfDefnInitializer.class);
	private GenericDao _dao;

	public void setGenericDao(GenericDao aDAO) {
		_dao = aDAO;
	}

	public GenericDao getGenericDao() {
		return _dao;
	}

	public UdfDefnInitializer() {
	}

	public UdfDefnInitializer(GenericDao dao) {
		setGenericDao(dao);
	}


	public void initialize() {
		List entitylist = getGenericDao().findAll(EntityDefn.class);

		for (int i = 0; i < entitylist.size(); i++) {
			EntityDefn theObj = (EntityDefn) entitylist.get(i);

			// create an instance of the entity
			UdfSource src;
			try {
				src = (UdfSource)	getInstanceForClassName(theObj.getClassName());

				// store the entity defn in a static variable for the entity to use
				src.setEntityDefn(theObj);
			} catch (Throwable e) {

				_logger.error("Unable to initialize user defined attributes " + theObj.getName());
			}

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
	
}
