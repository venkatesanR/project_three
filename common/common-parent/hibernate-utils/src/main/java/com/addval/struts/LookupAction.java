//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\LookupAction.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\LookupAction.java

package com.addval.struts;

import com.addval.metadata.EditorMetaData;
import java.util.Vector;
import java.util.Iterator;

import com.addval.metadata.ColumnMetaData;
import com.addval.environment.Environment;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action to create a Lookup Screen
 * @author AddVal Technology Inc.
 */
public class LookupAction extends ListAction
{

	/**
	 * @param mapping
	 * @param form
	 * @param req
	 * @param resp
	 * @return org.apache.struts.action.ActionForward
	 * @throws java.lang.Exception
	 * @roseuid 3DD2EDE903C2
	 */
	public org.apache.struts.action.ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception
	{
		BaseActionMapping baseMapping = (BaseActionMapping) mapping;
		String subsystem = (baseMapping.getSubsystem() == null)?getSubsystem(req):baseMapping.getSubsystem();

		Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceEnter( "execute.traceEnter" );
		LookupForm lkpForm = (LookupForm) form;

		ActionForward fwd = super.execute(mapping, form,req, resp);
		EditorMetaData   editorMetaData = lkpForm.getMetadata();

        Vector  displayableColumns = editorMetaData.getDisplayableColumns();
        String fieldName = lkpForm.getDisplayFieldName();
        String originalFieldName = fieldName;
        if ( fieldName.indexOf("_look")>0 || fieldName.indexOf("_edit")>0 || fieldName.indexOf("_search")>0 )
            originalFieldName = fieldName.substring(0,fieldName.lastIndexOf("_"));

        ColumnMetaData columnMetaData = null;
        int cellNo = 0;
        for (int index =0; index < displayableColumns.size(); index++) {
            columnMetaData= (ColumnMetaData) displayableColumns.get(index);
            if(columnMetaData.getName().equalsIgnoreCase( originalFieldName )){
                cellNo = index;
                break;
            }
        }
        lkpForm.setSelectCellNo(cellNo);
		Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceExit( "execute.traceExit" );
		return fwd;
	}
}
