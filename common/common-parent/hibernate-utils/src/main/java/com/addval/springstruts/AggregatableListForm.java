package com.addval.springstruts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import com.addval.environment.Environment;
import com.addval.ejbutils.server.EJBSEditorMetaDataHome;
import com.addval.ejbutils.server.EJBSEditorMetaDataRemote;
import com.addval.environment.EJBEnvironment;
import com.addval.dbutils.RSIterAction;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.ColumnMetaData;
import com.addval.ejbutils.dbutils.EJBResultSet;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.ArrayList;
import org.apache.struts.util.LabelValueBean;
import java.util.Vector;


/**
 * <b>An Action Form based on AddVal Editor Metadata Framework. This form is derived
 * from ListForm to display an aggregate list. This form can be
 * used to display a list.The user can select a list of dimensions to display
 * list of measures, and a filter.
 *
 * <form-bean name="carrierListForm" type="com.addval.struts.ListForm"
 * className="com.addval.struts.BaseFormBeanConfig">
 * <set-property property="securityManagerType"
 * value="com.addval.cargoresui.rulesui.RulesListSecurityManager" />
 * <set-property property="initialLookup" value="true" />
 * <set-property property="editorName" value="CARRIER" />
 * </form-bean>
 * </b>@author
 * AddVal Technology Inc.
 */
public class AggregatableListForm extends ListForm
{
	private ArrayList _allGroupByColumns = null;
	private ArrayList _allSearchColumns = null;
	private java.lang.String _groupByColumns[] = null;
	private ArrayList _allMeasures = null;
	private java.lang.String _measures[] = null;
	private java.lang.String _whereClause = null;
	private boolean _isRollup = false;
	private boolean _isCube = false;
	private String _filterColumns = null;
	private String _selectedFilterString = null;
	private Vector _selectedFilters = new Vector();
	private String _filters = null;
	private String _search = null;

	public ArrayList getAllSearchColumns()
	{
		return _allSearchColumns;
	}

	public void setAllSearchColumns(ArrayList searchColumns)
	{
		_allSearchColumns = searchColumns;
	}

	public ArrayList getAllGroupByColumns()
	{
		return _allGroupByColumns;
	}

	public void setAllGroupByColumns(ArrayList groupByColumns)
	{
		_allGroupByColumns = groupByColumns;
	}

	public String[] getGroupByColumns()
	{
		return _groupByColumns;
	}

	public void setGroupByColumns(String[] groupByColumns)
	{
		_groupByColumns = groupByColumns;
	}

	public ArrayList getSelectedGroupByColumns()
	{
		ArrayList selectColumns = new ArrayList();

		if ((getGroupByColumns() != null) && (_metadata != null))
		{
			for(int i=0; i<getGroupByColumns().length;++i)
			{
				selectColumns.add(_metadata.getColumnMetaData(getGroupByColumns()[i]));
			}
		}

		return selectColumns;
	}

	public String getGroupByColumnsString()
	{
		return null;
	}


	public ArrayList getAllMeasures()
	{
		return _allMeasures;
	}

	public void setAllMeasures(ArrayList allMeasures)
	{
		_allMeasures = allMeasures;
	}

	public String[] getMeasures()
	{
		return _measures;
	}


	public ArrayList getSelectedMeasures()
	{
		ArrayList selectMeasures = new ArrayList();

		if ((getMeasures() != null) && (_metadata != null))
		{
			for(int i=0; i<getMeasures().length;++i)
			{

				selectMeasures.add(_metadata.getColumnMetaData(getMeasures()[i]));
			}
		}

		return selectMeasures;
	}

	public String getSelectedMeasuresString()
	{
		return null;
	}


	public void setMeasures(String[] measures)
	{
		_measures = measures;
	}

	public String getWhereClause()
	{
		return _whereClause;
	}

	public void setWhereClause(String aClause)
	{
		_whereClause = aClause;
	}

	public String getFilterColumns()
	{
		return _filterColumns;
	}

	public void setFilterColumns(String aFilterColumns)
	{
		_filterColumns = aFilterColumns;
	}

	public boolean getRollupOption()
	{
		return _isRollup;
	}

	public void setRollupOption(boolean option)
	{
		_isRollup = option;
	}


	public boolean getCube()
	{
		return _isCube;
	}

	public void setCube(boolean option)
	{
		_isCube = option;
	}


	public void reset(ActionMapping mapping, HttpServletRequest request)
	{
		super.reset(mapping, request);

		setAllGroupByColumns(_metadata);
		setAllMeasures(_metadata);
		setAllSearchColumns(_metadata);
	}


	private void setAllGroupByColumns(EditorMetaData metadata)
	{
		setAllGroupByColumns(new ArrayList());

		if (_metadata != null)
		{
	        Iterator 		iterator 		= metadata.getColumnsMetaData().iterator();
	        ColumnMetaData 	columnMetaData	= null;

	        while (iterator.hasNext())
	        {
	            columnMetaData = (ColumnMetaData)iterator.next();

	            if (columnMetaData.isDisplayable() && !(columnMetaData.isAggregatable() || columnMetaData.isLinkable() ))
	            {
					getAllGroupByColumns().add(columnMetaData);
				}
			}

		}
	}




	private void setAllMeasures(EditorMetaData metadata)
	{
		setAllMeasures(new ArrayList());

		if (_metadata != null)
		{
	        Iterator 		iterator 		= metadata.getColumnsMetaData().iterator();
	        ColumnMetaData 	columnMetaData	= null;

	        while (iterator.hasNext())
	        {
	            columnMetaData = (ColumnMetaData)iterator.next();

	            if (columnMetaData.isAggregatable())
	            {
					getAllMeasures().add(columnMetaData);
				}
			}

		}
	}


	private void setAllSearchColumns(EditorMetaData metadata)
	{
		setAllSearchColumns(new ArrayList());

		if (_metadata != null)
		{
	        Iterator 		iterator 		= metadata.getColumnsMetaData().iterator();
	        ColumnMetaData 	columnMetaData	= null;

	        while (iterator.hasNext())
	        {
	            columnMetaData = (ColumnMetaData)iterator.next();

	            if (columnMetaData.isSearchable())
	            {
					getAllSearchColumns().add(columnMetaData);
				}
			}

		}
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors = new ActionErrors();

		if( request.getParameter("groupByColumns") == null )
			setGroupByColumns( null );

		if( request.getParameter("measures") == null )
			setMeasures( null )	;

		if (getGroupByColumns() == null || getGroupByColumns().length == 0)
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError( "errors.invalid","Select alteast one Dimension") );

		return errors;
	}

	public void setSelectedFilters(Vector aSelectedFilters)
	{
		_selectedFilters = aSelectedFilters;
	}
	public Vector getSelectedFilters()
	{
		return _selectedFilters;
	}

	public String getSelectedFilterString()
	{
		return _selectedFilterString;
	}

	public void setSelectedFilterString(String  aSelectedFilterString)
	{
		_selectedFilterString = aSelectedFilterString;
	}

	public void setFilters(String aFilters)
	{
		_filters = aFilters;
	}

	public String getFilters()
	{
		return _filters;
	}

	public void setSearch(String aSearch)
	{
		_search = aSearch;
	}

	public String getSearch()
	{
		return _search;
	}

	public void configureMetadata() {
		setAllGroupByColumns(_metadata);
		setAllMeasures(_metadata);
		setAllSearchColumns(_metadata);
	}
}