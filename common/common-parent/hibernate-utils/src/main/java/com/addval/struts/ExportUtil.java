
package com.addval.struts;

import com.addval.metadata.UserCriteria;
import com.addval.metadata.UserCriteriaMgr;
import java.util.Hashtable;
import java.sql.ResultSet;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.metadata.ColumnMetaData;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.struts.util.LabelValueBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionMapping;
import com.addval.metadata.ColumnDataType;
import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.server.EJBSTableManagerHome;
import com.addval.environment.Environment;
import com.addval.environment.EJBEnvironment;
import com.addval.ejbutils.server.EJBSTableManagerRemote;
import com.addval.ejbutils.server.EJBSEditorMetaDataHome;
import com.addval.ejbutils.server.EJBSEditorMetaDataRemote;
import com.addval.utils.AVConstants;
import com.addval.metadata.EditorMetaData;
import javax.servlet.http.HttpSession;
import com.addval.utils.XRuntime;
import com.addval.utils.StrUtl;
import com.addval.utils.Pair;
import com.addval.jasperutils.EditorMetaDataReportDesigner;
import com.addval.jasperutils.EJBResultSetJasperDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import java.rmi.RemoteException;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.naming.NamingException;
import javax.ejb.CreateException;

import net.sf.jasperreports.engine.util.JRImageLoader;
import net.sf.jasperreports.engine.JRImageRenderer;

public class ExportUtil {
	private String _subsystem = null;

	// The export feature is restricted to 50,000 rows. If more rows are needed
	// to be exported use the schedule export feature
	private static final int _EXPORT_MAX_RECORDS = 50000;

    public ExportUtil(String subsystem) {
		_subsystem = subsystem;
	}

    public String getSubsystem() {
		return _subsystem;
	}

	public void setSubsystem(String aSubsystem) {
		_subsystem = aSubsystem;
	}

    public void setDefaultValues(ExportForm form,HttpServletRequest request){
        form.setReportTitle( form.getMetadata().getDesc() );
        form.setExportType( String.valueOf( EditorMetaDataReportDesigner.CSV_FORMAT ) );
        form.setPageOrientation( "PORTRAIT" );
    }

    public ActionErrors export(ActionMapping mapping,ExportForm form,HttpServletRequest request,HttpServletResponse response) throws XRuntime,Exception {
        ActionErrors    errors          = new ActionErrors();
        EJBCriteria     ejbCriteria     = null;
        FormInterceptor formInterceptor = null;
        EJBResultSet    ejbResultSet    = null;
        String          userName        = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "";

        request.setAttribute("exactSearch",new Boolean(form.isExactSearch()));
        ejbCriteria = EjbUtils.getEJBCriteria( form.getMetadata() , request, true );
        ejbCriteria.setPageSize( _EXPORT_MAX_RECORDS );
        ejbCriteria.setPageAction( AVConstants._FETCH_FIRST );
        if(!StrUtl.isEmptyTrimmed( form.getFormInterceptorType() ) ){
            formInterceptor = FormInterceptorFactory.getInstance( form.getFormInterceptorType() );
        }

        if (formInterceptor != null){
            formInterceptor.beforeDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, request, response, ejbCriteria);
        }

        if( form.isUserCriteria() ) {

            HttpSession session = request.getSession( false );
            UserCriteria criteria = (UserCriteria) session.getAttribute(userName + "_" + form.getMetadata().getName() + "_USER_CRITERIA");
            if ( criteria != null ){
                UserCriteriaUtil utils = new UserCriteriaUtil( form.getSubSystem() );

                Pair pair = utils.getEditorMetaDataSQLPair(form.getMetadata(),criteria,request,form.isAdditionalFilter());
                form.setMetadata( ( EditorMetaData ) pair.getFirst() );
                EJBCriteria ejbCrit = (new EJBResultSetMetaData( form.getMetadata() ) ).getNewCriteria();
                ejbCrit.setPageSize( _EXPORT_MAX_RECORDS );
                ejbCrit.setPageAction( AVConstants._FETCH_FIRST );
                ejbResultSet = getEJBSTableManagerRemote().lookup( (String)pair.getSecond() , form.getMetadata(), ejbCrit );
            }
            else {
                EJBCriteria ejbCrit = (EJBCriteria) session.getAttribute(userName + "_" + form.getMetadata().getName() + "_EJB_CRITERIA");
                ejbCrit.setPageSize( _EXPORT_MAX_RECORDS );
                ejbCrit.setPageAction( AVConstants._FETCH_FIRST );
                ejbResultSet = getEJBSTableManagerRemote().lookup( ejbCrit );
            }

        }
        else{
            ejbResultSet = getEJBSTableManagerRemote().lookup( ejbCriteria );
        }
        if(ejbResultSet == null || ejbResultSet.getRecords().size() == 0){
            errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "No Records found!" ) );
            return errors;
        }
        boolean forLandscape = form.getPageOrientation().equalsIgnoreCase( "LANDSCAPE" );
        EditorMetaDataReportDesigner designer = new EditorMetaDataReportDesigner(form.getMetadata(),forLandscape,Integer.parseInt( form.getExportType() ));

        form.setJasperReport( designer.getJasperReport() );
        form.setJasperDataSource( new EJBResultSetJasperDataSource( ejbResultSet ) );

        if (formInterceptor != null){
            formInterceptor.afterDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, request, response, ejbCriteria);
        }

        //URL imageUrl = new URL("http://cuckoo:9001/cargores/images/customer_logo.gif");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(AVConstants._GMT_TIMEZONE);

        SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yy HHmm" );
        sdf.setTimeZone(AVConstants._GMT_TIMEZONE);
        String dateTime =sdf.format( calendar.getTime() );

        Map parameters = new HashMap();
        parameters.put( "ReportTitle", form.getReportTitle() );
        parameters.put( "User", userName );
        parameters.put( "DateTime", dateTime );

        form.setParameters( parameters );

        return errors;
	}

    public EJBSEditorMetaDataRemote getEJBSEditorMetaDataRemote() throws ClassNotFoundException,NamingException,CreateException,RemoteException {
        String 						homeName 		= Environment.getInstance(getSubsystem()).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSEditorMetaDataBeanName", "EJBSTableManager");
        EJBSEditorMetaDataHome   	metaDataHome	= (EJBSEditorMetaDataHome) EJBEnvironment.lookupEJBInterface( getSubsystem(), homeName , EJBSEditorMetaDataHome.class );
        EJBSEditorMetaDataRemote 	metaDataRemote	= metaDataHome.create( );
        return metaDataRemote;
    }

    private EJBSTableManagerRemote getEJBSTableManagerRemote() throws ClassNotFoundException,NamingException,CreateException,RemoteException {
        String                      homeName            = Environment.getInstance(getSubsystem()).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSTableManagerBeanName", "EJBSTableManager");
        EJBSTableManagerHome        tableManagerHome    = ( EJBSTableManagerHome ) EJBEnvironment.lookupEJBInterface( getSubsystem(), homeName , EJBSTableManagerHome.class );
        EJBSTableManagerRemote      tableManagerRemote  = tableManagerHome.create();
        return tableManagerRemote;
    }


}
