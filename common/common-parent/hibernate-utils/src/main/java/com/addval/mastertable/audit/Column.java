package com.addval.mastertable.audit;


import java.util.Vector;
import java.util.List;



public class Column
{
    private String _name;
    private String _type;
    private String _format;
    private int _columnCombo;
    private String _combotblName;
    private String _comboSelect;
    private String _orderBy;
    private int _minVal;
    private int _maxVal;
    private String _regExp;
    private String _errorMsg;
    private String _sourceName;
    private String _interceptor;
    private String _sourceSql;
    private int _columnKey;
    private int _comboCached;
    private int _comboOvd;
    private String _tblNameOvd;
    private String _selectOvd;
    private int _editKey;
    private int _nullable;
    private String _autoSeqName;
    private int _selectTag;
    private String _orderByOvd;
	private List _errors = null;
	private String _metaDataName;
	private String _dbType = null;
	private List _warnings=null;
	private String _dbFormat;
	private int _dbPrecision;
	private int _dbSize;
	private int _scale;



	public Column(String name)
    {
        _name = name;
		_errors = new Vector();
		_warnings=new Vector();
     }

    public String getName()
    {
        return _name;
    }
    public void setType(String type)
    {
        _type=type;
    }

    public String getType()
    {
        return _type;
    }
    public void  setFormat(String format)
    {
        _format=format;
    }
     public String getFormat()
     {
         return _format;
     }

	public void addError(String error)
	{
		_errors.add( error );
	}

	public void addWarning(String warning)
		{
			_warnings.add( warning );
		}

	public List getErrors()
	{

		return _errors;
	}

	public List getWarnings()
	{
		return _warnings;
	}
 	public void analyseColumn(String name,String sourceName,String type)  {

   	 //if(!(name.equals("EDIT")||(name.equals("DELETE"))||(name.equals("CLONE"))))
		 if(!(type.equals("CDT_LINK")))
		 {
		 if (_dbType == null)
		 		addError( "Column "+name +" : "+" not present in table " + sourceName );
			else if (!_dbType.equals( _type ))
			addWarning( "Column "+name +" : "+" Column Type " + _type + " is not matching with DB Type " + _dbType );
		 }
	 }

    public void setcolumnCombo(int columnCombo)
    {
    _columnCombo= columnCombo;
    }
    public int getcolumnCombo()
    {
        return _columnCombo;
    }
    public void setcomboTblName(String tblName)
    {
        _combotblName=tblName;
    }
    public String getcomboTblName()
    {
        return _combotblName;
    }

    public void setcomboSelect(String comboSelect)
    {
      _comboSelect =comboSelect;

    }
    public String getcomboSelect()
    {
        return _comboSelect;
    }
    public void setcomboOrderBy(String orderBy)
    {
        _orderBy= orderBy;
    }
    public String getcomboOrderBy()
    {
        return _orderBy;
    }

    public void setcolumnMinVal(int minVal)
    {
        _minVal=minVal;
    }
    public int getcolumnMinVal()
    {
        return _minVal;
    }
    public void setcolumnMaxVal(int maxVal)
    {
        _maxVal=maxVal;
    }
    public int getcolumnMaxVal()
    {
        return _maxVal;
    }
    public void setcolumnRegExp(String regExp)
    {
        _regExp=regExp;
    }
    public String getcolumnRegExp()
    {
        return _regExp;

    }

    public void setcolumnErrorMsg(String errorMsg)
    {
        _errorMsg=errorMsg;
    }
    public String getcolumnErrorMsg()
    {
        return _errorMsg;
    }

    public void seteditorSourceName(String sourceName)
    {
        _sourceName=sourceName;
    }
    public String geteditorSourceName()
    {
        return _sourceName;
    }

        public void seteditorInterceptor(String interceptor)
    {
        _interceptor=interceptor;
    }

    public String geteditorInterceptor()
    {
        return _interceptor;
    }
    public void seteditorSourceSql(String sourceSql)
        {
            _sourceSql=sourceSql;
        }

        public String geteditorSourceSql()
        {
            return _sourceSql;
        }
    public void seteditorColumns_columnKey(int columnKey)
    {
        _columnKey=columnKey;
    }
    public int geteditorColumns_columnKey()
    {
        return _columnKey;
    }
      public void seteditorColumns_selectTag(int selectTag)
    {
        _selectTag=selectTag;
    }
    public int geteditorColumns_selectTag()
    {
        return _selectTag;
    }

    public void seteditorColumns_comboCached(int comboCached)
      {
          _comboCached=comboCached;
      }
      public int geteditorColumns_comboCached()
      {
          return _comboCached;
      }
    
    public void seteditorColumns_comboOvd(int comboOvd)
       {
           _comboOvd=comboOvd;
       }
       public int geteditorColumns_comboOvd()
       {
           return _comboOvd;
       }

    public void seteditorColumns_tblName(String tblNameOvd)
         {
             _tblNameOvd=tblNameOvd;
         }
         public String geteditorColumns_tblNameOvd()
         {
             return _tblNameOvd;
         }
      public void seteditorColumns_selectOvd(String selectOvd)
         {
             _selectOvd=selectOvd;
         }
         public String geteditorColumns_selectOvd()
         {
             return _selectOvd;
         }

    public void seteditorColumns_orderByOvd(String orderByOvd)
    {
       _orderByOvd =orderByOvd;
    }
    public String geteditorColumns_orderByOvd()
    {
       return  _orderByOvd;

    }
    public void seteditorTypeColumns_editKey(int editKey)
    {
        _editKey=editKey;
    }
    public int  geteditorTypeColumns_editKey()
    {
        return _editKey;
    }

      public void seteditorTypeColumns_nullable(int nullable)
    {
       _nullable=nullable;
    }
    public int  geteditorTypeColumns_nullable()
    {
        return _nullable;
    }
     public void seteditorTypeColumns_autoSeqName(String autoSeqName)
    {
       _autoSeqName=autoSeqName;
    }
    public String geteditorTypeColumns_autoSeqNamee()
    {
        return _autoSeqName;
    }

	public void setDBName(String metaDataName)
	{
		_metaDataName = metaDataName;
	}
	public String getDBName()
	{
			return _metaDataName;
	}

	public void setDBType(String dbType)
	{

		_dbType=dbType;
		int scale =getDBScale();
     	if (_dbType.equals( "NUMBER" )){
			 if(scale != 0)
                _dbType ="CDT_DOUBLE";
			 else
				 _dbType="CDT_INT";
       		 }
			  	else if (_dbType.equals("VARCHAR2"))
				  	_dbType = "CDT_STRING";
			  	else if (_dbType.equals("DATE"))
				  	_dbType="CDT_DATE";
			  	else if (_dbType.equals("TIMESTAMP"))
				  	_dbType="CDT_TIMESTAMP";

	}

	public String getDBType()
	{
		return _dbType;
	}


	public void setDBFormat(String dbFormat)
	{
		_dbFormat =dbFormat;
	}
	public String getDBFormat()
	{
		return _dbFormat;
	}

	public void setDBPrecision(int dbPrecision)
	{
		_dbPrecision = dbPrecision;
	}

	public int getDBPrecision()
	{
		return _dbPrecision;
	}

	public void setDBSize(int dbSize)
	{
		_dbSize=dbSize;
	}
	public int getDBSize()
	{
		return _dbSize;
	}
	public void setDBScale(int scale)
	{
		_scale =scale;

	}
	public int getDBScale()
	{
		return _scale;
	}
}







