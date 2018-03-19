package com.addval.wqutils.server;

import com.addval.wqutils.metadata.WQMetaData;
import com.addval.wqutils.WQEMetaDataBeanPK;
import com.addval.wqutils.utils.WQConstants;
import com.addval.environment.EJBEnvironment;
import com.addval.environment.Environment;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.AVConstants;
import com.addval.ejbutils.server.EJBSEditorMetaDataHome;
import com.addval.ejbutils.server.EJBSEditorMetaDataRemote;

import javax.naming.NamingException;
import java.rmi.RemoteException;
import java.sql.*;
import javax.ejb.*;

import org.apache.commons.lang.StringUtils;

public class WQEMetaDataBean implements EntityBean
{
    /**
     * Attributes declaration
    */
    private final static String m_module = "com.addval.wqutils.server.WQEMetaDataBean";
    private String m_subSystem = null;
    private String m_serverType = null;
    private WQMetaData  m_metaData = null;
    private EntityContext  m_context = null;

    /**
     * @roseuid 3FDE5828026B
     * @J2EE_METHOD  --  ejbFindByPrimaryKey
     * Invoked by the container on the instance when the container selects the instance to
     * execute a matching client-invoked find() method. It executes in the transaction
     * context determined by the transaction attribute of the matching find() method.
     * @param pk
     * @return com.addval.wqutils.server.WQEMetaDataBeanPK
     */
    public WQEMetaDataBeanPK ejbFindByPrimaryKey    (WQEMetaDataBeanPK pk) throws FinderException
    {
		Connection               conn = null;
		PreparedStatement        stmt = null;
		ResultSet                rs   = null;
		try {
			// If the queue name is given then check the Queues table for the
			// existance of the Queue
			if (pk != null && pk.getName() != null) {
				conn = getConnection();

                if(!StringUtils.isNumeric(pk.getName())){
                    String sql = "SELECT 1 FROM QUEUES WHERE QUEUE_NAME = ?";
                    stmt = conn.prepareStatement( sql );
                    stmt.setString( 1, pk.getName() );
                }

                rs = stmt.executeQuery();

                if (!rs.next())
					throw new ObjectNotFoundException( "Queue with name : " + pk.getName() + " not found in the database" );
			}
			return pk;
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

    /**
     * @roseuid 3FDE5828036F
     * @J2EE_METHOD  --  setEntityContext
     * Set the associated entity context. The container invokes this method on an instance
     * after the instance has been created. This method is called in an unspecified transaction
     * context.
     */
    public void setEntityContext    (EntityContext context) throws EJBException, RemoteException
    {
 		m_context = context;
        try {

            m_subSystem  = (String)EJBEnvironment.lookupLocalContext( "SUBSYSTEM"   );
            m_serverType = Environment.getInstance( m_subSystem ).getDbPoolMgr().getServerType();
        }
        catch(NamingException nex){
			nex.printStackTrace();
		}
    }

    /**
     * @roseuid 3FDE58290136
     * @J2EE_METHOD  --  unsetEntityContext
     * Unset the associated entity context. The container calls this method before removing
     * the instance. This is the last method that the container invokes on the instance.
     * The Java garbage collector will  invoke the finalize() method on the instance. It
     * is called in an unspecified transaction context.
     * @throws javax.ejb.EJBException
     * @throws java.rmi.RemoteException
     */
    public void unsetEntityContext    () throws EJBException, RemoteException
    {
 		m_context = null;
    }

    /**
     * @roseuid 3FDE5829028A
     * @J2EE_METHOD  --  ejbRemove
     * A container invokes this method before it removes the EJB object that is currently
     * associated with the instance. It is invoked when a client invokes a remove operation
     * on the enterprise Bean's home or remote interface. It transitions the instance from
     * the ready state to the pool of available instances. It is called in the transaction
     * context of the remove operation.
     */
    public void ejbRemove    () throws RemoveException, EJBException, RemoteException
    {

    }

    /**
     * @roseuid 3FDE582A0065
     * @J2EE_METHOD  --  ejbActivate
     * A container invokes this method when the instance is taken out of the pool of available
     * instances to become associated with a specific EJB object. This method transitions
     * the instance to the ready state. This method executes in an unspecified transaction
     * context.
     * @throws javax.ejb.EJBException
     * @throws java.rmi.RemoteException
     */
    public void ejbActivate    () throws EJBException, RemoteException
    {

    }

    /**
     * @roseuid 3FDE582A017D
     * @J2EE_METHOD  --  ejbPassivate
     * A container invokes this method on an instance before the instance becomes disassociated
     * with a specific EJB object. After this method completes, the container will place
     * the instance into the pool of available instances. This method executes in an unspecified
     * transaction context.
     * @throws javax.ejb.EJBException
     * @throws java.rmi.RemoteException
     */
    public void ejbPassivate    () throws EJBException, RemoteException
    {

    }

    /**
     * @roseuid 3FDE582A0278
     * @J2EE_METHOD  --  ejbLoad
     * A container invokes this method to instruct the instance to synchronize its state
     * by loading it from the underlying database. This method always executes in the transaction
     * context determined by the value of the transaction attribute in the deployment descriptor.
     * @throws javax.ejb.EJBException
     * @throws java.rmi.RemoteException
     */
    public void ejbLoad    () throws EJBException, RemoteException
    {
		Connection conn = null;

		try {

			WQEMetaDataBeanPK pk = (WQEMetaDataBeanPK) m_context.getPrimaryKey();
			String name = pk.getName();
			String sql  = null;
			conn        = getConnection();
			buildQueueMetaData( name, conn );

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

			if (conn != null) releaseConnection( conn );
		}
    }

    /**
     * @roseuid 3FDE582A0372
     * @J2EE_METHOD  --  ejbStore
     * A container invokes this method to instruct the instance to synchronize its state
     * by storing it to the underlying database. This method always executes in the transaction
     * context determined by the value of the transaction attribute in the deployment descriptor.
     */
    public void ejbStore    () throws EJBException, RemoteException
    {

    }

    /**
     * @roseuid 3FDE582B0084
     * @J2EE_METHOD  --  lookup
     */
    public WQMetaData lookup    () throws RemoteException
    {
 		return m_metaData;
    }

    /**
     * @roseuid 3FDE582B0161
     * @J2EE_METHOD  --  getConnection
     */
    private Connection getConnection    ()
    {
 		return Environment.getInstance( m_subSystem ).getDbPoolMgr().getConnection();
    }

    /**
     * @roseuid 3FDE582B01D9
     * @J2EE_METHOD  --  releaseConnection
     */
    private void releaseConnection    (Connection conn)
    {
 		Environment.getInstance( m_subSystem ).getDbPoolMgr().releaseConnection( conn );
    }

	private void buildQueueMetaData(String name, Connection conn) throws SQLException, NamingException, RemoteException, CreateException, ClassNotFoundException {

		ResultSet	rs		   = null;
		Statement	stmt	   = null;
		String		sql		   = null;

		try {

			stmt = conn.createStatement();
			sql  = buildQueueSql( name );
			rs   = stmt.executeQuery( sql );

			if (rs.next()) {

				m_metaData = new WQMetaData(rs.getString( WQConstants.QueuesTbl.QUEUE_NAME ),
											rs.getString( WQConstants.QueuesTbl.QUEUE_DESC ),
                                            rs.getString( WQConstants.QueuesTbl.DEFAULT_PROMPT_DEFINITION ),
                                            buildEditorMetaData( rs.getString( WQConstants.QueuesTbl.EDITOR_NAME ) ),
											rs.getString( WQConstants.QueuesTbl.QUEUE_GROUP_NAME ),
											rs.getString( WQConstants.QueuesTbl.QUEUE_CONDITION ),
											rs.getLong ( WQConstants.QueuesTbl.QUEUE_STATUS_TIMEOUT ),
                                            rs.getLong( WQConstants.QueuesTbl.QUEUE_AUTOREFRESH_TIME)
											);

				try {
					long queueNewMessageTime = rs.getLong(WQConstants.QueuesTbl.QUEUE_NEWMESSAGE_TIME);
					m_metaData.setQueueNewMessageTime(queueNewMessageTime);
				}
				finally {
					// do nothing during for migration
				}
			}
		}
		finally {

			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
		}
	}

	private String buildQueueSql(String queueName) {
        
            return "select * from "+ WQConstants.QueuesTbl.QUEUES_TBL + " where "+ WQConstants.QueuesTbl.QUEUE_NAME + "='" + queueName + "' order by " + WQConstants.QueuesTbl.QUEUE_GROUP_NAME;     
	}

	private EditorMetaData buildEditorMetaData(String editorName) throws NamingException, RemoteException, CreateException, ClassNotFoundException {

		String 					 beanName = Environment.getInstance( m_subSystem ).getCnfgFileMgr().getPropertyValue( "editorMetaData.EJBSEditorMetaDataBeanName", "" );
		EJBSEditorMetaDataHome 	 home 	  = (EJBSEditorMetaDataHome)EJBEnvironment.lookupEJBInterface( m_subSystem, beanName, EJBSEditorMetaDataHome.class );
		EJBSEditorMetaDataRemote remote   = home.create();

		EditorMetaData editorMetaData = remote.lookup( editorName, AVConstants._DEFAULT );

		return (EditorMetaData)editorMetaData.clone();
	}

}