package com.addval.ejbutils.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.addval.metadata.EditorMetaData;
import com.addval.metadata.ColumnInfo;
import com.addval.utils.Pair;

import java.util.Vector;

/**
 * Remote Interface class for the EJBSEditorMetaDataBean
 * @author AddVal Technology Inc.
 */
public interface EJBSEditorMetaData {

    /**
     * Looks up an EditorMetData object. This interface for the session bean is meant to
     *  be called from the client. If the user has any customizations for the EditorMetaData,
     *  then the customized EditorMetaData is returned.
     * @roseuid 3E20744D01E0
     */
    public EditorMetaData lookup    (String name, String type) throws RemoteException;

    /**
     * Looks up an ColumnInfo object. This interface for the session bean can be called from
     *  the client
     * @roseuid 3E20744D0370
     */
    public ColumnInfo lookupColumnInfo    (String colName) throws RemoteException;

    /**
     * This interface returns the ColumnMetaData for all columns of the editor irrespective
     *  of user customization
     * @roseuid 3E20744E00DD
     */
    public java.util.Vector lookupAllColumns    (String name, String type) throws RemoteException;

    /**
     * This interface update the EditorMetaData to the database. The interface customizes
     *  this EditorMetaData for the callee.
     * @roseuid 3E20744E026D
     */
    public EditorMetaData update    (EditorMetaData editorMetaData) throws RemoteException;

    /**
     * The interface is similar to the lookup() interface with the explicit userId specified
     *  for user customizations
     * @roseuid 3E20744F002A
     */
    public EditorMetaData lookup(String name, String type, String userId) throws RemoteException;


    public EditorMetaData lookup(String name, String type, String userId,boolean configRole) throws RemoteException;

    /**
     * The interface is similar to the update() interface with the explicit userId specified
     * @roseuid 3E20744F0201
     */
    public EditorMetaData update    (EditorMetaData editorMetaData, String userId) throws RemoteException;
    
    
    public List<Pair> lookupNameLabelPairs(String type) throws RemoteException;    
}