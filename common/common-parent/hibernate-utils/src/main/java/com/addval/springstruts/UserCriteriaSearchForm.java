//Source file: D:\\projects\\COMMON\\src\\com\\addval\\struts\\UserCriteriaSearchForm.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.springstruts;

import java.util.ArrayList;
import com.addval.metadata.UserCriteria;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.StrUtl;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;

public class UserCriteriaSearchForm extends ListForm
{
	private String _criteriaName = null;
	private String _criteriaDesc = null;
	private ArrayList _criteriaNames = null;
	private String _subSystem = null;
	private String _userId = null;
	private String _kindOfAction = null;
	private UserCriteria _criteria = null;
	private ArrayList _userGroups = null;
    private boolean additionalFilter = false;
    private EditorMetaData editorMetaDataCopy = null;
    private String chartXML = null;
    private boolean loadChartXml = false;
    
    
    /**
	 * @roseuid 3F9D564E012C
	 */
	public UserCriteriaSearchForm()
	{
		setSubSystem(null);
		setCriteriaNames(null);
		setCriteriaName(null);
		setCriteriaDesc(null);
		setUserId(null);
		setKindOfAction(null);
		setCriteria(null);
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

	/**
	 * @param mapping
	 * @param request
	 * @return org.apache.struts.action.ActionErrors
	 * @roseuid 3F9D57670054
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors = new ActionErrors();
		return errors;
	}

	public String getCriteriaDesc()
	{
		return _criteriaDesc;
	}

	public void setCriteriaDesc(String aCriteriaDesc)
	{
		_criteriaDesc= aCriteriaDesc;
	}
	public ArrayList getUserGroups()
	{
		return _userGroups;
	}

	public void setUserGroups(ArrayList aUserGroups)
	{
		_userGroups = aUserGroups;
	}

    public boolean isAdditionalFilter() {
        return additionalFilter;
    }

    public void setAdditionalFilter(boolean additionalFilter) {
        this.additionalFilter = additionalFilter;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request){
    	
    	String kindOfAction = request.getParameter("kindOfAction");
		if( "report".equals( kindOfAction ) ) {
			String editorName 	= request.getParameter( "EDITOR_NAME_KEY" ) 	!= null ? request.getParameter("EDITOR_NAME_KEY") 	:"";
			request.setAttribute("EDITOR_NAME",editorName);
		}
		super.reset(mapping,request);

		
		org.apache.struts.config.ModuleConfig appConfig = (org.apache.struts.config.ModuleConfig) request.getAttribute(org.apache.struts.Globals.MODULE_KEY);
        BaseFormBeanConfig   formConfig = (BaseFormBeanConfig) appConfig.findFormBeanConfig(mapping.getName());

		String editorName = formConfig.getEditorName();
		if (StrUtl.isEmpty(editorName)) {
			setAdditionalFilter( getMetadata().isQueryToolAdditionalFilter() );
			if (getEditorMetaDataCopy() == null) {
				setEditorMetaDataCopy((EditorMetaData) getMetadata().clone());
			}
		}
		else {
			 setAdditionalFilter( formConfig.isAdditionalFilter() );
		}
    }

    public EditorMetaData getEditorMetaDataCopy() {
        return editorMetaDataCopy;
    }

    public void setEditorMetaDataCopy(EditorMetaData editorMetaDataCopy) {
        this.editorMetaDataCopy = editorMetaDataCopy;
    }

	public String getChartXML() {
		return chartXML;
	}

	public void setChartXML(String chartXML) {
		this.chartXML = chartXML;
	}

	public boolean isLoadChartXml() {
		return loadChartXml;
	}

	public void setLoadChartXml(boolean loadChartXml) {
		this.loadChartXml = loadChartXml;
	}


}
