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

import java.rmi.RemoteException;

import java.util.ArrayList;


public interface BulkUpdateMgr
{
    public ArrayList<DirectoryInfo> lookupDirectoryInfos(DirectoryInfo filter) throws RemoteException;

    public DirectoryInfo lookupDirectoryInfo(DirectoryInfo filter) throws RemoteException;

    public ArrayList<BulkUpdate> lookupBulkUpdates(BulkUpdate filter) throws RemoteException;

    public BulkUpdate lookupBulkUpdate(BulkUpdate filter) throws RemoteException;

    public void addNewBulkUpdate(BulkUpdate bulkUpdate) throws RemoteException;

    public void updateBulkUpdate(BulkUpdate bulkUpdate) throws RemoteException;

    public void deleteBulkUpdate(BulkUpdate bulkUpdate) throws RemoteException;

    public void validate(EditorMetaData metadata, BulkUpdate bulkUpdate) throws RemoteException;

    public void runOverwriteBatch(String directoryName, String editorName, String updateName) throws RemoteException;

    // when the editor name alone is available / sufficient to do the update
    public void runOverwriteBatch(String editorName) throws RemoteException;

    public void runOverwriteBatch(String scenarioKey, String editorName) throws RemoteException;
}
