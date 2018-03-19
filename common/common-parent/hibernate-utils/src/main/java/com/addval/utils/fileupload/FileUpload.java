//Source file: E:\\projects\\common\\source\\src\\com\\addval\\utils\\fileupload\\FileUpload.java

package com.addval.utils.fileupload;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import com.addval.utils.XRuntime;

public class FileUpload
{
    protected byte _binArray[];
    private int _totalBytes;
    private int _currentIndex;
    private int _startData;
    private int _endData;
    private String _boundary;
    private String _filename;
    private long _totalMaxFileSize;
    private long _maxFileSize;
    private boolean _denyPhysicalPath;
    private boolean _forcePhysicalPath;
    private String _contentDisposition;
    private int _counter;
    private static final transient String _module = "FileUpload";
    protected HttpServletRequest _request;
    protected HttpServletResponse _response;
    protected ServletContext _application;
    private Files _files;
    private Vector _deniedFilesList;
    private Vector _allowedFilesList;
    private Hashtable _parameters;

    /**
     * @roseuid 3B7D02EE021F
     */
    public FileUpload()
    {
        _totalBytes         = 0;
        _currentIndex       = 0;
        _startData          = 0;
        _endData            = 0;
        _boundary           = new String();
        _totalMaxFileSize   = 0L;
        _maxFileSize        = 0L;
        _deniedFilesList    = new Vector();
        _allowedFilesList   = new Vector();
        _denyPhysicalPath   = false;
        _forcePhysicalPath  = false;
        _contentDisposition = new String();
        _files              = new Files();
        _parameters         = new Hashtable();
        _counter            = 0;
    }

    /**
     * @param config
     * @throws javax.servlet.ServletException
     * @roseuid 3B7D02EE0233
     */
    public final void init(ServletConfig config) throws ServletException
    {
        _application = config.getServletContext();
    }

    /**
     * @param request
     * @param response
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     * @roseuid 3B7D02EE02AC
     */
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        _request    = request;
        _response   = response;
    }

    /**
     * @param config
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @roseuid 3B7D02EE0356
     */
    public final void initialize(ServletConfig config, HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
        _application    = config.getServletContext();
        _request        = request;
        _response       = response;
    }

    /**
     * @param pageContext
     * @throws javax.servlet.ServletException
     * @roseuid 3B7D02EF002C
     */
    public final void initialize(PageContext pageContext) throws ServletException
    {
        _application    = pageContext.getServletContext();
        _request        = (HttpServletRequest)pageContext.getRequest();
        _response       = (HttpServletResponse)pageContext.getResponse();
    }

    /**
     * @param application
     * @param session
     * @param request
     * @param response
     * @param out
     * @throws javax.servlet.ServletException
     * @roseuid 3B7D02EF0086
     */
    public final void initialize(ServletContext application, HttpSession session, HttpServletRequest request, HttpServletResponse response, JspWriter out) throws ServletException
    {
        _application    = application;
        _request        = request;
        _response       = response;
    }

    /**
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     * @roseuid 3B7D02EF01D1
     */
    public void upload() throws IOException, ServletException
    {
        int totalRead       = 0;
        int readBytes       = 0;
        long totalFileSize  = 0L;
        boolean found       = false;
        String dataHeader   = new String();
        String fieldName    = new String();
        String fileName     = new String();
        String fileExt      = new String();
        String filePathName = new String();
        String contentType  = new String();
        String contentDisp  = new String();
        String typeMIME     = new String();
        String subTypeMIME  = new String();
        boolean isFile      = false;
        _totalBytes         = _request.getContentLength();
        _binArray           = new byte[_totalBytes];
        for (; totalRead < _totalBytes; totalRead += readBytes) {
            try {
                _request.getInputStream();
                readBytes = _request.getInputStream().read( _binArray, totalRead, (_totalBytes - totalRead) );
            }
            catch( Exception e ) {
                throw new XRuntime( _module, "Unable to upload the file - " + fileName );
            }
        }

        for (; !found && _currentIndex < _totalBytes; _currentIndex++) {
            if (_binArray[_currentIndex] == 13)
                found = true;
            else
                _boundary = _boundary + (char)_binArray[_currentIndex];
        }

        if (_currentIndex == 1)
            return;
        _currentIndex++;
        do {
            if( _currentIndex >= _totalBytes )
                break;
            dataHeader = getDataHeader();
            _currentIndex = _currentIndex + 2;
            isFile = dataHeader.indexOf( "filename" ) > 0;
            fieldName = getDataFieldValue( dataHeader, "name" );
            if (isFile) {
                filePathName    = getDataFieldValue( dataHeader, "filename" );
                fileName        = getFilePath( filePathName );
                fileExt         = getFileExt( fileName );
                contentType     = getContentType( dataHeader );
                contentDisp     = getContentDisp( dataHeader );
                typeMIME        = getTypeMIME( contentType );
                subTypeMIME     = getSubTypeMIME( contentType );
            }
            getDataSection();
            if (isFile && fileName.length() > 0) {
                if (_deniedFilesList.contains( fileExt ))
                    throw new SecurityException("The extension of the file is denied to be uploaded. " + fileExt);
                if (!_allowedFilesList.isEmpty() && !_allowedFilesList.contains( fileExt ))
                    throw new SecurityException( "The extension of the file is not allowed to be uploaded. " + fileExt);
                if (_maxFileSize > (long)0 && (long)((_endData - _startData) + 1) > _maxFileSize)
                    throw new SecurityException("Size exceeded for this file : " + fileName );

                totalFileSize += (_endData - _startData) + 1;
                if (_totalMaxFileSize > (long)0 && totalFileSize > _totalMaxFileSize)
                    throw new SecurityException( "Total File Size (" + totalFileSize + ") exceeded maximum permitted (" + _totalMaxFileSize );
            }
            if (isFile) {
                FileAction newFile = new FileAction();
                setFileName( fileName );
                newFile.setParent( this );
                newFile.setFieldName( fieldName );
                newFile.setFileName( fileName );
                newFile.setFileExt( fileExt );
                newFile.setFilePathName( filePathName );
                newFile.setIsMissing( filePathName.length() == 0 );
                newFile.setContentType( contentType );
                newFile.setContentDisp( contentDisp );
                newFile.setTypeMIME( typeMIME );
                newFile.setSubTypeMIME( subTypeMIME );
                if (contentType.indexOf( "application/x-macbinary" ) > 0)
                    _startData = _startData + 128;
                newFile.setSize( (_endData - _startData) + 1 );
                newFile.setStartData( _startData );
                newFile.setEndData( _endData );
                _files.addFile( newFile );
            }
            else {
                String value = new String( _binArray, _startData, (_endData - _startData) + 1 );
                if (fieldName== null)
                    throw new IllegalArgumentException( "The name of an element cannot be null." );
                if (_parameters.containsKey( fieldName )) {
                    Hashtable values = (Hashtable)_parameters.get( fieldName );
                    values.put( new Integer( values.size() ), value );
                }
                else {
                    Hashtable values = new Hashtable();
                    values.put( new Integer( 0 ), value );
                    _parameters.put( fieldName, values );
                    _counter++;
                }
            }
            if ((char)_binArray[_currentIndex + 1] == '-')
                break;
            _currentIndex = _currentIndex + 2;
        } while (true);
    }

    /**
     * @param destPathName
     * @param fromPathName
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     * @roseuid 3B7D02EF022B
     */
    public void saveAs(String fromPathName, String destPathName) throws IOException, ServletException
    {
        save( fromPathName, destPathName, 0  );
    }

    /**
     * @param destPathName
     * @param option
     * @param fromPathName
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     * @roseuid 3B7D02EF02A3
     */
    private void save(String fromPathName, String destPathName, int option) throws IOException, ServletException
    {
        if (destPathName == null) {
           throw new IllegalArgumentException( "There is no specified destination file." );
       }
        else {
            _files.getFile(fromPathName).saveAs( destPathName, option);
        }
    }

    /**
     * @return int
     * @roseuid 3B7D02EF034D
     */
    public int getSize()
    {
        return _totalBytes;
    }

    /**
     * @param index
     * @return byte
     * @roseuid 3B7D02EF036B
     */
    public byte getBinaryData(int index)
    {
        byte retval;
        try {
            retval = _binArray[index];
        }
        catch(Exception e) {
            throw new ArrayIndexOutOfBoundsException("Index out of range.");
        }
        return retval;
    }

    /**
     * @return com.addval.utils.fileupload.Files
     * @roseuid 3B7D02EF0389
     */
    public Files getFiles()
    {
        return _files;
    }

    /**
     * @param dataHeader
     * @param fieldName
     * @return java.lang.String
     * @roseuid 3B7D02EF03B1
     */
    private String getDataFieldValue(String dataHeader, String fieldName)
    {
        String token    = new String();
        String value    = new String();
        int pos         = 0;
        int i           = 0;
        int start       = 0;
        int end         = 0;
        token           = String.valueOf((new StringBuffer(String.valueOf(fieldName))).append("=").append('"'));
        pos             = dataHeader.indexOf(token);
        if (pos > 0) {
            i = pos + token.length();
            start = i;
            token = "\"";
            end = dataHeader.indexOf( token, i );
            if (start > 0 && end > 0)
                value = dataHeader.substring( start, end );
        }
        return value;
    }

    /**
     * @param fileName
     * @return java.lang.String
     * @roseuid 3B7D02F00005
     */
    private String getFileExt(String fileName)
    {
        String value = new String();
        int start = 0;
        int end = 0;
        if (fileName == null)
            return null;
        start   = fileName.lastIndexOf( 46 ) + 1; // position of .
        end     = fileName.length();
        value   = fileName.substring( start, end );
        if (fileName.lastIndexOf( 46 ) > 0)
            return value.toLowerCase();
        else
            return "";
    }

    /**
     * @param dataHeader
     * @return java.lang.String
     * @roseuid 3B7D02F00023
     */
    private String getContentType(String dataHeader)
    {
        String token = new String();
        String value = new String();
        int start   = 0;
        int end     = 0;
        token       = "Content-Type:";
        start       = dataHeader.indexOf(token) + token.length();
        if (start != -1) {
            end     = dataHeader.length();
            value   = dataHeader.substring( start, end );
        }
        return value;
    }

    /**
     * @param ContentType
     * @return java.lang.String
     * @roseuid 3B7D02F00042
     */
    private String getTypeMIME(String ContentType)
    {
        String value = new String();
        int pos = 0;
        pos = ContentType.indexOf( "/" );
        if (pos != -1)
            return ContentType.substring( 1, pos );
        else
            return ContentType;
    }

    /**
     * @param ContentType
     * @return java.lang.String
     * @roseuid 3B7D02F0006A
     */
    private String getSubTypeMIME(String ContentType)
    {
        String value = new String();
        int start = 0;
        int end = 0;
        start = ContentType.indexOf( "/" ) + 1;
        if (start != -1) {
            end = ContentType.length();
            return ContentType.substring( start, end );
        }
        else {
            return ContentType;
        }
    }

    /**
     * @param dataHeader
     * @return java.lang.String
     * @roseuid 3B7D02F00088
     */
    private String getContentDisp(String dataHeader)
    {
        String value = new String();
        int start   = 0;
        int end     = 0;
        start       = dataHeader.indexOf( ":" ) + 1;
        end         = dataHeader.indexOf( ";" );
        value       = dataHeader.substring( start, end );
        return value;
    }

    /**
     * @roseuid 3B7D02F000B0
     */
    private void getDataSection()
    {
        boolean found       = false;
        String dataHeader   = new String();
        int searchPos       = _currentIndex;
        int keyPos          = 0;
        int boundaryLen     = _boundary.length();
        _startData          = _currentIndex;
        _endData            = 0;
        while (true) {
            if (searchPos >= _totalBytes)
                break;
            if (_binArray[searchPos] == (byte)_boundary.charAt( keyPos )) {
                if(keyPos == boundaryLen - 1) {
                    _endData = ((searchPos - boundaryLen) + 1) - 3;
                    break;
                }
                searchPos++;
                keyPos++;
            } else {
                searchPos++;
                keyPos = 0;
            }
        }
        _currentIndex = _endData + boundaryLen + 3;
    }

    /**
     * @return java.lang.String
     * @roseuid 3B7D02F000BA
     */
    private String getDataHeader()
    {
        int start       = _currentIndex;
        int end         = 0;
        int len         = 0;
        boolean found   = false;
        while (!found)
            if (_binArray[_currentIndex] == 13 && _binArray[_currentIndex + 2] == 13) {
                found = true;
                end = _currentIndex - 1;
                _currentIndex = _currentIndex + 2;
            } else {
                _currentIndex++;
            }
        String dataHeader = new String( _binArray, start, (end - start) + 1 );
        return dataHeader;
    }

    /**
     * @param filePathName
     * @return java.lang.String
     * @roseuid 3B7D02F000C4
     */
    private String getFilePath(String filePathName)
    {
        String token    = new String();
        String value    = new String();
        int pos         = 0;
        int i           = 0;
        int start       = 0;
        int end         = 0;
        pos = filePathName.lastIndexOf( 47 );
        if (pos != -1)
            return filePathName.substring( pos + 1, filePathName.length() );
        pos = filePathName.lastIndexOf( 92 );
        if (pos != -1)
            return filePathName.substring( pos + 1, filePathName.length() );
        else
            return filePathName;
    }

    /**
     * @return java.lang.String
     * @roseuid 3B7D02F000EC
     */
    public String getFileName()
    {
        return _filename;
    }

    /**
     * @param deniedFilesList
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     * @roseuid 3B7D02F000F6
     */
    public void setDeniedFilesList(String deniedFilesList) throws IOException, ServletException
    {
        String ext = "";
        if (deniedFilesList != null) {
            ext = "";
            for (int i = 0; i < deniedFilesList.length(); i++)
                if (deniedFilesList.charAt( i ) == ',') {
                    if (!_deniedFilesList.contains( ext ))
                        _deniedFilesList.addElement( ext );
                    ext = "";
                }
                else {
                    ext = ext + deniedFilesList.charAt( i );
                }

            if (ext != "")
                _deniedFilesList.addElement( ext );
        }
        else {
            _deniedFilesList = null;
        }
    }

    /**
     * @param allowedFilesList
     * @roseuid 3B7D02F0016E
     */
    public void setAllowedFilesList(String allowedFilesList)
    {
        String ext = "";
        if (allowedFilesList != null) {
            ext = "";
            for (int i = 0; i < allowedFilesList.length(); i++)
                if (allowedFilesList.charAt( i ) == ',') {
                    if (!_allowedFilesList.contains( ext ))
                        _allowedFilesList.addElement( ext );
                    ext = "";
                }
                else {
                    ext = ext + allowedFilesList.charAt( i );
                }

            if (ext != "")
                _allowedFilesList.addElement( ext.toLowerCase() );
        }
        else {
            _allowedFilesList = null;
        }
    }

    /**
     * @param deny
     * @roseuid 3B7D02F00196
     */
    public void setDenyPhysicalPath(boolean deny)
    {
        _denyPhysicalPath = deny;
    }

    /**
     * @param force
     * @roseuid 3B7D02F001B4
     */
    public void setForcePhysicalPath(boolean force)
    {
        _forcePhysicalPath = force;
    }

    /**
     * @param contentDisposition
     * @roseuid 3B7D02F001D2
     */
    public void setContentDisposition(String contentDisposition)
    {
        _contentDisposition = contentDisposition;
    }

    /**
     * @param totalMaxFileSize
     * @roseuid 3B7D02F001FA
     */
    public void setTotalMaxFileSize(long totalMaxFileSize)
    {
        _totalMaxFileSize = totalMaxFileSize;
    }

    /**
     * @param maxFileSize
     * @roseuid 3B7D02F00218
     */
    public void setMaxFileSize(long maxFileSize)
    {
        _maxFileSize = maxFileSize;
    }

    /**
     * @param fileName
     * @roseuid 3B7D02F0024A
     */
    public void setFileName(String fileName)
    {
        _filename = fileName;
    }

    /**
     * @param filePathName
     * @param option
     * @return java.lang.String
     * @throws java.io.IOException
     * @roseuid 3B7D02F00268
     */
    public String getPhysicalPath(String filePathName, int option) throws IOException
    {
        String path             = new String();
        String fileName         = new String();
        String fileSeparator    = new String();
        boolean isPhysical      = false;
        fileSeparator           = System.getProperty("file.separator");
        if (filePathName == null)
            throw new IllegalArgumentException( "There is no specified destination file" );
        if (filePathName.equals( "" ))
            throw new IllegalArgumentException( "There is no specified destination file." );
        if (filePathName.lastIndexOf( "\\" ) >= 0) {
            path = filePathName.substring( 0, filePathName.lastIndexOf( "\\" ) );
            fileName = filePathName.substring( filePathName.lastIndexOf("\\") + 1 );
        }
        if (filePathName.lastIndexOf( "/" ) >= 0) {
            path = filePathName.substring( 0, filePathName.lastIndexOf( "/" ) );
            fileName = filePathName.substring( filePathName.lastIndexOf( "/" ) + 1 );
        }
        path = path.length() != 0 ? path : "/";
        File physicalPath = new File( path );
        if (physicalPath.exists())
            isPhysical = true;
        if (option == 0) {
            if (isVirtual( path )) {
                path = _application.getRealPath( path );
                if (path.endsWith( fileSeparator ))
                    path = path + fileName;
                else
                    path = String.valueOf( (new StringBuffer(String.valueOf(path))).append(fileSeparator).append(fileName) );
                return path;
            }
            if (isPhysical) {
                if (_denyPhysicalPath)
                    throw new IllegalArgumentException( "Physical path is denied." );
                else
                    return filePathName;
            }
            else {
                throw new IllegalArgumentException( "This path does not exist." );
            }
        }
        if (option == 1) {
            if (isVirtual( path )) {
                path = _application.getRealPath( path );
                if(path.endsWith(fileSeparator))
                    path = path + fileName;
                else
                    path = String.valueOf( (new StringBuffer( String.valueOf(path) )).append( fileSeparator ).append( fileName ));
                return path;
            }
            if (isPhysical)
                throw new IllegalArgumentException( "The path is not a virtual path.");
            else
                throw new IllegalArgumentException( "This path does not exist.");
        }
        if (option == 2) {
            if (isPhysical)
                if (_denyPhysicalPath)
                    throw new IllegalArgumentException( "Physical path is denied." );
                else
                    return filePathName;
            if (isVirtual( path ))
                throw new IllegalArgumentException( "The path is not a physical path." );
            else
                throw new IllegalArgumentException( "This path does not exist." );
        }
        else {
            return null;
        }
    }

    /**
     * @param destFilePathName
     * @throws java.io.IOException
     * @roseuid 3B7D02F002CC
     */
    public void uploadInFile(String destFilePathName) throws IOException
    {
        int intsize = 0;
        int pos = 0;
        int readBytes = 0;
        if (destFilePathName == null)
            throw new IllegalArgumentException( "There is no specified destination file (1025)." );
        if (destFilePathName.length() == 0)
            throw new IllegalArgumentException("There is no specified destination file (1025).");
        if (!isVirtual( destFilePathName ) && _denyPhysicalPath)
            throw new SecurityException( "Physical path is denied (1035)." );
        intsize = _request.getContentLength();
        _binArray = new byte[intsize];
        for (; pos < intsize; pos += readBytes)
            try {
                readBytes = _request.getInputStream().read( _binArray, pos, intsize - pos );
            }
            catch( Exception e ){
                throw new XRuntime( _module, "Unable to upload." );
            }

        if (isVirtual( destFilePathName ))
            destFilePathName = _application.getRealPath( destFilePathName );
        try {
            File file = new File( destFilePathName );
            FileOutputStream fileOut = new FileOutputStream( file );
            fileOut.write( _binArray );
            fileOut.close();
        }
        catch( Exception e ) {
            throw new XRuntime( _module, "The Form cannot be saved in the specified file (1030)." );
        }
    }

    /**
     * @param pathName
     * @return boolean
     * @roseuid 3B7D02F00327
     */
    private boolean isVirtual(String pathName)
    {
        if (_application.getRealPath(pathName) != null) {
            File virtualFile = new File( _application.getRealPath( pathName ) );
            return virtualFile.exists();
        } else {
            return false;
        }
    }

    /**
     * @param fileName
     * @return java.lang.String
     * @roseuid 3B7E1206017F
     */
    public String getFilePathName(String fileName)
    {
        String path = _files.getFile( fileName ).getFilePathName();
        return path;
    }

    /**
     * Delete a file in the Server
     * @param fileName - Name of the file to be deleted
     * @throws IOException
     * @throws ServletException
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     * @roseuid 3B7E51F200CE
     */
    public void deleteFile(String fileName) throws IOException, ServletException
    {
        if (fileName == null) {
            throw new IllegalArgumentException( "There is no specified destination file." );
        }
        fileName = getPhysicalPath( fileName, 1 );
        if (fileName == null)
            throw new IllegalArgumentException( "There is no specified destination file." );
        java.io.File file = new java.io.File( fileName );
        file.delete();
    }

    /**
     * @return java.util.Vector
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     * @roseuid 3B8F3C780301
     */
    public Vector getFileNames() throws IOException, ServletException
    {
        Vector fileNames = new Vector();
        for (Enumeration enumeration = _files.getEnumeration(); enumeration.hasMoreElements() ;)
            fileNames.add( (String) ((FileAction)enumeration.nextElement()).getFileName() );

        return fileNames;
    }
}
