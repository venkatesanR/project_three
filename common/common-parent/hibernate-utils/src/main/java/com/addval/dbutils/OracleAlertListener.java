//Source file: d:\\projects\\common\\source\\com\\addval\\dbutils\\OracleAlertListener.java

//Source file: D:\\Projects\\Common\\source\\com\\addval\\dbutils\\OracleAlertListener.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.sql.*;
import java.util.HashMap;
import java.util.Date;
import java.io.Serializable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import com.addval.environment.Environment;

/**
 * Singleton class useful to attach DBMS_ALERT based alerts
 * maintains the details about the alerts registered and fires messageEvents to the
 * alert registered classes. rought equivalent of a MessageDrivenBean
 * Also has static method to do onetime listening to the given alert.
 * @author AddVal Technology Inc.
 */
public class OracleAlertListener implements Serializable
{
	private static String _projectName = null;
	private static final HashMap _alerts = new HashMap ();
	private static final String _module = "OracleAlertListener";
	private static OracleAlertListener _oracleAlertListener = null;
	private static Connection _conn = null;

	/**
	 * constructor is kept private as per Singleton pattern
	 *
	 * @param projectName
	 * @roseuid 3FF55790009C
	 */
	private OracleAlertListener(String projectName)
	{
		_projectName = projectName;
	}

	/**
	 * Waits for given Oracle alert for a specified amount of time (as specified
	 * in the timoutSec argument.). Returns "true" if the alert occurs within the
	 * the timeout period. Returns "false" if timedout.
	 * @param alertName Name of the alert to wait for.
	 * @param conn Connection to the Database
	 * @param timeoutSec long. Amount to time to wait before timing-out.
	 * @return "true" if the alert occurs within the the timeout period. Returns
	 * "false" if timed-out.
	 * @throws java.sql.SQLException If executeQuery statements fail.
	 * @roseuid 3B7B19C00321
	 */
	public static boolean waitForAlert(String alertName, Connection conn, long timeoutSec) throws SQLException
	{
		CallableStatement cstmt = null;
		try {
			final int ALERT_OCCURRED = 0;
			cstmt = conn.prepareCall( "begin DBMS_ALERT.REGISTER( ? ); end;" );
			cstmt.setString( 1, alertName );
			cstmt.executeQuery();
			cstmt = conn.prepareCall( "begin DBMS_ALERT.WAITONE( ?, ?, ?, ? ); end;" );
			cstmt.setString( 1, alertName );
			cstmt.registerOutParameter( 2, java.sql.Types.VARCHAR );
			cstmt.registerOutParameter( 3, java.sql.Types.INTEGER );
			cstmt.setLong( 4, timeoutSec );//timeout in sec
			cstmt.executeQuery();
			boolean ret = cstmt.getInt( 3 ) == ALERT_OCCURRED;
			cstmt = conn.prepareCall( "begin DBMS_ALERT.REMOVE( ? ); end;" );
			cstmt.setString( 1, alertName );
			cstmt.executeQuery();
			return ret;
		}
		finally {
			closeStatement( cstmt );
		}
	}

	/**
	 * static method to get the intitalised Singleton instance of OracleAlertListener
	 *
	 * @param projectName
	 * @return OracleAlertListener
	 * @roseuid 3FF5579000BB
	 */
	public static synchronized OracleAlertListener getInstance(String projectName)
	{
		if (projectName == null || projectName.trim().length() == 0)
		    throw new RuntimeException( "projectName(the Name of the .properties file) should not be null!" );
		addShutdownHook();
		if (_oracleAlertListener == null)
			_oracleAlertListener = new OracleAlertListener( projectName );
		return _oracleAlertListener;
	}

	/**
	 * if the calling object is providing the connection, then it is set here
	 *
	 * @param conn
	 * @roseuid 3FF5579000DA
	 */
	public void setConnection(Connection conn)
	{
		_conn = conn;
	}

	/**
	 * if the calling object decides to withdraw the connection object this method is
	 * used
	 * @roseuid 3FF557900109
	 */
	public void removeConnection()
	{
		_conn = null;
	}

	/**
	 * method useful to identify the properties file name
	 *
	 * @return java.lang.String
	 * @roseuid 3FF557900119
	 */
	public static String getProjectName()
	{
		return _projectName;
	}

	/**
	 * method to register an alert to the specified alertName
	 *
	 * @param alertName
	 * @throws java.sql.SQLException
	 * @roseuid 3FF5579001C5
	 */
	public void registerAlert(String alertName) throws SQLException
	{
		registerAlert( alertName, getConnection() );
	}

	/**
	 * emthod to register an alert to the specified alertName using the given
	 * connection
	 *
	 * @param alertName
	 * @param conn
	 * @throws java.sql.SQLException
	 * @roseuid 3FF557900242
	 */
	public void registerAlert(String alertName, Connection conn) throws SQLException
	{
		registerAlert( null, alertName, conn );
	}

	/**
	 * method to register an alert and also attach a listener to the event
	 *
	 * @param listener
	 * @param alertName
	 * @throws java.sql.SQLException
	 * @roseuid 3FF5579002CE
	 */
	public void registerAlert(PropertyChangeListener listener, String alertName) throws SQLException
	{
		registerAlert( listener, alertName, getConnection() );
	}

	/**
	 * method to register an alert and also attach a listener to the event with the
	 * given connection
	 *
	 * @param listener
	 * @param alertName
	 * @param conn
	 * @throws java.sql.SQLException
	 * @roseuid 3FF55790036B
	 */
	public void registerAlert(PropertyChangeListener listener, String alertName, Connection conn) throws SQLException
	{
		Object object = _alerts.get( alertName );
		if (object != null) {
			if (listener == null)
				return;
			((OracleAlert)object).addPropertyChangeListener( listener );
			System.out.println( "Listener attached for alert - " + alertName );
		}
		handleAlert( alertName, conn, false );
		System.out.println( "Registered Alert - " + alertName );
		OracleAlert alert = new OracleAlert( alertName );
		_alerts.put( alertName, alert );
		if (listener != null) {
			alert.addPropertyChangeListener( listener );
			System.out.println( "Listener attached for alert - " + alertName );
		}
	}

	private synchronized void handleAlert(String alertName, Connection conn, boolean isRemove) throws SQLException
	{
        CallableStatement cstmt = null;
		String sql = null;
		if (isRemove)
			sql = "begin DBMS_ALERT.REMOVE( ? ); end;";
		else
			sql = "begin DBMS_ALERT.REGISTER( ? ); end;";
		try {
			if (conn == null)
				conn = getConnection();
			cstmt = conn.prepareCall( sql );
			cstmt.setString( 1, alertName );
			cstmt.executeQuery();
		}
		finally {
			if (cstmt != null) {
				cstmt.close();
				cstmt = null;
			}
		}
	}


	/**
	 * method to attach a listener to an already registered alert
	 *
	 * @param listener
	 * @param alertName
	 * @throws java.sql.SQLException
	 * @roseuid 3FF55791003E
	 */
	public void listenForAlert(PropertyChangeListener listener, String alertName) throws SQLException
	{
		listenForAlert( listener, alertName, getConnection() );
	}

	/**
	 * method to attach a listener to an already registered alert with the given
	 * connection
	 *
	 * @param listener
	 * @param alertName
	 * @param conn
	 * @throws java.sql.SQLException
	 * @roseuid 3FF5579100DA
	 */
	public void listenForAlert(PropertyChangeListener listener, String alertName, Connection conn) throws SQLException
	{
		registerAlert( listener, alertName, conn );
	}

	/**
	 * friendly method to get the names of all registered alerts
	 *
	 * @return java.lang.String[]
	 * @roseuid 3FF557910196
	 */
	public static synchronized String[] getAlertNames()
	{
		return (String[])_alerts.keySet().toArray( new String[_alerts.size()] );
	}

	/**
	 * get the alert object associated with the given alerName. Null if none is
	 * registered.
	 *
	 * @param alertName
	 * @return com.addval.dbutils.OracleAlertListener.OracleAlert
	 * @roseuid 3FF5579101A5
	 */
	private static OracleAlertListener.OracleAlert getAlert(String alertName)
	{
		return (OracleAlert)_alerts.get( alertName );
	}

	/**
	 * method to remove the alert from database fro the given alertName
	 *
	 * @param alertName
	 * @throws java.sql.SQLException
	 * @roseuid 3FF5579101F4
	 */
	public void removeAlert(String alertName) throws SQLException
	{
		removeAlert( null, alertName );
	}

	/**
	 * method to remove listener as well as the registration for the given alert
	 *
	 * @param listener
	 * @param alertName
	 * @throws java.sql.SQLException
	 * @roseuid 3FF557910271
	 */
	public void removeAlert(PropertyChangeListener listener, String alertName) throws SQLException
	{
		removeAlert( listener, alertName, getConnection() );
	}

	/**
	 * method to remove listener as well as the registration for the given alert with
	 * the given connection
	 *
	 * @param listener
	 * @param alertName
	 * @param conn
	 * @throws java.sql.SQLException
	 * @roseuid 3FF55791030D
	 */
	public synchronized void removeAlert(PropertyChangeListener listener, String alertName, Connection conn) throws SQLException
	{
		OracleAlert alert = (OracleAlert)_alerts.get( alertName );
		if (listener != null)
			alert.removePropertyChangeListener( listener );
		if (!alert.hasListeners( alertName ))
			removeAlert( alertName, conn );
	}

	/**
	 * @param alertName
	 * @param conn
	 * @throws java.sql.SQLException
	 * @roseuid 3FF5579103D8
	 */
	private void removeAlert(String alertName, Connection conn) throws SQLException
	{
		handleAlert( alertName, conn, true );
	}

	/**
	 * friendly method to get the connection. If client has supplied a connection
	 * already
	 * that connection is used. Else Connection is created from info provided in
	 * properties file
	 *
	 * @return java.sql.Connection
	 * @roseuid 3FF55792008C
	 */
	private static Connection getConnection()
	{
	    if (_conn != null)
	        return _conn;
		return Environment.getInstance( getProjectName() ).getDbPoolMgr().getConnection();
	}

	/**
	 * friendly method to release connection after usage. Care is taken not to close
	 * the client supplied connection
	 *
	 * @param conn
	 * @roseuid 3FF5579200BB
	 */
	private static void releaseConnection(Connection conn)
	{
		if (_conn != null)
			Environment.getInstance( getProjectName() ).getDbPoolMgr().releaseConnection( conn );
	}

	/**
	 * friendly method to close the Statement object after usage
	 *
	 * @param stmt
	 * @throws java.sql.SQLException
	 * @roseuid 3FF5579200FA
	 */
	private static void closeStatement(Statement stmt) throws SQLException
	{
		if (stmt != null) {
			stmt.close();
			stmt = null;
		}
	}

	/**
	 * method to add a shutdown hook to the JVM sothat when the server is brought
	 * down, the clean-up action could be done
	 * @roseuid 3FF557920186
	 */
	private static synchronized void addShutdownHook()
	{
	    Runtime.getRuntime().addShutdownHook( new Thread() {
	            public void run() {
					cleanup();
	            }
	        });
	}

	/**
	 * do any clean-work needed here
	 * @roseuid 3FF5579201A5
	 */
	private static void cleanup()
	{
		try {
			System.out.println( "JVM is down. Startng cleaning-up Operation - \t" + new Date() );
			String[] alertNames = getAlertNames();
			for (int i=0; i<alertNames.length; i++) {
				System.out.println( alertNames[i] + " Cleaning-up started" );
				getAlert( alertNames[i] ).close();
				System.out.println( alertNames[i] + " Closed." );
//				removeAlert( alertNames[i], getConnection() );
				System.out.println( alertNames[i] + " Removed.\n" );
			}
			System.out.println( "JVM is down. completed cleaning-up Operation - \t" + new Date() );
		}
		catch(Exception e) {
			System.out.println( "Error while cleaning-up " + e.getMessage() );
		}
	}

	/**
	 * inner class to contain the details of Alerts. One per the alert. Even if there
	 * are multiple listeners
	 * this class is created only once per alertName. MessageDrivenBean type
	 * functionality
	 * is simulated using the PropertyChangeListener and bound property
	 */
	private class OracleAlert implements Runnable, Serializable
	{
		private String _name = null;
		private boolean _isStarted = false;
		private PropertyChangeSupport _changes = null;

		/**
		 * Constructor. The Thread operation is started only when the first listener is
		 * attached
		 *
		 * @param name
		 * @roseuid 3FF557930128
		 */
		private OracleAlert(String name)
		{
			_name = name;
			_changes = new PropertyChangeSupport( this );
		}

		/**
		 * method to add listener to the given alert. Thread is started only when the
		 * first listener is attached.
		 *
		 * @param listener
		 * @roseuid 3FF557930139
		 */
		private void addPropertyChangeListener(PropertyChangeListener listener)
		{
			if (!_isStarted) {
				new Thread( this ).start();
				_isStarted = true;
			}
		    _changes.addPropertyChangeListener( listener );
		}

		/**
		 * method to remove the listener when alert is no more needed
		 *
		 * @param listener
		 * @roseuid 3FF557930157
		 */
		private void removePropertyChangeListener(PropertyChangeListener listener)
		{
		    _changes.removePropertyChangeListener( listener );
		}

		/**
		 * indicates whether there are any listeners for this alert
		 *
		 * @param alertName
		 * @return boolean
		 * @roseuid 3FF557930168
		 */
		private boolean hasListeners(String alertName)
		{
			return _changes.hasListeners( alertName );
		}

		/**
		 * implemented method of Runnable interface. In a non-blocking fashion, this
		 * indefinite loop checks for the messages
		 * @roseuid 3FF557930178
		 */
		public void run()
		{
			CallableStatement cstmt = null;
			Connection conn = null;
			try {
				conn = getConnection();
				long timeoutSecs = Long.parseLong( Environment.getInstance( _projectName ).getCnfgFileMgr().getPropertyValue( "alertListen.intervelSeconds", "-1" ));
				if (timeoutSecs == -1) // procedure would wait for DEFAULT timeout to receive alert
					cstmt = conn.prepareCall( "begin DBMS_ALERT.WAITONE( ?, ?, ? ); end;" );
				else
					cstmt = conn.prepareCall( "begin DBMS_ALERT.WAITONE( ?, ?, ?, ? ); end;" );
				cstmt.setString( 1, getName() );
				cstmt.registerOutParameter( 2, java.sql.Types.VARCHAR );
				cstmt.registerOutParameter( 3, java.sql.Types.INTEGER );
				if (timeoutSecs != -1)
					cstmt.setLong( 4, timeoutSecs );//timeout in sec
				while (true) {
					cstmt.executeQuery();
					new ProcessAlert( cstmt.getString( 2 ) );
				}
			}
			catch(Exception e) {
				System.out.println( "Error during alertListening - alertName = " + getName() );
				System.out.println( e.getMessage() );
			}
			finally {
				try {
					closeStatement( cstmt );
				}
				catch(Exception e) {
					System.out.println( "Error while clong Statement in the Thread - Alertname = " + getName() );
					System.out.println( e.getMessage() );
				}
				releaseConnection( conn );
			}
		}

		/**
		 * friendly method to return the name of the alert to which this object is attached
		 *
		 * @return java.lang.String
		 * @roseuid 3FF557930186
		 */
		private String getName()
		{
			return _name;
		}

		/**
		 * method to do any resource clean-up action
		 * @roseuid 3FF557930187
		 */
		private void close()
		{
			// do if any clean-up action is required
		}

		/**
		 * inner class to the <code>OracleAlert</code> inner class. Useful to return
		 * control to the executeQuery
		 * sothat the listening process is least interrupted. Sole purpose is to fire
		 * propertyChange
		 */
		private class ProcessAlert implements Runnable, Serializable
		{
			private String _message = null;

			/**
			 * @param message
			 * @roseuid 3FF5579303C8
			 */
			private ProcessAlert(String message)
			{
				// if no message there to deliver, don't create the thread
				if (message == null)
					return;
				_message = message;
				new Thread( this ).start();
			}

			/**
			 * implemented method of <code>Runnable</code> interface. sole purpose is to fire
			 * PropertyChange
			 * @roseuid 3FF5579303D9
			 */
			public void run()
			{
				_changes.firePropertyChange( getName(), null, _message );
			}
		}
	}
}
