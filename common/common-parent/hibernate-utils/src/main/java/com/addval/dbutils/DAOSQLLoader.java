//Source file: C:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\DAOSQLLoader.java

//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\DAOSQLLoader.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;

import java.io.IOException;
import java.net.MalformedURLException;
import com.addval.utils.XRuntime;
import org.xml.sax.SAXException;
import java.net.URL;
import java.util.*;

/**
 * @author AddVal Technology Inc.
 * This class helps in loading the SQL Statements needed by DAO Objects from an
 * DAO Configuration XML file
 */
public class DAOSQLLoader {
	private static final String _MODULE = "com.addval.dbutils.DAOSQLLoader";
	public static final String _RULES_URL = "db.DAO.rulesURL";
	public static final String _SQL_URL = "db.DAO.sqlURL";


	/*
		 this method loads a comma separated list of sql files. The sqlfiles that appear later in the list will override
		 the ones that appears first in the list
		 For example if a SQL Statement called SQL123 is defined in filea.xml and fileb.xml
		 If the filenames are defined as: filea.xml,fileb.xml - then the definition of the SQL statement in fileb.xml overrides that in filea.xml

		 This can be used to customize or override sql definitions at a project level but use other sql definitions at the product level
	*/
	public Hashtable loadSQL(String rulesFile, String inputFile) {
		String[] files = inputFile.split(",");
		Hashtable sqls = null;

		if ((files == null) || (files.length == 0))	{
			sqls = loadOneSQLFile(rulesFile, inputFile);

		} else	{
			sqls = new Hashtable();

			for(int i=0;i<files.length;++i) {
				Hashtable tempSqls = null;

				tempSqls = loadOneSQLFile(rulesFile, files[i]);

				sqls.putAll(tempSqls);
			}
		}

		return sqls;
	}


	/**
	 * @param rulesFile
	 * @param inputFile
	 * @return java.util.Hashtable
	 * @roseuid 3EA0670D032E
	 */
	private Hashtable loadOneSQLFile(String rulesFile, String inputFile) {

		try {
			URL rules = DAOSQLLoader.class.getClassLoader().getResource( rulesFile );
        	if (rules == null) {
				// lets try the threads class loader
            	ClassLoader loader = Thread.currentThread().getContextClassLoader();
            	rules = loader.getResource( rulesFile );
        	}

			URL input = DAOSQLLoader.class.getClassLoader().getResource( inputFile );
        	if (input == null) {

				// lets try the threads class loader
            	ClassLoader loader = Thread.currentThread().getContextClassLoader();
            	input = loader.getResource( inputFile );
        	}

			if (rules == null || input == null)
				throw new XRuntime( _MODULE, "The Rules File and Input DAO SQL File could not be read" + inputFile );

			Digester digester = DigesterLoader.createDigester( rules );

			digester.setNamespaceAware ( false );
			digester.setValidating	   ( false );

			DAOSQLConfiguration config = (DAOSQLConfiguration)digester.parse( input.openStream() );

            adjustSelectInQueries( config );

            annotateSQLWithOrigin( config, inputFile );

	        return config.getDAOSQLStatements();
		}
		catch (MalformedURLException mue) {

			mue.printStackTrace();
			throw new XRuntime( _MODULE, mue.getMessage() );
		}
		catch (IOException ioe) {

			ioe.printStackTrace();
			throw new XRuntime( _MODULE, ioe.getMessage() );
		}
		catch (SAXException se) {

			se.printStackTrace();
			throw new XRuntime( _MODULE, se.getMessage() );
		}
	}


    private void adjustSelectInQueries(DAOSQLConfiguration config) {

        for(int i=0; i<config.getDAOSQLStatements().size(); i++) {
            DAOSQLStatement daoSqlStmt =  (DAOSQLStatement)config.getDAOSQLStatements().get(config.getDAOSQLStatements().keySet().toArray()[i]);
            DAOParam 	 param  = null;
            Vector 		 params = daoSqlStmt.getCriteriaParams();
            int 		 size   = params == null ? 0 : params.size();

            Map daoSqls = daoSqlStmt.getDaoSqls();
            Iterator iterator = daoSqls.keySet().iterator();
            boolean paramSetFlag = false;
            while (iterator.hasNext()) {
                int position = 0;
                String serverName = (String) iterator.next();
                String sqlString = daoSqlStmt.getSQL( serverName );

                for (int index = 0; index < size; index++) {
                    param = (DAOParam)params.get( position );
                    if (!param.getIsCollection()) {
                        position++;
                        continue;
                    }
                    if (param.getMaxInCount() < 1)
                        throw new XRuntime( _MODULE, "The specified Param property : maxInCount for " + param.getName() +" is not valid or it is not specified. Check that the property exists in the DAOSQL file" );
                    if (param.getMaxInCount() == 1) {
                        position++;
                        continue;
                    }
                    // Only one time New Params are added for a particular DAOSQLStatement.
                    if (!paramSetFlag) {
                        populateNewDaoParams( daoSqlStmt, param.getMaxInCount(), position, param );
					}
					String replacementString = getReplacementString( param.getMaxInCount() );
                    sqlString = sqlString.replaceFirst("\\s*IN\\s*\\(\\s*\\?\\s*\\)", replacementString );
                    daoSqlStmt.setSqlString( sqlString, serverName );
                    position += param.getMaxInCount();
                }

	            if(params != null && params.size() > 0) {
					// set the correct index for all the params
					for (int j=1; j<=params.size(); j++) {
						param = (DAOParam)params.get( j-1 );
						param.setIndex(j);
					}
	            }
               paramSetFlag = true;
            }
        }
    }

    private String getReplacementString(int size)
    {
        StringBuffer buffer = new StringBuffer( " IN( " );
        for (int i=0; i<size; i++)
            buffer.append( "?," );
        return buffer.deleteCharAt( buffer.length() - 1 ).append( " )" ).toString();
    }

    private void populateNewDaoParams(DAOSQLStatement daoSqlStmt, int size, int index, DAOParam param)
    {
        size--;
        for (int i=0; i<size; i++) {
            DAOParam newParam = new DAOParam( param );
            newParam.setName(param.getName());
            newParam.setIndex( index++ );
            daoSqlStmt.getCriteriaParams().insertElementAt( newParam, index );
        }
    }

    private void annotateSQLWithOrigin(DAOSQLConfiguration config, String inputFile) {

		/* Get rid of the "path" portion of the input file name */
		int lastFolderSeparatorIndx = inputFile.lastIndexOf('/');
		lastFolderSeparatorIndx++;
		String origin = inputFile.substring( lastFolderSeparatorIndx );

        for(int i=0; i<config.getDAOSQLStatements().size(); i++) {
            DAOSQLStatement daoSqlStmt =  (DAOSQLStatement)config.getDAOSQLStatements().get(config.getDAOSQLStatements().keySet().toArray()[i]);

            Hashtable daoSqls = daoSqlStmt.getSQLS();
            Enumeration enumer = daoSqls.elements();
            while (enumer.hasMoreElements() ) {
				DAOSQL daoSql = (DAOSQL) enumer.nextElement();

				if ( isAnnotatable( daoSql )) {

					// Add SQL comment to the SQL text, containing info about the origin: filename and statement name.
					// This is to enhance DBStatement/DBPrepared statement logged messages.
					String annotatedSql = "/* " + origin + " " + daoSqlStmt.getName() + " */\n" + daoSql.getSql() + "\n";
					daoSql.setSql( annotatedSql );
				}
            }
        }
    }

    private boolean isAnnotatable( DAOSQL daoSql ) {
		if (!daoSql.isAllowAnnotation()) {
			return false;
		}
		else {
			String firstSqlWord = (daoSql.getSql().trim().toUpperCase().split("[ \n\r\t]+"))[0];
			boolean appearsToBeNormalSqlStatement = "SELECT".equals(firstSqlWord)
					|| "UPDATE".equals(firstSqlWord)
					|| "INSERT".equals(firstSqlWord)
					|| "DELETE".equals(firstSqlWord)
				//	|| ( firstSqlWord!=null && firstSqlWord.startsWith("{"))			// is reported to cause SQL ERROR ORA-900 Invalid SQL statement
					;
			return appearsToBeNormalSqlStatement;
		}
	}


}

