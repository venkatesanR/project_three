package com.addval.wqutils.server;

import com.addval.wqutils.utils.WQConstants;
import com.addval.wqutils.metadata.WQMetaData;
import com.addval.wqutils.WQSMetaDataHome;
import com.addval.wqutils.WQSMetaDataRemote;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.server.EJBSTableManagerHome;
import com.addval.ejbutils.server.EJBSTableManagerRemote;
import com.addval.ejbutils.server.EJBSEditorMetaDataHome;
import com.addval.ejbutils.server.EJBSEditorMetaDataRemote;
import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.environment.Environment;
import com.addval.environment.EJBEnvironment;
import com.addval.utils.WorkQueueUtils;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.ColumnMetaData;
import com.addval.dbutils.DAOSQLStatement;
import com.addval.dbutils.DAOUtils;
import com.addval.dbutils.DAOTxtUtils;
import javax.naming.NamingException;
import java.rmi.RemoteException;
import java.util.*;
import java.sql.*;
import javax.ejb.*;

import javax.jms.ObjectMessage;
import org.apache.commons.messenger.Messenger;
import org.apache.commons.messenger.MessengerManager;
import javax.jms.Destination;


public class WQServerBean implements SessionBean {

    private final static String m_module = "com.addval.wqutils.server.WQServerBean";
    private static final String _USER_WORKQUEUE_EDITORNAMES = "getUserWorkQueueEditorNames";
    private static final String _UPDATE_MESSAGESTATUS = "updateMessageStatus";

    private String m_subSystem = null;
    private String m_notifyTopicName = null;
    private String m_notifyMessengerName = null;
    private String m_daoCacheName = null;
    private SessionContext  m_context = null;

    public WQServerBean() {

    }

	public void sendMessage(String queueName,HashMap params) throws RemoteException,EJBXRuntime {

		try {

			String 	beanName 	= Environment.getInstance( m_subSystem ).getCnfgFileMgr().getPropertyValue( "queueMetaData.WQSMetaDataBeanName", "AVWQMetaDataServer" );
			WQSMetaDataHome   wqMetaDataHome   = ( WQSMetaDataHome ) EJBEnvironment.lookupEJBInterface( m_subSystem , beanName , WQSMetaDataHome.class );
			WQSMetaDataRemote wqMetaDataRemote = wqMetaDataHome.create( );
			WQMetaData wqMetaData = wqMetaDataRemote.lookup( queueName );
			EditorMetaData metadata = wqMetaData.getEditorMetaData();
			EJBResultSet ejbRS = WorkQueueUtils.getInstance().getInsertEJBResultSet( metadata, params );
			sendMessage(ejbRS);
			notifyTopic(wqMetaData, ejbRS);
		}
        catch(EJBXRuntime ex){
            m_context.setRollbackOnly();
            Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( ex );
            throw ex;
        }
		catch (Exception e) {
			Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( e );
			throw new EJBException( e );

		}
	}
	public void sendMessage(EJBResultSet ejbRS) throws RemoteException,EJBXRuntime {
		try {
			EJBSTableManagerRemote 	remote = getTableManagerRemote();
			ejbRS = remote.updateTransaction ( ejbRS );
			if (ejbRS == null) {
				throw new EJBXRuntime( "Insert failed" );
			}
		}
	     catch(EJBXRuntime ex){
            m_context.setRollbackOnly();
            Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( ex );
            throw ex;
        }
		catch (Exception e) {
			Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( e );
			throw new EJBException( e );

		}
	}


    public void setSessionContext (SessionContext context) throws EJBException, RemoteException {

    	m_context = context;

        try {

            m_subSystem  = (String)EJBEnvironment.lookupLocalContext( "SUBSYSTEM" );

        }
        catch(NamingException nex) {
            nex.printStackTrace();
            throw new EJBException( nex );
        }

        try {

            m_notifyTopicName  = (String)EJBEnvironment.lookupLocalContext( "NOTIFYTOPICNAME" );

        }
        catch(NamingException nex) {
        	m_notifyTopicName = null;
        	Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logWarning( "NOTIFYTOPICNAME is not specified in ejb-jar.xml" );
        	Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logWarning( nex.getMessage() );
        }

        try {

            m_notifyMessengerName  = (String)EJBEnvironment.lookupLocalContext( "NOTIFYMESSENGERNAME" );

        }
        catch(NamingException nex) {
        	m_notifyMessengerName = null;
        	Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logWarning( "NOTIFYMESSENGERNAME is not specified in ejb-jar.xml" );
        	Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logWarning( nex.getMessage() );
        }

        try {

            m_daoCacheName = (String)EJBEnvironment.lookupLocalContext( "DAO_CACHE_NAME" );

        }
        catch(NamingException nex) {
        	m_daoCacheName = null;
        	Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logWarning( "DAO_CACHE_NAME is not specified in ejb-jar.xml" );
        	Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logWarning( nex.getMessage() );
        }


    }

    public void ejbCreate    () throws RemoteException {

    }

    public void ejbRemove    () throws EJBException, RemoteException {

    }

    public void ejbActivate    () throws EJBException, RemoteException {

    }

    public void ejbPassivate    () throws EJBException, RemoteException {

    }

    public void unsetSessionContext    () throws RemoteException {

		m_context = null;
    }

    private EJBSTableManagerRemote getTableManagerRemote()  throws NamingException, RemoteException, Exception {
		String 			  	 	beanName 	= Environment.getInstance( m_subSystem ).getCnfgFileMgr().getPropertyValue( "editorMetaData.EJBSTableManagerBeanName", "" );
		EJBSTableManagerHome 	home 	 	= (EJBSTableManagerHome)EJBEnvironment.lookupEJBInterface( m_subSystem, beanName, EJBSTableManagerHome.class );
		return home.create();
	}
	private EJBSEditorMetaDataRemote getEditorMetadataRemote()  throws NamingException, RemoteException, Exception {
		String 			  	 	beanName 	= Environment.getInstance( m_subSystem ).getCnfgFileMgr().getPropertyValue( "editorMetaData.EJBSEditorMetaDataBeanName", "" );
		EJBSEditorMetaDataHome 	home 	 	= (EJBSEditorMetaDataHome)EJBEnvironment.lookupEJBInterface( m_subSystem, beanName, EJBSEditorMetaDataHome.class );
		return home.create();
	}

	public EJBResultSet deleteMessage(EJBResultSet ejbRS) throws RemoteException,EJBXRuntime {
		try {

			EJBSTableManagerRemote 	remote = getTableManagerRemote();
			ejbRS = remote.updateTransaction ( ejbRS );
			if (ejbRS == null) {
				throw new EJBXRuntime( "Delete failed" );
			}
            return ejbRS;
		}
        catch(EJBXRuntime ex){
            m_context.setRollbackOnly();
            Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( ex );
            throw ex;
        }
		catch (Exception e) {
			Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( e );
			throw new EJBException( e );
		}
	}

	public EJBResultSet deleteMessage(String queueName,HashMap params) throws RemoteException,EJBXRuntime {
		try {
			String 	beanName 	= Environment.getInstance( m_subSystem ).getCnfgFileMgr().getPropertyValue( "queueMetaData.WQSMetaDataBeanName", "AVWQMetaDataServer" );
			WQSMetaDataHome   wqMetaDataHome   = ( WQSMetaDataHome ) EJBEnvironment.lookupEJBInterface( m_subSystem , beanName , WQSMetaDataHome.class );
			WQSMetaDataRemote wqMetaDataRemote = wqMetaDataHome.create( );
			WQMetaData wqMetaData = wqMetaDataRemote.lookup( queueName );
			EditorMetaData metadata = wqMetaData.getEditorMetaData();
			EJBResultSet ejbRS = WorkQueueUtils.getInstance().getDeleteEJBResultSet( metadata, params );
			return deleteMessage(ejbRS);
		}
        catch(EJBXRuntime ex){
            m_context.setRollbackOnly();
            Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( ex );
            throw ex;
        }
		catch (Exception e) {
			Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( e );
			throw new EJBException( e );
		}

	}
	public EJBResultSet processNextMessage(EJBCriteria wqCriteria) throws EJBXRuntime {
		try {
			EJBSTableManagerRemote 	remote 		= getTableManagerRemote();
			EJBResultSet 			ejbRS 		= remote.lookup( wqCriteria );
			EJBResultSetMetaData 	rsMetaData	= (EJBResultSetMetaData)ejbRS.getMetaData();

			if(ejbRS.next()) {

				Vector keyColumns = rsMetaData.getEditorMetaData().getKeyColumns();

				Hashtable params = new Hashtable();

				if(keyColumns != null) {

					ColumnMetaData 	columnMetaData  = null;

					for(Iterator iterator = keyColumns.iterator(); iterator.hasNext();) {

						columnMetaData = (ColumnMetaData)iterator.next();
						params.put( columnMetaData.getName(), ejbRS.getString( columnMetaData.getName() ) );

					}

				}
				wqCriteria.where (params,null);
				return processMessage(wqCriteria);
			}
			return null;
		}
        catch(EJBXRuntime ex){
            m_context.setRollbackOnly();
            Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( ex );
            throw ex;
        }
		catch (Exception e) {
			Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( e );
			throw new EJBException( e );

		}
	}

    public void unProcessMessage(String userName) throws EJBXRuntime {
    	if (m_daoCacheName != null) {
            //Update Message Status to UNPROCESSED.
            Hashtable sqls = (Hashtable)Environment.getInstance( m_subSystem ).getCacheMgr().get( m_daoCacheName );
            updateUnProcessMessage(userName,sqls);
		}
    }

    public void unProcessMessage(String userName,Hashtable sqls) throws EJBXRuntime {
        updateUnProcessMessage(userName,sqls);
    }

    private void updateUnProcessMessage(String userName,Hashtable sqls) throws EJBXRuntime {
        Connection 		  conn  		= null;
		PreparedStatement selectpstmt 		= null;
        Statement         updatestmt 		= null;
		ResultSet		  rs    		= null;

		if (m_daoCacheName != null) {

			try {
				//Update Message Status to UNPROCESSED.
				String  editorSourceName    = null;
				HashMap map = new HashMap();
				map.put("userName",userName);

				DAOSQLStatement selectStatement 	= getDAOSQLStatement(sqls,_USER_WORKQUEUE_EDITORNAMES );
				DAOUtils 	 	selectUtils		    = new DAOUtils		( selectStatement, getServerType() );
				DAOSQLStatement updateStatement 	= getDAOSQLStatement(sqls, _UPDATE_MESSAGESTATUS );
				DAOTxtUtils 	updateUtils		    = new DAOTxtUtils		( updateStatement, getServerType() );

				conn  = getConnection();
				updatestmt = conn.createStatement();
				selectpstmt = conn.prepareStatement( selectStatement.getSQL( getServerType() ) );
				selectUtils.setProperties(selectpstmt,map);
				rs 	  = selectpstmt.executeQuery();

				while (rs.next()) {

					editorSourceName   = (String) selectUtils.getProperty( rs ,  "editorSourceName" );
					map.put("editorSourceName",editorSourceName);
					updateUtils.setProperties(map);
					updatestmt.addBatch( updateUtils.getText(map) );
				}
				int retValue[] = updatestmt.executeBatch();
			}
			catch (SQLException ex) {
				Environment.getInstance( m_subSystem ).getLogFileMgr( getClass().getName() ).logError( ex );
			}
			catch(Exception ex) {
				Environment.getInstance( m_subSystem ).getLogFileMgr( getClass().getName() ).logError( ex );
			}
			finally {
				try {
					if (rs    != null) rs.close();
					if (selectpstmt != null) selectpstmt.close();
					if (updatestmt != null) updatestmt.close();
					if (conn  != null) releaseConnection( conn );
				}
				catch( SQLException ex) {
					Environment.getInstance( m_subSystem ).getLogFileMgr( getClass().getName() ).logError( ex );
				}
			}
		}
    }



    public EJBResultSet processMessage(EJBCriteria wqCriteria) throws EJBXRuntime {
		Hashtable params = new Hashtable();
		params.put(WQConstants._MESSAGE_STATUS_COLUMN, WQConstants._STATUS_INPROCESS);
		return updateMessage( wqCriteria, params );
    }

	private EJBResultSet updateMessage (EJBCriteria wqCriteria, Hashtable params) throws EJBXRuntime {
		try {
		    if(params == null)
				throw new EJBXRuntime("Update params  should not be null");
            EJBSTableManagerRemote 	remote 		= getTableManagerRemote();
			EJBResultSet 			ejbRS 		= remote.lookup( wqCriteria );
			EJBResultSetMetaData 	rsMetaData	= (EJBResultSetMetaData)ejbRS.getMetaData();

			String columnName = null;
			String columnValue = null;

			for(Iterator iterator = params.keySet().iterator();iterator.hasNext();) {

				columnName = (String)iterator.next();
				if (!rsMetaData.getEditorMetaData().isColumnValid( columnName ) )
					throw new EJBXRuntime( "The Queue : " + rsMetaData.getEditorMetaData().getName() + " does not have the required " + columnName + " associated with the table : " + rsMetaData.getEditorMetaData().getSource() );
			}
            if(ejbRS == null || ejbRS.getRecords().size() == 0)
                throw new EJBXRuntime("Record is already processed by someone, Please process Next.");

			while ( ejbRS.next() ) {

				for(Iterator iterator = params.keySet().iterator();iterator.hasNext();) {

					columnName = (String)iterator.next();
					columnValue = (String) params.get(columnName);
					ejbRS.updateString( columnName, columnValue );

				}
			}
			ejbRS = remote.updateTransaction ( ejbRS );
			return ejbRS;
		}
        catch(EJBXRuntime ex){
            m_context.setRollbackOnly();
            Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( ex );
            throw ex;
        }
		catch (Exception ex) {

			Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( ex );
			throw new EJBException( ex.getMessage() );

		}
	}


	private void notifyTopic(WQMetaData wqMetaData, EJBResultSet ejbRS)
	{

		if ((m_notifyTopicName != null) && (m_notifyMessengerName != null))
		{
			try
			{
				MessengerManager mgr 	  = MessengerManager.getInstance();
				Messenger 	  messenger   = null;
				Vector        msg     	  = new Vector();
				Destination   destination = null;
				ObjectMessage message 	  = null;

				if (mgr != null)
				{
					messenger = mgr.get( m_notifyMessengerName );

					if (messenger != null)
					{
						msg.add(wqMetaData);
						msg.add(ejbRS);

						destination = messenger.getDestination( m_notifyTopicName );

						// Create the Object Message
						message = messenger.createObjectMessage( msg );
						messenger.send( destination, message );
					}

				}
			} catch (Exception e) {
				Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( e );
				throw new EJBException( e );
			}

		}
	}

    protected DAOSQLStatement getDAOSQLStatement(Hashtable sqls,String type) {
        return (DAOSQLStatement)sqls.get( Environment.getInstance( m_subSystem ).getCnfgFileMgr().getPropertyValue( type, type ) );
    }

    protected String getServerType() {
        return Environment.getInstance( m_subSystem ).getDbPoolMgr().getServerType();
    }

    protected Connection getConnection() {
        return Environment.getInstance( m_subSystem ).getDbPoolMgr().getConnection();
    }

    protected void releaseConnection(Connection conn) {
        Environment.getInstance( m_subSystem ).getDbPoolMgr().releaseConnection( conn );
    }

}