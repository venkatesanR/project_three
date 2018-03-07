package com.addval.utils;

import java.io.Reader;
import java.io.Writer;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;

// class for converting the Ebcdic charset (usually from IBM mainframe to Ascii / unicode)
public class EbcdicToAscii
{
	private String _sourceEncoding = null;

	public EbcdicToAscii()
	{
		// set the default encoding as Cp307, Cp500 also seem similar
		setSourceEncoding( "Cp037" );
	}

	public EbcdicToAscii(String sourceEncoding)
	{
		if (sourceEncoding == null)
			sourceEncoding = "Cp037";
		setSourceEncoding( sourceEncoding );
	}

	public void setSourceEncoding(String sourceEncoding)
	{
		_sourceEncoding = sourceEncoding;
	}

	public String getSourceEncoding()
	{
		return _sourceEncoding;
	}

	public String getAscii(String ebcdicString) throws Exception
	{
		if (ebcdicString == null)
			return null;
		if (_sourceEncoding == null)
			throw new RuntimeException( "No encoding is specified." );
		Reader ebcdicReader = null;
		Writer asciiWriter = null;
		try {
			InputStream ebcdicStream = new ByteArrayInputStream( ebcdicString.getBytes() );
			ebcdicReader = new InputStreamReader( ebcdicStream, _sourceEncoding );
			asciiWriter = new StringWriter();
			for (int c=0; (c = ebcdicReader.read()) != -1; asciiWriter.write((char) c))
				continue;
		    return asciiWriter.toString();
		}
		finally {
			if (ebcdicReader != null)
				ebcdicReader.close();
			if (asciiWriter != null)
				asciiWriter.close();
		}
	}

	public void convert2Ascii(String ebcdicFileName, String asciiFileName) throws Exception
	{
		if (_sourceEncoding == null)
			throw new RuntimeException( "No encoding is specified." );
		Reader ebcdicReader = null;
		Writer asciiWriter = null;
		try {
			InputStream ebcdicStream = new FileInputStream( ebcdicFileName );
			ebcdicReader = new InputStreamReader( ebcdicStream, _sourceEncoding );
			asciiWriter = new BufferedWriter( new FileWriter( new File( asciiFileName ) ) );
			for (int c=0; (c = ebcdicReader.read()) != -1;asciiWriter.write((char) c))
				continue;
		}
		finally {
			if (ebcdicReader != null)
				ebcdicReader.close();
			if (asciiWriter != null)
				asciiWriter.close();
		}
	}

	public static String preProcess(String input, char[] source, char[] substitute)
	{
		for (int i=0; i<source.length; i++)
			input = input.replace( source[i], substitute[i] );
		return input;
	}

	public static String preProcess(String input, String[] source, String[] substitute)
	{
		for (int i=0; i<source.length; i++)
			input = preProcess( input, source[i], substitute[i] );
		return input;
	}

	public static String preProcess(String input, char source, char substitute)
	{
		return input.replace( source, substitute );
	}

	public static String preProcess(String input, String source, String substitute)
	{
		return input.replaceAll( source, substitute );
	}

    public static String[] replaceAsciiWithChar( String[] sourceStr ) {
        for ( int i = 0; i < sourceStr.length; i++ ) {
            //System.out.println("Val at=" + i + ",val=" + replaceValue[i]);
            try {
                int replaceAsciiValue = Integer.parseInt(sourceStr[i]);
                char testChar = (char) replaceAsciiValue;
                sourceStr[i] = String.valueOf(testChar);
            }
            catch ( NumberFormatException nfe ) {
                //if the substituted value is not an integer, let us not convert the value. Let
                // sourceStr[i] contain the original value from property file.
            }
        }
        return sourceStr;
    }

	public static void main(String args[]) throws Exception
	{
		// FFR\n coded as hexa Decimal is converted to ASCII (FFR\n)
		EbcdicToAscii parser = new EbcdicToAscii();
		char c[] = new char[]{0xc6,0xc6,0xd9,0x15};
		String asString = new String( c );
		System.out.println( "FFR\\n as Ebcdic - " + asString );
		System.out.println( "FFR\\n as Ascii  - " + parser.getAscii( asString ) );

		// SSM\n coded as hexa Decimal is converted to ASCII (SSM\n)
		// preprocessing may not be required for SSM messages
		c = new char[]{0xe2,0xe2,0xd4,0x15};
		asString = new String( c );
		System.out.println( "SSM\\n as Ebcdic - " + asString );
		System.out.println( "SSM\\n as Ascii  - " + parser.getAscii( asString ) );

		parser.convert2Ascii( args[0], args[1] );
/*
		// convert from a known string
		String asciiString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		System.out.println( "ASCII Before  - " + asciiString );
		System.out.println(	"EBCDIC String - " + new String( asciiString.getBytes("Cp037") ) );
		System.out.println( "ASCII After   - " + EbcdicToAscii.getAscii( new String( asciiString.getBytes("Cp037") ) ) );

		// convert from a ebcdic string
		String ebcdicString = "ㅢ�@ԅ��?��?@���@������@��@�����@Ö��������";
		System.out.println( "\nEBCDIC String is - " + ebcdicString );
		System.out.println( "ASCII  String is - " + EbcdicToAscii.getAscii( ebcdicString ) );

		// convert file content
		EbcdicToAscii.convert2Ascii( "d:\\sample_FFR.txt", "d:\\outfile.txt" );
*/
	}
}
