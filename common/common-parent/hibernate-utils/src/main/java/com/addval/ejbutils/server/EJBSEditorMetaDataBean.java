package com.addval.ejbutils.server;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.ejb.EJBException;
import javax.ejb.ObjectNotFoundException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.addval.environment.EJBEnvironment;
import com.addval.environment.Environment;
import com.addval.metadata.ColumnInfo;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.Pair;

/**
 * EJBSEditorMetaDataBean Implementaion Class. The client calls only the
 * EJBSEditorMetaData session bean for looking up an editor. This session bean
 * inturn calls the EJBEEditorMetaData entity bean for looking up EditorMetaData from
 *  the database.
 * @author AddVal Technology Inc.
 * @author AddVal Technology Inc.
 */
public class EJBSEditorMetaDataBean implements SessionBean {
    /**
     * Attributes declaration
    */
    private SessionContext m_context;
    private final static String m_module = "com.addval.ejbutils.server.EJBSEditorMetaDataBean";
    private String m_subSystem = "";

    public String getSubSystem() {
        return m_subSystem;
    }


    /**
     * Called by the container to create a session bean instance. Its parameters typically
     * contain the information the client uses to customize the bean instance for its use.
     * It requires a matching pair in the bean class and its home interface.
     * @roseuid 3AF926CA01F0
     * @J2EE_METHOD  --  ejbCreate
     * @throws RemoteException
     */
    public void ejbCreate    () throws RemoteException {

    }

    /**
     * Set the associated session context. The container calls this method after the instance
     * creation. The enterprise Bean instance should store the reference to the context
     * object in an instance variable. This method is called with no transaction context.
     * @roseuid 3AF926CA01EA
     * @J2EE_METHOD  --  setSessionContext
     * @param context
     * @throws RemoteException
     */
    public void setSessionContext    (SessionContext context) throws RemoteException {

        m_context = context;
        try{
            m_subSystem = (String) EJBEnvironment.lookupLocalContext( "SUBSYSTEM" );
        }
        catch(Exception e){

			e.printStackTrace();
			throw new EJBException( e );
		}
    }

    /**
     * @roseuid 3B1A7C350066
     * @J2EE_METHOD  --  unsetSessionContext
     * @param context
     * @throws RemoteException
     */
    public void unsetSessionContext    (SessionContext context) throws RemoteException {

        m_context = null;
    }

    /**
     * A container invokes this method before it ends the life of the session object. This
     * happens as a result of a client's invoking a remove operation, or when a container
     * decides to terminate the session object after a timeout. This method is called with
     * no transaction context.
     * @roseuid 3AF926CA01EC
     * @J2EE_METHOD  --  ejbRemove
     * @throws RemoteException
     */
    public void ejbRemove    () throws RemoteException {

    }

    /**
     * The activate method is called when the instance is activated from its 'passive' state.
     * The instance should acquire any resource that it has released earlier in the ejbPassivate()
     * method. This method is called with no transaction context.
     * @roseuid 3AF926CA01ED
     * @J2EE_METHOD  --  ejbActivate
     * @throws RemoteException
     */
    public void ejbActivate    () throws RemoteException {

    }

    /**
     * The passivate method is called before the instance enters the 'passive' state. The
     * instance should release any resources that it can re-acquire later in the ejbActivate()
     * method. After the passivate method completes, the instance must be in a state that
     * allows the container to use the Java Serialization protocol to externalize and store
     * away the instance's state. This method is called with no transaction context.
     * @roseuid 3AF926CA01EE
     * @J2EE_METHOD  --  ejbPassivate
     * @throws RemoteException
     */
    public void ejbPassivate    () throws RemoteException {

    }

    /**
     * Given an editorName and editorType, this method looks up a serializable
     * EditorMetaData object. This method calls an entity bean EJBEEditorMetaData for
     * looking up the editor details.
     * @roseuid 3AF926CA01F1
     * @J2EE_METHOD  --  lookup
     * @param name
     * @param type
     * @return EditorMetaData
     * @throws RemoteException
     */
    public EditorMetaData lookup    (String name, String type) throws RemoteException {
		return lookup(name, type, "DEFAULT" );
    }

    /**
     * @roseuid 3C87BEE7008E
     * @J2EE_METHOD  --  lookupColumnInfo
     * @param colName
     * @return ColumnInfo
     * @throws RemoteException
     */
    public ColumnInfo lookupColumnInfo    (String colName) throws RemoteException {

		try {

			String 					 beanName = Environment.getInstance( m_subSystem ).getCnfgFileMgr().getPropertyValue( "editorMetaData.EJBEEditorMetaDataBeanName", "" );
            EJBEEditorMetaDataHome   home 	  = (EJBEEditorMetaDataHome)EJBEnvironment.lookupEJBInterface( m_subSystem, beanName, EJBEEditorMetaDataHome.class );
			EJBEEditorMetaDataRemote remote   = home.findByPrimaryKey( new EJBEEditorMetaDataBeanPK( colName, "COLUMN_INFO", "DEFAULT" ) );

			return remote.lookupColumnInfo();
		}
		catch (Exception e) {
			Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( e );
			throw new EJBException( e );
		}
    }

    /**
     * This method will update the Entity Bean with the relavant information
     * @roseuid 3D50040601E2
     * @J2EE_METHOD  --  update
     * @param editorMetaData
     * @return EditorMetaData
     * @throws RemoteException
     */
    public EditorMetaData update    (EditorMetaData editorMetaData) throws RemoteException {
         return update(editorMetaData, "DEFAULT" );
    }

    /**
     * Gets all columns for an Editor irrespective of customization
     * @roseuid 3D51A0510390
     * @J2EE_METHOD  --  lookupAllColumns
     * @param name
     * @param type
     * @return Vector
     * @throws RemoteException
     */
    public java.util.Vector lookupAllColumns    (String name, String type) throws RemoteException {

		try {

			String 					 beanName 		= Environment.getInstance( m_subSystem ).getCnfgFileMgr().getPropertyValue( "editorMetaData.EJBEEditorMetaDataBeanName", "" );
            EJBEEditorMetaDataHome   home 	  		= (EJBEEditorMetaDataHome)EJBEnvironment.lookupEJBInterface( m_subSystem, beanName, EJBEEditorMetaDataHome.class );
            EJBEEditorMetaDataRemote remote   		= home.findByPrimaryKey( new EJBEEditorMetaDataBeanPK( name, type, "DEFAULT" ) );
            EditorMetaData 			 editorMetaData = remote.lookup();
            Vector					 columns		= editorMetaData != null ? editorMetaData.getColumnsMetaData() : null;

            return columns;
        }
        catch (Exception e) {

            Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( e );
            throw new EJBException( e );
        }
    }

    /**
     * Added to resolve JRUN Bug with userPrincipals
     * @roseuid 3D5D65E8035C
     * @J2EE_METHOD  --  lookup
     * @param name
     * @param type
     * @param userId
     * @return EditorMetaData
     * @throws RemoteException
     */
    public EditorMetaData lookup    (String name, String type, String userId) throws RemoteException {

		try {
			EJBEEditorMetaDataBeanPK pk = null;

            if (isEditorCustomized( name, userId ) ) {

            	pk = new EJBEEditorMetaDataBeanPK( name, type, userId );

			}
            else {

            	pk = new EJBEEditorMetaDataBeanPK( name, type, "DEFAULT" );
			}

			String 					 beanName = Environment.getInstance( m_subSystem ).getCnfgFileMgr().getPropertyValue( "editorMetaData.EJBEEditorMetaDataBeanName", "" );
            EJBEEditorMetaDataHome   home 	  = (EJBEEditorMetaDataHome)EJBEnvironment.lookupEJBInterface( m_subSystem, beanName, EJBEEditorMetaDataHome.class );
            EJBEEditorMetaDataRemote remote   = home.findByPrimaryKey( pk );

            return remote.lookup();
        }
        catch (Exception e) {
            Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( e );


            throw new EJBException( e );
        }
    }


	public EditorMetaData lookup(String name, String type, String userId, boolean configRole) throws RemoteException {
		//Not Implemented.
		return lookup(name,type,userId);
	}

    /**
     * @roseuid 3DFA3AC4035C
     * @J2EE_METHOD  --  update
     * @param editorMetaData
     * @param userId
     * @return EditorMetaData
     * @throws RemoteException
     */
    public EditorMetaData update    (EditorMetaData editorMetaData, String userId) throws RemoteException {
 		try {

			EditorMetaData			rv		 = null;
			String 					beanName = Environment.getInstance( m_subSystem ).getCnfgFileMgr().getPropertyValue( "editorMetaData.EJBEEditorMetaDataBeanName", "" );
            EJBEEditorMetaDataHome  home 	 = (EJBEEditorMetaDataHome)EJBEnvironment.lookupEJBInterface( m_subSystem, beanName, EJBEEditorMetaDataHome.class );

			try {

				EJBEEditorMetaDataRemote remote = home.findByPrimaryKey( new EJBEEditorMetaDataBeanPK( editorMetaData.getName(), editorMetaData.getType(), userId ) );
				rv = remote.update( editorMetaData );
			}
			catch (ObjectNotFoundException oe) {

				throw new EJBException( "The Editor :" + editorMetaData.getName() + " is not found in the database. The insert method has not been implemented as yet." );
				// The EditorMetaData was not found in the database and so try inserting the new EditorMetaData
				//rv = home.create( editorMetaData );
			}

			return rv;
		}
		catch (Exception e) {

			Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( e );
			throw new EJBException( e );
		}
    }

	private boolean isEditorCustomized(String name,String userId) {

		boolean customized = false;

		if (userId == null || userId.trim().length() == 0 || userId.trim().equalsIgnoreCase( "DEFAULT" ) )
			return customized;

        Connection	conn = null;
		PreparedStatement 	stmt = null;
		ResultSet 	rs   = null;

        try {

			conn 	= getConnection();
			String sql = "SELECT 1 FROM USER_EDITOR_COLUMNS WHERE EDITOR_NAME= ? AND USER_ID in( ? ,'ALL') ";
			
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setString(2, userId);
			
			rs = stmt.executeQuery();
			customized = rs.next();
        }
        catch (SQLException se) {

            Environment.getInstance( getSubSystem() ).getLogFileMgr( m_module ).logError( se );
            throw new EJBException( se );

        }
        finally {

            try {
                // MS Access throws error if we try to close a closed result set
                if (rs   != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) releaseConnection( conn );
            }
            catch (SQLException se) {

                Environment.getInstance( getSubSystem() ).getLogFileMgr( m_module ).logError( se );
                throw new EJBException( se );

            }
        }
        return customized;
	}

    public List<Pair> lookupNameLabelPairs(String type) throws RemoteException {
    	throw new EJBException("Not Implemented");    	
    }
	
	
	
    private Connection getConnection() throws SQLException  {

        return Environment.getInstance( getSubSystem() ).getDbPoolMgr().getConnection();

    }

    private void releaseConnection(Connection conn) throws SQLException {

        Environment.getInstance( getSubSystem() ).getDbPoolMgr().releaseConnection( conn );

    }


}
