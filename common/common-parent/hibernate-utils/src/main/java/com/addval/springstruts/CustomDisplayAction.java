
package com.addval.springstruts;

import com.addval.environment.Environment;
import com.addval.utils.XRuntime;
import org.apache.struts.action.*;
import org.apache.struts.Globals;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomDisplayAction extends BaseAction {

	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(CustomDisplayAction.class);

	private CustomDisplayUtil _utility = null;

	public void setCustomDisplayUtility(CustomDisplayUtil util) {
		_utility = util;
	}

	public CustomDisplayUtil getCustomDisplayUtility() {
		return _utility;
	}


	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		BaseActionMapping baseMapping = (BaseActionMapping) mapping;

		//String subsystem = (baseMapping.getSubsystem() == null) ? getSubsystem( request ) : baseMapping.getSubsystem();
		String userName  = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "DEFAULT";

		_logger.trace( "execute.traceEnter" );
        ActionMessages messages = new ActionMessages();

        try {
			super.execute(mapping, form, request, response);
            CustomDisplayUtil util = getCustomDisplayUtility();

			CustomDisplayForm customDisplayForm = (CustomDisplayForm) form;
            //customDisplayForm.setSubSystem(subsystem);
            customDisplayForm.setUserId( userName );

            String kindOfAction = customDisplayForm.getKindOfAction() != null ? customDisplayForm.getKindOfAction() : "";

            if( kindOfAction.equals( "save"  ) || kindOfAction.equals( "reset"  ) ) {
                util.update(customDisplayForm,request);
				removeMetadata(customDisplayForm.getEditorName(), customDisplayForm.getEditorType(), userName, request.getSession());
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

			_logger.error("Error looking up data in CustomDisplayAction");
			_logger.error(xr);

			ActionErrors errors = new ActionErrors();
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", xr.getMessage() ) );
			saveErrors( request, errors );

			return mapping.findForward( ListAction._ERROR_FORWARD );

		}
		catch (Exception ex) {

			_logger.error("Error looking up data in CustomDisplayAction");
			_logger.error(ex);

			ActionErrors errors = new ActionErrors();
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Critical System Error - Please Contact System Administrator") );
			saveErrors( request, errors );

			return mapping.findForward( ListAction._ERROR_FORWARD );

		}

        _logger.trace( "execute.traceExit" );
		return mapping.findForward(ListAction._DONE_FORWARD );
	}
}
