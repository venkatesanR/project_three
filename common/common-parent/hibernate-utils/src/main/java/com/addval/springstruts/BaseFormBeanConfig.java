//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\BaseFormBeanConfig.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\BaseFormBeanConfig.java

package com.addval.springstruts;

import org.apache.struts.config.FormBeanConfig;

/**
 * BaseFormBeanConfig Class - This class is used to configure/customize parameters
 * for any form beans defined in  struts-config.xml that are of the following type
 * or derived from them
 * com.addval.struts.ListForm
 * com.addval.struts.EditForm
 * com.addval.struts.DetailEditForm
 * For each Editor defined in the editor metadata, a formbean has to be defined in
 * struts- config.xml. Some of the default characteristics of the form can be
 * specified using this class in the form bean tag as shown below.
 * <form-bean name="carrierListForm" type="com.addval.struts.ListForm"
 * className="com.addval.struts.BaseFormBeanConfig">
 * <set-property property="securityManagerType"
 * value="com.addval.cargoresui.rulesui.RulesListSecurityManager" />
 * <set-property property="initialLookup" value="true" />
 * <set-property property="editorName" value="CARRIER" />
 * </form-bean>
 */
public class BaseFormBeanConfig extends FormBeanConfig
{
	protected String _securityManagerType = "";
	protected String _editorName = "";
	protected String _editorType = "DEFAULT";
	protected String _detailEditorName = "";
	protected String _detailEditorType = "DEFAULT";
	protected boolean _initialLookup = false;
	protected boolean _forLookup = false;
	protected boolean _exactMatchLookup = false;
    protected boolean _additionalFilter = false;
    protected String _subsystem = null;
	protected int _pageSize = 100;
	protected String _viewrole = "";
	protected String _editrole = "";
	protected String _formInterceptorType = "";
	private String _hasChild ="";
	private String _childActions="";
	private String _listedit="";
    private String _footerPrivilege="";
    private String _addNewPrivilege="";
	private String _cancelPrivilege="";
    private String _customDisplayPrivilege="";
    private String _exportPrivilege="";
    private String _scheduleExportPrivilege="";
    private String _multiDeletePrivilege="";
    private String _hasHelp ="";
    private boolean _exactSearch = true;
    // Default number of columns per row = 4
    private int _searchColumnsPerRow = 4;
    // Default ../images/addNew.gif - Set in list tag
	private String _addlinkImage= "../images/add_new.gif";
	// Default ../images/delete_button.gif - Set in list tag
	private String _deletelinkImage= "../images/delete_button.gif";

	/**
	 * set a security manager class name
	 * aSecurityManager is a class name that implements the
	 * com.addval.struts.FormSecurityManager interface.
	 * The security manager class is typically custom for each project. The security
	 * manager class enables implementing data based security by changing the metadata
	 * for an editor at runtime depending on the user
	 * @param aSecurityManager
	 * @roseuid 3E205C300201
	 */
	public void setSecurityManagerType(String aSecurityManager)
	{
		_securityManagerType = aSecurityManager;
	}

	/**
	 * get the security manager class name associated with this configuration
	 * aSecurityManager is a class name that implements the
	 * com.addval.struts.FormSecurityManager interface.
	 * The security manager class is typically custom for each project. The security
	 * manager class enables implementing data based security by changing the metadata
	 * for an editor at runtime depending on the user
	 * @return java.lang.String
	 * @roseuid 3E205C300215
	 */
	public String getSecurityManagerType()
	{
		return _securityManagerType;
	}

	/**
	 * set a Editor Name
	 * This is the name of the editor that is configured in the AddVal Editor
	 * Metadata.
	 * @param aEditorName
	 * @roseuid 3E205C30021F
	 */
	public void setEditorName(String aEditorName)
	{
		_editorName = aEditorName;
	}

	/**
	 * get a Editor Name
	 * This is the name of the editor that is configured in the AddVal Editor
	 * Metadata.
	 * @return java.lang.String
	 * @roseuid 3E205C30022A
	 */
	public String getEditorName()
	{
		return _editorName;
	}

	/**
	 * set Editor Type
	 * Editor Type is the type of editor configured in AddVal Editor Metadata. This is
	 * usually 'DEFAULT'
	 * @param aEditorType
	 * @roseuid 3E205C30023E
	 */
	public void setEditorType(String aEditorType)
	{
		_editorType = aEditorType;
	}

	/**
	 * get Editor Type
	 * Editor Type is the type of editor configured in AddVal Editor Metadata. This is
	 * usually 'DEFAULT'
	 * @return java.lang.String
	 * @roseuid 3E205C300249
	 */
	public String getEditorType()
	{
		return _editorType;
	}

	/**
	 * set a Detail Editor Name
	 * This is the name of the editor that is configured in the AddVal Editor Metadata.
	 * This is used in Master/Detail type of Editor. The Editor and the Detail Editor
	 * should have keys in common
	 * @param aEditorName
	 * @roseuid 3E205C300252
	 */
	public void setDetailEditorName(String aEditorName)
	{
		_detailEditorName = aEditorName;
	}

	/**
	 * get a Detail Editor Name
	 * This is the name of the editor that is configured in the AddVal Editor Metadata.
	 * This is used in Master/Detail type of Editor. The Editor and the Detail Editor
	 * should have keys in common
	 * @return java.lang.String
	 * @roseuid 3E205C300267
	 */
	public String getDetailEditorName()
	{
		return _detailEditorName;
	}

	/**
	 * set Editor Type for Detail Editor
	 * Editor Type is the type of editor configured in AddVal Editor Metadata. This is
	 * usually 'DEFAULT'
	 * @param aEditorType
	 * @roseuid 3E205C300270
	 */
	public void setDetailEditorType(String aEditorType)
	{
		_detailEditorType = aEditorType;
	}

	/**
	 * get Editor Type for Detail Editor
	 * Editor Type is the type of editor configured in AddVal Editor Metadata. This is
	 * usually 'DEFAULT'
	 * @return java.lang.String
	 * @roseuid 3E205C300284
	 */
	public String getDetailEditorType()
	{
		return _detailEditorType;
	}

	/**
	 * set whether there be a lookup done when the list page is first shown to the user
	 * @return boolean
	 * @roseuid 3E205C30028E
	 */
	public boolean getInitialLookup()
	{ return _initialLookup;
	}

	/**
	 * get whether there would be a lookup done when the list page is first shown to
	 * the user
	 * @param lkpStatus
	 * @roseuid 3E205C300298
	 */
	public void setInitialLookup(boolean lkpStatus)
	{
      _initialLookup = lkpStatus;
	}

	/**
	 * set whether this editor/form is to be used for a lookup.
	 * Lookup forms/editors are typically used by users to search based on entering a
	 * few characters.
	 * @return boolean
	 * @roseuid 3E205C3002AC
	 */
	public boolean getForLookup()
	{
		return _forLookup;
	}

	/**
	 * get whether this editor/form is to be used for a lookup.
	 * Lookup forms/editors are typically used by users to search based on entering a
	 * few characters.
	 * @param lkpStatus
	 * @roseuid 3E205C3002B6
	 */
	public void setForLookup(boolean lkpStatus)
	{
      _forLookup = lkpStatus;
	}

	/**
	 * set whether this editor/form is to be used for a exact match lookup.
	 * This flag is used when a description needs to be looked up for a key value in
	 * the user interface.
	 * @return boolean
	 * @roseuid 3E205C3002C1
	 */
	public boolean getExactMatchLookup()
	{
		return _exactMatchLookup;
	}

	/**
	 * get whether this editor/form is to be used for a exact match lookup.
	 * This flag is used when a description needs to be looked up for a key value in
	 * the user interface.
	 * @param lkpStatus
	 * @roseuid 3E205C3002D4
	 */
	public void setExactMatchLookup(boolean lkpStatus)
	{
      _exactMatchLookup = lkpStatus;
	}

	/**
	 * set the subsystem associated with this form for editor getting metadata
	 * @param aSubsystem
	 * @roseuid 3E205C3002E8
	 */
	public void setSubsystem(String aSubsystem)
	{
		_subsystem = aSubsystem;
	}

	/**
	 * get the subsystem associated with this form for editor getting metadata
	 * @param aSubsystem@return java.lang.String
	 * @roseuid 3E205C3002F3
	 */
	public String getSubsystem()
	{
		return _subsystem;
	}

	/**
	 * sets the page size for the list pages
	 * @param aSize
	 * @roseuid 3E5FC0CD0034
	 */
	public void setPageSize(int aSize)
	{
		_pageSize = aSize;
	}

	/**
	 * gets the page size for the list pages
	 * @return int
	 * @roseuid 3E5FC0CD0052
	 */
	public int getPageSize()
	{
		return _pageSize;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3EFE334801AC
	 */
	public String getViewrole()
	{
		return _viewrole;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3EFE334801F2
	 */
	public String getEditrole()
	{
		return _editrole;
	}

	/**
	 * @param aViewrole
	 * @roseuid 3EFE33480239
	 */
	public void setViewrole(String aViewrole)
	{
		_viewrole = aViewrole;
	}

	/**
	 * @param aEditrole
	 * @roseuid 3EFE334802D9
	 */
	public void setEditrole(String aEditrole)
	{
		_editrole = aEditrole;
	}

	/**
	 * @param aFormInterceptor
	 * @roseuid 3F07B6BB0160
	 */
	public void setFormInterceptorType(String aFormInterceptor)
	{
	    _formInterceptorType = aFormInterceptor;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3F07B6BB0188
	 */
	public String getFormInterceptorType()
	{
		return _formInterceptorType;
	}
	public String getHasHelp()
	{
		return _hasHelp;
	}
	public void setHasHelp(String hasHelp)
	{
		_hasHelp = hasHelp;
	}

	public String getHasChild()
	{
		return _hasChild;
	}
	public void setHasChild(String haschild)
	{
		_hasChild = haschild;
	}
	public void setChildActions(String childActions)
	{
		_childActions = childActions;
	}

	public String getChildActions()
	{
		return _childActions;
	}

	public String getListEdit()
	{
		return _listedit;
	}

	public void setListEdit(String listedit)
	{
		_listedit = listedit;
	}

    public String getFooterPrivilege() {
        return _footerPrivilege;
    }
    public void setFooterPrivilege(String footerPrivilege) {
        this._footerPrivilege = footerPrivilege;
    }

    public String getAddNewPrivilege() {
        return _addNewPrivilege;
    }

    public void setAddNewPrivilege(String addNewPrivilege) {
        this._addNewPrivilege = addNewPrivilege;
    }

    public String getCancelPrivilege() {
        return _cancelPrivilege;
    }

    public void setCancelPrivilege(String cancelPrivilege) {
        this._cancelPrivilege = cancelPrivilege;
    }

    public String getCustomDisplayPrivilege() {
        return _customDisplayPrivilege;
    }

    public void setCustomDisplayPrivilege(String customDisplayPrivilege) {
        this._customDisplayPrivilege = customDisplayPrivilege;
    }
    public String getExportPrivilege() {
        return _exportPrivilege;
    }

    public void setExportPrivilege(String exportPrivilege) {
        this._exportPrivilege = exportPrivilege;
    }

    public String getScheduleExportPrivilege() {
        return _scheduleExportPrivilege;
    }

    public void setScheduleExportPrivilege(String scheduleExportPrivilege) {
        this._scheduleExportPrivilege = scheduleExportPrivilege;
	}

    public String getMultiDeletePrivilege() {
        return _multiDeletePrivilege;
    }

    public void setMultiDeletePrivilege(String multiDeletePrivilege) {
        this._multiDeletePrivilege = multiDeletePrivilege;
    }
    public boolean isExactSearch() {
        return _exactSearch;
    }

    public void setExactSearch(boolean exactSearch) {
        this._exactSearch = exactSearch;
    }

	public int getSearchColumnsPerRow() {
		return _searchColumnsPerRow;
	}

	public void setSearchColumnsPerRow( int aSearchColumnsPerRow ) {
		_searchColumnsPerRow = aSearchColumnsPerRow;
	}

	public String getAddlinkImage() {
		return _addlinkImage;
	}

	public void setAddlinkImage(String aAddlinkImage){
		_addlinkImage = aAddlinkImage;
	}

	public String getDeletelinkImage() {
		return _deletelinkImage;
	}

	public void setDeletelinkImage(String aDeletelinkImage){
		_deletelinkImage = aDeletelinkImage;
	}

    public boolean isAdditionalFilter() {
        return _additionalFilter;
    }

    public void setAdditionalFilter(boolean additionalFilter) {
        this._additionalFilter = additionalFilter;
    }

}
