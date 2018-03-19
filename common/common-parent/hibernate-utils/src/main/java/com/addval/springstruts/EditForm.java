//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\EditForm.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\EditForm.java

package com.addval.springstruts;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.StrUtl;

/**
 * <b>An Action Form based on AddVal Editor Metadata Framework. This form can be
 * used to edit. The metadata associated with the form is read from the database
 * through the EJBSEditorMetaData bean and cached within this form the first
 * time this beans reset method is called. Some of the default properties for
 * this form can be configured using the FormBeanConfig class. A sample form
 * bean declaration is shown below <form-bean name="carrierEditForm"
 * type="com.addval.struts.EditForm"
 * className="com.addval.struts.BaseFormBeanConfig"> <set-property
 * property="securityManagerType"
 * value="com.addval.cargoresui.rulesui.RulesEditSecurityManager" />
 * <set-property property="editorName" value="CARRIER" /> </form-bean> </b>@author
 * AddVal Technology Inc.
 */
public class EditForm extends BaseForm {
	String _kindOfAction = null;
	String _action = null;
	String _errorMsg = null;
	private String _securityManagerType = "";

	/**
	 * When update happens, we should use this to preserve the values if a
	 * server error happens
	 */
	private boolean _mutable = true;
	private String _viewrole = "";
	private String _editrole = "";
	private String _formInterceptorType = "";
	EditorMetaData _metadata = null;
	EJBResultSet _resultset = null;
	protected FormSecurityManager _securityManager = null;
	protected FormInterceptor _formInterceptor = null;
	boolean _hasChild = false;
	ArrayList _childActions = null;
	private String _listedit = "";
	private String _childIndex = "";
	boolean _hasHelp = true;
	private String _editorName = "";
	private String _editorType = "";

	public String getEditorName() {
		return _editorName;
	}

	public void setEditorName(String aEditorName) {
		_editorName = aEditorName;
	}

	public String getEditorType() {
		return _editorType;
	}

	public void setEditorType(String aEditorType) {
		_editorType = aEditorType;
	}

	public void setEDITOR_NAME(String aName) {
		_editorName = aName;
	}

	/**
	 * Determines if the _mutable property is true.
	 * 
	 * @return <code>true<code> if the _mutable property is true
	 */
	public boolean getMutable() {
		return _mutable;
	}

	/**
	 * Sets the value of the _mutable property.
	 * 
	 * @param aMutable
	 *            the new value of the _mutable property
	 */
	public void setMutable(boolean aMutable) {
		this._mutable = aMutable;
	}

	/**
	 * @return com.addval.metadata.EditorMetaData
	 * @roseuid 3DCC8FB30173
	 */
	public EditorMetaData getMetadata() {
		return _metadata;
	}

	/**
	 * @param aMetadata
	 * @roseuid 3DCC8FB301E2
	 */
	public void setMetadata(EditorMetaData aMetadata) {
		_metadata = aMetadata;
	}

	/**
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3DCC8FB30282
	 */
	public EJBResultSet getResultset() {
		return _resultset;
	}

	/**
	 * @param aResultset
	 * @roseuid 3DCC8FB302FA
	 */
	public void setResultset(EJBResultSet aResultset) {
		_resultset = aResultset;
	}

	/**
	 * @return String
	 * @roseuid 3DCC8FB3039A
	 */
	public String getKindOfAction() {
		return _kindOfAction;
	}

	/**
	 * @param aKindOfAction
	 * @roseuid 3DCC8FB303C2
	 */
	public void setKindOfAction(String aKindOfAction) {
		_kindOfAction = aKindOfAction;
	}

	/**
	 * @return String
	 * @roseuid 3DCC8FB40034
	 */
	public String getAction() {
		return _action;
	}

	/**
	 * @param aAction
	 * @roseuid 3DCC8FB4005C
	 */
	public void setAction(String aAction) {
		_action = aAction;
	}

	/**
	 * @return String
	 * @roseuid 3DCC8FB400B7
	 */
	public String getErrorMsg() {
		return _errorMsg == null ? "" : _errorMsg;
	}

	/**
	 * @param aErrorMsg
	 * @roseuid 3DCC8FB400DF
	 */
	public void setErrorMsg(String aErrorMsg) {
		_errorMsg = aErrorMsg;
	}

	/**
	 * Reset all properties to their default values.
	 * 
	 * @param mapping
	 *            The mapping used to select this instance
	 * @param request
	 *            The servlet request we are processing
	 * @roseuid 3DCC8FB40143
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		
	       

		BaseActionMapping baseMapping = (BaseActionMapping) mapping;

		// org.apache.struts.config.ApplicationConfig appConfig =
		// (org.apache.struts.config.ApplicationConfig)
		// request.getAttribute(org.apache.struts.action.Action.APPLICATION_KEY);

		org.apache.struts.config.ModuleConfig appConfig = (org.apache.struts.config.ModuleConfig) request.getAttribute(org.apache.struts.Globals.MODULE_KEY);

		BaseFormBeanConfig formConfig = (BaseFormBeanConfig) appConfig.findFormBeanConfig(mapping.getName());
		setViewrole(formConfig.getViewrole());
		setEditrole(formConfig.getEditrole());

		setHasChild(formConfig.getHasChild() != null && formConfig.getHasChild().equalsIgnoreCase("TRUE"));
		setChildActions(formConfig.getChildActions());

		if (formConfig.getHasHelp() != null && formConfig.getHasHelp().trim().length() > 0)
			setHasHelp(formConfig.getHasHelp().equalsIgnoreCase("TRUE"));

		if ((formConfig.getSubsystem() == null) || (formConfig.getSubsystem().equals(""))) {
			BaseControllerConfig baseConfig = (BaseControllerConfig) appConfig.getControllerConfig();

			if (formConfig.getViewrole().equals("")) {
				setViewrole(baseConfig.getViewrole());
			}

			if (formConfig.getEditrole().equals("")) {
				setEditrole(baseConfig.getEditrole());
			}
		}

		if (!formConfig.getSecurityManagerType().equals("")) {
			setSecurityManagerType(formConfig.getSecurityManagerType());
		}

		if (!formConfig.getFormInterceptorType().equals("")) {
			setFormInterceptorType(formConfig.getFormInterceptorType());
		}
		if (StrUtl.isEmpty(formConfig.getEditorName())) {
			_metadata = null;
		}

		if (isMutable()) {
			_errorMsg = "";
			_resultset = null;
		}
		if (StrUtl.isEmpty( _kindOfAction ) && StrUtl.isEmpty(request.getParameter("kindOfAction"))) {
			_kindOfAction = "edit";
		}
		else
		{
			_kindOfAction = request.getParameter("kindOfAction");
		}
		_action = "";
		
		String editorName = formConfig.getEditorName();
		if(StrUtl.isEmptyTrimmed( editorName )){
			editorName = (String) request.getAttribute("EDITOR_NAME");
		}
		if(StrUtl.isEmptyTrimmed( editorName )){
			editorName = (String) request.getParameter("EDITOR_NAME");
		}
		if(StrUtl.isEmptyTrimmed( editorName )){
			editorName = request.getParameter("editorName");
		}
		
		String editorType = formConfig.getEditorType();
		if(StrUtl.isEmptyTrimmed( editorType )){
			editorType = (String) request.getAttribute("EDITOR_TYPE");
		}
		if(StrUtl.isEmptyTrimmed( editorType )){
			editorType = (String) request.getParameter("EDITOR_TYPE");
		}
		if(StrUtl.isEmptyTrimmed( editorType )){
			editorType =  request.getParameter("editorType");
		}

		setEditorName(editorName);
		setEditorType(editorType);

		if (_metadata == null) {
			String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "DEFAULT";
			_metadata = lookupMetadata(getEditorName(), getEditorType(), userName, request.getSession());
			initializeFromMetadata(request, mapping, _metadata);
		}

		if (_securityManager != null) {
			_securityManager.enforceSecurity(mapping, request, this);
		}
		
		if (StrUtl.isEmpty(_listedit) && !StrUtl.isEmptyTrimmed(request.getParameter("listEdit"))) {
			_listedit = request.getParameter("listEdit");
		}
		if (StrUtl.isEmpty(_childIndex) && !StrUtl.isEmptyTrimmed(request.getParameter("childIndex"))) {
			_childIndex = request.getParameter("childIndex");
		}
		
	}

	/**
	 * @param mapping
	 * @param request
	 * @return org.apache.struts.action.ActionErrors
	 * @roseuid 3DCC8FB4026F
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		// if ((book.getTitle() == null) || (book.getTitle().length() < 3))
		// { errors.add("Title", new ActionError("error.book.title")); }

		// if (book.getPages() < 1)
		// { errors.add("Page", new ActionError("error.book.page")); }

		return errors;
	}

	/**
	 * @param aSecurityManager
	 * @roseuid 3DE26B6701B8
	 */
	public void setFormSecurityManager(FormSecurityManager aSecurityManager) {
		_securityManager = aSecurityManager;
	}

	/**
	 * @return FormSecurityManager
	 * @roseuid 3DE26B6701E0
	 */
	public FormSecurityManager getFormSecurityManager() {
		return _securityManager;
	}

	/**
	 * @param aSecurityManager
	 * @roseuid 3E00BC2A003C
	 */
	public void setSecurityManagerType(String aSecurityManager) {
		_securityManagerType = aSecurityManager;

		if ((_securityManagerType != null) && (!_securityManagerType.equals(""))) {
			setFormSecurityManager(FormSecurityManagerFactory.getInstance(_securityManagerType));
		}
	}

	/**
	 * @return String
	 * @roseuid 3E00BC2A005A
	 */
	public String getSecurityManagerType() {
		return _securityManagerType;
	}

	/**
	 * Retrieve the mutable.
	 * 
	 * @return boolean
	 * @roseuid 3EEFD7DD01F4
	 */
	public boolean isMutable() {
		return this._mutable;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3F07B6C30143
	 */
	public String getViewrole() {
		return _viewrole;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3F07B6C30161
	 */
	public String getEditrole() {
		return _editrole;
	}

	/**
	 * @param aViewrole
	 * @roseuid 3F07B6C3017F
	 */
	public void setViewrole(String aViewrole) {
		_viewrole = aViewrole;
	}

	/**
	 * @param aEditrole
	 * @roseuid 3F07B6C301BB
	 */
	public void setEditrole(String aEditrole) {
		_editrole = aEditrole;
	}

	/**
	 * @param aFormInterceptor
	 * @roseuid 3F07B6C301ED
	 */
	public void setFormInterceptor(FormInterceptor aFormInterceptor) {
		_formInterceptor = aFormInterceptor;
	}

	/**
	 * @return com.addval.struts.FormInterceptor
	 * @roseuid 3F07B6C30233
	 */
	public FormInterceptor getFormInterceptor() {
		return _formInterceptor;
	}

	/**
	 * @param aFormInterceptor
	 * @roseuid 3F07B6C3025B
	 */
	public void setFormInterceptorType(String aFormInterceptor) {
		_formInterceptorType = aFormInterceptor;

		if ((_formInterceptorType != null) && (!_formInterceptorType.equals(""))) {
			setFormInterceptor(FormInterceptorFactory.getInstance(_formInterceptorType));
		}
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3F07B6C30298
	 */
	public String getFormInterceptorType() {
		return _formInterceptorType;
	}

	public void setHasChild(boolean haschild) {
		_hasChild = haschild;
	}

	public boolean hasChild() {
		return _hasChild;
	}

	public void setChildActions(String childActions) {
		_childActions = new ArrayList();
		if (childActions != null) {
			String[] childactions = StringUtils.split(childActions, ",");
			for (int i = 0; i < childactions.length; i++) {
				_childActions.add(childactions[i]);
			}
		}
	}

	public ArrayList getChildActions() {
		if (_childActions == null)
			_childActions = new ArrayList();

		return _childActions;
	}

	public boolean isListEdit() {
		return getListEdit().equalsIgnoreCase("TRUE");
	}

	public String getListEdit() {
		return _listedit;
	}

	public void setListEdit(String listedit) {
		_listedit = listedit;
	}

	public void setListEdit(boolean listedit) {
		_listedit = listedit ? "TRUE" : "FALSE";
	}

	public String getChildIndex() {
		return _childIndex;
	}

	public void setChildIndex(String childIndex) {
		this._childIndex = childIndex;
	}

	public void setHasHelp(boolean hasHelp) {
		_hasHelp = hasHelp;
	}

	public boolean hasHelp() {
		return _hasHelp;
	}

	public void initializeFromMetadata(HttpServletRequest request, ActionMapping mapping, EditorMetaData metadata) {
		org.apache.struts.config.ModuleConfig appConfig = (org.apache.struts.config.ModuleConfig) request.getAttribute(org.apache.struts.Globals.MODULE_KEY);
		BaseFormBeanConfig formConfig = (BaseFormBeanConfig) appConfig.findFormBeanConfig(mapping.getName());

		// if editorName is not configured in formConfig
		String editorName = formConfig.getEditorName();

		if (StrUtl.isEmpty(editorName)) {

			setEditorName(metadata.getName());
			setEditorType(metadata.getType());

			// setInitialLookup(metadata.isInitialLookup());

			if (!StrUtl.isEmpty(metadata.getClientInterceptorForEdit())) {
				setFormInterceptorType(metadata.getClientInterceptorForEdit());
			}

			if (!StrUtl.isEmpty(metadata.getSecurityManagerForEdit())) {
				setSecurityManagerType(metadata.getSecurityManagerForEdit());
			}

			// setListEditValue(metadata.isListEdit());
			// setAddNewPrivilege(metadata.isAddNewPriv());
			// setHasMultiDeletePrivilege(metadata.isMultiDeletePriv());
			// setHasCustomDisplayPrivilege(metadata.isCustomDisplayPriv());
			// setHasExportPrivilege(metadata.isExportPriv());
			// setHasScheduleExportPrivilege(metadata.isScheduledExportPriv());
			// setFooterPrivilege(metadata.isFooterPriv());
			// setCancelPrivilege(metadata.isCancelPriv());
			// setExactSearch(metadata.isExactMatchLookup());
			setHasHelp(metadata.hasHelp());
			setHasChild(metadata.hasChild());
			// setPageSize(Integer.toString(metadata.getPageSize()));
			// setSearchColumnsPerRow(metadata.getSearchColsPerRow());
			setViewrole(metadata.getViewRole());
			setEditrole(metadata.getEditRole());
			setChildActions(metadata.getChildActions());
			// setDetailEditorName(metadata.getDetailEditorName());
			// setCacheToRefresh(metadata.getCacheToRefresh());
			// setShowHeader(metadata.isShowHeader());
			// setShowPositions(metadata.isShowPositions());
			// setAddlinkImage(metadata.getAddlinkImage());
			// setDeletelinkImage(metadata.getDeletelinkImage());
			// setCancellinkImage(metadata.getCancellinkImage());
			// setSearchColumnsPerRow(metadata.getSearchColsPerRow());

			if (_securityManager != null) {
				_securityManager.enforceSecurity(mapping, request, this);
			}
		}

	}

}
