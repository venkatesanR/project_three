package com.addval.ejbutils.server;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.environment.Environment;
import com.addval.metadata.EditorMetaData;

/**
 * The remote interface for the session bean EJBSTableManager
 */
public interface EJBSTableManager {
	/**
	 * @roseuid 3B66EC970349
	 * @J2EE_METHOD -- lookup This interface allows clients to query a table/view that is represented using EditorMetaData. The interface accepts an EJBCriteria that represents the search criteria as input and returns an EJBResultSet
	 */
	public EJBResultSet lookupForUpdate(EJBCriteria criteria) throws RemoteException;

	/**
	 * @roseuid 3B66EC970349
	 * @J2EE_METHOD -- lookup This interface allows clients to query a table/view that is represented using EditorMetaData. The interface accepts an EJBCriteria that represents the search criteria as input and returns an EJBResultSet
	 */
	public EJBResultSet lookup(EJBCriteria criteria) throws RemoteException;

	/**
	 * to be used by aggregatable list process where the sql is prebuilt and the editor meta data has super set of columns. This method can be used to set the pageSize, navigation within the resultset etc.
	 */
	public EJBResultSet lookup(String sql, EditorMetaData editorMetaData, EJBCriteria criteria) throws RemoteException;

	/**
	 * to be used by aggregatable list process where the sql is prebuilt and the editor meta data has super set of columns.
	 */
	public EJBResultSet lookup(String sql, EditorMetaData editorMetaData) throws RemoteException;

	/**
	 * @roseuid 3B66EC970371
	 * @J2EE_METHOD -- update The update interface enables insert/update/delete operations on an EJBResultSet that is build for a particular table/view represented by EditorMetaData. The status of a record in the EJBResultSet represented by the EJBRecord indicates whether the record is to be
	 *              insert/update/delete from the database. The syncStatus attribute of the EJBRecord will be updated to indicate if the operation specified for the record was successful. This interface will not ensure that all records in the resultset participate in a single transaction.
	 */
	public EJBResultSet update(EJBResultSet rs) throws RemoteException, EJBXRuntime;

	/**
	 * @roseuid 3B82E2F500E5
	 * @J2EE_METHOD -- updateTransaction This interface is similar to the update() interface, but it will ensure that all records in the resultset are updated to the database in a single transaction.
	 */
	public EJBResultSet updateTransaction(EJBResultSet rs) throws RemoteException, EJBXRuntime;

	/**
	 * @roseuid 3F2A19500242
	 * @J2EE_METHOD -- updateTransaction
	 */
	public boolean updateTransaction(EJBResultSet masterSet, EJBResultSet deleteSet, EJBResultSet insertSet) throws EJBXRuntime, RemoteException;

	public boolean updateTransaction(EJBResultSet masterSet, List<EJBResultSet> deleteSet, List<EJBResultSet> insertSet) throws EJBXRuntime, RemoteException;

	/*
	 * Mass Update
	 */
	public int updateTransaction(EJBCriteria criteria) throws RemoteException, EJBXRuntime;

	public void setCallerPrincipalName(String name) throws RemoteException;
	
	public void setEnvironmentInstances(Map<String,Environment> envInstances) throws RemoteException;

}