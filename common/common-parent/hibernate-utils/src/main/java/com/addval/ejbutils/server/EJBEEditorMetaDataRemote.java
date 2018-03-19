package com.addval.ejbutils.server;

import com.addval.metadata.EditorMetaData;
import java.rmi.RemoteException;
import com.addval.metadata.ColumnInfo;
import javax.ejb.*;

/**
 * The remote interface for the entity bean EJBEEditorMetaDataBean
 */
public interface EJBEEditorMetaDataRemote extends EJBObject {

    /**
     * Looks up an EditorMetData object. This interface for the entity bean should not be
     *  called from the client
     * @roseuid 3B1B97CC0389
     */
    public EditorMetaData lookup    () throws RemoteException;

    /**
     * Looks up a ColumnInfo object. This interface for the entity bean should not be called
     *  from the client
	 * @roseuid 3C87D9770349
     */
    public ColumnInfo lookupColumnInfo    () throws RemoteException;

    /**
     * @roseuid 3D5052AD034D
 */
    public EditorMetaData update    (EditorMetaData editorMetaData) throws RemoteException;
}
