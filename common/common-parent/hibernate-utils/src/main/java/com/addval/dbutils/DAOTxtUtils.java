
package com.addval.dbutils;

import org.apache.commons.beanutils.WrapDynaBean;
import com.addval.utils.XRuntime;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Time;
import com.addval.metadata.ColumnDataType;
import com.addval.utils.AVConstants;
import org.apache.commons.beanutils.ConvertingWrapDynaBean;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import com.addval.utils.DateUtl;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.Connection;

public class DAOTxtUtils
{
	private static final String _module = "com.addval.utils.DAOTxtUtils";
    private String _serverType = null;
	private DAOSQLStatement _statement = null;
	private HashMap _values = null;
	private HashMap _tagNames = null;

	public DAOTxtUtils(DAOSQLStatement statement, String serverType){
		_statement  = statement;
		_serverType = serverType;
		_values = new HashMap();
		_tagNames = new HashMap();
	}

	private HashMap getTagSet(int index) {
		String template =  getStatement().getSQL( getServerType() );
        int position = 0;
		int pos = -1;

        for(int ind = 1;ind <= index; ind++){
            pos = template.indexOf("?");
            if(pos == -1){
                throw new XRuntime("com.addval.dbutils.DAOTxtUtils.getTagSet()", "? not found");
            }
            template= template.substring(pos + 1);
            position += pos + 1;
        }
        template =  getStatement().getSQL( getServerType() );
        for(pos = position; pos > 0; pos--) {
            if(template.charAt(pos) == '<' ) {
                break;
            }
        }

        String tagName =template.substring(pos+1,position-1);
        StringBuffer tagSet = new StringBuffer(template.substring(pos,template.indexOf("</"+tagName +">") + tagName.length()+ 3 ) );
		HashMap tag = new HashMap();
        tag.put(tagName,tagSet.toString());
		return tag;
	}

	private void loadTagName(DAOParam param)  {
		if(param.getColumnType() == ColumnDataType._CDT_ITERATOR){
			int index = param.getIndex();
			Vector params  = getStatement().getCriteriaParams();
			_tagNames.put(String.valueOf(index),getTagSet(index));
			DAOParam tempParam = null;
			for (int ind = param.getIndex(); ind < index + param.getParameters(); ind++) {
				tempParam = (DAOParam)params.elementAt( ind );
				loadTagName(tempParam);
			}
		}
	}

	private int getParameterCount(DAOParam param){
		int retVal=0;

		if(param.getColumnType() == ColumnDataType._CDT_ITERATOR){
			Vector params  = getStatement().getCriteriaParams();
			retVal += param.getParameters();
			DAOParam tempParam = null;
			for (int ind = param.getIndex(); ind < param.getIndex() + param.getParameters(); ind++) {
				tempParam = (DAOParam)params.elementAt( ind );
				retVal += getParameterCount(tempParam);
			}
		}
		else {
			retVal += param.getParameters();
		}
		return retVal;
	}

    private void setValue(DAOParam param,Object value ,StringBuffer buffer){
		int index = param.getIndex();
		int pos = -1;
        if (value == null) {
			//System.out.println("Value is Null" );
			pos = buffer.toString().indexOf("?");
			buffer = buffer.replace(pos, pos + 1,"");
		}
		else {
            Vector params  = getStatement().getCriteriaParams();
            WrapDynaBean bean = null;
			DAOParam tempParam = null;

            switch (param.getColumnType()) {
                case ColumnDataType._CDT_ITERATOR:
                    Collection collection = (Collection) value;

                    HashMap tag =(HashMap)_tagNames.get(String.valueOf(index));
                    String tagName = (String)tag.keySet().iterator().next();
                    String tagSet = (String)tag.get(tagName);
                    boolean isEmpty = !collection.iterator().hasNext();
                    if(isEmpty) {
						buffer.insert(buffer.toString().indexOf("?")-tagName.length() -1, "</" + tagName +">");
					}

                    for(Iterator iterator = collection.iterator();iterator.hasNext();) {

						bean = new WrapDynaBean( iterator.next() );

                        buffer.insert(buffer.toString().indexOf("?")-tagName.length() -1, tagSet);
                        buffer = buffer.deleteCharAt(buffer.toString().indexOf("?"));

						for (int ind = param.getIndex(); ind < index + param.getParameters(); ind++) {
							tempParam = (DAOParam)params.elementAt( ind );
							if (tempParam.hasInMode() && bean.getDynaClass().getDynaProperty( tempParam.getName() ) != null ) {
								setValue(tempParam,bean.get( tempParam.getName() ),buffer);
							}
						}

					}
                    pos = buffer.toString().indexOf(tagSet);
                    buffer = buffer.delete(pos,pos + tagSet.length());
                    break;
                case ColumnDataType._CDT_OBJECT:

                	buffer = buffer.deleteCharAt(buffer.toString().indexOf("?"));

                    bean = new WrapDynaBean( value );
					for (int ind = param.getIndex(); ind < param.getIndex() + param.getParameters() ; ind++) {
						tempParam = (DAOParam)params.elementAt( ind );
						if (tempParam.hasInMode() && bean.getDynaClass().getDynaProperty( tempParam.getName() ) != null ) {
							setValue(tempParam,bean.get( tempParam.getName() ),buffer);
						}
					}
                    break;
                default :
                    pos = buffer.toString().indexOf("?");
                    buffer = buffer.replace(pos, pos + 1,value.toString());
            }
        }
    }
	public void setProperties(Object instance) {
		if (instance == null)
			return;
		WrapDynaBean bean   = null;
		DAOParam 	 param  = null;
		Object 		 value  = null;
		Vector 		 params = getStatement().getCriteriaParams();
		int 		 size   = params == null ? 0 : params.size();
		for (int index = 0; index < size; index++) {
			if (bean == null)
				bean = new WrapDynaBean( instance );

			param = (DAOParam)params.elementAt( index );
			if (param.hasInMode() && bean.getDynaClass().getDynaProperty( param.getName() ) != null) {
				value = bean.get( param.getName() );
                value = (value != null) ? value :"";
                _values.put(String.valueOf(param.getIndex()),value);
			}
		}
	}
    public String getText(Map values)  {
		String retVal = null;
        StringBuffer buffer = new StringBuffer( getStatement().getSQL( getServerType() ) );

        if (values != null) {
            DAOParam 	 param  = null;
            Object 		 value  = null;
            Vector 		 params = getStatement().getCriteriaParams();
            int 		 size   = params == null ? 0 : params.size();
            for (int index = 0; index < size; index++) {
                param = (DAOParam)params.elementAt( index );
                value = values.get( param.getName() );
                if (param.hasInMode() && values.containsKey( param.getName() ))
                    setValue(param,value,buffer);
            }
        }

        retVal = buffer.toString();
        return retVal;
    }

    public String getText()  {
		String retVal = null;
        Vector params  = getStatement().getCriteriaParams();
        if(params == null)
            return retVal;

        StringBuffer buffer = new StringBuffer( getStatement().getSQL( getServerType() ) );
        DAOParam	param  = null;
		Object value  = null;
        String key = null;

        for (int index = 0; index < params.size(); index++) {
			param = (DAOParam)params.elementAt( index );
            key = String.valueOf( param.getIndex() );
            value = _values.get(key);
			loadTagName(param);
            setValue(param,value,buffer);
            index += getParameterCount(param);
        }
        retVal = buffer.toString();
        return retVal;

	}

	public String getServerType() {
		return _serverType;
	}

	public void setServerType(String aServerType) {
		_serverType = aServerType;
	}

	public DAOSQLStatement getStatement() {
		return _statement;
	}

	public void setStatement(DAOSQLStatement aStatement) {
		_statement = aStatement;
	}
}



