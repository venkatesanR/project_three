package com.addval.ejbutils.server;

import javax.ejb.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.ColumnInfo;
import java.util.Vector;

/**
 * Remote Interface class for the EJBSEditorMetaDataBean
 * @author AddVal Technology Inc.
 */
public interface EJBSEditorMetaDataRemote extends EJBSEditorMetaData, EJBObject
{
}
