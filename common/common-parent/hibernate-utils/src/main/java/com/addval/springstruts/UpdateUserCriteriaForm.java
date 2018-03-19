package com.addval.springstruts;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.Globals;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.UpdateUserCriteria;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.ColumnDataType;
import com.addval.ejbutils.server.EJBSEditorMetaData;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Iterator;

public class UpdateUserCriteriaForm extends ActionForm
{
	private String subSystem = null;
	private ArrayList criteriaNames = null;
	private ArrayList directoryNames = null;
	private String editorName = null;
    private String editorType = "DEFAULT";
	private String directoryName = null;
	private String criteriaName = null;
	private String criteriaDesc = null;
	private String newCriteriaName = null;
	private String newCriteriaDesc = null;
	private String searchCriteria = null;
	private Vector updateColumns = null;
	private String kindOfAction = null;
	private String overwrite = null;
	private UpdateUserCriteria criteria = null;
	private EditorMetaData metadata = null;

	public UpdateUserCriteriaForm()
	{
		setSubSystem(null);
		setCriteriaNames(null);
		setDirectoryNames(null);
		setEditorName(null);
		setDirectoryName(null);
		setCriteriaName(null);
		setCriteriaDesc(null);
		setNewCriteriaName(null);
		setNewCriteriaDesc(null);
		setSearchCriteria(null);
		setUpdateColumns(new Vector(5));
		setOverwrite(null);
		setCriteria(null);
		setKindOfAction(null);
	}


	public void reset(ActionMapping mapping, HttpServletRequest request)
	{
		try {
			ModuleConfig appConfig = (ModuleConfig) request.getAttribute(Globals.MODULE_KEY);
			BaseFormBeanConfig   formConfig = (BaseFormBeanConfig) appConfig.findFormBeanConfig(mapping.getName());
			setSubSystem(formConfig.getSubsystem());
			if (getSubSystem() == null || getSubSystem().equals("")) {
				BaseControllerConfig baseConfig = (BaseControllerConfig) appConfig.getControllerConfig();
				setSubSystem( baseConfig.getSubsystem() );
			}
			setEditorName(formConfig.getEditorName());
			setEditorType(formConfig.getEditorType());

			metadata = null;
			/*
			String 						homeName 		= Environment.getInstance(getSubSystem()).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSEditorMetaDataBeanName", "EJBSTableManager");
			EJBSEditorMetaDataHome   	metaDataHome   	= ( EJBSEditorMetaDataHome ) EJBEnvironment.lookupEJBInterface( getSubSystem(), homeName , EJBSEditorMetaDataHome.class );
			EJBSEditorMetaDataRemote 	metaDataRemote 	= metaDataHome.create( );
			metadata = metaDataRemote.lookup( getEditorName(), formConfig.getEditorType() );
			*/

		}
		catch (Exception ex){
			metadata = null;
		}
	}
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors = new ActionErrors();

		if(getKindOfAction() != null && !getKindOfAction().equals("") && !com.addval.utils.StrUtl.equals( getKindOfAction(), "cancel" )) {

			if(getDirectoryName() == null || getDirectoryName().equals("")) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("usercriteria.directoryname.required"));
			}

			if(getKindOfAction().equals("insert") || getKindOfAction().equals("update")) {

				if(getUpdateColumns() != null){
					UpdateColumn updateColumn = null;
                    boolean allNumericValues = true;

					for(Iterator iterator = getUpdateColumns().iterator();iterator.hasNext(); ) {
						updateColumn = (UpdateColumn)iterator.next();

						if ( ( updateColumn.getColumnName() == null  || updateColumn.getColumnName().trim().equals("")   ) &&
							( updateColumn.getColumnValue() != null && !updateColumn.getColumnValue().trim().equals("") ) ) {
								errors.add(ActionErrors.GLOBAL_ERROR, new ActionError( "usercriteria.updatevalue.required" ) );
						}
						else if ( ( updateColumn.getColumnName() != null && !updateColumn.getColumnName().trim().equals("")  ) &&
								 ( updateColumn.getColumnValue() == null || updateColumn.getColumnValue().trim().equals("") ) ) {
								errors.add(ActionErrors.GLOBAL_ERROR, new ActionError( "usercriteria.updatevalue.required" ) );
						}
						if (updateColumn.getColumnOperator() != null && updateColumn.getColumnOperator().trim().equals("*") && allNumericValues) {
							try{
								if(updateColumn.getColumnValue() != null && ! updateColumn.getColumnValue().trim().equals( "" ) ) {
									RE _matcher = new RE( "^(\\d*(\\.\\d+)|(\\d*))$" );
									if (_matcher == null || !_matcher.match( updateColumn.getColumnValue() )) {
										allNumericValues = false;
									}
								}
							}
							catch(RESyntaxException ex) {
								allNumericValues = false;
							}
						}
					}
					if (updateColumn != null && updateColumn.getColumnOperator() != null && updateColumn.getColumnOperator().trim().equals("*") && !allNumericValues)
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError( "usercriteria.updatevalue.invalid" ) );

					boolean hasUpdate = getUpdateColumns().iterator().hasNext();

					if (!hasUpdate && (getSearchCriteria() == null || getSearchCriteria().trim().equals(""))) {
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError( "usercriteria.updatefilter.required" ) );
					}
					else if(!hasUpdate) {
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError( "usercriteria.updatevalue.required" ) );
					}
					else if (getSearchCriteria() == null || getSearchCriteria().trim().equals("")) {
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError( "usercriteria.filter.required" ) );
					}
				}
			}

		}
		return errors;
	}
	public String getSubSystem() {
		return subSystem;
	}

	public void setSubSystem(String subSystem) {
		this.subSystem = subSystem;
	}

	public ArrayList getCriteriaNames() {
		return criteriaNames;
	}

	public void setCriteriaNames(ArrayList criteriaNames) {
		this.criteriaNames = criteriaNames;
	}

	public ArrayList getDirectoryNames() {
		return directoryNames;
	}

	public void setDirectoryNames(ArrayList directoryNames) {
		this.directoryNames = directoryNames;
	}
	public String getEditorName() {
		return editorName;
	}

	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

	public String getEditorType() {
		return editorType;
	}

	public void setEditorType(String editorType) {
		this.editorType = editorType;
	}

	public void setEDITOR_NAME(String aName)
	{
      editorName = aName;
	}

	public String getDirectoryName() {
		return directoryName;
	}

	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}

	public String getCriteriaName() {
		return criteriaName;
	}

	public void setCriteriaName(String criteriaName) {
		this.criteriaName = criteriaName;
	}

	public String getCriteriaDesc() {
		return criteriaDesc;
	}

	public void setCriteriaDesc(String criteriaDesc) {
		this.criteriaDesc = criteriaDesc;
	}

	public String getNewCriteriaName() {
		return newCriteriaName;
	}

	public void setNewCriteriaName(String newCriteriaName) {
		this.newCriteriaName = newCriteriaName;
	}

	public String getNewCriteriaDesc() {
		return newCriteriaDesc;
	}

	public void setNewCriteriaDesc(String newCriteriaDesc) {
		this.newCriteriaDesc = newCriteriaDesc;
	}
	public String getSearchCriteria() {
		return (searchCriteria != null)?searchCriteria.trim():searchCriteria;
	}

	public void setSearchCriteria(String searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public String getKindOfAction() {
		return kindOfAction;
	}

	public void setKindOfAction(String kindOfAction) {
		this.kindOfAction = kindOfAction;
	}

	public String getOverwrite() {
		return overwrite;
	}

	public void setOverwrite(String overwrite) {
		this.overwrite = overwrite;
	}

	public UpdateUserCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(UpdateUserCriteria criteria) {
		this.criteria = criteria;
	}

	public EditorMetaData getMetadata() {
		return metadata;
	}

	public void setMetadata(EditorMetaData metadata) {
		this.metadata = metadata;
	}

	public Vector getUpdateColumnList() {
		return updateColumns;
	}

	public Vector getUpdateColumns() {
		return updateColumns;
	}

	public void setUpdateColumns(Vector updateColumns) {
		this.updateColumns = updateColumns;
	}

	public void setUpdateColumns(int i, UpdateColumn updateColumn) {
		if ( i >= updateColumns.size()){
			int oldsize = updateColumns.size();
			for(int idx=oldsize;idx<=i;++idx)
				updateColumns.add(new UpdateColumn());
			updateColumns.setElementAt(updateColumn, i);
		}
		else {
			updateColumns.setElementAt(updateColumn, i);
		}
	}
	public UpdateColumn getUpdateColumns(int i) {
		if (i < updateColumns.size()){
			if (updateColumns.elementAt(i) == null)
				setUpdateColumns(i, new UpdateColumn());
			return (UpdateColumn) updateColumns.elementAt(i);
		}
		else{
			setUpdateColumns(i, new UpdateColumn());
			return (UpdateColumn) updateColumns.elementAt(i);
		}
	}

	public Vector getUpdateableColumns(){
		if(metadata == null)
			return null;

    	Vector columnsMetaData = new Vector();
        ColumnMetaData 	columnMetaData	= null;
        int	columnDataType = 0;

		for(Iterator iterator = metadata.getColumnsMetaData().iterator();iterator.hasNext();) {

			columnMetaData = (ColumnMetaData)iterator.next();
			columnDataType	= columnMetaData.getType();

            if (columnDataType != ColumnDataType._CDT_LINK &&
				columnDataType != ColumnDataType._CDT_TIMESTAMP &&
				columnDataType != ColumnDataType._CDT_VERSION &&
				columnDataType != ColumnDataType._CDT_USER &&
				columnMetaData.isUpdatable() &&
				!columnMetaData.isEditKey()) {

				columnsMetaData.add( columnMetaData );
			}
		}
        return columnsMetaData.size() > 0 ? columnsMetaData : null;
	}


	public void initializeFromMetadata(HttpServletRequest request, ActionMapping mapping, EditorMetaData metadata) {
		org.apache.struts.config.ModuleConfig appConfig = (org.apache.struts.config.ModuleConfig) request.getAttribute(org.apache.struts.Globals.MODULE_KEY);
		//BaseFormBeanConfig   formConfig = (BaseFormBeanConfig) appConfig.findFormBeanConfig(mapping.getName());
	}

}
