/*
 * AggregatableListAction.java
 *
 * Created on July 31, 2003, 3:39 PM
 */

package com.addval.springstruts;

import javax.servlet.http.*;
import org.apache.struts.action.*;
import com.addval.metadata.EditorMetaData;
import com.addval.ejbutils.server.EJBSTableManager;
import com.addval.ejbutils.dbutils.EJBStatement;
import com.addval.ejbutils.dbutils.EJBResultSet;
import java.util.Vector;
import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.environment.Environment;

/**
 *
 * @author  ravi
 */
public class AggregatableListAction extends BaseAction
{
	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(AggregatableListAction.class);

	/** Creates a new instance of AggregatableListAction */
	public AggregatableListAction()
	{
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception
	{
		BaseActionMapping baseMapping = (BaseActionMapping) mapping;

		_logger.trace("execute.traceEnter");

		try{
			AggregatableListForm listForm = (AggregatableListForm) form;
			EditorMetaData   editorMetaData = listForm.getMetadata();
			if (editorMetaData == null) {
				   editorMetaData = getEditorMetadataServer().lookup(listForm.getEditorName(), listForm.getEditorType());
				   listForm.setMetadata(editorMetaData);
				   listForm.configureMetadata();
			}

			super.execute(mapping, form, req, res);
		}
		catch (java.lang.Exception ex){
			_logger.error("Error looking up data in ListAction");
			_logger.error(ex);
			throw(ex);
		}

		_logger.trace("execute.traceExit");

		return mapping.findForward(ListAction._SHOWLIST_FORWARD);
	}
}
