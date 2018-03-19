//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\DAOSQLConfiguration.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.util.Iterator;
import java.util.Hashtable;

/**
 * @author AddVal Technology Inc.
 */
public class DAOSQLConfiguration {
	private Hashtable _DAOSQLStatements = null;

	/**
	 * Access method for the _DAOSQLStatements property.
	 *
	 * @return   the current value of the _DAOSQLStatements property
	 */
	public Hashtable getDAOSQLStatements() {
		return _DAOSQLStatements;
	}

	/**
	 * Sets the value of the _DAOSQLStatements property.
	 *
	 * @param aDAOSQLStatements the new value of the _DAOSQLStatements property
	 */
	public void setDAOSQLStatements(Hashtable aDAOSQLStatements) {
		_DAOSQLStatements = aDAOSQLStatements;
	}

	/**
	 * @param sqlStatement
	 * @roseuid 3EA06B1F03C2
	 */
	public void addDAOSQLStatement(DAOSQLStatement sqlStatement) {

		if (_DAOSQLStatements == null)
			_DAOSQLStatements = new Hashtable();

		_DAOSQLStatements.put( sqlStatement.getName(), sqlStatement );
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3EA0734503D6
	 */
	public String toString() {

		StringBuffer sqls = new StringBuffer();

		if (getDAOSQLStatements() != null) {

			Iterator 		iterator = getDAOSQLStatements().keySet().iterator();
			DAOSQLStatement sql 	 = null;

			while (iterator.hasNext()) {

				String name = (String)iterator.next();
				sqls.append( "Name : " + name );
				sqls.append( System.getProperty( "line.separator" ) );

				sqls.append( getDAOSQLStatements().get( name ).toString() );
				sqls.append( System.getProperty( "line.separator" ) );
			}
		}

		return sqls.toString();
	}
}
