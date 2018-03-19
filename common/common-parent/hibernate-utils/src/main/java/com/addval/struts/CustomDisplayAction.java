
package com.addval.struts;

import com.addval.environment.Environment;
import com.addval.utils.XRuntime;
import org.apache.struts.action.*;
import org.apache.struts.Globals;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomDisplayAction extends BaseAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		BaseActionMapping baseMapping = (BaseActionMapping) mapping;

		String subsystem = (baseMapping.getSubsystem() == null) ? getSubsystem( request ) : baseMapping.getSubsystem();
		String userName  = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "DEFAULT";

		Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceEnter( "execute.traceEnter" );
        ActionMessages messages = new ActionMessages();

        try {
			super.execute(mapping, form, request, response);
            CustomDisplayUtil util = new CustomDisplayUtil(subsystem);

			CustomDisplayForm customDisplayForm = (CustomDisplayForm) form;
            customDisplayForm.setSubSystem(subsystem);
            customDisplayForm.setUserId( userName );

            String kindOfAction = customDisplayForm.getKindOfAction() != null ? customDisplayForm.getKindOfAction() : "";

            if( kindOfAction.equals( "save"  ) || kindOfAction.equals( "reset"  ) ) {
                util.update(customDisplayForm,request);
                messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", "Updated Successfully." ));
                customDisplayForm.setKindOfAction("saved");
			}
            else {
                util.lookup( customDisplayForm );
                if(customDisplayForm.getAllColumns().size() == 0){
                    messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", "No columns to cutomize. Key columns are not customizable."));
                }

            }
            saveMessages(request, messages);

		}
		catch (XRuntime xr) {

			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error looking up data in CustomDisplayAction");
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(xr);

			ActionErrors errors = new ActionErrors();
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", xr.getMessage() ) );
			saveErrors( request, errors );

			return mapping.findForward( ListAction._ERROR_FORWARD );

		}
		catch (Exception ex) {

			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error looking up data in CustomDisplayAction");
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(ex);

			ActionErrors errors = new ActionErrors();
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Critical System Error - Please Contact System Administrator") );
			saveErrors( request, errors );

			return mapping.findForward( ListAction._ERROR_FORWARD );

		}

        Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceExit( "execute.traceExit" );
		return mapping.findForward(ListAction._DONE_FORWARD );
	}
}
