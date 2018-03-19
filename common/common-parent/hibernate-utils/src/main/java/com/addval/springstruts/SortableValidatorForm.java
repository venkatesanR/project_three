// SortableValidatorForm.java

package com.addval.springstruts;

import org.apache.struts.validator.ValidatorForm;
import com.addval.utils.GenUtl;
import com.addval.utils.Sortable;

public abstract class SortableValidatorForm extends ValidatorForm implements Sortable
{
	protected	String _sortBy		= null;
	protected	String _sortOrder	= null;

	public void setSortBy( String aSortBy )
	{
		_sortBy = aSortBy;
	}

	public String getSortBy()
	{
		return _sortBy;
	}

	public void setSortOrder( String aSortOrder )
	{
		_sortOrder = aSortOrder;
	}

	public String getSortOrder()
	{
		if (_sortOrder == null || _sortOrder.trim().length() == 0 )
			_sortOrder = "ASC";
		else if ("DESC".equals( _sortOrder ))
				_sortOrder = "ASC";
			 else
			 	_sortOrder = "DESC";
		return _sortOrder;
	}

	public void performSort( java.util.Vector vectorTobeSorted ) throws Exception
	{
		GenUtl.performSort( getSortBy(), getSortOrder(), vectorTobeSorted );
	}

}