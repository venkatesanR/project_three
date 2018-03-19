package com.addval.utils;

import java.text.MessageFormat;

import com.addval.utils.CnfgFileMgr;
import com.addval.utils.StrUtl;

public class PropertyReader {
	CnfgFileMgr _cnfgFileMgr = null;

	public PropertyReader(){
	}

	public PropertyReader(String propertyFileName){
		_cnfgFileMgr = new CnfgFileMgr( propertyFileName );
	}

	public CnfgFileMgr getCnfgFileMgr(){
		return _cnfgFileMgr;
	}

	public String readPropertyValue(String propertyName, Object[] value) {
        if (StrUtl.isEmptyTrimmed(propertyName)) {
            return null;
        }

        String readedProperty = (String)getCnfgFileMgr().getPropertyValue( propertyName, null);

        if( StrUtl.isEmptyTrimmed( readedProperty ))
        	return null;

        return new MessageFormat(readedProperty).format(value);
    }

	public String readPropertyValue(String propertyName){
		return (String)getCnfgFileMgr().getPropertyValue( propertyName, null);
	}

	public String getDateFormat(){
		return (String)getCnfgFileMgr().getPropertyValue("dateformat", "dd-MMM-yyyy");
	}
}
