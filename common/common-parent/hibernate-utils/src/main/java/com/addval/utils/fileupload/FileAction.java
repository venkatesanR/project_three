//Source file: E:\\projects\\common\\source\\src\\com\\addval\\utils\\fileupload\\FileAction.java

package com.addval.utils.fileupload;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import javax.servlet.ServletException;
import com.addval.utils.XRuntime;

public class FileAction 
{
    private int _startData;
    private int _endData;
    private int _size;
    private String _fieldname;
    private String _filename;
    private String _fileExt;
    private String _filePathName;
    private String _contentType;
    private String _contentDisp;
    private String _typeMime;
    private String _subTypeMime;
    private String _contentString;
    private boolean _isMissing;
    private static final transient String _module = "FileAction";
    private FileUpload _parent;
    
    /**
     * @roseuid 3B7CEE3D0060
     */
    FileAction() 
    {
        _startData          = 0;
        _endData            = 0;
        _size               = 0;
        _fieldname          = new String();
        _filename           = new String();
        _fileExt            = new String();
        _filePathName       = new String();
        _contentType        = new String();
        _contentDisp        = new String();
        _typeMime           = new String();
        _subTypeMime        = new String();
        _contentString      = new String();
        _isMissing          = true;     
    }
    
    /**
     * @param destFilePathName
     * @throws java.io.IOException
     * @roseuid 3B7CEE3D006A
     */
    protected void saveAs(String destFilePathName) throws IOException 
    {
        saveAs( destFilePathName, 0 );     
    }
    
    /**
     * @param destFilePathName
     * @param optionSaveAs
     * @throws java.io.IOException
     * @roseuid 3B7CEE3D0165
     */
    protected void saveAs(String destFilePathName, int optionSaveAs) throws IOException 
    {
        String path = new String();
        path = _parent.getPhysicalPath( destFilePathName, optionSaveAs );
        if (path == null)
            throw new IllegalArgumentException( "There is no specified destination file." );
        try {
            java.io.File file = new java.io.File( path );
            FileOutputStream fileOut = new FileOutputStream( file );
            fileOut.write( _parent._binArray, _startData, _size );
            fileOut.close();
        }
        catch( IOException e ) {
            throw new XRuntime( _module, "File can't be saved." );
        }     
    }
    
    /**
     * @return boolean
     * @roseuid 3B7CEE3D0241
     */
    protected boolean isMissing() 
    {
        return _isMissing;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 3B7CEE3D024B
     */
    protected String getFieldName() 
    {
        return _fieldname;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 3B7CEE3D0255
     */
    protected String getFileName() 
    {
        return _filename;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 3B7CEE3D025F
     */
    protected String getFilePathName() 
    {
        return _filePathName;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 3B7CEE3D0269
     */
    protected String getFileExt() 
    {
        return _fileExt;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 3B7CEE3D0273
     */
    protected String getContentType() 
    {
        return _contentType;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 3B7CEE3D027D
     */
    protected String getContentDisp() 
    {
        return _contentDisp;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 3B7CEE3D0287
     */
    protected String getContentString() 
    {
        String strTMP = new String( _parent._binArray, _startData, _size );
        return strTMP;     
    }
    
    /**
     * @return java.lang.String
     * @throws java.io.IOException
     * @roseuid 3B7CEE3D0291
     */
    protected String getTypeMIME() throws IOException 
    {
        return _typeMime;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 3B7CEE3D02C3
     */
    protected String getSubTypeMIME() 
    {
        return _subTypeMime;     
    }
    
    /**
     * @return int
     * @roseuid 3B7CEE3D02CD
     */
    protected int getSize() 
    {
        return _size;     
    }
    
    /**
     * @return int
     * @roseuid 3B7CEE3D02D7
     */
    protected int getStartData() 
    {
        return _startData;     
    }
    
    /**
     * @return int
     * @roseuid 3B7CEE3D02E1
     */
    protected int getEndData() 
    {
        return _endData;     
    }
    
    /**
     * @param parent
     * @roseuid 3B7CEE3D02EB
     */
    protected void setParent(FileUpload parent) 
    {
        _parent = parent;     
    }
    
    /**
     * @param startData
     * @roseuid 3B7CEE3D033B
     */
    protected void setStartData(int startData) 
    {
        _startData = startData;     
    }
    
    /**
     * @param endData
     * @roseuid 3B7CEE3D0346
     */
    protected void setEndData(int endData) 
    {
        _endData = endData;     
    }
    
    /**
     * @param size
     * @roseuid 3B7CEE3D0359
     */
    protected void setSize(int size) 
    {
        _size = size;     
    }
    
    /**
     * @param isMissing
     * @roseuid 3B7CEE3D0364
     */
    protected void setIsMissing(boolean isMissing) 
    {
        _isMissing = isMissing;     
    }
    
    /**
     * @param fieldName
     * @roseuid 3B7CEE3D0377
     */
    protected void setFieldName(String fieldName) 
    {
        _fieldname = fieldName;     
    }
    
    /**
     * @param fileName
     * @roseuid 3B7CEE3D0382
     */
    protected void setFileName(String fileName) 
    {
        _filename = fileName;     
    }
    
    /**
     * @param filePathName
     * @roseuid 3B7CEE3D0395
     */
    protected void setFilePathName(String filePathName) 
    {
        _filePathName = filePathName;     
    }
    
    /**
     * @param fileExt
     * @roseuid 3B7CEE3D03A9
     */
    protected void setFileExt(String fileExt) 
    {
        _fileExt = fileExt;     
    }
    
    /**
     * @param contentType
     * @roseuid 3B7CEE3D03B4
     */
    protected void setContentType(String contentType) 
    {
        _contentType = contentType;     
    }
    
    /**
     * @param contentDisp
     * @roseuid 3B7CEE3D03C7
     */
    protected void setContentDisp(String contentDisp) 
    {
        _contentDisp = contentDisp;     
    }
    
    /**
     * @param TypeMime
     * @roseuid 3B7CEE3D03D2
     */
    protected void setTypeMIME(String TypeMime) 
    {
        _typeMime = TypeMime;     
    }
    
    /**
     * @param subTypeMime
     * @roseuid 3B7CEE3D03E5
     */
    protected void setSubTypeMIME(String subTypeMime) 
    {
        _subTypeMime = subTypeMime;     
    }
    
    /**
     * @param index
     * @return byte
     * @roseuid 3B7CEE3E0008
     */
    public byte getBinaryData(int index) 
    {
        if (_startData + index > _endData)
            throw new ArrayIndexOutOfBoundsException( "Index Out of range." );
        if (_startData + index <= _endData)
            return _parent._binArray[_startData + index];
        else
            return 0;     
    }
}
