/*
 * Copyright (c) 2001-2006 Accenture LLC.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Accenture. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Accenture.
 *
 * ACCENTURE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ACCENTURE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.addval.ejbutils.server;

import com.addval.metadata.BulkUpdate;
import com.addval.metadata.DirectoryInfo;
import com.addval.metadata.EditorMetaData;

import com.addval.springutils.ServerRegistry;

import org.springframework.ejb.support.AbstractStatelessSessionBean;

import java.io.Serializable;

import java.rmi.RemoteException;

import java.util.ArrayList;

import javax.ejb.CreateException;
import javax.ejb.SessionContext;


public class BulkUpdateMgrBeanSpringImpl extends AbstractStatelessSessionBean implements BulkUpdateMgr, Serializable
{
    private static final long serialVersionUID = -510235926860294774L;
    private BulkUpdateMgr _utility;
    protected String _PRIMARY_CONTEXT_ID = "com.addval";
    protected String _BULK_UPDATE_MGR_BEAN_ID = "com.addval.ejbutils.server.BulkUpdateMgr";

    public void setSessionContext(SessionContext sessionContext)
    {
        super.setSessionContext(sessionContext);
        ServerRegistry registry = new ServerRegistry();
        setBeanFactoryLocator(registry.getBeanFactoryLocator());
        setBeanFactoryLocatorKey(getSpringContextId());
    }

    protected String getSpringContextId()
    {
        return _PRIMARY_CONTEXT_ID;
    }

    protected String getSpringBeanId()
    {
        return _BULK_UPDATE_MGR_BEAN_ID;
    }

    protected void onEjbCreate() throws CreateException
    {
        _utility = (BulkUpdateMgr) getBeanFactory().getBean(getSpringBeanId());
    }

    public ArrayList<DirectoryInfo> lookupDirectoryInfos(DirectoryInfo filter) throws RemoteException
    {
        return _utility.lookupDirectoryInfos(filter);
    }

    public DirectoryInfo lookupDirectoryInfo(DirectoryInfo filter) throws RemoteException
    {
        return _utility.lookupDirectoryInfo(filter);
    }

    public ArrayList<BulkUpdate> lookupBulkUpdates(BulkUpdate filter) throws RemoteException
    {
        return _utility.lookupBulkUpdates(filter);
    }

    public BulkUpdate lookupBulkUpdate(BulkUpdate filter) throws RemoteException
    {
        return _utility.lookupBulkUpdate(filter);
    }

    public void addNewBulkUpdate(BulkUpdate bulkUpdate) throws RemoteException
    {
        _utility.addNewBulkUpdate(bulkUpdate);
    }

    public void updateBulkUpdate(BulkUpdate bulkUpdate) throws RemoteException
    {
        _utility.updateBulkUpdate(bulkUpdate);
    }

    public void deleteBulkUpdate(BulkUpdate bulkUpdate) throws RemoteException
    {
        _utility.deleteBulkUpdate(bulkUpdate);
    }

    public void validate(EditorMetaData metadata, BulkUpdate bulkUpdate) throws RemoteException
    {
        _utility.validate(metadata, bulkUpdate);
    }

    public void runOverwriteBatch(String directoryName, String editorName, String updateName) throws RemoteException
    {
        _utility.runOverwriteBatch(directoryName, editorName, updateName);
    }

    public void runOverwriteBatch(String scenarioKey, String editorName) throws RemoteException
    {
        _utility.runOverwriteBatch(scenarioKey, editorName);
    }

    public void runOverwriteBatch(String editorName) throws RemoteException
    {
        _utility.runOverwriteBatch(editorName);
    }
}
