/*
 * Copyright (c) 2001-2006 Accenture LLC.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Accenture. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Accenture.
 *
 * ACCENTURE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ACCENTURE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.addval.springstruts;

import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.regexp.RE;
import org.apache.struts.action.Action;

import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.server.EJBSEditorMetaData;
import com.addval.ejbutils.server.EJBSTableManager;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.StrUtl;
import com.addval.workqueue.api.WQSMetaData;
import com.addval.wqutils.metadata.WQMetaData;

/**
 * Base Class - For all actions. Derive from this instead of Action class.
 */
public class BaseAction extends Action {
	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(BaseAction.class);
	public static final String _DONE_FORWARD = "done";
	public static final String _FAILED_FORWARD = "failed";
	public static final String _ERROR_FORWARD = "error";
	public static final String _CANCEL_FORWARD = "cancel";
	public static final String _SHOWLIST_FORWARD = "showList";
	public static final String _SHOWEDIT_FORWARD = "showEdit";
	public static final String _INSERT_FORWARD = "insert";
	public static final String _EDIT_FORWARD = "edit";
	public static final String _DELETE_FORWARD = "delete";
	public static final String _CLONE_FORWARD = "clone";
	public static final String _OPEN_FORWARD = "open";
	public static final String _SEARCH_FORWARD = "search";
	private EJBSTableManager _tableManager = null;
	private EJBSEditorMetaData _editorMetadataServer = null;
	private String _serverType = "";
	private String _subsystem = "";
	private static final String _ERROR_STRING_PATTERN = "[\\[]([^.]*)[\\]]";
	private RE _regExp;
	
	
	/*
	 * get method for TableManager
	 */
	public EJBSTableManager getTableManager() {
		return _tableManager;
	}

	public EJBSTableManager getTableManagerBean(EJBCriteria ejbcriteria) {
		return _tableManager;
	}
	
	/*
	 * set method for TableManager
	 */
	public void setTableManager(EJBSTableManager tableManager) {
		_tableManager = tableManager;
	}

	public void setEditorMetadataServer(EJBSEditorMetaData editorMetadataServer) {
		_editorMetadataServer = editorMetadataServer;
	}

	public EJBSEditorMetaData getEditorMetadataServer() {
		return _editorMetadataServer;
	}

	public void setErrorRegularExpression( String pattern ) {
	
			_regExp 		= new RE( pattern );
	}
	
	
	/*
	 * get method for ServerType
	 */
	public String getServerType() {
		return _serverType;
	}

	/*
	 * set method for ServerType
	 */
	public void setServerType(String serverType) {
		_serverType = serverType;
	}

	/*
	 * get method for subsystem
	 */
	public String getSubsystem() {
		return _subsystem;
	}

	/*
	 * set method for subsystem
	 */
	public void setSubsystem(String subSystem) {
		_subsystem = subSystem;
	}

	/**
	 * override this method in the derived class to build actions
	 * 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @return org.apache.struts.action.ActionForward
	 * @throws java.lang.Exception
	 * @roseuid 3DCC746700E4
	 */
	public org.apache.struts.action.ActionForward execute(org.apache.struts.action.ActionMapping arg0, org.apache.struts.action.ActionForm arg1, javax.servlet.http.HttpServletRequest arg2, javax.servlet.http.HttpServletResponse arg3) throws Exception {
		return null;
	}

	public String getUserGroupName(HttpSession session) {
		if (session != null && session.getAttribute("USER_PROFILE") != null) {
			Hashtable userProfile = (Hashtable) session.getAttribute("USER_PROFILE");
			if (userProfile.containsKey("USER_GROUPS")) {
				return (String) userProfile.get("USER_GROUPS");
			}
		}
		return null;
	}

	protected Hashtable getUserEditors(javax.servlet.http.HttpSession session) {
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

	private String getUserEditorKey(String editorName, String editorType, String userId,String templateName) {
		String key = editorName.toUpperCase() + "-" + editorType.toUpperCase() + "-" + ((userId == null) ? "DEFAULT" : userId.toUpperCase());
		if(!StrUtl.isEmptyTrimmed( templateName )) {
			key += "-" + templateName;
		}
		return key;
	}

	public EditorMetaData lookupMetadata(String editorName, String editorType, String userId,String templateName,String componentPrefix, HttpSession session,boolean clone) {
		Hashtable userEditors = getUserEditors(session);
		EditorMetaData editorMetaData = null;
		try {
			String key = null;
			if(StrUtl.isEmptyTrimmed(componentPrefix)){
				key = getUserEditorKey(editorName, editorType, userId,templateName);
			}
			else {
				key = getUserEditorKey(componentPrefix +"_" + editorName, editorType, userId,templateName);
			}
			
			if (userEditors.containsKey(key)) {
				editorMetaData = (EditorMetaData) userEditors.get(key);
				lookupEditorComboCache(editorMetaData, session);
			}
			else {
				String userGroupName = getUserGroupName(session);
				// <USER_GROUPS> : <TEMPLATE_NAME> : <COMPONENT_PREFIX>
				editorType = (!StrUtl.isEmptyTrimmed(userGroupName)) ? userGroupName + ":" :":";
				editorType += (!StrUtl.isEmptyTrimmed(templateName)) ? templateName + ":" :":";
				editorType += (!StrUtl.isEmptyTrimmed(componentPrefix)) ? componentPrefix + ":" :":";

				editorMetaData = getEditorMetadataServer().lookup(editorName, editorType, userId);
				userEditors.put(key, editorMetaData);
				System.out.println(templateName + "lookupMetadata" + editorName +","+ editorType +","+ userId);
			}
			if(clone){
				editorMetaData = (EditorMetaData) editorMetaData.clone();	
			}
		}
		catch (java.lang.Exception ex) {
			_logger.error(ex);
		}
		return editorMetaData;
	}

	public EditorMetaData lookupMetadata(String editorName, String editorType, String userId, HttpSession session,boolean clone) {
		return lookupMetadata(editorName,editorType,userId,null,null,session,clone);
	}

	public EditorMetaData lookupMetadata(String editorName, String editorType, String userId, HttpSession session) {
		return lookupMetadata(editorName,editorType,userId,null,null,session,true);
	}

	public void removeMetadata(String editorName, String editorType, String userId, javax.servlet.http.HttpSession session) {
		Hashtable userEditors = getUserEditors(session);
		String key = getUserEditorKey(editorName, editorType, userId,null);
		System.out.println("removeMetadata" + key);
		userEditors.remove(key);
	}
	public void removeMetadata(String editorName, String editorType, String userId,String templateName, javax.servlet.http.HttpSession session) {
		Hashtable userEditors = getUserEditors(session);
		String key = getUserEditorKey(editorName, editorType, userId,templateName);
		System.out.println("removeMetadata" + key);
		userEditors.remove(key);
	}
	public String getEditorName(HttpServletRequest request){
		String editorName = request.getParameter("EDITOR_NAME");
		if (StrUtl.isEmptyTrimmed(editorName)) {
			editorName = (String) request.getAttribute("EDITOR_NAME");
		}
		if (StrUtl.isEmptyTrimmed(editorName)) {
			editorName = request.getParameter("editorName");
		}
		return editorName;
	}

	public String getEditorType(HttpServletRequest request){
		String editorType = (String) request.getParameter("EDITOR_TYPE");
		if (StrUtl.isEmptyTrimmed(editorType)) {
			editorType = (String) request.getAttribute("EDITOR_TYPE");
		}
		if (StrUtl.isEmptyTrimmed(editorType)) {
			editorType = request.getParameter("editorType");
		}
		if (StrUtl.isEmptyTrimmed(editorType)) {
			editorType = "DEFAULT";
		}
		return editorType;
	}

	public WQMetaData lookupWqSMetaData(WQSMetaData wqSMetaData, HttpSession session, String queue) {
		Hashtable userEditors = getUserEditors(session);
		WQMetaData wqMetaData = null;
		try {
			if (userEditors.containsKey(queue))
				wqMetaData = (WQMetaData) userEditors.get(queue);
			else {
				wqMetaData = wqSMetaData.lookup(queue);
				userEditors.put(queue, wqMetaData);
			}
		}
		catch (java.lang.Exception ex) {
			_logger.error(ex);
		}

		return wqMetaData;
	}

	public Vector<WQMetaData> lookupWqSMetaData(WQSMetaData wqSMetaData, HttpSession session, Vector<String> queueCollection) {
		if ((queueCollection == null) || (queueCollection.size() == 0))
			return null;

		Vector<WQMetaData> wqMetaDatas = new Vector<WQMetaData>();
		WQMetaData wqMetaData = null;
		String queue = null;
		for (int i = 0; i < queueCollection.size(); i++) {
			queue = queueCollection.get(i);
			wqMetaData = lookupWqSMetaData(wqSMetaData, session, queue);
			wqMetaDatas.add(wqMetaData);
		}

		return wqMetaDatas;
	}

	private EditorMetaData lookupEditorComboCache(EditorMetaData metadata, HttpSession session) throws Exception {
		ColumnMetaData columnMetaData = null;
		for (int i = 1; i <= metadata.getColumnCount(); i++) {
			columnMetaData = metadata.getColumnMetaData(i);
			if (columnMetaData.isCombo() && columnMetaData.isComboSelectTag()) {
				if ((columnMetaData.isComboCached() == false) || (columnMetaData.getEjbResultSet() == null)) {
					if (columnMetaData.isCombo() && columnMetaData.isComboSelectTag()) {
						String name = columnMetaData.getComboTblName();
						String type = "DEFAULT";
						String userId = "DEFAULT";
						EditorMetaData comboMetaData = lookupMetadata(name, type, userId, session);
						EJBCriteria ejbCriteria = (new EJBResultSetMetaData(comboMetaData)).getNewCriteria();
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
	
	
	// Internationalize the error message. Search for a string like [.....] (for example: [2292XFKRTADDON_RTADDONDTL]) in the error message string
	// if found use the value within the square brackets as a key value prefixed by "error." and lookup the resource string for message and extract it
	// if nothing found, use the input errormessage as the returned message
	// for the first time - compile the reqular expression
	public String translateErrorMessage(HttpServletRequest request, String errorMessage) {
		String message = null;

		if (_regExp == null) {
			setErrorRegularExpression(_ERROR_STRING_PATTERN);
		}
		
		if (errorMessage != null) {
			boolean matched = _regExp.match( errorMessage );

			if (matched) {
				String key = "error." + _regExp.getParen( 1 );
				message = ResourceUtils.message(request, key, errorMessage);
				if(message.contains(_regExp.getParen( 1 ))){
					message=message.replace("[" + _regExp.getParen( 1 ) + "]", "");
				}
			}
		}

		return message;
	}
	
	
}
