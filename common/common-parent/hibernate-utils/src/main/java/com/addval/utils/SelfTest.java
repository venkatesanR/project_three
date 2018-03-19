package com.addval.utils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;


/**
   A simple class to test some of other classes in the utils package.
   @author Sankar Dhanushkodi
   @revision $Revision$
 */
public class SelfTest {

   public SelfTest() {}

   /**
      Main method. You can pass in the name of the class to test.
      Default is to test all classes.
      @param args[] String array
      @return
      @exception
      @roseuid 37C193E503B6
    */
   public static void main(String args[]) {
      System.out.println( "com.addval.utils self-test utility" );
      System.out.println( "Usage: java -classpath <path to where /com starts> com.addval.utils.SelfTest [ClassName]" );
      System.out.println( "if ClassName is ommited runs all tests" );
      System.out.println( "--------------------------------------" );

      SelfTest t = new SelfTest();

      if ( args.length == 0 ) {
         t.testLogFileMgr();
         t.testCnfgFileMgr();
      }
      else if ( "LogFileMgr".equals( args[0] ) ) {
         t.testLogFileMgr();
      }
      else if ( "CnfgFileMgr".equals( args[0] ) ) {
         t.testCnfgFileMgr();
      }

      System.gc();
   }

   /**
      Performs the test on the class "LogFileMgr"
      @param
      @return
      @exception
      @roseuid 37C1945D02AA
    */
   public void testLogFileMgr() {
      final String filename = "test.log";
      String msgTypes = null;

      System.out.println( "Running LogFileMgr test" );
      System.out.println( "Please Enter Message Types to print (select from the following)" );
      System.out.println( LogFileMgr._ERROR_TYPE );
      System.out.println( LogFileMgr._INFO_TYPE  );
      System.out.println( LogFileMgr._WARNING_TYPE );
      System.out.println( LogFileMgr._SQL_TYPE );
      System.out.println( LogFileMgr._TRACE_TYPE );
      System.out.println( "> " );
      try {
         msgTypes = (new BufferedReader( new InputStreamReader( System.in ) )).readLine();
      }
      catch ( IOException e ) {
         System.out.println( "exception = " + e );
         return;
      }

      System.out.println( "Creating log file test.log in the current directory..." );
      LogFileMgr mgr = LogFileMgr.getInstance( filename, msgTypes );

      System.out.println( "writing " + LogFileMgr._ERROR_TYPE );
      mgr.logError( "Source", "Message" );

      System.out.println( "writing " + LogFileMgr._INFO_TYPE );
      mgr.logInfo( "Source", "Message" );

      System.out.println( "writing " + LogFileMgr._WARNING_TYPE );
      mgr.logWarning( "Source", "Message" );

      System.out.println( "writing " + LogFileMgr._SQL_TYPE );
      mgr.logSql( "Source", "Message" );

      System.out.println( "writing " + LogFileMgr._TRACE_TYPE );
      mgr.traceEnter( "Source" );
      mgr.traceExit ( "Source" );
   }

   /**
      Performs the test on the class "CnfgFileMgr"
      @param
      @return
      @exception
      @roseuid 37C194980204
    */
   public void testCnfgFileMgr() {
      System.out.println( "Running CnfgFileMgr test" );
      System.out.print( "Please Enter configuration file name: " );
      BufferedReader rdr =new BufferedReader( new InputStreamReader( System.in ) );
      try {
         String filename = rdr.readLine();

         System.out.println( "Creating CnfgFileMgr..." );
         CnfgFileMgr mgr = new CnfgFileMgr( filename );

         boolean cont = true;

         while ( cont ) {
            System.out.print( "Please enter property name: " );
            String name = rdr.readLine();

            System.out.println( "value for property |" + name + "| = " +
                                 mgr.getPropertyValue( name, "def" ) );

            System.out.print( "Continue [Y/N]: " );
            String c = rdr.readLine();
            cont = "Y".equals( c );
         } //end while
      }
      catch( IOException e ) {
         System.out.println( "exception = " + e );
         return;
      }

   }

}
