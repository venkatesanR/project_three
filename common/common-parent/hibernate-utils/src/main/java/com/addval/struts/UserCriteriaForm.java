//Source file: D:\\projects\\COMMON\\src\\com\\addval\\struts\\UserCriteriaForm.java

package com.addval.struts;

import org.apache.struts.action.ActionForm;
import com.addval.environment.EJBEnvironment;
import com.addval.environment.Environment;
import com.addval.ejbutils.server.EJBSEditorMetaDataRemote;
import com.addval.ejbutils.server.EJBSEditorMetaDataHome;
import com.addval.metadata.EditorMetaData;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Iterator;

import com.addval.metadata.UserCriteria;
import com.addval.metadata.ColumnMetaData;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;

public class UserCriteriaForm extends ActionForm
{
	private String _subSystem = null;
	private String _editorName = null;
	private String _editorType = null;
	private String _userId = null;
	private EditorMetaData _metadata = null;
	private String _newCriteriaName = null;
	private String _criteriaName = null;
	private String _chartType= null;
	private String _chartJs= null;
	private String _newCriteriaDesc = null;
	private String _criteriaDesc = null;
	private ArrayList _criteriaNames = null;
	private Vector _displayables = null;

    private Vector _aggregatables = null;
	private String _selectedDisplayables[] = null;
    private String _selectedSubTotalColumns[] = null;
    private String _selectedAggregatables[] = null;
	private String _kindOfAction = null;
	private String _searchCriteria = null;
	private UserCriteria _criteria = null;
	private String _overwrite = null;
	private boolean _sharedAll;
	private boolean _sharedGroup;
	private ArrayList _userGroups = null;
    private String _owner = null;
    private String[] _customDisplayableList = null;
    private String[] _columnSortOrderSeqList = null;
    private String[] _columnSortOrderList = null;
    private boolean _showSubtotalDetail;


	public UserCriteriaForm()
	{
		setNewCriteriaName(null);
		setNewCriteriaDesc(null);
		setSubSystem(null);
		setEditorName(null);
		setEditorType(null);
		setCriteriaName(null);
		setCriteriaDesc(null);
		setDisplayables(null);
        setAggregatables(null);
		setSelectedDisplayables(null);
		setSelectedAggregatables(null);
        setSelectedSubTotalColumns(null);
        setSearchCriteria(null);
		setCriteria(null);
		setUserId(null);
		setOverwrite(null);
		setUserGroups(null);
        setOwner(null);
        setCustomDisplayableList(null);
    }

	/**
	 * Access method for the _subSystem property.
	 *
	 * @return   the current value of the _subSystem property
	 */
	public String getSubSystem()
	{
		return _subSystem;
	}

	/**
	 * Sets the value of the _subSystem property.
	 *
	 * @param aSubSystem the new value of the _subSystem property
	 */
	public void setSubSystem(String aSubSystem)
	{
		_subSystem = aSubSystem;
	}

	/**
	 * Access method for the _editorName property.
	 *
	 * @return   the current value of the _editorName property
	 */
	public String getEditorName()
	{
		return _editorName;
	}

	/**
	 * Sets the value of the _editorName property.
	 *
	 * @param aEditorName the new value of the _editorName property
	 */
	public void setEditorName(String aEditorName)
	{
		_editorName = aEditorName;
	}

	/**
	 * Access method for the _editorType property.
	 *
	 * @return   the current value of the _editorType property
	 */
	public String getEditorType()
	{
		return _editorType!= null? _editorType : "DEFAULT";
	}

	/**
	 * Sets the value of the _editorType property.
	 *
	 * @param aEditorType the new value of the _editorType property
	 */
	public void setEditorType(String aEditorType)
	{
		_editorType = aEditorType;
	}

	/**
	 * Access method for the _userId property.
	 *
	 * @return   the current value of the _userId property
	 */
	public String getUserId()
	{
		return _userId;
	}

	/**
	 * Sets the value of the _userId property.
	 *
	 * @param aUserId the new value of the _userId property
	 */
	public void setUserId(String aUserId)
	{
		_userId = aUserId;
	}

	/**
	 * Access method for the _metadata property.
	 *
	 * @return   the current value of the _metadata property
	 */
	public EditorMetaData getMetadata()
	{
		return _metadata;
	}

	/**
	 * Sets the value of the _metadata property.
	 *
	 * @param aMetadata the new value of the _metadata property
	 */
	public void setMetadata(EditorMetaData aMetadata)
	{
		_metadata = aMetadata;
	}

	/**
	 * Access method for the _newCriteriaName property.
	 *
	 * @return   the current value of the _newCriteriaName property
	 */
	public String getNewCriteriaName()
	{
		return _newCriteriaName;
	}

	/**
	 * Sets the value of the _newCriteriaName property.
	 *
	 * @param aNewCriteriaName the new value of the _newCriteriaName property
	 */
	public void setNewCriteriaName(String aNewCriteriaName)
	{
		_newCriteriaName = aNewCriteriaName;
	}

	/**
	 * Access method for the _criteriaName property.
	 *
	 * @return   the current value of the _criteriaName property
	 */
	public String getCriteriaName()
	{
		return _criteriaName;
	}

	/**
	 * Sets the value of the _criteriaName property.
	 *
	 * @param aCriteriaName the new value of the _criteriaName property
	 */
	public void setCriteriaName(String aCriteriaName)
	{
		_criteriaName = aCriteriaName;
	}

	/**
	 * Access method for the _criteriaNames property.
	 *
	 * @return   the current value of the _criteriaNames property
	 */
	public ArrayList getCriteriaNames()
	{
		return _criteriaNames;
	}

	/**
	 * Sets the value of the _criteriaNames property.
	 *
	 * @param aCriteriaNames the new value of the _criteriaNames property
	 */
	public void setCriteriaNames(ArrayList aCriteriaNames)
	{
		_criteriaNames = aCriteriaNames;
	}

	/**
	 * Access method for the _displayables property.
	 *
	 * @return   the current value of the _displayables property
	 */
	public Vector getDisplayables()
	{
		return _displayables;
	}

	/**
	 * Sets the value of the _displayables property.
	 *
	 * @param aDisplayables the new value of the _displayables property
	 */
	public void setDisplayables(Vector aDisplayables)
	{
		_displayables = aDisplayables;
	}

    public Vector getDisplayablesColumnNames()
    {
        Vector displayablesCols = new Vector();
        if(_displayables != null){
            for(Iterator iterator = _displayables.iterator();iterator.hasNext();) {
                ColumnMetaData columnMetaData = (ColumnMetaData)iterator.next();
                displayablesCols.add(columnMetaData.getName());
            }
        }
        return displayablesCols;
    }

    public Vector getAggregatables()
    {
		return _aggregatables;
    }

    /**
	 * Access method for the _aggregatables property.
	 *
	 * @return   the current value of the _aggregatables property
	 */
	public Vector getAggregatablesColumnNames()
	{
        Vector aggregatablesCols = new Vector();
        if(_aggregatables != null){
            for(Iterator iterator = _aggregatables.iterator();iterator.hasNext();) {
                ColumnMetaData columnMetaData = (ColumnMetaData)iterator.next();
                aggregatablesCols.add(columnMetaData.getName());
            }
        }
        return aggregatablesCols;
	}

	/**
	 * Sets the value of the _aggregatables property.
	 *
	 * @param aAggregatables the new value of the _aggregatables property
	 */
	public void setAggregatables(Vector aAggregatables)
	{
		_aggregatables = aAggregatables;
	}

	/**
	 * Access method for the _selectedDisplayables property.
	 *
	 * @return   the current value of the _selectedDisplayables property
	 */
	public String[] getSelectedDisplayables()
	{
		return _selectedDisplayables;
	}

	/**
	 * Sets the value of the _selectedDisplayables property.
	 *
	 * @param aSelectedDisplayables the new value of the _selectedDisplayables property
	 */
	public void setSelectedDisplayables(String[] aSelectedDisplayables)
	{
		_selectedDisplayables = aSelectedDisplayables;
	}

	/**
	 * Access method for the _selectedAggregatables property.
	 *
	 * @return   the current value of the _selectedAggregatables property
	 */
	public String[] getSelectedAggregatables()
	{
		return _selectedAggregatables;
	}

	/**
	 * Sets the value of the _selectedAggregatables property.
	 *
	 * @param aSelectedAggregatables the new value of the _selectedAggregatables property
	 */
	public void setSelectedAggregatables(String[] aSelectedAggregatables)
	{
		_selectedAggregatables = aSelectedAggregatables;
	}

	/**
	 * Access method for the _kindOfAction property.
	 *
	 * @return   the current value of the _kindOfAction property
	 */
	public String getKindOfAction()
	{
		return _kindOfAction;
	}

	/**
	 * Sets the value of the _kindOfAction property.
	 *
	 * @param aKindOfAction the new value of the _kindOfAction property
	 */
	public void setKindOfAction(String aKindOfAction)
	{
		_kindOfAction = aKindOfAction;
	}

	/**
	 * Access method for the _searchCriteria property.
	 *
	 * @return   the current value of the _searchCriteria property
	 */
	public String getSearchCriteria()
	{
		return (_searchCriteria != null)?_searchCriteria.trim():_searchCriteria;
	}

	/**
	 * Sets the value of the _searchCriteria property.
	 *
	 * @param aSearchCriteria the new value of the _searchCriteria property
	 */
	public void setSearchCriteria(String aSearchCriteria)
	{
		_searchCriteria = aSearchCriteria;
	}

	/**
	 * Access method for the _criteria property.
	 *
	 * @return   the current value of the _criteria property
	 */
	public UserCriteria getCriteria()
	{
		return _criteria;
	}

	/**
	 * Sets the value of the _criteria property.
	 *
	 * @param aCriteria the new value of the _criteria property
	 */
	public void setCriteria(UserCriteria aCriteria)
	{
		_criteria = aCriteria;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request)
	{
		try {
			BaseActionMapping baseMapping = (BaseActionMapping) mapping;
			String editorName = request.getParameter( "EDITOR_NAME_KEY" ) != null ? request.getParameter("EDITOR_NAME_KEY") :getEditorName();
			
			if( editorName == null ){
				editorName = request.getParameter( "EDITOR_NAME_lookUp" ) != null ? request.getParameter("EDITOR_NAME_lookUp") :getEditorName();
			}
			setEditorName( editorName );
			
			if( getSubSystem() == null){
				String subsystem = (baseMapping.getSubsystem() == null) ? getSubsystem( request ) : baseMapping.getSubsystem();
				setSubSystem( subsystem );
			}
			String 						homeName 		= Environment.getInstance(getSubSystem()).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSEditorMetaDataBeanName", "CargoresEditorMetaData");
			EJBSEditorMetaDataHome   	metaDataHome   	= ( EJBSEditorMetaDataHome ) EJBEnvironment.lookupEJBInterface( getSubSystem(), homeName , EJBSEditorMetaDataHome.class );
			EJBSEditorMetaDataRemote 	metaDataRemote 	= metaDataHome.create( );
			_metadata = metaDataRemote.lookup( getEditorName(), getEditorType() );
		}
		catch (Exception ex){
			_metadata = null;
		}
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors = new ActionErrors();
		return errors;
	}

	public String getNewCriteriaDesc()
	{
		return _newCriteriaDesc;
	}

	public void setNewCriteriaDesc(String aNewCriteriaDesc)
	{
		_newCriteriaDesc = aNewCriteriaDesc;
	}
	public String getCriteriaDesc()
	{
		return _criteriaDesc;
	}
	public void setCriteriaDesc(String aCriteriaDesc)
	{
		_criteriaDesc = aCriteriaDesc;
	}
	public String getOverwrite()
	{
		return _overwrite;
	}
	public void setOverwrite(String aOverwrite)
	{
		_overwrite = aOverwrite;
	}

	public boolean getSharedAll() {
		return _sharedAll;
	}

	public void setSharedAll(boolean aSharedAll)
	{
		this._sharedAll = aSharedAll;
	}

	public boolean getSharedGroup()
	{
		return _sharedGroup;
	}

	public void setSharedGroup(boolean aSharedGroup)
	{
		this._sharedGroup = aSharedGroup;
	}
	public ArrayList getUserGroups()
	{
		return _userGroups;
	}

	public void setUserGroups(ArrayList aUserGroups)
	{
		_userGroups = aUserGroups;
	}

    public String getOwner() {
        return _owner;
    }

    public void setOwner(String owner) {
        this._owner = owner;
    }

    public String[] getCustomDisplayableList() {
        return _customDisplayableList;
    }

    public void setCustomDisplayableList(String[] customDisplayableList) {
        this._customDisplayableList = customDisplayableList;
    }

    public String[] getColumnSortOrderSeqList() {
        return _columnSortOrderSeqList;
    }

    public void setColumnSortOrderSeqList(String[] columnSortOrderSeqList) {
        this._columnSortOrderSeqList = columnSortOrderSeqList;
    }

    public String[] getColumnSortOrderList() {
        return _columnSortOrderList;
    }

    public void setColumnSortOrderList(String[] columnSortOrderList) {
        this._columnSortOrderList = columnSortOrderList;
    }

    public String[] getSelectedSubTotalColumns() {
        return _selectedSubTotalColumns;
    }

    public void setSelectedSubTotalColumns(String[] selectedSubTotalColumns) {
        this._selectedSubTotalColumns = selectedSubTotalColumns;
    }

    public boolean isShowSubtotalDetail() {
        return _showSubtotalDetail;
    }

    public void setShowSubtotalDetail(boolean showSubtotalDetail) {
        this._showSubtotalDetail = showSubtotalDetail;
    }

    public String getChartType() {
        return _chartType;
    }

    public void setChartType(String chartType) {
        this._chartType = chartType;
    }

	public String getChartJs() {
		return _chartJs;
	}

	public void setChartJs(String chartJs) {
		this._chartJs = chartJs;
	}
	
	public String getSubsystem(HttpServletRequest req){
		org.apache.struts.config.ModuleConfig appConfig = (org.apache.struts.config.ModuleConfig) req.getAttribute(org.apache.struts.Globals.MODULE_KEY);
		BaseControllerConfig baseConfig = (BaseControllerConfig) appConfig.getControllerConfig();
		return baseConfig.getSubsystem();
	}
	

}

