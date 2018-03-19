package com.addval.dbutils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.addval.esapiutils.validator.AppSecurityValidatorException;
import com.addval.esapiutils.validator.FileSecurityValidator;
import com.addval.esapiutils.validator.FileSecurityValidatorESAPIImpl;
import com.addval.utils.CnfgFileMgr;

public class SelfTest {
	private FileSecurityValidator fileSecurityValidator;
	public void setFileSecurityValidator(FileSecurityValidator fileSecurityValidator)
	{
		this.fileSecurityValidator=fileSecurityValidator;
	}

	public FileSecurityValidator getFileSecurityValidator()
	 {
		return new FileSecurityValidatorESAPIImpl();
	  }

    /**
       @roseuid 37C1C49E01F8
     */
   public static final void main(String [] args) {
      System.out.println( "running com.addval.dbutils.SelfTest..." );

      if ( args.length == 0 ) {
         System.out.println( "Usage: java SelfTest <ClassName>" );
         System.out.println( " specify 'all' to test all classes" );
         System.exit( 1 );
      }

      SelfTest t = new SelfTest();

      if ( "all".equals( args[0] ) ) {
         // run all tests
         t.testDBPool();
      }
      else if ( "DBPool".equals( args[0] ) ) {
         t.testDBPool();
      }

      System.out.println( "Test Completed" );
      System.gc();
   }

    /**
       @roseuid 37C1C4A301B9
     */
   public void testDBPool() {
	   
	  
     // BufferedReader rdr = new BufferedReader( new InputStreamReader( System.in ) );

      try {
         System.out.print( "Please enter configuration filename: " );
         String cfgFile = getFileSecurityValidator().safeReadLine(System.in);
         //String cfgFile = rdr.readLine();

         System.out.println( "creating CnfgFileMgr object..." );
         CnfgFileMgr cfgMgr = new CnfgFileMgr( cfgFile );

         System.out.println( "creating DBPool object..." );
         DBPoolMgr obj = new DBPoolMgr( "SelfTest", cfgMgr );

         System.out.println( "SCHEMA INFO :" );
         System.out.println( obj.getSchema().toString() );

      }
      catch (AppSecurityValidatorException e) {
	     // TODO Auto-generated catch block
		  e.printStackTrace();
		}
      catch ( Exception e ) {
         System.out.println( "exception: " + e );
         return;
      }
   }
}
