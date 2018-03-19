//Source file: d:\\projects\\common\\source\\com\\addval\\utils\\ftp\\RemoteFile.java


package com.addval.utils.ftp;

import java.text.ParsePosition;
import java.util.Calendar;
import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Date;

class RemoteFile {
	private String _name = null;
	private long _size = 0;
	private boolean _isFile = false;
	private boolean _isDirectory = false;
	private String _time = "00:00";
	private static final String _months[] = {
	"january",
	"february",
	"march",
	"april",
	"may",
	"june",
	"july",
	"august",
	"september",
	"october",
	"november",
	"december"
	};
	private Date _date = null;
	
	/**
	 * @param ftp
	 * @param fileDetails
	 * @throws java.lang.Exception
	 * @roseuid 3E1D19520034
	 */
	public RemoteFile(FTP ftp, String fileDetails) throws Exception {
        populateDetails( ftp, fileDetails );		
	}
	
	/**
	 * @param ftp
	 * @param fileDetails
	 * @throws java.lang.Exception
	 * @roseuid 3E1D1952012E
	 */
	private void populateDetails(FTP ftp, String fileDetails) throws Exception {
        if (ftp.getOperatingSystem().startsWith( "Windows_NT version 5.0" ))
            populateWindows2000( fileDetails );
        else
           populateOtherOS( ftp, fileDetails );		
	}
	
	/**
	 * for windows 2000 systems, the file listing is as 
	 * 12-11-02  10:51AM                   27 CONFIG.SYS
	 * 12-11-02  10:40AM       <DIR>          Documents and Settings
	 * @param fileDetails
	 * @roseuid 3E1D195201F6
	 */
	private void populateWindows2000(String fileDetails) {
        String details = fileDetails;
        String[] words = new String[3];
        for (int i=0; i<3; i++)
            details = getNextWord( details, words, i );
        SimpleDateFormat sdf = new SimpleDateFormat( "mm-dd-yy" );
        try {
            _name = lTrim( details );
            _date = sdf.parse( words[0], new ParsePosition( 0 ) );
            _time = words[1];
            if (Character.isDigit( words[2].charAt( 0 ))) {
                _size = Long.parseLong( words[2] );
                setAsFile();
            }
            else {
                setAsDirectory();
            }
        }
        catch( NullPointerException npe ) {
            throw new RuntimeException( "FileList format is non-standard. Please use list() to view. " + fileDetails );
        }		
	}
	
	/**
	 * for non-windows 2000 systems, the file listing is as 
	 * lrwxrwxrwx   3 ftpuser  ftpusers        8 Sep 16 22:17 readmeFile -> README
	 * lrwxrwxrwx   9 ftpuser  ftpusers        8 Sep 16 22:17 armlinux -> linus/arm    
	 * -rwxrwxrwx  10 ftpuser  ftpusers        8 Sep 16 22:17 README
	 * drwxr-s---   2 korg     mirrors      4096 May 21  2001 for_mirrors_only
	 * (permissions  NoOfLinks  owner  group  size  month  date  time/year  name -> 
	 * link)
	 * @param ftp
	 * @param fileDetails
	 * @throws java.lang.Exception
	 * @roseuid 3E1D1952020A
	 */
	private void populateOtherOS(FTP ftp, String fileDetails) throws Exception {
        String details = fileDetails;
        String[] words = new String[8];
        for (int i=0; i<8; i++)
            details = getNextWord( details, words, i );
        Calendar cal = Calendar.getInstance();
        _name = lTrim( details );
        try {
            if (words[0].startsWith( "d" ))
                setAsDirectory();
            if (words[0].startsWith( "-" ))
                setAsFile();
            else if (words[0].startsWith( "l" ))
                translateLink( ftp );
            else
                return;
            _size = Long.parseLong( words[4] );
            cal.set(Calendar.DATE, getMonth( words[5] ) );
            cal.set(Calendar.DATE, Integer.parseInt( words[6] ) );
            if (words[7].indexOf( ":" ) != -1)
                _time = words[7];
            else
                cal.set(Calendar.YEAR, Integer.parseInt( words[7] ) );
            _date = cal.getTime();
        }
        catch( NullPointerException npe ) {
            throw new RuntimeException( "FileList format is non-standard. Please use list() to view. " + fileDetails );
        }		
	}
	
	/**
	 * friendly internal method to find out whether a link is File or Directory
	 * @param ftp
	 * @throws java.lang.Exception
	 * @roseuid 3E1D195202D3
	 */
	private void translateLink(FTP ftp) throws Exception {
        String fullName = getName();
        int i = fullName.indexOf( "->" );
        if (i == -1)
            return;
        _name = fullName.substring( 0, i ) ;
        String linkName = lTrim( fullName.substring( i + 2 ) );
        // save the current directory
        String currentDir = ftp.getCurrentDirectory();
        try {
            // try changing to the actual name of the link,
            // considering it as Directory
            ftp.changeDirectoryTo( linkName );
            setAsDirectory();
            // switch back to the current Directory
            ftp.changeDirectoryTo( currentDir );
        }
        catch(Exception e) {
            // as there is error of type access denied, the link actually is a File
            if (ftp.getReturnCode() == 550)
                setAsFile();
        }		
	}
	
	/**
	 * @roseuid 3E1D195203AF
	 */
	private void setAsFile() {
       _isFile = true;
       _isDirectory = false;		
	}
	
	/**
	 * @roseuid 3E1D195203C3
	 */
	private void setAsDirectory() {
       _isFile = false;
       _isDirectory = true;		
	}
	
	/**
	 * friendly internal method to convert a String month to Number
	 * @param monthName
	 * @return int
	 * @roseuid 3E1D195203D7
	 */
	private int getMonth(String monthName) {
        if (monthName.length()<3)
            throw new RuntimeException( "Wrong value for month " + monthName );
        for (int i=0; i<12; i++)
            if (_months[i].startsWith( monthName.toLowerCase() ))
                return i + 1;
        throw new RuntimeException( "Wrong value for month " + monthName );		
	}
	
	/**
	 * friendly internal method to get the next word
	 * @param input
	 * @param words
	 * @param count
	 * @param words[]
	 * @return String
	 * @roseuid 3E1D19530003
	 */
	private static String getNextWord(String input, String words[], int count) {
        // remove the leading spaces
        input = lTrim( input );
        CharacterIterator iter = new StringCharacterIterator( input );
        String word = "";
        // get the characters till finding a space
        for (char c = iter.first(); !Character.isWhitespace( c ); c=iter.next())
            word += c;
        if (words != null && !word.equals( "" ) && count != -1)
            words[count] = word;
        return input.substring( iter.getIndex() );		
	}
	
	/**
	 * method for removing the leading spaces
	 * @param input
	 * @return String
	 * @roseuid 3E1D1953002B
	 */
	private static String lTrim(String input) {
        CharacterIterator iter = new StringCharacterIterator( input );
        for (char c = iter.first(); Character.isWhitespace( c ); c=iter.next())
            continue;
        return input.substring( iter.getIndex() );		
	}
	
	/**
	 * @return String
	 * @roseuid 3E1D19530049
	 */
	public String getName() {
        return _name;		
	}
	
	/**
	 * @return long
	 * @roseuid 3E1D19530067
	 */
	public long getSize() {
        return _size;		
	}
	
	/**
	 * @return java.util.Date
	 * @roseuid 3E1D1953007B
	 */
	public Date getDate() {
        return _date;		
	}
	
	/**
	 * @return String
	 * @roseuid 3E1D195300AD
	 */
	public String getTime() {
        return _time;		
	}
	
	/**
	 * @return String
	 * @roseuid 3E1D195300C1
	 */
	public String getDateString() {
        return getDateString( "mm/dd/yyyy" );		
	}
	
	/**
	 * @param format
	 * @return String
	 * @roseuid 3E1D195300D5
	 */
	public String getDateString(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat( format );
        return sdf.format( getDate() );		
	}
	
	/**
	 * @return boolean
	 * @roseuid 3E1D195300F3
	 */
	public boolean isFile() {
        return _isFile;		
	}
	
	/**
	 * @return boolean
	 * @roseuid 3E1D19530108
	 */
	public boolean isDirectory() {
        return _isDirectory;		
	}
	
	/**
	 * @return String
	 * @roseuid 3E1D1953011C
	 */
	public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "\nName  \t" ).append( getName() )
              .append( "\nType  \t" ).append( isFile() ? "File" : "Directory" )
              .append( "\nSize  \t" ).append( getSize() )
              .append( "\nDate  \t" ).append( getDate() )
              .append( "\nTime  \t" ).append( getTime() );
        return buffer.toString();		
	}
}
