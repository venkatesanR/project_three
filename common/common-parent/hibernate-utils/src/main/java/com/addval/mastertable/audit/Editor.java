package com.addval.mastertable.audit;

import java.util.*;


public class Editor
{
    private String _sourceName;
    private String _interceptor;
    private String _sourceSql;
    private String _name;
    private Hashtable _columns;
    private Vector _errors;
	private int count;


    public Editor(String name)
    {
        _name = name;
        _columns = new Hashtable();
        _errors=new Vector();

    }

    public String getName()
    {
        return _name;
    }
    
    public void setSourceName(String name)
    {
        _sourceName=name;
    }

    public String getSourceName()
    {
        return _sourceName;
    }

    public void setInterceptor(String interceptor)
    {
        _interceptor=interceptor;
    }

    public String getInterceptor()
    {
        return _interceptor;
    }
    public void setSourceSql(String sourceSql)
   {
           _sourceSql=sourceSql;
   }

    public String getSourceSql()
    {
       return _sourceSql;
   }

    public void addColumn(Column column)
    {
        count++;
		_columns.put( column.getName(), column );

    }

    public Column getColumn(String name)
    {
        return (Column)_columns.get( name );
    }
    public Hashtable getColumnMap()
    {
        return _columns;

    }


    public Set getColumns()
    {

		return _columns.keySet();
    }


    public int getSizeOfColumns()
    {
        return _columns.size();
    }
    public boolean isColumnExisting(String name)
    {
        return _columns.containsKey( name );
    }

    public void addError(String error)
    {
        _errors.addElement(error);
		count++;
    }

	public int getErrorCount()
	{
    	return count;

	}
    public Vector getErrors()
    {
        return _errors;
    }

	public List getColumnErrors()  {
		List list = new ArrayList();
     	for (Iterator iter = _columns.values().iterator(); iter.hasNext();) {
			Column column = (Column)iter.next();
			column.analyseColumn(column.getName(),getSourceName(),column.getType());
			list.addAll( column.getErrors() );
		}
		return list;
	}

	public List getColumnWarnings()
	{
		List list = new ArrayList();
		for (Iterator iter = _columns.values().iterator(); iter.hasNext();) {
			Column column = (Column)iter.next();
			list.addAll( column.getWarnings() );
		}
		return list;
	}

}