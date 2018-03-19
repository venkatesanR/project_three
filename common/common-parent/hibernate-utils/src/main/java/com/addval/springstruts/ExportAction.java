package com.addval.springstruts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import com.addval.esapiutils.validator.HTMLSecurityValidator;
import com.addval.jasperutils.EditorMetaDataReportDesigner;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;

public class ExportAction extends BaseAction {
	private HTMLSecurityValidator htmlSecurityValidator;

	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(ExportAction.class);
	private ExportUtil _utility = null;
	

	public HTMLSecurityValidator getHtmlSecurityValidator() {
		return htmlSecurityValidator;
	}

	public void setHtmlSecurityValidator(HTMLSecurityValidator htmlSecurityValidator) {
		this.htmlSecurityValidator = htmlSecurityValidator;
	}

	public void setExportUtility(ExportUtil util) {
		_utility = util;
	}

	public ExportUtil getExportUtility() {
		return _utility;
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		ActionMessages messages = new ActionMessages();
		ActionErrors errors = new ActionErrors();
		BaseActionMapping baseMapping = (BaseActionMapping) mapping;
		_logger.trace("execute.traceEnter");
		String forwardPage = ListAction._ERROR_FORWARD;
		try {
			super.execute(mapping, form, request, response);
			ExportForm exportForm = (ExportForm) form;
			
			String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "DEFAULT";
			htmlSecurityValidator= getHtmlSecurityValidator();

			String templateName=request.getParameter("templateName");
			if(!StrUtl.isEmptyTrimmed(templateName)) {
				templateName=htmlSecurityValidator.getValidInput("ExportAction", templateName);
			}
			else{
				templateName="";
			}
			
			//String templateName = request.getParameter("templateName") != null ? request.getParameter("templateName") : "";
			String componentPrefix = request.getParameter("componentPrefix") != null ? request.getParameter("componentPrefix") : "";
			EditorMetaData editorMetadata = null;
			if(!StrUtl.isEmptyTrimmed(templateName)) {
				editorMetadata = lookupMetadata(exportForm.getEditorName(), exportForm.getEditorType(), userName,templateName,componentPrefix,request.getSession(),true);
			}
			else {
				editorMetadata = lookupMetadata(exportForm.getEditorName(), exportForm.getEditorType(), userName,null,componentPrefix,request.getSession(),true);
			}
			exportForm.setMetadata(editorMetadata);
			String kindOfAction = exportForm.getKindOfAction() != null ? exportForm.getKindOfAction() : "";

			if (kindOfAction.equals("initialize")) {
				getExportUtility().setDefaultValues(exportForm, request);
			}
			else if (kindOfAction.equals("export")) {

				if (exportForm.getExportType().equalsIgnoreCase(String.valueOf(EditorMetaDataReportDesigner.XLS_FORMAT))) {
					forwardPage = "exportxls";
				}
				else if (exportForm.getExportType().equalsIgnoreCase(String.valueOf(EditorMetaDataReportDesigner.CSV_FORMAT))) {
					forwardPage = "exportcsv";
				}
				else if (exportForm.getExportType().equalsIgnoreCase(String.valueOf(EditorMetaDataReportDesigner.PDF_FORMAT))) {
					forwardPage = "exportpdf";
				}
				else if (exportForm.getExportType().equalsIgnoreCase(String.valueOf(EditorMetaDataReportDesigner.HTML_FORMAT))) {
					forwardPage = "exporthtml";
				}
                /*
                System.setProperty("jasper.reports.compile.class.path", Environment.getInstance( SUB_SYSTEM ).getCnfgFileMgr().getPropertyValue("jasper.reports.compile.class.path","") );
                System.setProperty("jasper.reports.compile.temp", Environment.getInstance( SUB_SYSTEM ).getCnfgFileMgr().getPropertyValue("jasper.reports.compile.temp","") );
                System.setProperty("jasper.reports.compiler.class", Environment.getInstance( SUB_SYSTEM ).getCnfgFileMgr().getPropertyValue("jasper.reports.compiler.class","net.sf.jasperreports.engine.design.JRBshCompiler") );
                System.setProperty("jasper.reports.compile.keep.java.file", Environment.getInstance( SUB_SYSTEM ).getCnfgFileMgr().getPropertyValue("jasper.reports.compile.keep.java.file","false") );

                System.out.println("jasper.reports.compile.class.path" + System.getProperty("jasper.reports.compile.class.path"));
                System.out.println("jasper.reports.compile.temp" + System.getProperty("jasper.reports.compile.temp"));
                System.out.println("jasper.reports.compiler.class" + System.getProperty("jasper.reports.compiler.class"));
                System.out.println("jasper.reports.compile.keep.java.file" + System.getProperty("jasper.reports.compile.keep.java.file"));
                */
				
				errors = getExportUtility().export(mapping, exportForm, request, response);
				saveErrors(request, errors);
				return mapping.findForward(forwardPage);
			}
		}
		catch (XRuntime xr) {

			_logger.error("Error looking up data in ExportAction");
			_logger.error(xr);
			xr.printStackTrace();
			errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", xr.getMessage()));
			saveErrors(request, errors);

			return mapping.findForward(forwardPage);

		}
		catch (Exception ex) {

			_logger.error("Error looking up data in ExportAction");
			_logger.error(ex);
			errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", ResourceUtils.message(request, "error.criticalerror", "Critical System Error - Please Contact System Administrator")));
			saveErrors(request, errors);

			return mapping.findForward(forwardPage);

		}

		_logger.trace("execute.traceExit");
		return mapping.findForward(ListAction._DONE_FORWARD);
	}
}
