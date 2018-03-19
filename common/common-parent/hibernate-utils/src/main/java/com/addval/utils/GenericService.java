/* Copyright AddVal Technology Inc. */

package com.addval.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author
 * @version
 */
public abstract class GenericService extends Thread implements ServiceInterface, Cloneable
{
	private static final String _module = "GenericService";
	private Thread _instance = null;
	private ServerSocket _serverSocket = null;
	private Socket _clientSocket = null;
	private boolean _listen = false;
	private boolean _active = false;
	public static final String _START = "start";
	public static final String _STOP = "stop";

	/**
	 * @exception
	 * @roseuid 3B2FFAB00382
	 */
	public GenericService()
	{
	}

	/**
	 * @return void
	 * @exception
	 * @roseuid 3B2FD430026E
	 */
	public final void run()
	{
		if (_serverSocket != null) {

			Thread.currentThread().setPriority( Thread.MAX_PRIORITY );

			System.out.println( "AddVal Technology Inc. : " + getServiceName() + " listening on port: " + String.valueOf( getPort() ) );

			while (true) {

				if (isActive()) {

					try {

						// Create new Thread to handle request
						// Note: This may be too much overhead for simple requests
						// The call to accept() will block till a particular connection from
						// the client is made
						Socket socket = _serverSocket.accept();

						if (!_listen)
							break;

						// If a stop service was set to this port.
						// Shutdown the JVM
						BufferedReader reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ));
						if (reader.readLine().equalsIgnoreCase( _STOP )) {

							close();
							shutDown();
							return;
						}

						// Clone this service thus creating a thread to process the request
						// Set the new GenericServer's client socket to the one returned by accept()
						// Set the _serverSocket for this clone to NULL, so that the child service
						// can start (if (serverSocket != null)) fails
						GenericService service 	= (GenericService)clone();
						service._serverSocket 	= null;
						service._clientSocket 	= socket;

						Thread thread = new Thread( service );
						thread.setDaemon( true );
						thread.start();
					}
					catch (Exception e) {

						try {
							_serverSocket.close();
						}
						catch(IOException e2) {

							e.fillInStackTrace();
						}

						// If you could not start a thread, then fail
						throw new XRuntime( _module, e.getMessage() );
					}
				}
				else {

					throw new XRuntime( _module, "Server is not active on port :" + getPort() );
				}
			}//end while
		}//end if _serverSocket != null
		else {

			try {

				// The serviceRequest() method is overwritten in every derived class
				serviceRequest();
			}
			catch (Exception e) {

				throw new XRuntime( _module, e.getMessage() );
			}
		}

		close();
	}

	/**
	 * @return void
	 * @exception
	 * @roseuid 3B2FD43C01FD
	 */
	public void startUp()
	{
		try {
			_serverSocket 	= new ServerSocket( getPort() );
			// If any available port was used get the dynamically assigned port
			setPort( _serverSocket.getLocalPort() );
			_instance 		= new Thread( this );
			_listen 		= true;
			// Cannot set this thread to daemon, since JVM will exit
			//_instance.setDaemon( true );
			_instance.start();
        }
        catch (IOException e) {

			throw new XRuntime( _module, e.getMessage() );
        }
	}

	/**
	 * @return void
	 * @exception
	 * @roseuid 3B2FD43C0211
	 */
	public void shutDown()
	{
		_listen = false;
        try {

			InetAddress address = InetAddress.getLocalHost();
			int port = getPort();
			String serviceName = getServiceName();
			Socket s = new Socket( address, port );
			Thread.currentThread().sleep( 100 );
			_instance = null;
			System.out.println( "AddVal Technology Inc. : " + serviceName + " listening on port: " + String.valueOf( port ) + " is exiting" );
			System.exit( 0 );
		}
		catch (Exception e) {

			throw new XRuntime( _module, e.getMessage() );
		}
	}

	/**
	 * @return void
	 * @exception
	 * @roseuid 3B2FD43C0225
	 */
	public void activate()
	{
		_active = true;
	}

	/**
	 * @return void
	 * @exception
	 * @roseuid 3B2FD43C0239
	 */
	public void deactivate()
	{
		_active = false;
	}

	/**
	 * @return boolean
	 * @exception
	 * @roseuid 3B2FD43C0257
	 */
	public boolean isActive()
	{
		return _active;
	}

	/**
	 * @return void
	 * @exception
	 * @roseuid 3B2FF9620346
	 */
	public void close()
	{
		try {
			InputStream  input;
			OutputStream output;

			if (_clientSocket != null) {

				input  = _clientSocket.getInputStream();
				output = _clientSocket.getOutputStream();
				_clientSocket.close();
				input.close();
				output.close();
			}

			_clientSocket = null;
			input = null;
			output = null;

        }
        catch (Exception e) {
        }
	}

	/**
	 * @return int
	 * @exception
	 * @roseuid 3B2FF9870213
	 */
	public abstract int getPort();

	/**
	 * @param port
	 * @return void
	 * @exception
	 * @roseuid 3B2FF99000C1
	 */
	public abstract void setPort(int port);

	/**
	 * @return void
	 * @exception
	 * @roseuid 3B2FFC9B008F
	 */
	public abstract void serviceRequest();

	/**
	 * @return String
	 * @exception
	 * @roseuid 3B3CDA5201D7
	 */
	public abstract String getServiceName();
}
