package com.addval.mastertable.audit;


import com.addval.environment.Environment;

import java.util.*;
import java.util.Date;
import java.sql.*;
import java.util.regex.Pattern;
import java.io.*;



public class MasterTableAuditor
{
    private String _projectName;
    private Vector _editorVect;
    private Vector _editorNameVect;
	private BufferedWriter _errorFile;
	private BufferedWriter _warningFile;
    private String datasourceName;
	private int _errorCount=0;
	private int _warningCount=0;
	private StringBuffer _header;
	private StringBuffer _errors;
	private StringBuffer _warning;
	private StringBuffer _CountHeader;
	private StringBuffer _warningHeader;

   	public MasterTableAuditor(String projectName) throws IOException
	   {
       	_projectName = projectName;
     	_editorNameVect= new Vector();
		_header = new StringBuffer();
		_errors = new StringBuffer();
		_warning =new StringBuffer();
		_CountHeader =new StringBuffer();
		_warningHeader =new StringBuffer();

		String outDir = Environment.getInstance( _projectName ).getCnfgFileMgr().getPropertyValue( "outputDir" , null );
		_errorFile = new BufferedWriter(new FileWriter( outDir +"Errors.txt"));
		_warningFile =new BufferedWriter(new FileWriter( outDir +"Warnings.txt"));

       	populateEditor();
       	verifyTable();
     	verifyFormat();
		verifyRegExp();
		logError();
		verifyCombo();
	    verifySourceSql();

       }

    private void populateEditor()
    {

        ResultSet rs = null;
        String sql ="SELECT  editor_columns.editor_name, editor_columns.column_name,\n" +
				"       editors.editor_source_name, editors.editor_interceptor,\n" +
				"       editors.editor_source_sql, editor_columns.column_key,\n" +
				"       editor_columns.column_combo_cached,\n" +
				"       editor_columns.column_combo_select_tag, editor_columns.column_combo_ovd,\n" +
				"       editor_columns.column_combo_tblname_ovd,\n" +
				"       editor_columns.column_combo_select_ovd,\n" +
				"       editor_columns.column_combo_orderby_ovd, COLUMNS.column_type,\n" +
				"       COLUMNS.column_format, COLUMNS.column_combo,\n" +
				"       COLUMNS.column_combo_tblname, COLUMNS.column_combo_select,\n" +
				"       COLUMNS.column_combo_orderby, COLUMNS.column_minval,\n" +
				"       COLUMNS.column_maxval, COLUMNS.column_regexp, COLUMNS.column_errormsg,\n" +
				"       editor_type_columns.column_edit_key,\n" +
				"       editor_type_columns.column_nullable,\n" +
				"       editor_type_columns.column_autoseq_name\n" +
				"  		FROM editor_columns, editors, editor_type_columns, COLUMNS\n" +
				"  		WHERE ((editors.editor_name = editor_columns.editor_name)\n" +
				"           AND (COLUMNS.column_name = editor_columns.column_name)\n" +
				"           AND (editor_columns.editor_name = editor_type_columns.editor_name(+))\n" +
				"           AND (editor_columns.column_name = editor_type_columns.column_name(+))\n" +
				"          )\n" +
				"ORDER BY editors.editor_name ASC" ;
            rs=getDb(sql);
            _editorVect= new Vector();

        try {
        	if (!rs.next())
            	return;
			boolean isEndOfRs = false;
			while (!isEndOfRs) {
				Editor editor = new Editor( rs.getString( 1 ) );
				try {
				createEditor(rs,editor);
				_editorVect.addElement(editor);
				while (true) {
					Column column = createColumn( rs, editor );
					if (column != null )
						editor.addColumn(column);
					if (!rs.next()) {
						isEndOfRs = true;
						break;
					}
					if (!editor.getName().equals( rs.getString( 1 )))
						break;
				}
              }catch(SQLException e){
				editor.addError(  e.toString() );
				}
			}
        }
        catch (SQLException e) {
			e.printStackTrace();
		}
  }

    public void createEditor(ResultSet rs, Editor editor)
	{
        try {
            editor.setSourceName( rs.getString( 3 ) );
            editor.setInterceptor( rs.getString( 4 ) );
            editor.setSourceSql( rs.getString( 5 ) );
		}
        catch ( SQLException e ) {
            editor.addError( e.toString() );

        }
    }

    public Column createColumn(ResultSet rs, Editor editor)
    {
        Column column = null;

        try {
            String name = rs.getString( 2 );
            if (editor.isColumnExisting( name ))
                return null;

            column= new Column( name );
            column.seteditorColumns_columnKey( rs.getInt(6) );
            column.seteditorColumns_comboCached( rs.getInt(7) );
            column.seteditorColumns_selectTag( rs.getInt(8) );
            column.seteditorColumns_comboOvd( rs.getInt(9) );
            column.seteditorColumns_tblName( rs.getString(10) );
            column.seteditorColumns_selectOvd( rs.getString(11) );
            column.seteditorColumns_orderByOvd( rs.getString(12) );
            column.setType( rs.getString(13) );
            column.setFormat( rs.getString(14) );
            column.setcolumnCombo( rs.getInt(15) );
            column.setcomboTblName( rs.getString(16) );
            column.setcomboSelect( rs.getString(17) );
            column.setcomboOrderBy( rs.getString(18) );
            column.setcolumnMinVal( rs.getInt(19) );
            column.setcolumnMaxVal( rs.getInt(20) );
            column.setcolumnRegExp( rs.getString(21) );
            column.setcolumnErrorMsg( rs.getString(22) );
            column.seteditorTypeColumns_editKey( rs.getInt(23) );
            column.seteditorTypeColumns_nullable( rs.getInt(24) );
            column.seteditorTypeColumns_autoSeqName( rs.getString(25) );
        }
        catch (SQLException e) {
			editor.addError( e.toString() );
        }
        return column;
    }

   public void verifyTable()
    {
        for(int i=0;i<_editorVect.size();i++) {
            Editor editor =( Editor ) _editorVect.elementAt(i);
            String sourceName = editor.getSourceName();
            String sql = "select * from " + sourceName + " where 1=1" ;
            checkSource( sql, editor );
        }
    }

    private void checkSource(String sql, Editor editor)
    {
        Connection conn = null;
		ResultSet rs = null;
		ResultSetMetaData metaData = null;
        try {
            conn = getConnection();
            rs = getResultSet( sql, conn ).getResultSet();
			metaData = rs.getMetaData();
		   	createColumnMetaData( metaData, editor );
        }
        catch(Exception e) {
            editor.addError(editor.getSourceName() +" : table or view does not exists \n" ) ;
	     }
        finally {
            releaseConnection( conn );
        }
    }

	private void createColumnMetaData( ResultSetMetaData metaData, Editor editor ) throws Exception
	{
		int count = metaData.getColumnCount();
        for (int i=1; i<=count; i++) {
			String name = metaData.getColumnName( i );
			String type =metaData.getColumnTypeName(i);
			Column column = editor.getColumn( name );
			if (column == null)
				continue;
			column.setDBScale(metaData.getScale(i));
			column.setDBType( type );
	      	column.setDBPrecision( metaData.getPrecision(i) );
			column.setDBSize( metaData.getColumnDisplaySize(i) );

        }
   	}

  	private void verifyFormat()
	{
		for(int i=0;i<_editorVect.size();i++) {
			try
			{
				Editor editor =( Editor ) _editorVect.elementAt(i);
				Set columns = editor.getColumns();
				_editorNameVect.addElement( editor.getName() );
        		for (Iterator iter=columns.iterator();iter.hasNext();)
				{
					Object col=iter.next();
					String name= col.toString();
		        	Column column=editor.getColumn(name);
			    	String format=column.getFormat();
			    	String type=column.getType();
			 		int size=0;
               		int dbSize=column.getDBSize();
					if(format!=null){
						size = format.length();
					if(!((type.equals("CDT_DATE")) || (type.equals("CDT_TIMESTAMP"))
							|| (type.equals("CDT_DATETIME")) || (type.equals("CDT_LINK")))) {
						size=getSize( size,format );
						checkSize(size,dbSize,column,name);
                		if (type.equals("CDT_STRING")||type.equals("CDT_USER")){
				  			if(!(format.startsWith("&")))
						  		column.addError("Column Name : " +name +" format for String is not  correct , format :   "+format );
							}else if(type.equals("CDT_INT") || (type.equals("CDT_DOUBLE"))
									|| (type.equals("CDT_LONG")) || (type.equals("CDT_KEY"))){
									if(!(format.startsWith("#")))
						 				column.addError("Column " +name +" format is not  correct,  format :   "+format );
					  		}
					}else{
					   if(type.equals("CDT_DATE")){
						   if(!format.equals("dd/MM/yy"))
							  column.addWarning("Column "+name +": "+"Date format not correct  "+ format);
					   }else if(type.equals("CDT_TIMESTAMP")){
						   if(!format.equals("dd-MMM-yyyy H:mm:ss"))
							 column.addWarning("Column "+name +": "+"TimeStamp format not correct  "+ format);
					   }else if(type.equals("CDT_DATETIME")){
						   if(!format.equals("dd-MMM-yyyy H:mm"))
							 column.addWarning("Column "+name +": "+"DateTime format not correct  "+ format);
					   	}
				   	}
				}else
					column.addWarning("Column "+name+ " : "+ " Format missing"  );
			}
		}
		catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public void checkSize(int size,int dbSize,Column column,String name)
	{
		if(size<dbSize)
			column.addWarning("Column "+name + " : "+ " size not matched  "+" size = "+size +" dbSize = "+dbSize);
		else if(size>dbSize)
			column.addError("Column " +name+" : "+" size greater than DB Size , "+ " size = "+size +" dbSize= "+dbSize);
    }

	public int getSize(int size,String format)
	{
		char[] c=format.toCharArray();
			for(int j=0;j<c.length;j++){
				if(c[j]=='}'){
					CharSequence css=format.subSequence(2,j);
					size=Integer.parseInt(css.toString());
     			}
			}
		return size;
    }


	private void verifyRegExp()
	{
		for (int i=0;i<_editorVect.size();i++) {
			Editor editor =(Editor) _editorVect.elementAt(i);
			Set columns = editor.getColumns();
			for (Iterator iter=columns.iterator();iter.hasNext();) {
				 Object col=iter.next();
				 String name= col.toString();
				 Column column=editor.getColumn(name);
				 String regExp =column.getcolumnRegExp();
	      	     String type =column.getType();
                 try {
					if (regExp!=null){
						if(!type.equals("CDT_LINK"))
							Pattern.compile( regExp );
					}
				 }
				catch(Exception e) {
					column.addError( "Reg Exp  not valid - " + e.toString() );
				}
          	}
		}
	}

	private void verifyCombo()
	{
		for(int i=0;i<_editorVect.size();i++){
			Editor editor =(Editor)_editorVect.get(i);
			Set columns = editor.getColumns();
			for (Iterator iter=columns.iterator();iter.hasNext();) {
				Object col=iter.next();
				String name= col.toString();
				Column column=editor.getColumn(name);
				int isCombo = column.getcolumnCombo();
				if(isCombo == 1 ){
					String tableName =column.getcomboTblName();
					for(int j=0;j<_editorNameVect.size();j++) {
						if(!_editorNameVect.contains(tableName))
							column.addError(tableName +" for columnCombo does not exist for column " +name);
					}
				}
     		}
		}
    }

	private void verifySourceSql()
	{
		Connection conn = null;
		ResultSet rs = null;
		for (int i=0;i<_editorVect.size();i++){
			Editor editor =(Editor)_editorVect.get(i);
			String sql=editor.getSourceSql();
			if(sql!=null){
				 try {
          		 	 conn = getConnection();
    	       		 rs = getResultSet( sql, conn ).getResultSet();
	           		}
        			catch(SQLException e) {
            				editor.addError(editor.getSourceSql() +" "+ e.toString() );
			        }
                    finally {
            			releaseConnection( conn );
        			}
			}
		}
	}

	private void logError() throws IOException
	{
		int editorSize =0 ;
		int columnSize = 0;
        for(int i=0;i<_editorVect.size();i++) {
			Editor editor =(Editor) _editorVect.elementAt(i);
           	List editorError =editor.getErrors();
			editorSize =editor.getErrors().size();
        	List columnError =editor.getColumnErrors();
			List columnWarning =editor.getColumnWarnings();
			String name =editor.getName();
			columnSize = columnError.size();
			 if(columnSize!=0)
		      	 _errorCount++;
           	if((editorSize!=0) ||(columnSize!=0)){
                _errors.append("\n"+"Editor Name : "+editor.getName() + "\n\n");
	           		printEditorErrors( editor);
               	if(editorError.size() > 0)
					continue;
        		printColumnErrors( columnError );
				_errors.append("\n\n\n");
			}
    		printColumnWarnings( columnWarning , name );
    	}
		 printHeader();
		_errorFile.write(_header.toString());
		_warningFile.write(_header.toString());
		_errorFile.write(_CountHeader.toString());
		_warningFile.write(_warningHeader.toString());
        _errorFile.write(_errors.toString());
	    _warningFile.write(_warning.toString());

		_errorFile.close();
		_warningFile.close();
	}


	private void printHeader()  {

		Date now = Calendar.getInstance().getTime();
		String serverType =Environment.getInstance( _projectName ).getDbPoolMgr().getServerType();
        datasourceName = Environment.getInstance( _projectName ).getDbPoolMgr().getName();
		String driverName = Environment.getInstance( _projectName ).getCnfgFileMgr().getPropertyValue( "db." + datasourceName +".driver", null );
		String userName = Environment.getInstance( _projectName ).getCnfgFileMgr().getPropertyValue( "db." + datasourceName +".user", null );
		String password =  Environment.getInstance( _projectName ).getCnfgFileMgr().getPropertyValue( "db." + datasourceName +".password", null );
		_header.append("\t\t\tMasterTable  Auditor \n");
		_header.append("\t\t\t******************** \n\n");
		_header.append("################################################################################\n");
		_header.append("Created on     : " ).append( now ).append( "\n" );
		_header.append("DataSourceName : " ).append( datasourceName ).append( "\n" );
		_header.append("ServerType     : " ).append( serverType ).append( "\n" );
		_header.append("Driver         : " ).append( driverName ).append( "\n" );
		_header.append("User Name      : " ).append( userName ).append( "\n" );
		_header.append("Password       : " ).append( password ).append( "\n" );
		_header.append("################################################################################\n\n");

		printInfo(_CountHeader , "Errors");
		printInfo(_warningHeader,"Warnings");

    }
	private void printInfo(StringBuffer header,String name )
	{
		header.append("------------------------------------------------------------------------------"+"\n\n");
		header.append("No of Editors Checked : " +_editorVect.size()+"\n");
		if(name.equals("Errors"))
   			header.append("No of Editor " + name+ "   : "+_errorCount+"\n");
		else if(name.equals("Warnings"))
				header.append("No of Editor " + name+ " : "+_warningCount+"\n");
		header.append("------------------------------------------------------------------------------"+"\n\n");
	}

	private void printEditorErrors( Editor editor )  {
		List editorError = editor.getErrors();
		for(int k=0;k<editorError.size();k++)
			Environment.getInstance( _projectName ).getLogFileMgr( "auditError").logError(editorError.get(k).toString() );
		try {
    		if(editorError.size()!=0){
				 _errors.append("Editor Error : "+"\n");
	     		for(int k=0;k<editorError.size();k++)
				   _errors.append("\t"+editorError.get(k).toString()+"\n");
     	   }
  		} catch (Exception e) {}
	}

	private void printColumnErrors(List  columnError) {
        for(int j=0;j<columnError.size();j++)
			Environment.getInstance( _projectName ).getLogFileMgr("auditError").logError( columnError.get(j).toString() );
		try {
         		if(columnError.size()!=0){
					 _errors.append("ColumnErrors : "+"\n");
					for(int k=0;k<columnError.size();k++)
						_errors.append("\t"+columnError.get(k).toString()+"\n");
						 _errors.append("\n\n");
				}
		} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private void printColumnWarnings(List columnWarning,String name )
	{
	    int size =columnWarning.size() ;
		for(int j=0;j<size;j++)
       		Environment.getInstance( _projectName ).getLogFileMgr("auditWarning").logError( columnWarning.get(j).toString() );
		try{
			if(size >0){
                _warning.append("\n"+"Editor Name : " +name +"\n") ;
				for(int i=0;i<size;i++)
				_warning.append("\t"+columnWarning.get(i)+"\n");
		    	 _warningCount++;
            }
			_warning.append("\n");
		} catch (Exception e) {
				e.printStackTrace();
			}
	}


    public ResultSet getDb(String sql)
   {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = getResultSet( sql, conn );
            rs = stmt.getResultSet();
         } catch (SQLException e) {
      }
         return rs;
    }

    private Statement getResultSet(String sql, Connection conn ) throws SQLException
    {
        Statement stmt = conn.createStatement();
        stmt.executeQuery( sql );
        return stmt;

    }

     private Connection getConnection()
    {
        return Environment.getInstance( _projectName ).getDbPoolMgr().getConnection();
    }

    private void releaseConnection( Connection conn)
    {
        if (conn != null)
            Environment.getInstance( _projectName ).getDbPoolMgr().releaseConnection( conn );
    }

	 public static void main(String args[])
    {
       try {
            new MasterTableAuditor( "MasterTableAuditor" );
        } catch (Exception e) {}
    }

}
