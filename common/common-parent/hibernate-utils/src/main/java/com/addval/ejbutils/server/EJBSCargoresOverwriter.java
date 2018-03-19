package com.addval.ejbutils.server;


import java.rmi.RemoteException;
import java.util.Vector;
import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.UpdateUserCriteria;

public interface EJBSCargoresOverwriter
{
	public void validate(EditorMetaData metadata,UpdateUserCriteria criteria) throws RemoteException;

	public void runOverwriteBatch(String directoryName,String editorName,String criteriaName) throws RemoteException;

	public void runOverwriteBatch(String scenarioKey,String editorName) throws RemoteException;

}