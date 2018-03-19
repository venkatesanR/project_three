//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\ListForm.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\ListForm.java

package com.addval.struts;

import javax.servlet.http.*;
import org.apache.struts.action.*;
import com.addval.environment.Environment;
import com.addval.ejbutils.server.EJBSEditorMetaDataHome;
import com.addval.ejbutils.server.EJBSEditorMetaDataRemote;
import com.addval.environment.EJBEnvironment;
import com.addval.dbutils.RSIterAction;
import com.addval.metadata.EditorMetaData;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBDateTime;
import com.addval.utils.StrUtl;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;
import com.addval.ejbutils.dbutils.EJBDateConverter;
import com.addval.ejbutils.dbutils.EJBColumn;


import java.util.Enumeration;

/**
 * <b>An Action Form based on AddVal Editor Metadata Framework. This form can be
 * used to display a list.The metadata associated with the form is read from the
 * database through the EJBSEditorMetaData bean and cached within this form the
 * first time this beans reset method is called. Some of the default properties
 * for this form can be configured using the FormBeanConfig class. A sample form
 * bean declaration is shown below
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
public class  ListForm extends ActionForm
{
	boolean _initialLookup = false;
	boolean _showHeader = true;
	boolean _showPositions = true;
	boolean _showAddButton = true;
	String _sortOrder = null;
	String _sortName = null;
	String _position = null;
	String _pagingAction = null;
	String _pageSize = null;
	boolean _forLookup = false;
	boolean _exactMatchLookup = false;
	private String _securityManagerType = "";
	private String _viewrole = "";
	private String _editrole = "";
	private String _formInterceptorType = "";
	EditorMetaData _metadata = null;
	EJBResultSet _resultset = null;
	boolean _isListEdit = false;
    boolean _hasAddNewPrivilege = true;
    boolean _hasCancelPrivilege = false;
    boolean _hasFooterPrivilege = true;
    boolean _hasCustomDisplayPrivilege = true;
    boolean _hasExportPrivilege = false;
    boolean _hasScheduleExportPrivilege = false;
    boolean _hasMultiDeletePrivilege = false;
    private String _childIndex = "";
    boolean _hasHelp = true;
    boolean exactSearch = true;
    // Default number of columns per row = 4 - Set in Base config
    private int _searchColumnsPerRow;
    // Default ../images/addNew.gif - Set in Base config
	private String _addlinkImage= "";
	private String _cancellinkImage= "";
	// Default ../images/delete_button.gif - Set in Base config
	private String _deletelinkImage= "";
	/**
	 * Security Manager Interface - PlugIn a security Manager to implement role based programmatic security.
	 * This can be used to alter metadata based on the user and roles
	 */
	protected FormSecurityManager _securityManager = null;
	protected FormInterceptor _formInterceptor = null;

	/**
	 * @return com.addval.metadata.EditorMetaData
	 * @roseuid 3DCC8E5E0105
	 */
	public EditorMetaData getMetadata()
	{ return _metadata;
	}

	public int getSearchColumnsPerRow() { return _searchColumnsPerRow; }

	public void setSearchColumnsPerRow( int aSearchColumnsPerRow ) { _searchColumnsPerRow = aSearchColumnsPerRow; }

	/**
	 * @param aMetadata
	 * @roseuid 3DCC8E5E017D
	 */
	public void setMetadata(EditorMetaData aMetadata)
	{
		_metadata = aMetadata;
	}

	/**
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3DCC8E5E0231
	 */
	public EJBResultSet getResultset()
	{ return _resultset;
	}

	/**
	 * @param aResultset
	 * @roseuid 3DCC8E5E02AA
	 */
	public void setResultset(EJBResultSet aResultset)
	{
		_resultset = aResultset;
	}

	/**
	 * @return boolean
	 * @roseuid 3DCC8E5E0368
	 */
	public boolean getInitialLookup()
	{ return _initialLookup;
	}

	/**
	 * @param lkpStatus
	 * @roseuid 3DCC8E5E0390
	 */
	public void setInitialLookup(boolean lkpStatus)
	{
      //_initialLookup = lkpStatus;
      _initialLookup = lkpStatus;
	}

	/**
	 * @return boolean
	 * @roseuid 3DCC8E5F0020
	 */
	public boolean getShowHeader()
	{ return _showHeader;
	}

	/**
	 * @param aShowHeader
	 * @roseuid 3DCC8E5F0048
	 */
	public void setShowHeader(boolean aShowHeader)
	{
      _showHeader = aShowHeader;
	}

	/**
	 * @return boolean
	 * @roseuid 3DCC8E5F00C0
	 */
	public boolean getShowPositions()
	{ return _showPositions;
	}

	/**
	 * @param aShowPositions
	 * @roseuid 3DCC8E5F00E8
	 */
	public void setShowPositions(boolean aShowPositions)
	{
      _showPositions = aShowPositions;
	}

	/**
	 * @return boolean
	 * @roseuid 3DCC8E5F0161
	 */
	public boolean getShowAddButton()
	{ return _showAddButton;
	}

	/**
	 * @param aShowAddButton
	 * @roseuid 3DCC8E5F0193
	 */
	public void setShowAddButton(boolean aShowAddButton)
	{
      _showAddButton = aShowAddButton;
	}

	/**
	 * @return String
	 * @roseuid 3DCC8E5F020B
	 */
	public String getPosition()
	{ return _position;
	}

	/**
	 * @param aPosition
	 * @roseuid 3DCC8E5F0233
	 */
	public void setPosition(String aPosition)
	{
      _position = aPosition;
	}

	/**
	 * @return String
	 * @roseuid 3DCC8E5F02B5
	 */
	public String getPageSize()
	{ return _pageSize;
	}

	/**
	 * @param aPageSize
	 * @roseuid 3DCC8E5F02DD
	 */
	public void setPageSize(String aPageSize)
	{
      _pageSize = aPageSize;
	}

	/**
	 * @return String
	 * @roseuid 3DCC8E5F035F
	 */
	public String getPagingAction()
	{ return _pagingAction;
	}

	/**
	 * @param aPagingAction
	 * @roseuid 3DCC8E5F0391
	 */
	public void setPagingAction(String aPagingAction)
	{
      _pagingAction = aPagingAction;
	}

	/**
	 * @return String
	 * @roseuid 3DCC8E600021
	 */
	public String getSort_Name()
	{ return _sortName;
	}

	/**
	 * @param aSortName
	 * @roseuid 3DCC8E600054
	 */
	public void setSort_Name(String aSortName)
	{
		_sortName = aSortName;
	}

	/**
	 * @return String
	 * @roseuid 3DCC8E6000D6
	 */
	public String getSort_Order()
	{ return _sortOrder;
	}

	/**
	 * @param aSortOrder
	 * @roseuid 3DCC8E600108
	 */
	public void setSort_Order(String aSortOrder)
	{
		_sortOrder = aSortOrder;
	}

	/**
	 * Reset all properties to their default values.
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 * @roseuid 3DCC8E60018A
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request)
	{

		//BaseActionMapping baseMapping = (BaseActionMapping) mapping;

		//org.apache.struts.config.ApplicationConfig appConfig = (org.apache.struts.config.ApplicationConfig) request.getAttribute(org.apache.struts.action.Action.APPLICATION_KEY);

		org.apache.struts.config.ModuleConfig appConfig = (org.apache.struts.config.ModuleConfig) request.getAttribute(org.apache.struts.Globals.MODULE_KEY);


		BaseFormBeanConfig   formConfig = (BaseFormBeanConfig) appConfig.findFormBeanConfig(mapping.getName());


		// Initialize the data elements from the formConfig
		String editorName = formConfig.getEditorName();
		String editorType = formConfig.getEditorType();
		String subsystem = null;

		setInitialLookup(formConfig.getInitialLookup());
		setForLookup(formConfig.getForLookup());
		setExactMatchLookup(formConfig.getExactMatchLookup());
		setExactSearch(formConfig.isExactSearch());
		setViewrole(formConfig.getViewrole());
		setEditrole(formConfig.getEditrole());

		// Default number of columns per row = 4
		setSearchColumnsPerRow( formConfig.getSearchColumnsPerRow() );

		if (formConfig.getAddlinkImage() != null && formConfig.getAddlinkImage().trim().length() > 0)
			setAddlinkImage( formConfig.getAddlinkImage() );

		//System.out.println( "***********" + formConfig.getDeletelinkImage() );
		//System.out.println( "***********" + getSearchColumnsPerRow() );

		if (formConfig.getDeletelinkImage() != null && formConfig.getDeletelinkImage().trim().length() > 0)
			setDeletelinkImage( formConfig.getDeletelinkImage() );

	   	setListEditValue(formConfig.getListEdit() != null && formConfig.getListEdit().equalsIgnoreCase("TRUE"));

        if(formConfig.getAddNewPrivilege() != null && formConfig.getAddNewPrivilege().trim().length() > 0)
            setAddNewPrivilege(formConfig.getAddNewPrivilege().equalsIgnoreCase("TRUE"));
        if(formConfig.getCancelPrivilege() != null && formConfig.getCancelPrivilege().trim().length() > 0)
            setCancelPrivilege(formConfig.getCancelPrivilege().equalsIgnoreCase("TRUE"));
        if(formConfig.getFooterPrivilege() != null && formConfig.getFooterPrivilege().trim().length() > 0)
            setFooterPrivilege(formConfig.getFooterPrivilege().equalsIgnoreCase("TRUE"));
        if(formConfig.getCustomDisplayPrivilege() != null && formConfig.getCustomDisplayPrivilege().trim().length() > 0)
            setHasCustomDisplayPrivilege(formConfig.getCustomDisplayPrivilege().equalsIgnoreCase("TRUE"));
        if(formConfig.getExportPrivilege() != null && formConfig.getExportPrivilege().trim().length() > 0)
            setHasExportPrivilege(formConfig.getExportPrivilege().equalsIgnoreCase("TRUE"));
        if(formConfig.getScheduleExportPrivilege() != null && formConfig.getScheduleExportPrivilege().trim().length() > 0)
            setHasScheduleExportPrivilege(formConfig.getScheduleExportPrivilege().equalsIgnoreCase("TRUE"));
        if(formConfig.getMultiDeletePrivilege() != null && formConfig.getMultiDeletePrivilege().trim().length() > 0)
            setHasMultiDeletePrivilege(formConfig.getMultiDeletePrivilege().equalsIgnoreCase("TRUE"));

        if(formConfig.getHasHelp() != null && formConfig.getHasHelp().trim().length() > 0)
            setHasHelp(formConfig.getHasHelp().equalsIgnoreCase("TRUE"));


	   if ((formConfig.getSubsystem() == null) || (formConfig.getSubsystem().equals("")))
	   {
		   BaseControllerConfig baseConfig = (BaseControllerConfig) appConfig.getControllerConfig();
		   subsystem = baseConfig.getSubsystem();

		   if (formConfig.getViewrole().equals(""))
		   {
			   setViewrole(baseConfig.getViewrole());
		   }

		   if (formConfig.getEditrole().equals(""))
		   {
			   setEditrole(baseConfig.getEditrole());
		   }
	   } else
	   {
			subsystem = formConfig.getSubsystem();
	   }




		if (!formConfig.getSecurityManagerType().equals(""))
		{
			setSecurityManagerType(formConfig.getSecurityManagerType());
		}


		if (!formConfig.getFormInterceptorType().equals(""))
		{
			setFormInterceptorType(formConfig.getFormInterceptorType());
		}



		// If editorName is not configured for this form. Check if it is available in the
		// request
		if ((editorName == null) || (editorName.equals("")))
		{
			editorName = (String) request.getAttribute("EDITOR_NAME");
		}

		if ((editorName == null) || (editorName.equals("")))
		{
			editorName = (String) request.getParameter("EDITOR_NAME");
		}

		if ((editorName == null) || (editorName.equals("")))
		{

		   Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logInfo("Subsystem is " + subsystem);
		   Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logInfo("EditorName is Null");
		   return;
		}



       setPosition("1");
       setPagingAction(RSIterAction._FIRST_STR);
       setSort_Name("");
       setSort_Order("ASC");
       setPageSize(Integer.toString(formConfig.getPageSize()));

	   if (formConfig.getEditorName().equals(""))
	   {
	      _metadata  = null;
	   }

	   _resultset = null;

	   //
	   // Metadata will be initialized only the first time unless the formConfiguration
	   // does not have the Editor
	   //
	   if (_metadata == null)
	   {
		   		try
		   		{
					 String homeName = Environment.getInstance(subsystem).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSEditorMetaDataBeanName", "EJBSTableManager");


				     Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logInfo("EJBSEditorMetadata Name is ... " + homeName);

					 Object 				  object   		 = EJBEnvironment.lookupEJBInterface( subsystem, homeName , EJBSEditorMetaDataHome.class );

					 Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logInfo("Home lookup returned an object of class : " + object.getClass().getName() );

				     EJBSEditorMetaDataHome   metaDataHome   = ( EJBSEditorMetaDataHome ) EJBEnvironment.lookupEJBInterface( subsystem, homeName , EJBSEditorMetaDataHome.class );
				     EJBSEditorMetaDataRemote metaDataRemote = metaDataHome.create( );

				     Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logInfo("After looking up EJBSEditorMetada " + homeName);

				     String userName  = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "DEFAULT";
				     _metadata = metaDataRemote.lookup( editorName, editorType , userName);


				     Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logInfo("After looking up metadata " + homeName);

		   	    }
				catch (java.lang.Exception ex)
				{
					_metadata = null;

				     Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error reading metadata for " + editorName);
				     Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(ex);
	        	}

		}
		// enforce security if a security manager is plugged in
		// moved this outside the _metaData==null check since we need to enforce for all requests
		if (_securityManager != null)
		{
			 _securityManager.enforceSecurity(mapping, request, this);
		}
	}

	/**
	 * @param mapping
	 * @param request
	 * @return org.apache.struts.action.ActionErrors
	 * @roseuid 3DCC8E6002FD
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
                ActionErrors errors = new ActionErrors();
				/*	Search gets initaited when enter key is pressed in list screen.
					When invalid date is entered, Nullpointer exception is thrown. To overcome this issue,
					formatDBValue will return null if invalid date id provided. -BAPRC-799
				*/
                if(_metadata != null) {
                    for ( int i = 1; i <= _metadata.getColumnCount(); i++ )
                    {
                        ColumnMetaData colMetaData = _metadata.getColumnMetaData(i);
                        if(colMetaData.isSearchable()) {
                            int columnType = colMetaData.getColumnInfo().getType();
                            if(columnType==ColumnDataType._CDT_DATE && !StrUtl.isEmptyTrimmed(request.getParameter(colMetaData.getName()+"_lookUp")) ) {
                                EJBDateConverter ejbDateConverter =  new EJBDateConverter(new EJBColumn(colMetaData),colMetaData, colMetaData.getColumnInfo().getFormat());
                                EJBDateTime ejbDateTime = ejbDateConverter.formatDBValue( request.getParameter(colMetaData.getName()+"_lookUp"),colMetaData.getColumnInfo().getFormat());
                                if(ejbDateTime == null) {
                                     errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general","Invalid " +colMetaData.getColumnInfo().getCaption()));
                                }
                            }
                        }
                    }
                }
                //if ((book.getTitle() == null) || (book.getTitle().length() < 3))
                //   { errors.add("Title", new ActionError("error.book.title")); }


	            //if (book.getPages() < 1)
	            //   { errors.add("Page", new ActionError("error.book.page")); }

                return errors;
	}

	/**
	 * @return boolean
	 * @roseuid 3DD3D7420045
	 */
	public boolean getForLookup()
	{
		return _forLookup;
	}

	/**
	 * This form is being used for Lookup. Expects to find displayFieldName and
	 * displayFieldValue in the request
	 * @param lkpStatus
	 * @roseuid 3DD3D74201B7
	 */
	public void setForLookup(boolean lkpStatus)
	{
      _forLookup = lkpStatus;
	}

	/**
	 * @return boolean
	 * @roseuid 3DD3D75A032E
	 */
	public boolean getExactMatchLookup()
	{
		return _exactMatchLookup;
	}

	/**
	 * This form is being used for exact match - looking up a key for a value
	 * @param lkpStatus
	 * @roseuid 3DD3D75B004A
	 */
	public void setExactMatchLookup(boolean lkpStatus)
	{
      _exactMatchLookup = lkpStatus;
	}

	/**
	 * @param aSecurityManager
	 * @roseuid 3DE268A90245
	 */
	public void setFormSecurityManager(FormSecurityManager aSecurityManager)
	{
		_securityManager = aSecurityManager;
	}

	/**
	 * @return FormSecurityManager
	 * @roseuid 3DE268FB02B1
	 */
	public FormSecurityManager getFormSecurityManager()
	{
		return _securityManager;
	}

	/**
	 * @param aSecurityManager
	 * @roseuid 3E00B57C0083
	 */
	public void setSecurityManagerType(String aSecurityManager)
	{
		_securityManagerType = aSecurityManager;

		if ((_securityManagerType != null) && (!_securityManagerType.equals("")))
		{
			setFormSecurityManager(FormSecurityManagerFactory.getInstance(_securityManagerType));
		}
	}

	/**
	 * @return String
	 * @roseuid 3E00B6030114
	 */
	public String getSecurityManagerType()
	{
		return _securityManagerType;
	}

	/**
	 * @param aPagingAction
	 * @roseuid 3E00B9B80335
	 */
	public void setPAGING_ACTION(String aPagingAction)
	{
      _pagingAction = aPagingAction;
	}

	/**
	 * @return String
	 * @roseuid 3E00B9B9003D
	 */
	public String getSortOrder()
	{
      return _sortOrder;
	}

	/**
	 * @param aSortName
	 * @roseuid 3E00B9B90098
	 */
	public void setSortName(String aSortName)
	{
      _sortName = aSortName;
	}

	/**
	 * @return String
	 * @roseuid 3E00B9B90188
	 */
	public String getSortName()
	{
      return _sortName;
	}

	/**
	 * @param aSortOrder
	 * @roseuid 3E00B9B901E2
	 */
	public void setSortOrder(String aSortOrder)
	{
      _sortOrder = aSortOrder;
	}

	/**
	 * @param aPosition
	 * @roseuid 3E00B9B902D2
	 */
	public void setPOSITION(String aPosition)
	{
      _position = aPosition;
	}

	/**
	 * Access method for the _addlinkImage property.
	 *
	 * @return   the current value of the _addlinkImage property
	 */
	public String getAddlinkImage()
	{
		return _addlinkImage;
	}

	/**
	 * Sets the value of the _addlinkImage property.
	 *
	 * @param aAddlink the new value of the _addlinkImage property
	 */
	public void setAddlinkImage(String aAddlinkImage)
	{
		_addlinkImage = aAddlinkImage;
	}

	/**
	 * Access method for the _addlinkImage property.
	 *
	 * @return   the current value of the _addlinkImage property
	 */
	public String getCancellinkImage()
	{
		return _cancellinkImage;
	}

	/**
	 * Sets the value of the _addlinkImage property.
	 *
	 * @param aAddlink the new value of the _addlinkImage property
	 */
	public void setCancellinkImage(String aCancellinkImage)
	{
		_cancellinkImage = aCancellinkImage;
	}

	public String getDeletelinkImage()
	{
		return _deletelinkImage;
	}

	/**
	 * Sets the value of the _addlinkImage property.
	 *
	 * @param aAddlink the new value of the _addlinkImage property
	 */
	public void setDeletelinkImage(String aDeletelinkImage)
	{
		_deletelinkImage = aDeletelinkImage;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3F07B6CE0121
	 */
	public String getViewrole()
	{
		return _viewrole;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3F07B6CE0149
	 */
	public String getEditrole()
	{
		return _editrole;
	}

	/**
	 * @param aViewrole
	 * @roseuid 3F07B6CE0171
	 */
	public void setViewrole(String aViewrole)
	{
		_viewrole = aViewrole;
	}

	/**
	 * @param aEditrole
	 * @roseuid 3F07B6CE01B7
	 */
	public void setEditrole(String aEditrole)
	{
		_editrole = aEditrole;
	}

	/**
	 * @param aFormInterceptor
	 * @roseuid 3F07B6CE01FD
	 */
	public void setFormInterceptor(FormInterceptor aFormInterceptor)
	{
		_formInterceptor = aFormInterceptor;
	}

	/**
	 * @return com.addval.struts.FormInterceptor
	 * @roseuid 3F07B6CE0257
	 */
	public FormInterceptor getFormInterceptor()
	{
		return _formInterceptor;
	}

	/**
	 * @param aFormInterceptor
	 * @roseuid 3F07B6CE0289
	 */
	public void setFormInterceptorType(String aFormInterceptor)
	{
		_formInterceptorType = aFormInterceptor;

		if ((_formInterceptorType != null) && (!_formInterceptorType.equals("")))
		{
			setFormInterceptor(FormInterceptorFactory.getInstance(_formInterceptorType));
		}
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3F07B6CE02D9
	 */
	public String getFormInterceptorType()
	{
		return _formInterceptorType;
	}

    public String getChildIndex() {
        return _childIndex;
    }

    public void setChildIndex(String childIndex) {
        this._childIndex = childIndex;
    }

	public void setListEditValue(boolean isListEdit)
	{
		_isListEdit = isListEdit;
	}

	public boolean isListEdit()
	{
		return _isListEdit;
	}


	public void setListEdit(String listedit)
	{
		_isListEdit = (listedit != null && listedit.equalsIgnoreCase("TRUE"));
	}


	public void setCancelPrivilege(boolean cancelPrivilege)
	{
		_hasCancelPrivilege = cancelPrivilege;
	}

    public void setAddNewPrivilege(boolean addNewPrivilege)
	{
		_hasAddNewPrivilege = addNewPrivilege;
	}

    public void setFooterPrivilege(boolean footerPrivilege)
	{
		_hasFooterPrivilege = footerPrivilege;
	}

    public boolean hasAddNewPrivilege()
	{
		return _hasAddNewPrivilege;
	}

    public boolean hasCancelPrivilege()
	{
		return _hasCancelPrivilege;
	}

    public boolean hasFooterPrivilege()
	{
		return _hasFooterPrivilege;
	}

    public void setHasHelp(boolean hasHelp)
	{
		_hasHelp = hasHelp;
	}

 	public boolean hasHelp()
	{
		return _hasHelp;
	}
    public boolean hasCustomDisplayPrivilege() {
        return _hasCustomDisplayPrivilege;
    }

    public void setHasCustomDisplayPrivilege(boolean hasCustomDisplayPrivilege) {
        this._hasCustomDisplayPrivilege = hasCustomDisplayPrivilege;
    }

    public boolean hasExportPrivilege() {
        return _hasExportPrivilege;
    }

    public void setHasExportPrivilege(boolean hasExportPrivilege) {
        this._hasExportPrivilege = hasExportPrivilege;
    }

    public boolean hasScheduleExportPrivilege() {
        return _hasScheduleExportPrivilege;
    }

    public void setHasScheduleExportPrivilege(boolean hasScheduleExportPrivilege) {
        this._hasScheduleExportPrivilege = hasScheduleExportPrivilege;
    }

    public boolean hasMultiDeletePrivilege() {
        return _hasMultiDeletePrivilege;
    }

    public void setHasMultiDeletePrivilege(boolean hasMultiDeletePrivilege) {
        this._hasMultiDeletePrivilege = hasMultiDeletePrivilege;
    }
    public boolean isExactSearch() {
        return exactSearch;
    }

    public void setExactSearch(boolean exactSearch) {
        this.exactSearch = exactSearch;
    }

    public String getCriteriaToExport(HttpServletRequest request){
        StringBuffer queryString = new StringBuffer();
        EditorMetaData editorMetaData	= this.getMetadata();

        queryString.append("&editorName=").append( editorMetaData.getName() );
        queryString.append("&exactSearch=").append( this.isExactSearch() );
        if(!StrUtl.isEmptyTrimmed( this.getFormInterceptorType() ) ){
            queryString.append("&formInterceptorType=").append( this.getFormInterceptorType() );
            queryString.append("&formInterceptor=").append( this.getFormInterceptor() );
        }

        String columnName = null;
        String columnValue =null;
        for ( Enumeration enumeration = request.getParameterNames( ); enumeration.hasMoreElements( ); ) {
            columnName = ( String ) enumeration.nextElement( );
            if(!StrUtl.isEmptyTrimmed( columnName ) && columnName.endsWith( "_lookUp" ) ){
                columnValue = request.getParameter( columnName );
                if(!StrUtl.isEmptyTrimmed( columnValue ) ){
                    queryString.append("&").append( columnName ).append( "=" ).append( columnValue );
                }
            }
        }
        return queryString.toString().substring( 1 ) ;
    }

}
