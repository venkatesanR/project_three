package com.addval.dbutils;

import com.addval.environment.Environment;
import com.addval.utils.LogFileMgr;


import java.sql.*;





/**
 * Created by IntelliJ IDEA.
 * User: rafeeq
 * Date: Dec 27, 2006
 * Time: 11:36:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestDBCllient {
    Environment _env = Environment.getInstance( "test_db_client" );
    Connection _conn = _env.getDbPoolMgr().getConnection();
    LogFileMgr _log =LogFileMgr.getInstance( "TEST_DB_CLIENT" );
    LogFileMgr _sqlLog =LogFileMgr.getInstance( "SQL" );
    LogFileMgr _errorSqlLog =LogFileMgr.getInstance( "ERROR_SQL" );
    LogFileMgr _batchErrorSqlLog =LogFileMgr.getInstance( "BATCH_ERROR_SQL" );


    String _sql = "select * from test_table";
     String _sql1 = "insert into test_table values(?,?)";
     String _sql2 = "insert into test_table values('3','three')";
     String _sql3 = "select * from test_table where test_id=?";
    String _sql4 = "select test_id,test_No from test_table";
    String _sql5 = "{call insert_procedure ( ? , ? )}";

    public void executeQueryForStmt( String sql ){
        try {
            //String _sql = "select test_id,test_No from test_table";
           // System.out.println("_log:");
          //  if(_log.debugOn())
          //      _log.logSql( "executeQuery( " + _sql +" )" );
            Statement st = _conn.createStatement();
            ResultSet rs = st.executeQuery( sql );
            while(rs.next())
            {
                System.out.println("Test_id :"+rs.getString(1));
                System.out.println("Test_Name :"+rs.getString(2));
               /* if(_log.debugOn()) {
                    _log.logInfo(" Test Id: "+rs.getString(1)+"Test Name :"+rs.getString(2));
                    _log.logInfo(" Test Id: "+rs.getString(1)+"Test Name :"+rs.getString(2));
                }*/
            }

        }
        catch(SQLException e) {

         /*   if(_errorSqlLog.debugOn()) {
                _errorSqlLog.logDebug("executeQuery---->("+_sql4+")");
                _errorSqlLog.logDebug("Error SQL--------> "+e.getMessage());
            }*/


        }
    }

    public void executeUpdateForStmt( String sql )
    {
        try
        {
            Statement st = _conn.createStatement();
            st.executeUpdate( sql );
        }
        catch(SQLException e) {
         /*  if(_errorSqlLog.debugOn()) {
               _errorSqlLog.logInfo("Inside executeUpdateForStmt()----->"+_sql2);
               _errorSqlLog.logInfo("Error-------> :"+e.getMessage());
           }*/
        }
    }

    public void executeQueryForprepareStmt(String sql , String input , boolean tobeSet) {
        try {
            PreparedStatement st = _conn.prepareStatement( sql );
            if(tobeSet)
                st.setString(1,input);
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                System.out.println("Inside excuteQueryForprepareStmt"+rs.getString(1));
                System.out.println("Inside excuteQueryForprepareStmt"+rs.getString(2));
            }
        }
        catch(SQLException e) {
           /* if(_errorSqlLog.debugOn()) {
                _errorSqlLog.logInfo("excuteQueryForprepareStmt--->"+_sql3);
                _errorSqlLog.logError("Error --->:"+e.getMessage());
            }*/
        }
    }

    public void executeUpdateForPrepareStmt( String sql , String input1 , String input2 , boolean tobeSet) {
        try {

            System.out.println("Inside executeUpdate()");
           // if(_log.debugOn())
              //  _log.logSql( "executeUpdate( " + _sql +" )" );
            PreparedStatement st = _conn.prepareStatement( sql );
            if(tobeSet)
                st.setString(1,input1);
                st.setString(2,input2);
            int result = st.executeUpdate();

        }
        catch(SQLException e) {

           /* if(_errorSqlLog.debugOn()) {
                 _errorSqlLog.logInfo("ExecuteUpdate*******("+_sql1+")");
                _errorSqlLog.logDebug("executeUpdate---->("+_sql1+")");
                _errorSqlLog.logDebug("Error SQL-> "+e);
            } */


        }

    }

    public void executeBatch( String sql ) {
        try
        {
        Statement st = _conn.createStatement();
        //_batchErrorSqlLog.logInfo("11111111111");

        st.addBatch( sql );
           // _batchErrorSqlLog.logInfo("22222222222");
        st.executeBatch();
           // _batchErrorSqlLog.logInfo("33333333333");
        }
        catch(SQLException e) {
            if(_batchErrorSqlLog.debugOn()) {
               // _batchErrorSqlLog.logDebug("executeBatchRafeeq :-->"+_sql2);
               //  _batchErrorSqlLog.logDebug("Error :-->"+e.getMessage());
            }
        }

    }

    public void executeQueryForCallable()
    {
        try {

            CallableStatement  st=_conn.prepareCall("{call HELLOWORLD}");
            ResultSet rs = st.executeQuery();
            while(rs.next())
            {
                System.out.println("Helloworld---->"+rs.getString("HELLO"));
            }
        }
        catch(SQLException ex) {
           // _errorSqlLog.logDebug("executeQueryForCallRafeeq :-->");
            //_errorSqlLog.logDebug("Error :-->"+ex.getMessage());
        }

    }

    public void executeUpdateForCallableStmt(String input1 , String input2)
    {
	try {
            CallableStatement st = _conn.prepareCall(_sql5);
            st.setString(1,input1);
            st.setString(2,input2);
            st.executeUpdate();
            System.out.println("It works");
        }
        catch(SQLException ex) {
           /* if(_errorSqlLog.debugOn()) {
                _errorSqlLog.logInfo("Inside executeUpdateForCallableStmt()---->" );
                _errorSqlLog.logInfo("Error->:"+ex.getMessage());
            } */
        }
    }

    public static void main(String args[]) {
      System.out.println("Inside TestDBCLient Client Main()");
       TestDBCllient obj = new TestDBCllient();


        //String sql = "select test_id,test_No from test_table";   //invalid identifier
       //String sql = "select test_id,test_Name from test_table1"; //table or view does not exist
       //obj.executeQueryForStmt( sql );

        //String sql = "insert into test_table values('3','three')"; //calling with this twice will result in unique constraint exception
        //String sql = "insert into test_table1 values('3','three')"; //table or view does not exist
        //String sql = "insert into test_table values('3','threeeeeeeeeeee')"; //inserted value too large for column
        //obj.executeUpdateForStmt( sql );

        //String sql = "select * from test_table where test_id=?";
        //String sql = "select * from test_table1 where test_id=?"; //table or view does not exist
        //String sql = "select * from test_table where test_id1=?"; //invalid identifier
        //obj.executeQueryForprepareStmt( sql , "3" , true );
        //obj.executeQueryForprepareStmt( sql , "3" , false ); // not all variables bound


       //String sql = "insert into test_table values(?,?)";
        //String sql = "insert into test_table1 values(?,?)";//table or view does not exist
        //String sql = "insert into test_table values(?,?)";
        //obj.executeUpdateForPrepareStmt( sql , "23" , "asdfasdfasdfasdf" , true);//inserted value too large for column
        //obj.executeUpdateForPrepareStmt( sql , "23" , "asdff" , false); //executeUpdateMissing IN or OUT parameter at index:: 1

        //String sql = "insert into test_table1 values('31','three')"; //table or view does not exist
        //String sql = "insert into test_table values('31','three')"; // Running this twice will result in unique constraint
        String sql = "insert into test_table values('31','thre')";//value too large for column
        obj.executeBatch( sql );

        //obj.executeQueryForCallable();
		//obj.executeUpdateForCallableStmt( args[0] , args[1] );
        //obj.executeUpdateForCallableStmt( "12","asdf");//calling this method twice will result in Unique Constraint
        //obj.executeUpdateForCallableStmt( "56" , "aaaaaaaaaaaaaaa" );//inserted value too large for column
        /* *******************************
           Running this script in SQL Editor and executing the testDbClient will result in "INSERT_PROCEDURE is invalid" Exception
           *********************************
        CREATE OR REPLACE procedure insert_procedure( in_testid test_table.test_id%type,
	   	  		  							  in_testname  test_table.test_name%type)
        is

        begin

        INSERT INTO test_table1(test_id,test_name) VALUES(in_testid,in_testname);
        commit;

        end;
        */


    }
}
