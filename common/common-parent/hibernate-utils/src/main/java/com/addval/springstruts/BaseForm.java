package com.addval.springstruts;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
//import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.server.EJBSEditorMetaData;
import com.addval.ejbutils.server.EJBSTableManager;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.StrUtl;

//baseForm will get Initialized using aop from editor-applicationContext.xml
//@Configurable("BaseForm")
public class BaseForm extends ActionForm{
	private static final long serialVersionUID = -470972745734830742L;
	private static transient final Logger _logger = com.addval.utils.LogMgr.getLogger(BaseForm.class);
	private EJBSEditorMetaData _editorMetadataServer = null;
	private EJBSTableManager _tableManager = null;

	public BaseForm(){
	}

	private void initializeBeans(HttpSession session){
		WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext( session.getServletContext() );
		this.setEditorMetadataServer((EJBSEditorMetaData) context.getBean( "editorMetaDataServer" ));
		this.setTableManager((EJBSTableManager) context.getBean( "editorTableManager" ));
	}

	public void setEditorMetadataServer(EJBSEditorMetaData editorMetadataServer) {
		_editorMetadataServer = editorMetadataServer;
	}

	public EJBSEditorMetaData getEditorMetadataServer() {
		return _editorMetadataServer;
	}

	public EJBSTableManager getTableManager() {
		return _tableManager;
	}
	public void setTableManager(EJBSTableManager tableManager) {
		_tableManager = tableManager;
	}


	protected String getUserGroupName(HttpSession session){
		if (session != null && session.getAttribute("USER_PROFILE") != null ) {
			Hashtable userProfile = (Hashtable) session.getAttribute("USER_PROFILE");
			if (userProfile.containsKey("USER_GROUPS")) {
				return (String) userProfile.get("USER_GROUPS");
			}
		}
		return null;
	}

	protected Hashtable getUserEditors(HttpSession session) {
		Hashtable userEditors = null;
		if (session != null) {
			userEditors = (Hashtable) session.getAttribute("USER_EDITORS");
			if (userEditors == null) {
				userEditors = new Hashtable();
				session.setAttribute("USER_EDITORS", userEditors);
			}
		}
		return userEditors;
	}
	
	protected Map<String, String> getUserTemplates(HttpSession session) {
		Map<String, String> userTemplates= null;
		if (session != null) {
			userTemplates = (Map<String, String>) session.getAttribute("USER_TEMPLATES");
			if (userTemplates == null) {
				userTemplates = new HashMap<String, String>();
				session.setAttribute("USER_TEMPLATES", userTemplates);
			}
		}
		return userTemplates;
	}
	
	protected String getTemplateName(HttpServletRequest request, String editorName, String editorType, String userId) {
		if(request.getParameter("templateName") == null){
			String userTemplateKey = getUserEditorKey(editorName, editorType, userId, null);
			Map<String, String> userTemplates = getUserTemplates(request.getSession());
			if(userTemplates.containsKey(userTemplateKey)){
				return userTemplates.get(userTemplateKey);
			}
		}
		return request.getParameter("templateName");
	}
	
	public void removeTemplateName(HttpSession session, String editorName, String editorType, String userId) {
		String userTemplateKey = getUserEditorKey(editorName, editorType, userId, null);
		Map<String, String> userTemplates = getUserTemplates(session);
		if(userTemplates.containsKey(userTemplateKey)){
			userTemplates.remove(userTemplateKey);
		}
	}

	private String getUserEditorKey(String editorName, String editorType, String userId,String templateName) {
		String key = editorName.toUpperCase() + "-" + editorType.toUpperCase() + "-" + ((userId == null) ? "DEFAULT" : userId.toUpperCase());
		if(!StrUtl.isEmptyTrimmed( templateName )) {
			key += "-" + templateName;
		}
		return key;
	}
	
	
	
	public EditorMetaData lookupMetadata(String editorName,String editorType, String userId, String templateName,String componentPrefix,HttpSession session) {
		Hashtable userEditors = getUserEditors(session);
		EditorMetaData editorMetaData = null;
		try {
			//In Websphere we are facing issues in spring aspectj. So null check added and initialize the beans using WebApplicationContextUtils.
			if(getEditorMetadataServer() == null){
				initializeBeans(session);
			}

			String userTemplateKey = getUserEditorKey(editorName, editorType, userId, null);
			Map<String, String> userTemplates = getUserTemplates(session);
			
			String key = null;
			if(StrUtl.isEmptyTrimmed(componentPrefix)){
				key = getUserEditorKey(editorName, editorType, userId,templateName);
			}
			else {
				key = getUserEditorKey(componentPrefix +"_" + editorName, editorType, userId,templateName);
			}
			if (userEditors.containsKey(key)) {
				editorMetaData = (EditorMetaData) userEditors.get(key);
				lookupEditorComboCache(editorMetaData,session);
			}
			else {
				// <USER_GROUPS> : <TEMPLATE_NAME> : <COMPONENT_PREFIX>
				String userGroupName = getUserGroupName(session);
				editorType = (!StrUtl.isEmptyTrimmed(userGroupName)) ? userGroupName + ":" :":";
				editorType += (!StrUtl.isEmptyTrimmed(templateName)) ? templateName + ":" :":";
				editorType += (!StrUtl.isEmptyTrimmed(componentPrefix)) ? componentPrefix + ":" :":";
				
				editorMetaData = getEditorMetadataServer().lookup(editorName, editorType, userId);
				userEditors.put(key, editorMetaData);
				userTemplates.put(userTemplateKey, templateName);
				_logger.debug(templateName + "lookupMetadata" + editorName +","+ editorType +","+ userId);
			}
			editorMetaData = (EditorMetaData) editorMetaData.clone();
		}
		catch (java.lang.Exception ex) {
			_logger.error(ex);
		}

		return editorMetaData;
	}

	public EditorMetaData lookupMetadata(String editorName,String editorType, String userId,HttpSession session) {
		return lookupMetadata(editorName,editorType,userId,null,null,session);
	}

    private EditorMetaData lookupEditorComboCache(EditorMetaData metadata,HttpSession session) throws Exception {
    	ColumnMetaData columnMetaData = null;
    	for (int i=1; i<=metadata.getColumnCount(); i++) {
            columnMetaData = metadata.getColumnMetaData( i );
            if (columnMetaData.isCombo() && columnMetaData.isComboSelectTag()) {
                if ((columnMetaData.isComboCached() == false) || (columnMetaData.getEjbResultSet() == null) ) {

                	if (columnMetaData.isCombo() && columnMetaData.isComboSelectTag()) {

                		String name 	= columnMetaData.getComboTblName();
                        String type 	= "DEFAULT";
                        String userId   = "DEFAULT";

                        EditorMetaData comboMetaData = lookupMetadata(name,type,userId,session);
                        EJBCriteria ejbCriteria= ( new EJBResultSetMetaData( comboMetaData ) ).getNewCriteria();
                        ejbCriteria.setEditormetadata(comboMetaData);
                        if (columnMetaData.getComboOrderBy() != null) {
                            Vector vector = new Vector();
                            vector.add(columnMetaData.getComboOrderBy());
                            ejbCriteria.orderBy(vector);
                        }
                        EJBResultSet ejbResultSet = getTableManager().lookup(ejbCriteria);
                        columnMetaData.setEjbResultSet(ejbResultSet);
                	}
                }
            }
        }
        return metadata;
    }
}
