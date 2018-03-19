package com.addval.ejbutils.server;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.addval.metadata.UserCriteria;

public interface UserCriteriaMgr {

	public UserCriteria lookupUserCriteria(UserCriteria filter) throws RemoteException;
	public void addNewUserCriteria(UserCriteria criteria) throws RemoteException;
	public void updateUserCriteria(UserCriteria criteria) throws RemoteException;
	public void deleteUserCriteria(UserCriteria criteria) throws RemoteException;
	
	public ArrayList getUserCriteriaNames(UserCriteria filter) throws RemoteException;

	
}
