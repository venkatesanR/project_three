package com.addval.wqutils.server;

import javax.ejb.*;
import com.addval.environment.EJBEnvironment;
import com.addval.environment.Environment;
import com.addval.wqutils.metadata.WQMetaData;
import com.addval.wqutils.WQEMetaDataHome;
import com.addval.wqutils.WQEMetaDataRemote;
import com.addval.wqutils.WQEMetaDataBeanPK;
import com.addval.wqutils.utils.WQConstants;
import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.utils.StrUtl;

import java.rmi.RemoteException;
import java.util.Vector;
import java.util.Iterator;
import java.util.Hashtable;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.apache.commons.lang.StringUtils;

public class WQSMetaDataBean implements SessionBean {
    /**
     * Attributes declaration
    */
    private String m_subSystem = null;
    private String m_serverType = null;
    private final static String m_module = "com.addval.wqutils.server.WQSMetaDataBean";
    private SessionContext  m_context;

    /**
     * @roseuid 3FDE485D02CB
     * @J2EE_METHOD  --  ejbCreate
     * Called by the container to create a session bean instance. Its parameters typically
     * contain the information the client uses to customize the bean instance for its use.
     * It requires a matching pair in the bean class and its home interface.
     */
    public void ejbCreate    () throws RemoteException
    {

    }

    /**
     * @roseuid 3FDE485D0357
     * @J2EE_METHOD  --  setSessionContext
     * Set the associated session context. The container calls this method after the instance
     * creation. The enterprise Bean instance should store the reference to the context
     * object in an instance variable. This method is called with no transaction context.
     */
    public void setSessionContext    (SessionContext context) throws EJBException, RemoteException
    {
		m_context = context;

		try {

            m_subSystem = (String)EJBEnvironment.lookupLocalContext( "SUBSYSTEM" );
            m_serverType = Environment.getInstance( m_subSystem ).getDbPoolMgr().getServerType();

        }
        catch(Exception e){
			throw new EJBException( e );
		}
    }

    /**
     * @roseuid 3FDE485E00C4
     * @J2EE_METHOD  --  unsetSessionContext
     */
    public void unsetSessionContext    (SessionContext context) throws RemoteException
    {
   	 	m_context = null;
    }

    /**
     * @roseuid 3FDE485E01A0
     * @J2EE_METHOD  --  ejbRemove
     * A container invokes this method before it ends the life of the session object. This
     * happens as a result of a client's invoking a remove operation, or when a container
     * decides to terminate the session object after a timeout. This method is called with
     * no transaction context.
     */
    public void ejbRemove    () throws EJBException, RemoteException
    {

    }

    /**
     * @roseuid 3FDE485E02AF
     * @J2EE_METHOD  --  ejbActivate
     * The activate method is called when the instance is activated from its 'passive' state.
     * The instance should acquire any resource that it has released earlier in the ejbPassivate()
     * method. This method is called with no transaction context.
     */
    public void ejbActivate    () throws EJBException, RemoteException
    {

    }

    /**
     * @roseuid 3FDE485E03B3
     * @J2EE_METHOD  --  ejbPassivate
     * The passivate method is called before the instance enters the 'passive' state. The
     * instance should release any resources that it can re-acquire later in the ejbActivate()
     * method. After the passivate method completes, the instance must be in a state that
     * allows the container to use the Java Serialization protocol to externalize and store
     * away the instance's state. This method is called with no transaction context.
     */
    public void ejbPassivate    () throws EJBException, RemoteException
    {

    }

    /**
     * @roseuid 3FDE485F00CF
     * @J2EE_METHOD  --  getConnection
     */
    private Connection getConnection    ()
    {
   		return Environment.getInstance( m_subSystem ).getDbPoolMgr().getConnection();
    }

    /**
     * @roseuid 3FDE485F012A
     * @J2EE_METHOD  --  releaseConnection
     */
    private void releaseConnection    (Connection conn)
    {
   		Environment.getInstance( m_subSystem ).getDbPoolMgr().releaseConnection( conn );
    }

    /**
     * @roseuid 3FDE485F01B6
     * @J2EE_METHOD  --  lookup
     */
    public WQMetaData lookup(String queueName) throws EJBXRuntime
    {
		try {
			if(queueName == null || queueName.trim().length() < 1  )
				throw new EJBXRuntime( "QueueName should not be empty." );

			String 			  beanName   = Environment.getInstance( m_subSystem ).getCnfgFileMgr().getPropertyValue( "queueMetaData.WQEMetaDataBeanName", "" );
			WQEMetaDataHome   home 	     = (WQEMetaDataHome)EJBEnvironment.lookupEJBInterface( m_subSystem, beanName, WQEMetaDataHome.class );
			WQEMetaDataRemote remote     = home.findByPrimaryKey( new WQEMetaDataBeanPK( queueName ) );
			WQMetaData 		  wqMetaData = remote.lookup();
			return wqMetaData;

		}
        catch(EJBXRuntime ex){
            Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( ex );
            throw ex;
        }
		catch (Exception e) {

			Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( e );
			throw new EJBException( e );
		}
    }

    /**
     * @roseuid 3FDE485F02CE
     * @J2EE_METHOD  --  lookup
     */
    public Vector lookup(Vector queueNames) throws EJBXRuntime
    {
		try {
			if(queueNames == null || queueNames.size() == 0  )
                return null;
				//throw new EJBXRuntime("No Queue(s) Exists");

            Vector queueMetadatas = new Vector();
			String queueName = null;
			for ( Iterator iterator = queueNames.iterator(); iterator.hasNext(); ) {

                queueName = (String) iterator.next();
                queueMetadatas.add( lookup( queueName ) );

			}
			return queueMetadatas;
		}
        catch(EJBXRuntime ex){
            Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( ex );
            throw ex;
        }
		catch (Exception e) {

			Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( e );
			throw new EJBException( e );

		}
    }

	public Vector queueStatus(Hashtable wqFilters, Vector wqMetaDatas) throws EJBXRuntime
	{
		try {

			if(wqMetaDatas == null || wqMetaDatas.size() == 0  )
				throw new EJBXRuntime( "WQMetaData List should not be empty." );

			WQMetaData wqMetaData = null;
			for(Iterator iterator=wqMetaDatas.iterator();iterator.hasNext(); ){

                wqMetaData = (WQMetaData)iterator.next();
				populateQueueStatus(wqFilters,wqMetaData);

			}

			return wqMetaDatas;
		}
        catch(EJBXRuntime ex){
            Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( ex );
            throw ex;
        }
		catch (Exception e) {

			Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( e );
			throw new EJBException( e );
		}

	}

    public String getVerifiedQueueName(String queue) throws RemoteException,EJBXRuntime {
        String queueName = null;

        Connection               conn = null;
        PreparedStatement        stmt = null;
        ResultSet                rs   = null;
        try {
            // If the queue name is given then check the Queues table for the existance of the Queue
            if(!StrUtl.isEmptyTrimmed( queue )) {
                conn = getConnection();
                if(!StringUtils.isNumeric( queue )){

                	String sql = "SELECT QUEUE_NAME FROM QUEUES WHERE QUEUE_NAME = ?";
                    stmt = conn.prepareStatement( sql );
                    stmt.setString( 1, queue );

                }

                rs = stmt.executeQuery();
                if( rs.next() ) {
                    queueName = rs.getString( "QUEUE_NAME" );
                }
            }
            return queueName;
        }
        catch (SQLException se) {
            Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( se );
            throw new EJBException( se );
        }
        catch (Exception e) {
            Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( e );
            throw new EJBException( e );
        }
        finally {
            try {
                if (rs != null ) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) releaseConnection( conn );
            }
            catch (SQLException se) {
                Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( se );
                throw new EJBException( se );
            }
        }
    }


	public Vector queueMonitor(Hashtable wqFilters, Vector wqMetaDatas) throws EJBXRuntime
	{
		try {

			if(wqMetaDatas == null || wqMetaDatas.size() == 0  )
				throw new EJBXRuntime( "WQMetaData List should not be empty." );

			WQMetaData wqMetaData = null;
			for(Iterator iterator=wqMetaDatas.iterator();iterator.hasNext(); ){

                wqMetaData = (WQMetaData)iterator.next();

                if (wqMetaData.getQueueNewMessageTime() >= 0)
					populateNewMessageCount(wqFilters,wqMetaData);
			}

			return wqMetaDatas;
		}
        catch(EJBXRuntime ex){
            Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( ex );
            throw ex;
        }
		catch (Exception e) {

			Environment.getInstance( m_subSystem ).getLogFileMgr( m_module ).logError( e );
			throw new EJBException( e );
		}

	}



	private void populateQueueStatus(Hashtable wqFilters,WQMetaData wqMetaData) throws SQLException {

		String sql = getQueueStatusSQL(wqFilters, wqMetaData );
		Connection 	conn = null;
		Statement 	stmt = null;
		ResultSet 	rs   = null;

		String messageStatus = null;

		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs   = stmt.executeQuery( sql );
			wqMetaData.setMessageInProcessCount( 0 );
			wqMetaData.setMessageUnProcessedCount( 0 );

			while(rs.next()) {
				messageStatus = rs.getString( WQConstants._MESSAGE_STATUS );

				if (messageStatus == null)
					messageStatus = WQConstants._STATUS_UNPROCESSED;

				if (messageStatus.equals( WQConstants._STATUS_INPROCESS )) {

					wqMetaData.setMessageInProcessCount( rs.getInt( "COUNT" ) );

				}
				else if (messageStatus.equals( WQConstants._STATUS_UNPROCESSED )) {

					wqMetaData.setMessageUnProcessedCount( rs.getInt( "COUNT" ) );

				}
			}
		}
		finally {

			if (rs   != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) releaseConnection( conn );
		}
	}



	private void populateNewMessageCount(Hashtable wqFilters,WQMetaData wqMetaData) throws SQLException {

		String sql = getNewMessageCountSQL(wqFilters, wqMetaData );
		Connection 	conn = null;
		Statement 	stmt = null;
		ResultSet 	rs   = null;

		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs   = stmt.executeQuery( sql );
			wqMetaData.setNewMessageUnProcessedCount( 0 );

			while(rs.next()) {
				wqMetaData.setNewMessageUnProcessedCount( rs.getInt( "COUNT" ) );
			}
		}
		finally {

			if (rs   != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) releaseConnection( conn );
		}
	}



	private String getQueueStatusSQL(Hashtable wqFilters, WQMetaData wqMetaData) {

		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append( "SELECT COUNT(*) AS COUNT," );
		sqlBuf.append( WQConstants._MESSAGE_STATUS );
		sqlBuf.append( " FROM " + wqMetaData.getEditorMetaData().getSource() + " " );
		String whereClause =  wqMetaData.getWhereClause(wqFilters);
		if(whereClause != null && whereClause.length() > 0) {
			sqlBuf.append( " WHERE " ).append(whereClause);
		}
		sqlBuf.append( " GROUP BY " + WQConstants._MESSAGE_STATUS );

		return sqlBuf.toString();
	}

	private String getNewMessageCountSQL(Hashtable wqFilters, WQMetaData wqMetaData) {

		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append( "SELECT COUNT(*) AS COUNT" );
		sqlBuf.append( " FROM " + wqMetaData.getEditorMetaData().getSource() + " " );
		String whereClause =  wqMetaData.getNewQueueMessageCountWhereClause(wqFilters);
		if(whereClause != null && whereClause.length() > 0) {
			sqlBuf.append( " WHERE " ).append(whereClause);
		}

		return sqlBuf.toString();
	}

}
