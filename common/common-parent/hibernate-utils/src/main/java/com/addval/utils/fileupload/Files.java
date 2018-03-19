//Source file: E:\\projects\\common\\source\\src\\com\\addval\\utils\\fileupload\\Files.java

package com.addval.utils.fileupload;

import java.util.*;
import java.io.IOException;

public class Files 
{
    private int _counter;
    private Hashtable _files;
    private FileUpload _parent;
    private Hashtable _filesWithName;
    
    /**
     * @roseuid 3B7CEE3E026A
     */
    Files() 
    {
        _files = new Hashtable();
        _filesWithName = new Hashtable();
        _counter = 0;     
    }
    
    /**
     * @param newFile
     * @roseuid 3B7CEE3E0274
     */
    protected void addFile(FileAction newFile) 
    {
        if (newFile == null) {
            throw new IllegalArgumentException( "newFile cannot be null." );
        }
        else {
            _files.put( new Integer( _counter ), newFile );
            if (newFile.getFileName() == null) {
                throw new IllegalArgumentException( "File Name is not yet set!" );
            }
            else {
                _filesWithName.put( newFile.getFileName(), new Integer( _counter ) );
            }
            _counter++;
        }     
    }
    
    /**
     * @param index
     * @return com.addval.utils.fileupload.FileAction
     * @roseuid 3B7CEE3E029D
     */
    private FileAction getFile(int index) 
    {
        if (index < 0)
            throw new IllegalArgumentException( "File's index cannot be a negative value." );

        FileAction retval = (FileAction)_files.get(new Integer(index));

        if (retval == null)
            throw new IllegalArgumentException( "File's name is invalid or does not exist." );
        else
            return retval;     
    }
    
    /**
     * @return int
     * @roseuid 3B7CEE3E02C5
     */
    protected int getCount() 
    {
        return _counter;     
    }
    
    /**
     * @return long
     * @throws java.io.IOException
     * @roseuid 3B7CEE3E02CE
     */
    protected long getSize() throws IOException 
    {
        long tmp = 0L;
        for (int i = 0; i < _counter; i++)
            tmp += getFile(i).getSize();

        return tmp;     
    }
    
    /**
     * @return java.util.Collection
     * @roseuid 3B7CEE3E02F6
     */
    protected Collection getCollection() 
    {
        return _files.values();     
    }
    
    /**
     * @return java.util.Enumeration
     * @roseuid 3B7CEE3E0314
     */
    protected Enumeration getEnumeration() 
    {
        return _files.elements();     
    }
    
    /**
     * @param fileName
     * @return com.addval.utils.fileupload.FileAction
     * @roseuid 3B90BB2900E9
     */
    protected FileAction getFile(String fileName) 
    {
        if (fileName == null)
            throw new IllegalArgumentException( "File's Name is null!" );

        return getFile(((Integer)_filesWithName.get( fileName )).intValue());     
    }
}
