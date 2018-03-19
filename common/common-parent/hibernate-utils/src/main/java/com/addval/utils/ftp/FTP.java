//Source file: d:\\projects\\common\\source\\com\\addval\\utils\\ftp\\FTP.java

package com.addval.utils.ftp;

import java.util.Calendar;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.text.CharacterIterator;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.StringTokenizer;
import com.addval.environment.Environment;
import java.io.PrintWriter;
import java.io.InputStream;
import java.net.Socket;
import java.util.Vector;
import com.addval.utils.LogFileMgr;
import com.addval.utils.XRuntime;

/**
 * Following commands based on RFC 959 have been implemented.
 * CDUP - Change to Parent Directory
 * SMNT - Structure Mount
 * STOU - Store Unique
 * RMD  - Remove Directory
 * MKD  - Make Directory
 * PWD  - Print Directory
 * SYST - System
 * TYPE - Type, binary, ascii EBSIDC
 * RETR - Retrieve a file
 * CWD  - Change working directory.
 * NLST - Send directory listing.
 * PORT - Set the port number.
 * SITE - Retrieve site info from remote host.
 * ABOR - Abort connection.
 * LIST - Get a directory lising from remote host.
 * STOR - Store a file on the target machine.
 */
public class FTP {
	private static final String _module = "FTP";
	private static final int _FTP_PORT = 21;
	private static final int _FTP_SUCCESSFULL = 1;
	private static final int _FTP_TRY_AGAIN = 2;
	private static final int _FTP_ERROR = 3;
	private static final int _UNIX = 4;
	private static final int _WINDOWS = 5;
	private static final int _VOS = 6;
	private static final int _MVS = 7;
	private static final int _VM = 8;
	private boolean _passiveMode = false;
	private boolean _binaryMode = false;
	private boolean _replyPending = false;
	private String _password = null;
	private String _userName = null;
	private String _host = null;
	private String _currentCommand = null;
	private String _operatingSystem = null;
	private int _portNumber = _FTP_PORT;
	private int _remoteFileListLength = 0;
	private int _remoteDirListLength = 0;
	private int _nOSType = 0;
	private int _returnCode;
	private Vector _lstServerResponse = new Vector (1);
	private PrintWriter _serverSideOutput;
	private InputStream _serverSideInput;
	private Socket _serverSocket = null;
	private Socket _socket = null;
	private LogFileMgr _logFileMgr = null;
	
	/**
	 * @param environment
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @throws java.lang.Exception
	 * @roseuid 3E1D19580336
	 */
	public FTP(Environment environment, String host, int port, String userName, String password) throws Exception {
        this( null, environment, host, port, userName, password );		
	}
	
	/**
	 * @param environment
	 * @param host
	 * @param userName
	 * @param password
	 * @throws java.lang.Exception
	 * @roseuid 3E1D1958023B
	 */
	public FTP(Environment environment, String host, String userName, String password) throws Exception {
        this( environment, host, -1, userName, password );		
	}
	
	/**
	 * @param environment
	 * @param host
	 * @param port
	 * @throws java.lang.Exception
	 * @roseuid 3E1D19580169
	 */
	public FTP(Environment environment, String host, int port) throws Exception {
        this( environment, host, port, null, null );		
	}
	
	/**
	 * @param environment
	 * @param host
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195800A1
	 */
	public FTP(Environment environment, String host) throws Exception {
        this( environment, host, -1 );		
	}
	
	/**
	 * @param environment
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195703C0
	 */
	public FTP(Environment environment) throws Exception {
        this( environment, null );		
	}
	
	/**
	 * @param projectName
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195702D0
	 */
	public FTP(String projectName, String host, int port, String userName, String password) throws Exception {
        this( projectName, null, host, port, userName, password );		
	}
	
	/**
	 * @param projectName
	 * @param host
	 * @param userName
	 * @param password
	 * @throws java.lang.Exception
	 * @roseuid 3E1D19570212
	 */
	public FTP(String projectName, String host, String userName, String password) throws Exception {
        this( projectName, null, host, -1, userName, password );		
	}
	
	/**
	 * @param projectName
	 * @param host
	 * @param port
	 * @throws java.lang.Exception
	 * @roseuid 3E1D19570153
	 */
	public FTP(String projectName, String host, int port) throws Exception {
        this( projectName, host, port, null, null );		
	}
	
	/**
	 * @param projectName
	 * @param host
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195700A9
	 */
	public FTP(String projectName, String host) throws Exception {
        this( projectName, host, -1 );		
	}
	
	/**
	 * @param projectName
	 * @throws java.lang.Exception
	 * @roseuid 3E1D19570009
	 */
	public FTP(String projectName) throws Exception {
        this( projectName, null );		
	}
	
	/**
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @throws java.lang.Exception
	 * @roseuid 3E1D19560329
	 */
	public FTP(String host, int port, String userName, String password) throws Exception {
        this( null, null, host, port, userName, password );		
	}
	
	/**
	 * @param host
	 * @param userName
	 * @param password
	 * @throws java.lang.Exception
	 * @roseuid 3E1D19560274
	 */
	public FTP(String host, String userName, String password) throws Exception {
        this( host, -1, userName, password );		
	}
	
	/**
	 * @param host
	 * @param port
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195601CA
	 */
	public FTP(String host, int port) throws Exception {
        this( host, port, null, null);		
	}
	
	/**
	 * ultimate constructor called from all other constructors.
	 * takes care to check for the presence of userName, password, host and port.
	 * 
	 * @param projectName
	 * @param environment
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195600D0
	 */
	private FTP(String projectName, Environment environment, String host, int port, String userName, String password) throws Exception {
        if (projectName != null && environment == null)  // try creating the Environment
            environment = createEnvironment( projectName );
        if (environment != null) {
            _logFileMgr = environment.getLogFileMgr( _module );
            if (host == null || host.equals( "" ))
                host = environment.getCnfgFileMgr().getPropertyValue( "ftp.host", "" );
            if (host.equals( "" )) {
                logError( "FTP Host name is to be either specified in the constructor or in the " + projectName + ".properties file." );
                throw new XRuntime( _module, "FTP Host name is to be either specified in the constructor or in the " + projectName + ".properties file." );
            }
            if (port == -1)
                port = Integer.parseInt( environment.getCnfgFileMgr().getPropertyValue( "ftp.port", String.valueOf( _FTP_PORT ) ) );
            if (userName == null || userName.equals( "" ))
                userName = environment.getCnfgFileMgr().getPropertyValue( "ftp.userName", "anonymous" );
            if (password == null || password.equals( "" ))
                password = environment.getCnfgFileMgr().getPropertyValue( "ftp.password", "test@test.com" );
        }
        if (host == null || host.equals( "" )) {
            logError( "FTP Host name should not be null or Empty." );
            throw new XRuntime( _module, "FTP Host name should not be null or Empty." );
        }
        if (port == -1)
            port = _FTP_PORT;
        // assume default parameters, if not provided already
        if (userName == null || userName.equals( "" ))
            _userName = "anonymous";
        else
            _userName = userName;
        if (password == null || password.equals( "" ))
            _password = "test@test.com";
        else
            _password = password;
        _host = host;
        _portNumber = port;
        openServer( host, port );
        if (userName != null && password != null)
            login();		
	}
	
	/**
	 * ignore the error in case there is failure to create environment
	 * 
	 * @param projectName
	 * @return com.addval.environment.Environment
	 * @roseuid 3E1D19590052
	 */
	private Environment createEnvironment(String projectName) {
        try {
            return Environment.getInstance( projectName );
        }
        catch(Exception e) {
            return null;
        }		
	}
	
	/**
	 * Login in to the remote host.
	 * @throws java.lang.Exception
	 * @roseuid 3E1D1959008E
	 */
	public void login() throws Exception {
       login( _userName, _password );		
	}
	
	/**
	 * Login in to the remote host.
	 * @param userName
	 * @throws java.lang.Exception
	 * @roseuid 3E1D19590142
	 */
	public void login(String userName) throws Exception {
        login( userName, _password );		
	}
	
	/**
	 * Login in to the remote host.
	 * @param userName
	 * @param password
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195901F6
	 */
	public void login(String userName, String password) throws Exception {
        if (!serverConnectionOpen()) {
           logError( " not connected to host.\n" );
           throw new XRuntime(_module, "Error: not connected to host.\n" );
        }
        if (userName == null)
            userName = "anonymous";
        if (password == null)
            password = "test@test.com";
        _userName = userName;
        _password = password;
        if (sendCurrentCommand("USER " + _userName) == _FTP_ERROR) {
            logError( " User not found.\n" );
            throw new XRuntime( _module, "Error: User not found.\n" + _userName );
        }
        if (sendCurrentCommand("PASS " + _password) == _FTP_ERROR) {
            logError( " Wrong Password for user " + _userName + ". Please check. ");
            throw new XRuntime( _module, "Error: Wrong Password for user " + _userName + ". Please check." );
        }
        syst();		
	}
	
	/**
	 * APPEND (with create) (APPE)
	 * This command causes the server-DTP to accept the data transferred via the data
	 * connection and to store the data in a file at the server site.  If the file
	 * specified in the pathname exists at the server site, then the data shall be
	 * appended to that file; otherwise the file specified in the pathname shall be
	 * created at the server site. Append to an ascii file.
	 * @param fileName
	 * @return java.io.BufferedWriter
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195902E7
	 */
	public BufferedWriter appendAscii(String fileName) throws Exception {
       openDataConnection("APPE " + fileName);
       return new BufferedWriter( new OutputStreamWriter(_socket.getOutputStream()), 4096 );		
	}
	
	/**
	 * Append to a binary file.
	 * @param fileName
	 * @return java.io.BufferedOutputStream
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195A0017
	 */
	public BufferedOutputStream appendBinary(String fileName) throws Exception {
       openDataConnection("APPE " + fileName);
       return new BufferedOutputStream(_socket.getOutputStream());		
	}
	
	/**
	 * LIST (LIST)
	 * This command causes a list to be sent from the server to the passive DTP.  If
	 * the pathname specifies a directory or other group of files, the server should
	 * transfer a list of files in the specified directory.  If the pathname specifies
	 * a file then the server should send current information on the file.  A null
	 * argument implies the user's current working or default directory.  The data
	 * transfer is over the data connection in type ASCII or type EBCDIC.
	 * @return java.io.BufferedReader
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195A00EA
	 */
	public BufferedReader list() throws Exception {
        setPassiveMode( true );
        openDataConnection("LIST");
        return new BufferedReader( new InputStreamReader(_socket.getInputStream()));		
	}
	
	/**
	 * @return com.addval.utils.ftp.RemoteFile[]
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195A01A8
	 */
	private RemoteFile[] getDirectoryContent() throws Exception {
        return getDirectoryContent(false, false);		
	}
	
	/**
	 * method to retrieve the full details about the directory contents
	 * @param isFilesOnly
	 * @param isDirOnly
	 * @return com.addval.utils.ftp.RemoteFile[]
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195A027A
	 */
	private RemoteFile[] getDirectoryContent(boolean isFilesOnly, boolean isDirOnly) throws Exception {
        setPassiveMode( true );
        Vector files = new Vector();
        BufferedReader listing = list();
        String line = null;
        while ((line = listing.readLine()) != null) {
            // sometimes the first line lists the total bytes in the dir, skip it
            if (line.startsWith("total ") || line.equals( "" ))
                continue;
            // try parsing the line, to infer date time and related file details
            RemoteFile remoteFile = new RemoteFile( this, line );
            if (isFilesOnly) {
                if (remoteFile.isFile())
                    files.add( remoteFile );
                continue;
            }
            if (isDirOnly) {
                if (remoteFile.isDirectory())
                    files.add( remoteFile );
                continue;
            }
            if (remoteFile.isDirectory() || remoteFile.isFile())
                files.add( remoteFile );
        }
        return (RemoteFile[])files.toArray(new RemoteFile[files.size()]);		
	}
	
	/**
	 * from the contents only the file detail objects are returned by this method
	 * From RemoteFile, we would be able to get name, size, whether directory or file
	 * and date and time
	 * @return com.addval.utils.ftp.RemoteFile[]
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195A0360
	 */
	public RemoteFile[] getFileDetails() throws Exception {
        RemoteFile[] files = getDirectoryContent(true, false);
        _remoteFileListLength = files.length;
        return files;		
	}
	
	/**
	 * friendly method to giveout an array of fileNames
	 * @return String[]
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195B0055
	 */
	public String[] getFileNames() throws Exception {
        RemoteFile[] contents = getFileDetails();
        String[] fileNames = new String[_remoteFileListLength];
        for (int i=0; i<_remoteFileListLength; i++)
            fileNames[i] = contents[i].getName();
        return sortIgnoreCase( fileNames );		
	}
	
	/**
	 * friendly method to giveout an array of directory names
	 * @return String[]
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195B0109
	 */
	public String[] getDirectoryNames() throws Exception {
        RemoteFile[] dirs = getDirectoryContent( false, true );
        _remoteDirListLength = dirs.length;
        String[] dirNames = new String[_remoteDirListLength];
        for (int i=0; i<_remoteDirListLength; i++)
            dirNames[i] = dirs[i].getName();
        return sortIgnoreCase( dirNames );		
	}
	
	/**
	 * using anonymous inner class, do a case insensitive sort
	 * 
	 * @param values[]
	 * @return java.lang.String[]
	 * @roseuid 3E1D195B01BD
	 */
	private String[] sortIgnoreCase(String values[]) {
        Arrays.sort(values, new java.util.Comparator() {
            public int compare(Object o1, Object o2) {
                return ((String)o1).compareToIgnoreCase( (String)o2 );
            }

            public boolean equals(Object obj) {
                return false;
            }
        });
        return values;		
	}
	
	/**
	 * CHANGE WORKING DIRECTORY (CWD)
	 * This command allows the user to work with a different directory or dataset for
	 * file storage or retrieval without altering his login or accounting information.
	 * Transfer parameters are similarly unchanged.  The argument is a pathname
	 * specifying a directory or other system dependent file group designator.
	 * @param remoteDirectory
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195B01E5
	 */
	public void cd(String remoteDirectory) throws Exception {
       sendCurrentCommandCheck("CWD " + remoteDirectory);		
	}
	
	/**
	 * @param remoteDirectory
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195B02A4
	 */
	public void changeDirectoryTo(String remoteDirectory) throws Exception {
       cd( remoteDirectory );		
	}
	
	/**
	 * NAME LIST (NLST)
	 * This command causes a directory listing to be sent from server to user site.
	 * The pathname should specify a directory or other system-specific file group
	 * descriptor; a null argument implies the current directory.  The server will
	 * return a stream of names of files and no other information.  The data will be
	 * transferred in ASCII or EBCDIC type over the data connection as valid pathname
	 * strings separated by <CRLF> or <NL>.  (Again the user must ensure that the TYPE
	 * is correct.)  This command is intended to return information that can be used
	 * by a program to further process the files automatically.  For example, in the
	 * implementation of a "multiple get" function.
	 * @return java.io.BufferedReader
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195B0362
	 */
	public BufferedReader nlst() throws Exception {
        return nlst( "" );		
	}
	
	/**
	 * NAME LIST (NLST)
	 * This command causes a directory listing to be sent from server to user site.
	 * The pathname should specify a directory or other system-specific file group
	 * descriptor; a null argument implies the current directory.  The server will
	 * return a stream of names of files and no other information.  The data will be
	 * transferred in ASCII or EBCDIC type over the data connection as valid pathname
	 * strings separated by <CRLF> or <NL>.  (Again the user must ensure that the TYPE
	 * is correct.)  This command is intended to return information that can be used
	 * by a program to further process the files automatically.  For example, in the
	 * implementation of a "multiple get" function.
	 * @param fileName
	 * @return java.io.BufferedReader
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195C006A
	 */
	public BufferedReader nlst(String fileName) throws Exception {
       openDataConnection("NLST " + fileName);
       return new BufferedReader( new InputStreamReader(_socket.getInputStream()));		
	}
	
	/**
	 * Return the length of the current list command
	 * @return int
	 * @roseuid 3E1D195C013D
	 */
	public int getRemoteFileListLength() {
        return _remoteFileListLength;		
	}
	
	/**
	 * RENAME FROM (RNFR)
	 * This command specifies the old pathname of the file which is to be renamed.
	 * This command must be immediately followed by a "rename to" command specifying
	 * the new file pathname.
	 * @param oldFile
	 * @param newFile
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195C015B
	 */
	public void rename(String oldFile, String newFile) throws Exception {
        sendCurrentCommandCheck("RNFR " + oldFile);
        sendCurrentCommandCheck("RNTO " + newFile);		
	}
	
	/**
	 * STORE (STOR)
	 * This command causes the server-DTP to accept the data transferred via the data
	 * connection and to store the data as a file at the server site.  If the file
	 * specified in the pathname exists at the server site, then its contents shall be
	 * replaced by the data being transferred.  A new file is created at the server
	 * site if the file specified in the pathname does not already exist.
	 * // Store in Ascii format
	 * @param fileName
	 * @return java.io.BufferedWriter
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195C0219
	 */
	public BufferedWriter putAscii(String fileName) throws Exception {
       openDataConnection("STOR " + fileName);
       return new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream()),4096);		
	}
	
	/**
	 * Store in Binary format
	 * @param fileName
	 * @return java.io.BufferedOutputStream
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195C02FF
	 */
	public BufferedOutputStream putBinary(String fileName) throws Exception {
       openDataConnection("STOR " + fileName);
       return new BufferedOutputStream( _socket.getOutputStream() );		
	}
	
	/**
	 * TYPE (TYPE)
	 * Set user specifed mode.
	 * @param strType
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195C03E6
	 */
	public void setType(String strType) throws Exception {
        sendCurrentCommandCheck("TYPE " + strType);		
	}
	
	/**
	 * Set binary mode.
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195D00B2
	 */
	public void setBinaryMode() throws Exception {
        setType( "I" );
        _binaryMode = true;		
	}
	
	/**
	 * Set ascii mode
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195D0166
	 */
	public void setAsciiMode() throws Exception {
        setType( "A" );
        _binaryMode = false;		
	}
	
	/**
	 * Set ebcdic mode, probably don't need to but what the heck.
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195D0224
	 */
	public void setEbcdic() throws Exception {
        setType( "E" );
        _binaryMode = true;		
	}
	
	/**
	 * SITE PARAMETERS (SITE)
	 * This command is used by the server to provide services specific to his system
	 * that are essential to file transfer but not sufficiently universal to be
	 * included as commands in the protocol.  The nature of these services and the
	 * specification of their syntax can be stated in a reply to the HELP SITE command.
	 * @param params
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195D02D9
	 */
	public void site(String params) throws Exception {
        sendCurrentCommandCheck("SITE "+ params);		
	}
	
	/**
	 * CHANGE TO PARENT DIRECTORY (CDUP)
	 * This command is a special case of CWD, and is included to simplify the
	 * implementation of programs for transferring directory trees between operating
	 * systems having different
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195D0397
	 */
	public void cdup() throws Exception {
       sendCurrentCommandCheck( "CDUP" );		
	}
	
	/**
	 * ABORT (ABOR)
	 * This command tells the server to abort the previous FTP service command and any
	 * associated transfer of data.  The abort command may require "special action",
	 * as discussed in the Section on FTP Commands, to force recognition by the
	 * server.  No action is to be taken if the previous command has been completed
	 * (including data transfer).  The control connection is not to be closed by the
	 * server, but the data connection must be closed.
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195E0077
	 */
	public void abort() throws Exception {
       sendCurrentCommandCheck( "ABOR" );		
	}
	
	/**
	 * MAKE DIRECTORY (MKD)
	 * This command causes the directory specified in the pathname to be created as a
	 * directory (if the pathname is absolute) or as a subdirectory of the current
	 * working directory (if the pathname is relative).
	 * @param strTemp
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195E012B
	 */
	public void mkdir(String strTemp) throws Exception {
        sendCurrentCommandCheck("MKD " + strTemp);		
	}
	
	/**
	 * DELETE (DELE)
	 * This command causes the file specified in the pathname to be deleted at the
	 * server site.  If an extra level of protection is desired (such as the query,
	 * "Do you really wish to delete?"), it should be provided by the user-FTP process.
	 * @param strTemp
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195E01E0
	 */
	public void delete(String strTemp) throws Exception {
       sendCurrentCommandCheck("DELE " + strTemp);		
	}
	
	/**
	 * PRINT WORKING DIRECTORY (PWD)
	 * This command causes the name of the current working directory to be returned in
	 * the reply.
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195E029E
	 */
	public void pwd() throws Exception {
       sendCurrentCommandCheck("PWD");		
	}
	
	/**
	 * @return String
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195E035C
	 */
	public String getCurrentDirectory() throws Exception {
        pwd();
        String response = receiveResponse();
        response = response.substring( response.indexOf( "\"" ) + 1);
        return response.substring( 0, response.indexOf( "\"" ));		
	}
	
	/**
	 * SYSTEM (SYST)
	 * This command is used to find out the type of operating system at the server.
	 * The reply shall have as its first word one of the system names listed in the
	 * current version of the Assigned Numbers document.
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195F003C
	 */
	public void syst() throws Exception {
        sendCurrentCommandCheck( "SYST" );
        String response = receiveResponse();
        if (!response.startsWith("215 "))
            return ;
        // Extract the operating system type
        response = response.substring( response.indexOf(' ') + 1 );
        // Now compare
        if (response.startsWith( "WINDOWS" ))
            _nOSType = _WINDOWS;
        else if(response.startsWith( "VM" ))
            _nOSType = _VM;
        else if(response.startsWith( "MVS" ))
            _nOSType = _MVS;
        else
            _nOSType = _UNIX;
        _operatingSystem = response;		
	}
	
	/**
	 * @return String
	 * @roseuid 3E1D195F00FB
	 */
	public String getOperatingSystem() {
        return _operatingSystem;		
	}
	
	/**
	 * REMOVE DIRECTORY (RMD)
	 * This command causes the directory specified in the pathname to be removed as a
	 * directory (if the pathname is absolute) or as a subdirectory of the current
	 * working directory (if the pathname is relative).
	 * @param strTemp
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195F0123
	 */
	public void rmdir(String strTemp) throws Exception {
       sendCurrentCommandCheck("RMD " + strTemp);		
	}
	
	/**
	 * PASSIVE (PASV)
	 * This command requests the server-DTP to "listen" on a data port (which is not
	 * its default data port) and to wait for a connection rather than initiate one
	 * upon receipt of a transfer command.  The response to this command includes the
	 * host and port address this server is listening on.
	 * @param mode
	 * @roseuid 3E1D195F01E1
	 */
	public void setPassiveMode(boolean mode) {
        _passiveMode = mode;		
	}
	
	/**
	 * Parse the remote host response.
	 * @return int
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195F0209
	 */
	public int readServerResponse() throws Exception {
        int             iTemp;
        int             continuingCode = -1;
        int             currnetCode = -1;
        StringBuffer    replyBuf = new StringBuffer(32);
        String          response;
        _lstServerResponse.removeAllElements();
        while (true) {
            while ((iTemp = _serverSideInput.read()) != -1) {
                 if (iTemp == '\r' && ((iTemp = _serverSideInput.read()) != '\n'))
                    replyBuf.append('\r');
                 replyBuf.append( (char)iTemp );
                 if (iTemp == '\n')
                      break;
            }
            response = replyBuf.toString();
            replyBuf.setLength(0);
            try {
                 currnetCode = Integer.parseInt(response.substring(0, 3));
            }
            catch (NumberFormatException e) {
                 currnetCode = -1;
            }
            catch (StringIndexOutOfBoundsException e) {
                 continue;
            }
            _lstServerResponse.addElement(response);
            if (continuingCode != -1) {
                 if (currnetCode != continuingCode || (response.length() >= 4 && response.charAt(3) == '-'))
                      continue;
                  continuingCode = -1;
                  break;
            }
            if (response.length() < 4 || response.charAt(3) != '-')
                break;
             continuingCode = currnetCode;
             continue;
       }
       return _returnCode = currnetCode;		
	}
	
	/**
	 * @return int
	 * @roseuid 3E1D195F02C7
	 */
	public int getReturnCode() {
        return _returnCode;		
	}
	
	/**
	 * @return String
	 * @roseuid 3E1D195F02E5
	 */
	public String receiveResponse() {
        return receiveResponse( true );		
	}
	
	/**
	 * @return String
	 * @roseuid 3E1D195F030E
	 */
	private String receiveResponseWithNoReset() {
        return receiveResponse( false );		
	}
	
	/**
	 * @param isReset
	 * @return String
	 * @roseuid 3E1D195F0336
	 */
	private String receiveResponse(boolean isReset) {
        String response = new String();
        for(int i = 0;i < _lstServerResponse.size();i++)
            response += _lstServerResponse.elementAt( i );
        if (isReset)
            _lstServerResponse = new Vector( 1 );
        return response;		
	}
	
	/**
	 * @param strCommand
	 * @roseuid 3E1D195F035E
	 */
	private void sendServer(String strCommand) {
       _serverSideOutput.println( strCommand );		
	}
	
	/**
	 * check whether  the server connection is opened
	 * @return boolean
	 * @roseuid 3E1D195F0390
	 */
	private boolean serverConnectionOpen() {
       return _serverSocket != null;		
	}
	
	/**
	 * @param strCommand
	 * @return int
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195F03AE
	 */
	private int sendCurrentCommand(String strCommand) throws Exception {
       _currentCommand = strCommand;
       int iReply;
       if (_replyPending && readResponse() == _FTP_ERROR)
            logInfo(" Error reading current pending reply\n");
       _replyPending = false;
       do {
           sendServer(strCommand);
           iReply = readResponse();
       } while (iReply == _FTP_TRY_AGAIN);
       return iReply;		
	}
	
	/**
	 * Method to check the current command for validity.
	 * @param strCommand
	 * @throws java.lang.Exception
	 * @roseuid 3E1D196000B6
	 */
	private void sendCurrentCommandCheck(String strCommand) throws Exception {
        if (sendCurrentCommand( strCommand ) == _FTP_SUCCESSFULL)
            return;
        logError( strCommand + " failed!" );
        throw new XRuntime( _module, strCommand + " failed." );		
	}
	
	/**
	 * LOGOUT (QUIT)
	 * This command terminates a USER and if file transfer is not in progress, the
	 * server closes the control connection.  If file transfer is in progress, the
	 * connection will remain open for result response and the server will then close
	 * it. If the user-process is transferring files for several USERs but does not
	 * wish to close and then reopen connections for each, then the REIN command
	 * should be used instead of QUIT. An unexpected close on the control connection
	 * will cause the server to take the effective action of an abort (ABOR) and a
	 * logout (QUIT).
	 * @throws java.lang.Exception
	 * @roseuid 3E1D19600174
	 */
	public void exitServer() throws Exception {
       if (!serverConnectionOpen())
            return;
       sendCurrentCommand( "QUIT" );
       if (!serverConnectionOpen())
            return;
       _serverSocket.close();
       _serverSocket     = null;
       _serverSideInput  = null;
       _serverSideOutput = null;
       logInfo( "Exiting FTP Server. " +  receiveResponse());		
	}
	
	/**
	 * method for processing the response codes and to throw exception where needed
	 * @return int
	 * @throws java.lang.Exception
	 * @roseuid 3E1D19600229
	 */
	private int readResponse() throws Exception {
       _returnCode = readServerResponse();
       switch (_returnCode / 100) {
       case 1:
           _replyPending = true;

       case 2: // Do nothing, pass through.

       case 3:
           return _FTP_SUCCESSFULL;

       case 4:
            if (_returnCode == 421) {
                logError( "Connection shut down" );
                throw new XRuntime( _module, "Connection shut down" );
            }
            if (_returnCode == 425) {
                logError( "Can't open data connection." );
                throw new XRuntime( _module, "Can't open data connection." );
            }
            if (_returnCode == 426) {
                logError( "Connection closed; transfer aborted." );
                throw new XRuntime( _module, "Connection closed; transfer aborted." );
            }
            if (_returnCode == 450) {
                logError( "Requested file action not taken. File unavailable (e.g., file busy). Please try later." );
                throw new XRuntime( _module, "Requested file action not taken. File unavailable (e.g., file busy). Please try later." );
            }
            if (_returnCode == 451) {
                logError( "Requested action aborted: local error in processing. Please try later." );
                throw new XRuntime( _module, "Requested action aborted: local error in processing. Please try later." );
            }
            if (_returnCode == 452) {
                logError( "Requested action not taken. Insufficient storage space in system." );
                throw new XRuntime( _module, "Requested action not taken. Insufficient storage space in system." );
            }
            return _FTP_ERROR;
       case 5:
            if (_returnCode == 500) {
                logError( "Syntax error or Command unrecognized. is command line too long?" );
                throw new XRuntime( _module, "Syntax error or Command unrecognized. is command line too long?" );
            }
            if (_returnCode == 501) {
                logError( "Syntax error in parameters or arguments." );
                throw new XRuntime( _module, "Syntax error in parameters or arguments." );
            }
            if (_returnCode == 502) {
                logError( "Command not implemented." );
                throw new XRuntime( _module, "Command not implemented." );
            }
            if (_returnCode == 503) {
                logError( "Bad sequence of commands." );
                throw new XRuntime( _module, "Bad sequence of commands." );
            }
            if (_returnCode == 504) {
                logError( "Command not implemented for that parameter." );
                throw new XRuntime( _module, "Command not implemented for that parameter." );
            }
            if (_returnCode == 532) {
                logError( "No account defined for user " + _userName );
                throw new XRuntime( _module, "No account defined for user " + _userName );
            }
            if (_returnCode == 550) {
                logError( "File not found or Access denied!" );
                throw new XRuntime( _module, "File not found or Access denied!" );
            }
            if (_returnCode == 551) {
                logError( "Requested action aborted: page type unknown." );
                throw new XRuntime( _module, "Requested action aborted: page type unknown." );
            }
            if (_returnCode == 552) {
                logError( "Requested file action aborted. Exceeded storage allocation (for current directory or dataset)." );
                throw new XRuntime( _module, "Requested file action aborted. Exceeded storage allocation (for current directory or dataset)." );
            }
            if (_returnCode == 553) {
                logError( "Requested action not taken. File name not allowed. or Permission denied!" );
                throw new XRuntime( _module, "Requested action not taken. File name not allowed. or Permission denied!" );
            }
        }
        return _FTP_ERROR;		
	}
	
	/**
	 * @param strCommand
	 * @throws java.lang.Exception
	 * @roseuid 3E1D196002E7
	 */
	private void openDataConnection(String strCommand) throws Exception {
        ServerSocket portSocket = null;
        String       portCmd = null;
        byte         addr[] = InetAddress.getLocalHost().getAddress();
        if (_passiveMode) {
            processPassiveMode( strCommand, portCmd, addr, portSocket );
            return;
        }
        portCmd = "PORT ";
        for (int i = 0; i < addr.length; i++)
            portCmd = portCmd + (addr[i] & 0xFF) + ",";

        try {
            portSocket = new ServerSocket(20000);
            portCmd = portCmd + ((portSocket.getLocalPort() >>> 8) & 0xff) + "," + (portSocket.getLocalPort() & 0xff);
            if (sendCurrentCommand( portCmd ) == _FTP_ERROR) {
                logError( "Error while opening port " + portCmd);
                throw new XRuntime( _module, "Error while opening port" + portCmd );
            }
            if (sendCurrentCommand( strCommand ) == _FTP_ERROR) {
                logError( strCommand + " falied!" );
                throw new XRuntime( _module, strCommand + " failed.");
            }
            _socket = portSocket.accept();
        }
        finally {
            if(portSocket != null)
                portSocket.close();
        }
        _socket = portSocket.accept();
        portSocket.close();		
	}
	
	/**
	 * @param strCommand
	 * @param portCmd
	 * @param addr
	 * @param portSocket
	 * @param addr[]
	 * @throws java.lang.Exception
	 * @roseuid 3E1D196003A5
	 */
	private void processPassiveMode(String strCommand, String portCmd, byte addr[], ServerSocket portSocket) throws Exception {
        try {
            receiveResponse();
            if (sendCurrentCommand("PASV") == _FTP_ERROR) {
                logError( "Passivate failed! " );
                throw new XRuntime( _module, "Passivate failed! " );
            }
            String reply = receiveResponseWithNoReset();
            reply = reply.substring(reply.lastIndexOf("(")+1, reply.lastIndexOf(")"));
            StringTokenizer st = new StringTokenizer(reply, ",");
            String[] nums = new String[6];
            int i = 0;
            while(st.hasMoreElements()) {
                nums[i] = st.nextToken();
                i++;
            }
            // Format ip address
            String ipaddress = nums[0] + "." + nums[1] + "." + nums[2] + "." + nums[3];
            int firstbits = Integer.parseInt(nums[4]) << 8;
            int lastbits = Integer.parseInt(nums[5]);
            int port = firstbits + lastbits;
            if (ipaddress == null || port == 0) {
                 logError( "Passivate failed! " );
                 throw new XRuntime( _module, "Passivate failed! " );
            }
            _socket = new Socket(ipaddress, port);
            if (sendCurrentCommand(strCommand) == _FTP_ERROR) {
                logError( strCommand + " failed!" );
                throw new XRuntime( _module, strCommand + " failed!" );
            }
        }
        catch (XRuntime xr) {
            portCmd = "PORT ";
            for (int i=0; i<addr.length; i++)
                portCmd = portCmd + (addr[i] & 0xFF) + ",";
            try {
                portSocket = new ServerSocket( 20000 );
                portCmd = portCmd + ((portSocket.getLocalPort() >>> 8) & 0xff) + "," + (portSocket.getLocalPort() & 0xff);
                if (sendCurrentCommand( portCmd ) == _FTP_ERROR) {
                    logError( "Error while opening port " + portCmd );
                    throw new XRuntime( _module, "Error while opening port " + portCmd );
                }
                if (sendCurrentCommand(strCommand) == _FTP_ERROR) {
                    logError( strCommand + " failed!" );
                    throw new XRuntime( _module, strCommand + " failed!" );
                }
                _socket = portSocket.accept();
            }
            finally {
                if (portSocket != null)
                    portSocket.close();
            }
            _socket = portSocket.accept();
            portSocket.close();
        }		
	}
	
	/**
	 * Open socket connect to the remote host.
	 * @param host
	 * @param port
	 * @throws java.lang.Exception
	 * @roseuid 3E1D196100CC
	 */
	private void openServer(String host, int port) throws Exception {
        try {
            if (_serverSocket != null)
                exitServer();
            _serverSocket     = new Socket( host, port );
            _serverSideOutput = new PrintWriter(new BufferedOutputStream(_serverSocket.getOutputStream()), true);
            _serverSideInput  = new BufferedInputStream(_serverSocket.getInputStream());
            if (readResponse() != _FTP_ERROR)
                return;
            logError( "Error while opening FTP Server " );
            throw new XRuntime( _module, "Error while opening FTP Server " );
        }
        catch( java.net.UnknownHostException uhe ) {
            uhe.printStackTrace();
            logError( "Host name \"" + host + "\" not found. Please check." );
            throw new XRuntime( _module, "Host name \"" + host + "\" not found. Please check." );
        }
        catch( IllegalArgumentException iae ) {
            iae.printStackTrace();
            logError( "Port number specified is not a valid one. Please check. " + port );
            throw new XRuntime( _module, "Port number specified is not a valid one. Please check. " + port );
        }		
	}
	
	/**
	 * RETRIEVE (RETR)
	 * This command causes the server-DTP to transfer a copy of the file, specified in
	 * the pathname, to the server- or user-DTP at the other end of the data
	 * connection.  The status and contents of the file at the server site shall be
	 * unaffected.Retrieve Ascii File
	 * // retrieve an Ascii file
	 * @param fileName
	 * @throws java.lang.Exception
	 * @roseuid 3E1D19610194
	 */
	public void getAsciiFile(String fileName) throws Exception {
        setFileSocket( fileName, true );
        BufferedReader fInputFile = new BufferedReader( new InputStreamReader( _socket.getInputStream() ));
        logInfo( _currentCommand );
        logInfo( receiveResponse() );
        PrintWriter fOutPutFile = new PrintWriter(new BufferedWriter(new FileWriter( fileName )));
        char c[] = new char[4096];
        for (int i; (i = fInputFile.read(c)) != -1; fOutPutFile.write(c,0,i))
            continue;
        fInputFile.close();
        fOutPutFile.close();
        logInfo( receiveResponse() );		
	}
	
	/**
	 * RETRIEVE (RETR)
	 * This command causes the server-DTP to transfer a copy of the file, specified in
	 * the pathname, to the server- or user-DTP at the other end of the data
	 * connection.  The status and contents of the file at the server site shall be
	 * unaffected.Retrieve Ascii File
	 * // retrieve a Binary file
	 * @param fileName
	 * @throws java.lang.Exception
	 * @roseuid 3E1D19610252
	 */
	public void getBinaryFile(String fileName) throws Exception {
        setFileSocket( fileName, false );
        BufferedInputStream fInputFile = new BufferedInputStream( _socket.getInputStream() );
        logInfo( _currentCommand );
        logInfo( receiveResponse() );
        BufferedOutputStream fOutputFile = new BufferedOutputStream(new FileOutputStream(fileName));
        byte b[] = new byte[4096];
        for (int i; (i = fInputFile.read( b )) != -1;fOutputFile.write(b,0,i));
        fInputFile.close();
        fOutputFile.close();
        logInfo( receiveResponse() );		
	}
	
	/**
	 * friendly internal method to get the Socket for file retrieval
	 * @param fileName
	 * @param isAscii
	 * @throws java.lang.Exception
	 * @roseuid 3E1D1961031A
	 */
	private void setFileSocket(String fileName, boolean isAscii) throws Exception {
        logInfo( receiveResponse() );
        setPassiveMode( true );
        if (isAscii)
            setAsciiMode();
        else
            setBinaryMode();
        logInfo( _currentCommand );
        logInfo( receiveResponse() );
        openDataConnection("RETR " + fileName);		
	}
	
	/**
	 * Login to remote host and retreive the indicated file from the remote host.
	 * @param fileName
	 * @throws java.lang.Exception
	 * @roseuid 3E1D196103E3
	 */
	public void sendBinaryFile(String fileName) throws Exception {
        logInfo( receiveResponse() );
        setPassiveMode(true);
        String localFile = fileName;
        setBinaryMode();
        logInfo( _currentCommand );
        BufferedOutputStream fOutputFile = putBinary( localFile );
        logInfo( _currentCommand );
        logInfo( receiveResponse() );
        BufferedInputStream fInputFile = new BufferedInputStream(new FileInputStream( localFile ));
        byte b[] = new byte[1024];
        for (int j; (j = fInputFile.read(b)) != -1; fOutputFile.write(b,0,j))
            continue;
        fInputFile.close();
        fOutputFile.flush();
        fOutputFile.close();
        logInfo( receiveResponse() );
        // Shut down
        exitServer();
        logInfo( _currentCommand );
        logInfo( receiveResponse() );		
	}
	
	/**
	 * Login to remote host and retreive the indicated file from the remote host.
	 * @param fileName
	 * @throws java.lang.Exception
	 * @roseuid 3E1D196200D7
	 */
	public void sendAsciiFile(String fileName) throws Exception {
        logInfo( receiveResponse() );
        setPassiveMode( true );
        String strLocalFile = fileName;
        setAsciiMode();
        logInfo( _currentCommand );
        BufferedWriter fOutputFile = putAscii( strLocalFile );
        logInfo( _currentCommand );
        logInfo( receiveResponse() );
        BufferedInputStream fInputFile = new BufferedInputStream(new FileInputStream( strLocalFile ));
        for (int j; (j = fInputFile.read()) != -1; fOutputFile.write(j))
            continue;
        fInputFile.close();
        fOutputFile.flush();
        fOutputFile.close();
        logInfo( receiveResponse());
        // Shut down
        exitServer();
        logInfo( _currentCommand );
        logInfo( receiveResponse() );		
	}
	
	/**
	 * @param info
	 * @roseuid 3E1D19620195
	 */
	private void logInfo(String info) {
        if (_logFileMgr == null)
            System.out.println( _module + " Info : " + info);
        else
            _logFileMgr.logInfo( info );		
	}
	
	/**
	 * @param error
	 * @roseuid 3E1D196201C7
	 */
	private void logError(String error) {
        if (_logFileMgr == null)
            System.out.println( _module + " Error : " + error);
        else
            _logFileMgr.logError( error );		
	}
	
	/**
	 * @param args[]
	 * @throws java.lang.Exception
	 * @roseuid 3E1D196201F9
	 */
	private static void main(String args[]) throws Exception {
        // connect and login to ftp server
        String hostName = "ftp.addvaltech.com";
        int port = 21;
        String userName = "sia";
        String password = "jarj-yoi";
/*
        if (args.length > 0)
            hostName = args[0];
        else if (args.length > 1)
            port = Integer.parseInt( args[1] );
        else if (args.length > 2)
            userName = args[2];
        else if (args.length > 3)
            password = args[3];
*/
        FTP ftp = new FTP(hostName, port, userName, password);
        try {
            System.out.println( ftp.getCurrentDirectory());
            ftp.changeDirectoryTo( "/cnos" );
            String[] files = ftp.getFileNames();
            System.out.println( "\n***********    Files    ******************" );
            // print the fileNames in current directory
            for (int i=0; i<files.length; i++)
                System.out.println( files[i] );

            // print the sub Directory Names of current directory
            String[] dirs = ftp.getDirectoryNames();
            System.out.println( "\n*********** Directories ******************" );
            for (int i=0; i<dirs.length; i++)
                System.out.println( dirs[i] );
            if (files.length == 0)
                return;
            //retrieve an Ascii file
//            ftp.getAsciiFile( files[0] );
            // retrieve a Binary file
//            ftp.getBinaryFile( files[0] );
        }
        finally {
            ftp.exitServer();
        }		
	}
}
