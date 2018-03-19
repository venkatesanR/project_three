
package com.addval.struts;

import com.addval.environment.Environment;
import com.addval.utils.XRuntime;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.UserCriteria;
import com.addval.jasperutils.EditorMetaDataReportDesigner;
import org.apache.struts.action.*;
import org.apache.struts.Globals;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;

public class ExportAction extends BaseAction {
    private static final String SUB_SYSTEM = "cargores_jasper";

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        ActionMessages      messages    = new ActionMessages();
        ActionErrors        errors      = new ActionErrors();
        BaseActionMapping   baseMapping = (BaseActionMapping) mapping;
		String              subsystem   = (baseMapping.getSubsystem() == null) ? getSubsystem( request ) : baseMapping.getSubsystem();
		Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceEnter( "execute.traceEnter" );
        String forwardPage = ListAction._ERROR_FORWARD;
        try {
			super.execute(mapping, form, request, response);
            ExportForm exportForm = (ExportForm) form;

            ExportUtil util = new ExportUtil( subsystem );
            exportForm.setSubSystem(subsystem);
            EditorMetaData  editorMetadata  =  util.getEJBSEditorMetaDataRemote().lookup( exportForm.getEditorName(), exportForm.getEditorType() );
            exportForm.setMetadata( editorMetadata );

            String kindOfAction = exportForm.getKindOfAction() != null ? exportForm.getKindOfAction() : "";

            if( kindOfAction.equals( "initialize"  ) ) {
                util.setDefaultValues(exportForm,request);
			}
            else if( kindOfAction.equals( "export"  )) {

                if(exportForm.getExportType().equalsIgnoreCase( String.valueOf( EditorMetaDataReportDesigner.XLS_FORMAT ) ) ) {
                    forwardPage = "exportxls";
                }
                else if(exportForm.getExportType().equalsIgnoreCase( String.valueOf( EditorMetaDataReportDesigner.CSV_FORMAT ) ) ){
                    forwardPage = "exportcsv";
                }
                else if(exportForm.getExportType().equalsIgnoreCase( String.valueOf( EditorMetaDataReportDesigner.PDF_FORMAT ) ) ){
                    forwardPage = "exportpdf";
                }
                else if(exportForm.getExportType().equalsIgnoreCase( String.valueOf( EditorMetaDataReportDesigner.HTML_FORMAT  ) ) ){
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
                errors = util.export(mapping,exportForm,request,response);
                saveErrors( request, errors );
                return mapping.findForward( forwardPage );
			}
		}
		catch (XRuntime xr) {

			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error looking up data in ExportAction");
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(xr);

			errors = new ActionErrors();
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", xr.getMessage() ) );
			saveErrors( request, errors );

            return mapping.findForward( forwardPage );

		}
		catch (Exception ex) {

			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error looking up data in ExportAction");
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(ex);

			errors = new ActionErrors();
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Critical System Error - Please Contact System Administrator") );
			saveErrors( request, errors );


			return mapping.findForward( forwardPage );

		}

        Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceExit( "execute.traceExit" );
		return mapping.findForward(ListAction._DONE_FORWARD );
	}
}
