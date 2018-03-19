package com.addval.jasperutils;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBStatement;
import com.addval.dbutils.RSIterator;
import com.addval.dbutils.RSIterAction;

public class EJBResultSetJasperDataSource implements JRDataSource {

	EJBResultSet _ejbRS 	= null;
	EJBStatement _ejbStmt 	= null;
	RSIterator   _rsItem    = null;

    public EJBResultSetJasperDataSource(EJBResultSet ejbRS){
		_ejbRS = ejbRS;
		_ejbStmt = new EJBStatement ( _ejbRS );
		_rsItem  = new RSIterator(_ejbStmt, "0", new RSIterAction(RSIterAction._FIRST_STR), _ejbRS.getRecords().size(), _ejbRS.getRecords().size());
	}

    public boolean next() throws JRException{
		return _rsItem.next();
	}

	public EJBResultSet getResultSet() {
		return _ejbRS;
	}
	
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		value = (Object) _rsItem.getString(fieldName);
		return (value != null) ? value : "";
	}
}
