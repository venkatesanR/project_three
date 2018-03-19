package com.addval.ejbutils.server;

import com.addval.utils.XRuntime;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.UpdateUserCriteria;
import java.util.StringTokenizer;

public class UpdateUserCriteriaSqlBuilder
{
	private EditorMetaData metadata = null;
	private UpdateUserCriteria criteria = null;

	public UpdateUserCriteriaSqlBuilder(EditorMetaData metadata, UpdateUserCriteria criteria) {
		this.metadata = metadata;
		this.criteria = criteria;
	}

	public EditorMetaData getMetadata() {
		return metadata;
	}

	public void setMetadata(EditorMetaData metadata) {
		this.metadata = metadata;
	}
	public UpdateUserCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(UpdateUserCriteria criteria) {
		this.criteria = criteria;
	}
	public String getUpdateSql() throws XRuntime {
		StringBuffer sqlBuf = new StringBuffer();
		try {
			String source =null;
			StringBuffer updateValue = new StringBuffer();
			StringBuffer updateWhere = new StringBuffer();

			source = getMetadata().getSource();
			if(source == null) {
				throw new Exception("Editor Source name should not be Null. Please Contact System Administrator" );
			}

			if(getCriteria().getUpdateValue() != null ) {

				StringTokenizer updateOTokens  = null;
				StringTokenizer updateITokens  = null;
				String updateSet = null;
				String columnName= null;
				String columnOperator= null;
				String columnValue= null;
                String firstOperators = "";
                String secondOperators = "";
                String thirdOperators = "";
                String finalOperators = "";

				updateOTokens  = new StringTokenizer(getCriteria().getUpdateValue(),",",false);

				while(updateOTokens.hasMoreTokens()) {

					updateSet = updateOTokens.nextToken();

					updateITokens =  new StringTokenizer(updateSet,"|",false);
					columnName = updateITokens.nextToken();
					columnOperator = updateITokens.nextToken();
					columnValue = updateITokens.nextToken();

                    //Order the columnOperator to be applied as follows
                    //Multiplication (*) or Division (/) first
                    //Addition (+) or Subtraction (-) second
                    //Equals (=) third
                    //other operators last
                    if (columnOperator != null && (columnOperator.equals("*") || columnOperator.equals("/"))) {
                        firstOperators += "," + columnName + "|" + columnOperator + "|" + columnValue;
                    } else
                    if (columnOperator != null && (columnOperator.equals("+") || columnOperator.equals("-"))) {
                        secondOperators += "," + columnName + "|" + columnOperator + "|" + columnValue;
                    } else
                    if (columnOperator != null && columnOperator.equals("=")) {
                        thirdOperators += "," + columnName + "|" + columnOperator + "|" + columnValue;
                    } else
                    if (columnOperator != null)
                    {
                        finalOperators += "," + columnName + "|" + columnOperator + "|" + columnValue;
                    }


/*
					updateValue.append(", ")
							.append( columnName )
							.append( "_OVD" )
							.append( " = " );

					if (columnOperator.trim().equalsIgnoreCase( "=")) {
						updateValue.append("'").append( columnValue ).append("'");
					}
					else {
						updateValue.append(" NVL( ")
							.append( columnName )
							.append( "_OVD" )
							.append( "," )
							.append( columnName )
							.append( ") ")
							.append( columnOperator )
							.append( columnValue );
					}
*/
				}

                String updateString = firstOperators + secondOperators + thirdOperators + finalOperators;

                updateOTokens  = new StringTokenizer(updateString.substring(1),",",false);
                while(updateOTokens.hasMoreTokens()) {
                    updateSet = updateOTokens.nextToken();
                    updateITokens =  new StringTokenizer(updateSet,"|",false);
                    columnName = updateITokens.nextToken();
                    columnOperator = updateITokens.nextToken();
                    columnValue = updateITokens.nextToken();

                    updateValue.append(", ")
                            .append( columnName )
                            .append( "_OVD" )
                            .append( " = " );

                    if (columnOperator.trim().equalsIgnoreCase( "=")) {
                        updateValue.append("'").append( columnValue ).append("'");
                    }
                    else {
                        updateValue.append(" NVL( ")
                            .append( columnName )
                            .append( "_OVD" )
                            .append( "," )
                            .append( columnName )
                            .append( ") ")
                            .append( columnOperator )
                            .append( columnValue );
                    }
                }
			}
			if (updateValue.toString().length() == 0) {
				throw new Exception("Update Values should not be Null." );
			}
			if (getCriteria().getFilter() != null && !getCriteria().getFilter().trim().equals("")) {
				updateWhere.append( " AND " ).append( getCriteria().getFilter() );
			}
//			if (  getCriteria().getDefaultFilter() != null && !getCriteria().getDefaultFilter().trim().equals("") ) {
//				updateWhere.append( " AND " ).append( getCriteria().getDefaultFilter() );
//			}

			if (updateWhere.toString().length() == 0) {
				throw new Exception("Update Where Clause should not be Null." );
			}

			sqlBuf.append( "UPDATE " )
					.append( source )
					.append(" SET ")
					.append( updateValue.toString().substring( 1 ) )
					.append(" WHERE ")
					.append( updateWhere.toString().substring( 4 ) );
		}
		catch(Exception ex) {
			throw new XRuntime(getClass().getName(),ex.getMessage());
		}
		return sqlBuf.toString();
	}
}
