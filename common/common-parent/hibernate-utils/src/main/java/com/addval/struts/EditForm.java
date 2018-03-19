//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\EditForm.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\EditForm.java

package com.addval.struts;

import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.apache.commons.lang.StringUtils;
import com.addval.environment.Environment;
import com.addval.ejbutils.server.EJBSEditorMetaDataHome;
import com.addval.ejbutils.server.EJBSEditorMetaDataRemote;
import com.addval.environment.EJBEnvironment;
import com.addval.dbutils.RSIterAction;
import com.addval.metadata.EditorMetaData;
import com.addval.ejbutils.dbutils.EJBResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>An Action Form based on AddVal Editor Metadata Framework. This form can be
 * used to edit. The metadata associated with the form is read from the database
 * through the EJBSEditorMetaData bean and cached within this form the first time
 * this beans reset method is called. Some of the default properties for this form
 * can be configured using the FormBeanConfig class. A sample form bean
 * declaration is shown below
 * <form-bean name="carrierEditForm" type="com.addval.struts.EditForm"
 * className="com.addval.struts.BaseFormBeanConfig">
 * <set-property property="securityManagerType"
 * value="com.addval.cargoresui.rulesui.RulesEditSecurityManager" />
 * <set-property property="editorName" value="CARRIER" />
 * </form-bean>
 * </b>@author
 * AddVal Technology Inc.
 */
public class EditForm extends ActionForm
{
	String _kindOfAction = null;
	String _action = null;
	String _errorMsg = null;
	private String _securityManagerType = "";

	/**
	 * When update happens, we should use
	 * this to preserve the values if a server error happens
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
	ArrayList _childActions=null;
	private String _listedit="";
	private String _childIndex = "";
    boolean _hasHelp = true;

	/**
	 * Determines if the _mutable property is true.
	 *
	 * @return   <code>true<code> if the _mutable property is true
	 */
	public boolean getMutable()
	{
		return _mutable;
		}

	/**
	 * Sets the value of the _mutable property.
	 *
	 * @param aMutable the new value of the _mutable property
	 */
	public void setMutable(boolean aMutable)
	{
        this._mutable = aMutable;
	}

	/**
	 * @return com.addval.metadata.EditorMetaData
	 * @roseuid 3DCC8FB30173
	 */
	public EditorMetaData getMetadata()
	{ return _metadata;
	}

	/**
	 * @param aMetadata
	 * @roseuid 3DCC8FB301E2
	 */
	public void setMetadata(EditorMetaData aMetadata)
	{
		_metadata = aMetadata;
	}

	/**
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3DCC8FB30282
	 */
	public EJBResultSet getResultset()
	{ return _resultset;
	}

	/**
	 * @param aResultset
	 * @roseuid 3DCC8FB302FA
	 */
	public void setResultset(EJBResultSet aResultset)
	{
		_resultset = aResultset;
	}

	/**
	 * @return String
	 * @roseuid 3DCC8FB3039A
	 */
	public String getKindOfAction()
	{ return _kindOfAction;
	}

	/**
	 * @param aKindOfAction
	 * @roseuid 3DCC8FB303C2
	 */
	public void setKindOfAction(String aKindOfAction)
	{
		_kindOfAction = aKindOfAction;
	}

	/**
	 * @return String
	 * @roseuid 3DCC8FB40034
	 */
	public String getAction()
	{ return _action;
	}

	/**
	 * @param aAction
	 * @roseuid 3DCC8FB4005C
	 */
	public void setAction(String aAction)
	{
		_action = aAction;
	}

	/**
	 * @return String
	 * @roseuid 3DCC8FB400B7
	 */
	public String getErrorMsg()
	{
		return _errorMsg == null ? "" : _errorMsg;
	}

	/**
	 * @param aErrorMsg
	 * @roseuid 3DCC8FB400DF
	 */
	public void setErrorMsg(String aErrorMsg)
	{
		_errorMsg = aErrorMsg;
	}

	/**
	 * Reset all properties to their default values.
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 * @roseuid 3DCC8FB40143
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request)
	{

	   BaseActionMapping baseMapping = (BaseActionMapping) mapping;

	   //org.apache.struts.config.ApplicationConfig appConfig = (org.apache.struts.config.ApplicationConfig) request.getAttribute(org.apache.struts.action.Action.APPLICATION_KEY);

       org.apache.struts.config.ModuleConfig appConfig = (org.apache.struts.config.ModuleConfig) request.getAttribute(org.apache.struts.Globals.MODULE_KEY);


	   BaseFormBeanConfig formConfig = (BaseFormBeanConfig) appConfig.findFormBeanConfig(mapping.getName());

	   // Initialize the data elements from the formConfig
	   String editorName = formConfig.getEditorName();
	   String editorType = formConfig.getEditorType();

	   String subsystem = null;
	   setViewrole(formConfig.getViewrole());
	   setEditrole(formConfig.getEditrole());

	   setHasChild(formConfig.getHasChild() != null && formConfig.getHasChild().equalsIgnoreCase("TRUE"));
	   setChildActions(formConfig.getChildActions());

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

	   if (formConfig.getEditorName().equals(""))
	   {
	      _metadata  = null;
	   }

		// Resetting the error message will remove off errors hence the errorMsg is updated only before server.updateTransaction

		if (isMutable()) {
			_errorMsg  	= "";
			_resultset = null;
		}
		//kindOfAction set to 'edit' only when if it is empty or null
		if(_kindOfAction == null || _kindOfAction.trim().length() == 0 ) {
			_kindOfAction = "edit";
		}
		_action 		= "";

	   //
	   // Metadata will be initialized only the first time unless the formConfiguration
	   // does not have the Editor
	   //
	   if (_metadata == null)
	   {
		       try {
					 String homeName = Environment.getInstance(subsystem).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSEditorMetaDataBeanName", "EJBSTableManager");

				     EJBSEditorMetaDataHome   metaDataHome   = ( EJBSEditorMetaDataHome ) EJBEnvironment.lookupEJBInterface( subsystem, homeName , EJBSEditorMetaDataHome.class );
				     EJBSEditorMetaDataRemote metaDataRemote = metaDataHome.create( );

				     _metadata = metaDataRemote.lookup( editorName, editorType );

		   	    }
				catch (java.lang.Exception ex)
				{
					_metadata = null;
				     Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error reading metadata for " + editorName);
				     Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(ex);
	        	}


		}
		if (_securityManager != null)
		{
		 _securityManager.enforceSecurity(mapping, request, this);
		}
	}

	/**
	 * @param mapping
	 * @param request
	 * @return org.apache.struts.action.ActionErrors
	 * @roseuid 3DCC8FB4026F
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
                ActionErrors errors = new ActionErrors();
                //if ((book.getTitle() == null) || (book.getTitle().length() < 3))
                //   { errors.add("Title", new ActionError("error.book.title")); }


	            //if (book.getPages() < 1)
	            //   { errors.add("Page", new ActionError("error.book.page")); }

                return errors;
	}

	/**
	 * @param aSecurityManager
	 * @roseuid 3DE26B6701B8
	 */
	public void setFormSecurityManager(FormSecurityManager aSecurityManager)
	{
		_securityManager = aSecurityManager;
	}

	/**
	 * @return FormSecurityManager
	 * @roseuid 3DE26B6701E0
	 */
	public FormSecurityManager getFormSecurityManager()
	{
		return _securityManager;
	}

	/**
	 * @param aSecurityManager
	 * @roseuid 3E00BC2A003C
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
	 * @roseuid 3E00BC2A005A
	 */
	public String getSecurityManagerType()
	{
		return _securityManagerType;
	}

	/**
	 * Retrieve the mutable.
	 *
	 * @return boolean
	 * @roseuid 3EEFD7DD01F4
	 */
	public boolean isMutable()
	{
        return this._mutable;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3F07B6C30143
	 */
	public String getViewrole()
	{
		return _viewrole;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3F07B6C30161
	 */
	public String getEditrole()
	{
		return _editrole;
	}

	/**
	 * @param aViewrole
	 * @roseuid 3F07B6C3017F
	 */
	public void setViewrole(String aViewrole)
	{
		_viewrole = aViewrole;
	}

	/**
	 * @param aEditrole
	 * @roseuid 3F07B6C301BB
	 */
	public void setEditrole(String aEditrole)
	{
		_editrole = aEditrole;
	}

	/**
	 * @param aFormInterceptor
	 * @roseuid 3F07B6C301ED
	 */
	public void setFormInterceptor(FormInterceptor aFormInterceptor)
	{
		_formInterceptor = aFormInterceptor;
	}

	/**
	 * @return com.addval.struts.FormInterceptor
	 * @roseuid 3F07B6C30233
	 */
	public FormInterceptor getFormInterceptor()
	{
		return _formInterceptor;
	}

	/**
	 * @param aFormInterceptor
	 * @roseuid 3F07B6C3025B
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
	 * @roseuid 3F07B6C30298
	 */
	public String getFormInterceptorType()
	{
		return _formInterceptorType;
	}

	public void setHasChild(boolean haschild)
	{
		_hasChild = haschild;
	}


	public boolean hasChild()
	{
		return _hasChild;
	}

	public void setChildActions(String childActions) {
		_childActions = new ArrayList();
        if(childActions != null) {
            String[] childactions = StringUtils.split(childActions,",");
            for(int i=0; i < childactions.length;i++){
                _childActions.add(childactions[i]);
            }
        }
	}

	public ArrayList getChildActions() {
		if(_childActions == null)
			_childActions = new ArrayList();

        return _childActions;
	}

	public boolean isListEdit()
	{
		return getListEdit().equalsIgnoreCase("TRUE");
	}


	public String getListEdit()
	{
		return _listedit;
	}

	public void setListEdit(String listedit)
	{
		_listedit = listedit;
	}
	public void setListEdit(boolean listedit)
	{
		_listedit = listedit?"TRUE":"FALSE";
	}
    public String getChildIndex() {
        return _childIndex;
    }

    public void setChildIndex(String childIndex) {
        this._childIndex = childIndex;
    }
    public void setHasHelp(boolean hasHelp)
	{
		_hasHelp = hasHelp;
	}
    public boolean hasHelp()
	{
		return _hasHelp;
	}




}
