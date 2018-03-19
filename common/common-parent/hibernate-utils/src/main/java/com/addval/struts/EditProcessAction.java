//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\EditProcessAction.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\EditProcessAction.java

package com.addval.struts;

import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;
import com.addval.ejbutils.server.EJBSTableManagerHome;
import com.addval.ejbutils.server.EJBSTableManagerRemote;
import java.util.Vector;
import java.util.Iterator;
import java.util.Hashtable;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.addval.metadata.EditorMetaData;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.ColumnDataType;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.environment.Environment;
import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.dbutils.RSIterAction;

/**
 * The processing Action from Edit Screen
 * @author AddVal Technology Inc.
 */
public class EditProcessAction extends BaseAction
{

	/**
	 * @param mapping The ActionMapping used to select this instance
	 * @param form The optional ActionForm bean for this request (if any)
	 * @param req The non-HTTP request we are processing
	 * @param res The non-HTTP response we are creating
	 * @return Return an ActionForward instance describing where and how
	 * control should be forwarded, or null if the response has already
	 * been completed.
	 * @throws java.lang.Exception
	 * @roseuid 3DCC93830376
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception
	{

		String 	 	 subsystem  = null;
		String 	  	 module	  	= getClass().getName();
		EditForm 	 editForm 	= (EditForm) form;
		EJBResultSet ejbRS 		= null;
		try {

			BaseActionMapping baseMapping = (BaseActionMapping) mapping;
			String 			  serverType  = getServertype(req);

			if (baseMapping.getSubsystem() == null)
			{
				subsystem = getSubsystem( req );
			}
			else
			{
				subsystem = baseMapping.getSubsystem();
			}

			//Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceEnter( "execute.traceEnter" );

			EditorMetaData	editorMetaData 	= editForm.getMetadata();

			//Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).logInfo( "Start perform(" + form + ") Action is . . ." + editForm.getAction()  );

			if (editForm.getAction().equalsIgnoreCase("cancel")) {
                if ( editForm.isListEdit() ) {
                    return getParentRefreshAction(mapping,editorMetaData,req,EditProcessAction._CANCEL_FORWARD,editForm.getKindOfAction());
				}
				else {
					return mapping.findForward(EditProcessAction._CANCEL_FORWARD);
				}
			}

			if ( editForm.getKindOfAction().equals("edit") )  {

				ejbRS = EjbUtils.getNewEJBResultSet ( editorMetaData, req, false );
			}
			else if ( editForm.getKindOfAction().equals("insert") || editForm.getKindOfAction().equals("clone") ) {

				ejbRS = EjbUtils.getInsertEJBResultSet ( editorMetaData ,req, false );
			}
			else if (editForm.getKindOfAction().equalsIgnoreCase( "delete" )) {

				ejbRS = EjbUtils.getDeleteEJBResultSet( editorMetaData, req );
			}

			boolean 			   updateStatus 	  = true;
			EJBSTableManagerHome   tableManagerHome   = getTableManagerHome( subsystem );
			EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();


			if (editForm.getFormInterceptor() != null)
			{
				editForm.getFormInterceptor().beforeDataProcess(mapping, form, req, res, ejbRS);

				ActionErrors errors = new ActionErrors();

				editForm.getFormInterceptor().customValidation(mapping,errors,ejbRS);

				if(errors.size() > 0) {
					saveErrors( req, errors );
					if (ejbRS != null) {
						ejbRS.beforeFirst();
						editForm.setResultset( ejbRS );
					}
					editForm.setMutable( false );
                    if(editForm.getKindOfAction().equalsIgnoreCase( "delete" ) && mapping.findForwardConfig(EditProcessAction._CANCEL_FORWARD) != null ) {
                        return mapping.findForward(EditProcessAction._CANCEL_FORWARD );
                    }
					return mapping.findForward( EditProcessAction._FAILED_FORWARD );
				}
			}

			// Reset the errors
			editForm.setErrorMsg( "" );

			if ( editForm.getKindOfAction().equals("insert") || editForm.getKindOfAction().equals("clone") ) {
				setFileByteArray2EjbResultSet( editForm, editorMetaData, req, ejbRS );
			}

			ejbRS = tableManagerRemote.updateTransaction ( ejbRS );

			if (ejbRS == null) {

				updateStatus = false;
				editForm.setErrorMsg(editForm.getKindOfAction() + " failed. ");
			}
			else {

				ejbRS.beforeFirst();
				while (updateStatus &&  ejbRS.next() ){
					if (( editForm.getKindOfAction().equals("delete") && !ejbRS.rowDeleted() ) || ( editForm.getKindOfAction().equals("clone") && !ejbRS.rowInserted() ) || ( editForm.getKindOfAction().equals("insert") && !ejbRS.rowInserted() ) || ( editForm.getKindOfAction().equals("edit") && !ejbRS.rowUpdated() ) ) {

						updateStatus = false;
						editForm.setErrorMsg(editForm.getKindOfAction() + " failed. ");
					}
					else if (ejbRS.getRecord().getSyncStatus() != com.addval.metadata.RecordStatus._RSS_SYNC) {

						updateStatus = false;
						editForm.setErrorMsg("Database update failed due to concurrency issues. Please try the transaction again.");
					}
				}
			}

			if ( updateStatus && serverType.equals("MSAccess")) {
				// For MS Access to commit changes to other connections sleep
				java.lang.Thread.currentThread().sleep(1000);
			}

			if (editForm.getFormInterceptor() != null)
			{
				editForm.getFormInterceptor().afterDataProcess(mapping, form, req, res, ejbRS);
			}

			if (updateStatus) {
				// Added so that the edit form has the resultset. If anyone overrides this
				// action class and calls super.execute(), the resultset will be available for them
				editForm.setResultset( ejbRS );
				if (ejbRS != null && editForm.hasChild() && (editForm.getKindOfAction().equals("insert") || editForm.getKindOfAction().equals("clone")) ) {
                    return getEditAction(editorMetaData,ejbRS,req);
				}
				else if ( editForm.isListEdit() ) {
                    return getParentRefreshAction(mapping,editorMetaData,req,EditProcessAction._DONE_FORWARD,editForm.getKindOfAction());
				}
				else {
                    return mapping.findForward(EditProcessAction._DONE_FORWARD);
				}
			}
			else {

				ActionErrors errors = new ActionErrors();
				errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.invalid", editForm.getErrorMsg() ) );
				saveErrors( req, errors );
				editForm.setResultset( null );
				editForm.setMutable( false );

                if(editForm.getKindOfAction().equalsIgnoreCase( "delete" ) && mapping.findForwardConfig(EditProcessAction._CANCEL_FORWARD) != null ) {
                    return mapping.findForward(EditProcessAction._CANCEL_FORWARD );
                }
                return mapping.findForward(EditProcessAction._FAILED_FORWARD);
			}

			//Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceExit( "execute.traceExit" );
		}
		catch (EJBXRuntime xr) {

			Environment.getInstance( subsystem ).getLogFileMgr( module ).logError ( xr );
			ActionErrors errors = new ActionErrors();
			// added changes for processing only a single line of error text
			// useful to filter off any excess Server side error message addition
			// under clustered condition - jeyaraj - 02-Oct-2003
			String errMsg = xr.getMessage();
			int index = errMsg.indexOf( "\n" );
			if (index > 0)
				errMsg = errMsg.substring( 0, index );
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.invalid", errMsg ) );
			saveErrors( req, errors );
			editForm.setErrorMsg( errMsg );
			if (ejbRS != null) {
				// Set the form's resultset
				ejbRS.beforeFirst();
				editForm.setResultset( ejbRS );
			}
			editForm.setMutable( false );
            if(editForm.getKindOfAction().equalsIgnoreCase( "delete" ) && mapping.findForwardConfig(EditProcessAction._CANCEL_FORWARD) != null ) {
                return mapping.findForward(EditProcessAction._CANCEL_FORWARD );
            }
			return mapping.findForward( EditProcessAction._FAILED_FORWARD );
		}
		catch (IOException ioe) {
			Environment.getInstance( subsystem ).getLogFileMgr( module ).logError ( ioe );
			ActionErrors errors = new ActionErrors();
			String errMsg = ioe.getMessage();
			int index = errMsg.indexOf( "\n" );
			if (index > 0)
				errMsg = errMsg.substring( 0, index );
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.invalid", errMsg ) );
			saveErrors( req, errors );
			editForm.setErrorMsg( errMsg );
			if (ejbRS != null) {
				// Set the form's resultset
				ejbRS.beforeFirst();
				editForm.setResultset( ejbRS );
			}
			editForm.setMutable( false );
			if(editForm.getKindOfAction().equalsIgnoreCase( "delete" ) && mapping.findForwardConfig(EditProcessAction._CANCEL_FORWARD) != null ) {
				return mapping.findForward(EditProcessAction._CANCEL_FORWARD );
			}
			return mapping.findForward( EditProcessAction._FAILED_FORWARD );
		}
		catch (Exception e) {

			Environment.getInstance( subsystem ).getLogFileMgr( module ).logError ( e );
			String message = "Critical System Error. Please contact your System Administrator.";
			ActionErrors errors = new ActionErrors();
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", message) );
			saveErrors( req, errors );
			editForm.setErrorMsg( message );
			if (ejbRS != null) {
				// Set the form's resultset
				ejbRS.beforeFirst();
				editForm.setResultset( ejbRS );
			}
			editForm.setMutable( false );
			if(editForm.getKindOfAction().equalsIgnoreCase( "delete" ) && mapping.findForwardConfig(EditProcessAction._CANCEL_FORWARD) != null ) {
				return mapping.findForward(EditProcessAction._CANCEL_FORWARD );
			}
			return mapping.findForward( EditProcessAction._FAILED_FORWARD );
		}
	}

    private ActionForward getParentRefreshAction(ActionMapping mapping,EditorMetaData editorMetaData,HttpServletRequest request,String action,String kindOfAction) throws java.io.UnsupportedEncodingException {

		String queryString 		= getQueryString(editorMetaData,request);

		ActionForward forward = mapping.findForward(action);
        String refreshparent 	= forward.getPath().substring(1);

        if(refreshparent.indexOf("?") != -1){
			refreshparent += queryString;
		}
		else if(queryString.length() > 0){
			refreshparent += "?" + queryString.substring(1);
		}

		refreshparent = "/refreshparent.jsp?LOAD_URL=" + java.net.URLEncoder.encode( refreshparent, "UTF-8" )+"&ActionType=" + kindOfAction;;

        return new ActionForward(EditProcessAction._DONE_FORWARD, refreshparent,true);
    }

	protected String getLinkJsp( EditorMetaData editorMetaData ) {

		String linkJsp = null;

        if (editorMetaData.getDisplayableColumns() != null) {

            ColumnMetaData columnMetaData = null;

            for (Iterator iter = editorMetaData.getDisplayableColumns().iterator(); iter.hasNext();) {

                columnMetaData = ( ColumnMetaData ) iter.next();

                if (columnMetaData.getType() == ColumnDataType._CDT_LINK && columnMetaData.getName().toUpperCase().endsWith("_EDIT") ) {
                    linkJsp = columnMetaData.getRegexp();
                    break;
                }
            }
        }

        return linkJsp;
	}

    private ActionForward getEditAction(EditorMetaData editorMetaData,EJBResultSet ejbRS, HttpServletRequest request) throws EJBXRuntime {
        String buildQuery = getQueryString(editorMetaData,request);
        /****************************************************************/
        String columnName = null;
        StringBuffer keys = new StringBuffer();
        if (editorMetaData.getKeyColumns() != null) {
            ejbRS.beforeFirst();
            if(ejbRS.next()) {
				for (Iterator iter = editorMetaData.getKeyColumns().iterator(); iter.hasNext();) {

					columnName = ((ColumnMetaData)iter.next()).getName();
					keys.append ( "&" );
					keys.append ( columnName + "_KEY" );
					keys.append ( "=" );
					keys.append ( ejbRS.getString( columnName ) );
				}
			}
        }
        String linkJsp = getLinkJsp( editorMetaData );

        if (linkJsp == null || linkJsp.equals( "" ))
            throw new EJBXRuntime( "Target Link file for columnName is not specified in RegExp column of Table!" );

        linkJsp += linkJsp.indexOf("?") != -1 ? "&" : "?";

        if(buildQuery.length() > 0 )
            linkJsp +=  buildQuery.substring(1) + keys.toString();
        else
            linkJsp += keys.toString().length() > 0 ? keys.substring(1) : "";

        if(!linkJsp.startsWith("/")){
            linkJsp = "/" + linkJsp;
        }
        return new ActionForward(EditProcessAction._DONE_FORWARD, linkJsp,true);

	}

    protected String getQueryString(EditorMetaData editorMetaData,HttpServletRequest request ){

        StringBuffer buildQuery = new  StringBuffer();
        String columnName = null;
        String columnValue = null;
        /****************************************************************/
        for ( java.util.Enumeration enumeration = request.getParameterNames(); enumeration.hasMoreElements(); ) {
            columnName  = (String) enumeration.nextElement();

            if ( columnName.endsWith( "childIndex" ) ||
				 columnName.endsWith( "_lookUp" ) ||
            	 columnName.endsWith( "_PARENT_KEY" ) ||
            	 columnName.endsWith( "operator_lookUp" ) &&
                 !columnName.equals( "EDITOR_NAME" ) &&
                 !columnName.equals( "EDITOR_TYPE" ) || columnName.equals( "POSITION" ) || columnName.equals( "DETAILS" ) ) {

                columnValue =request.getParameter( columnName );
                if (columnValue != null && columnValue.length() != 0)
                    buildQuery.append( "&" + columnName + "=" + request.getParameter( columnName ). trim() );
            }
            else if ( columnName.equals("PAGING_ACTION") ) {
                buildQuery.append( "&" + columnName + "=" + RSIterAction._CURR_STR );
            }
        }
        buildQuery.append( "&EDITOR_NAME=" + editorMetaData.getName() );
        buildQuery.append( "&EDITOR_TYPE=" + editorMetaData.getType() );
        return buildQuery.toString();
    }

/**
 *  This method convert file upload in the serveltInputStream to byte Array
 */
	public void setFileByteArray2EjbResultSet(EditForm form, EditorMetaData editorMetaData, HttpServletRequest request, EJBResultSet ejbRS) throws Exception
	{
		Vector columns      = editorMetaData.getColumnsMetaData();
		ColumnMetaData columnMetaData = null;
		ByteArrayOutputStream outStream = null;
		int size = (columns == null) ? 0 : columns.size();

		for (int index = 0; index < size; index++) {
			columnMetaData = (ColumnMetaData)columns.elementAt( index );
			if (columnMetaData.getType() == ColumnDataType._CDT_FILE) {

				try {
					Hashtable files = form.getMultipartRequestHandler().getFileElements();
					if (files == null) {
						return ;
					}

					Iterator iter = files.keySet().iterator();
					while (iter.hasNext()) {

						String fileFieldName = (String)iter.next();
//						System.out.println(fileFieldName);

						FormFile inputFile = (FormFile)files.get(fileFieldName);
						if (inputFile != null) {
							if (inputFile.getFileSize() <= 0 ) {
								throw new IOException("Please check the given file path is correct");
							}
							byte[] fileContents = null;
							InputStream inputStream = null;
							try {
								inputStream = inputFile.getInputStream();
								fileContents = new byte[inputFile.getFileSize()];
								inputStream.read(fileContents);
							}
							finally {
								inputStream.close();
							}
							inputFile.destroy();
							String colName = fileFieldName.substring (0 ,fileFieldName.lastIndexOf( "_edit") );
							ejbRS.updateObject( colName, fileContents );
						}
					}
				}
				catch( Exception e) {
						throw new IOException("Please check the given file path is correct");
				}
				finally {
					if (outStream != null) {
						outStream.close();
					}
				}
			}
		}
	}

	private void displayRequestParamter(HttpServletRequest request)
	{
		System.out.println( "displayRequestParamter " );
		java.util.Enumeration enumeration = request.getParameterNames();
		for (;enumeration.hasMoreElements();) {
			String key = (String) enumeration.nextElement();
			String value = request.getParameter( key );
			System.out.println( " key :" + key + "\t value :" +value);
		}
	}

}
