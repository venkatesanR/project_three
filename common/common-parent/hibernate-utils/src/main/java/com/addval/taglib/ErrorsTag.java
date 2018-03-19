package com.addval.taglib;

import java.util.Iterator;
import java.util.ArrayList;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.ResponseUtils;
import com.addval.metadata.EditorMetaData;

/**
 * Custom tag that renders error messages if an appropriate request attribute
 * has been created.  The tag looks for a request attribute with a reserved
 * key, and assumes that it is either a String, a String array, containing
 * message keys to be looked up in the application's MessageResources, or
 * an object of type <code>org.apache.struts.action.ActionErrors</code>.
 * <p>
 * The following optional message keys will be utilized if corresponding
 * messages exist for them in the application resources:
 * <ul>
 * <li><b>errors.header</b> - If present, the corresponding message will be
 *     rendered prior to the individual list of error messages.
 * <li><b>errors.footer</b> - If present, the corresponding message will be
 *     rendered following the individual list of error messages.
 * <li><b>
 * </ul>
 *
 */

public class ErrorsTag extends org.apache.struts.taglib.html.ErrorsTag {

    protected ArrayList propertyNames = null;
    protected String sectionName = null;
    protected int errorIndex= 0;
    protected String indexedListProperty = null;

    public int getErrorIndex() {
        return errorIndex;
    }

    public void setErrorIndex(int errorIndex) {
        this.errorIndex = errorIndex;
    }

    public String getIndexedListProperty() {
        return indexedListProperty;
    }

    public void setIndexedListProperty(String indexedListProperty) {
        this.indexedListProperty = indexedListProperty;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public void setPropertyNames(Object propertyNamesObj) throws JspTagException {
        if ( propertyNamesObj !=null && propertyNamesObj instanceof ArrayList ){
            propertyNames = (ArrayList) propertyNamesObj;
        }
        else {
            throw new IllegalArgumentException("ErrorsTag: Invalid PropertyNames.");
        }
    }

    public int doStartTag() throws JspException {
        // Were any error messages specified?
        ActionErrors errors = new ActionErrors();
        try {
            Object value = pageContext.getAttribute(name, PageContext.REQUEST_SCOPE);
            if (value == null) {
                ;
            }
            else if (value instanceof String) {
                errors.add(ActionErrors.GLOBAL_ERROR,new ActionError((String) value));
            }
            else if (value instanceof String[]) {
                String keys[] = (String[]) value;
                    for (int i = 0; i < keys.length; i++)
                        errors.add(ActionErrors.GLOBAL_ERROR,
                                   new ActionError(keys[i]));
            }
            else if (value instanceof ActionErrors) {
                errors = (ActionErrors) value;
            }
            else {
                JspException e = new JspException(messages.getMessage("errorsTag.errors",value.getClass().getName()));
                RequestUtils.saveException(pageContext, e);
                throw e;
            }
        }
        catch (Exception e) {
                ;
        }
        if (errors.empty()) {
            return (EVAL_BODY_INCLUDE);
        }
        // Check for presence of header and footer message keys
        boolean headerPresent = RequestUtils.present(pageContext, bundle, locale, "errors.header");
        boolean footerPresent = RequestUtils.present(pageContext, bundle, locale, "errors.footer");
        // Render the error messages appropriately
        /*
        for (Iterator iterator = errors.properties(); iterator.hasNext();) {
            System.out.println("Properties\t" + iterator.next() );
        }
        */
        Iterator reports = null;
        if (propertyNames == null && property == null) {
            reports = errors.get();
        }
        else {
            ArrayList errorReports = new ArrayList();
            if(propertyNames != null) {
                String fieldPropertyName = null;
                for (int i=0; i< propertyNames.size(); i++) {
                    fieldPropertyName = (String) propertyNames.get(i);
                    if(indexedListProperty != null){
                        ArrayList indexedPropertyNames = getIndexedPropertyNames(errors,indexedListProperty,fieldPropertyName);
                        for (int j=0; j< indexedPropertyNames.size(); j++) {
                            fieldPropertyName = (String) indexedPropertyNames.get(j);
                            reports = errors.get( fieldPropertyName );
                            while (reports.hasNext()) {
                                errorReports.add( reports.next() );
                            }
                        }
                        if(indexedPropertyNames.size() == 0){
                            reports = errors.get( fieldPropertyName );
                            while (reports.hasNext()) {
                                errorReports.add( reports.next() );
                            }
                        }
                    }
                    else {
                        reports = errors.get( fieldPropertyName );
                        while (reports.hasNext()) {
                            errorReports.add( reports.next() );
                        }
                    }
                }
            }
            if(property != null) {
                reports = errors.get( property );
                while (reports.hasNext()) {
                    errorReports.add( reports.next() );
                }
            }
            reports = errorReports.iterator();
        }

        StringBuffer results = new StringBuffer();
        String message = null;
        if (headerPresent) {
            message = RequestUtils.message(pageContext, bundle,locale, "errors.header");
        }
        // Render header iff this is a global tag or there is an error for this property
        boolean propertyMsgPresent = reports.hasNext();
        if ((message != null)&&(propertyNames == null && property == null) || propertyMsgPresent) {
            results.append(message);
            results.append("<SCRIPT>document.write(err_genst("+ errorIndex +"));</SCRIPT>");
            results.append("<TABLE style='Z-INDEX: 1; POSITION: relative' cellSpacing=0 cellPadding=0  border=0>");
            results.append("<TR>");
            results.append("<TD><IMG src='../images/spaceit.gif' width=8></TD>");
            results.append("<TD>&nbsp;</TD>");
            results.append("<TD align=left width='100%'>");

        }
        if(propertyMsgPresent && sectionName != null){
            results.append("<TABLE><TR><TD valign='top'>").append( sectionName ).append( "</TD><TD>" );
        }
        while (reports.hasNext()) {
            ActionError report = (ActionError) reports.next();
            message = RequestUtils.message(pageContext, bundle,locale, report.getKey(),report.getValues());
            if (message != null) {
                results.append("<LI class='errorTextStd'>");
                results.append(message);
                results.append("</LI>");
            }
        }

        if(propertyMsgPresent && sectionName != null){
            results.append("</TD></TR></TABLE>");
        }

        message = null;
        if (footerPresent) {
            message = RequestUtils.message(pageContext, bundle,locale, "errors.footer");
        }
        if ((message != null)&&(propertyNames == null  && property == null) || propertyMsgPresent) {
            results.append("</TD>");
            results.append("<TD><IMG src='../images/spaceit.gif' width=8></TD>");
            results.append("</TR>");
            results.append("</TABLE>");
            results.append("<SCRIPT>document.write(err_genend("+ errorIndex +"));</SCRIPT>");
            results.append(message);
        }
        // Print the results to our output writer
        ResponseUtils.write(pageContext, results.toString());
        // Continue processing this page
        return (EVAL_BODY_INCLUDE);
    }

    public void release() {
        super.release();
        propertyNames = null;
        sectionName = null;
    }

    private ArrayList getIndexedPropertyNames(ActionErrors errors,String indexedListProperty,String fieldPropertyName){
        ArrayList indexedPropertyNames = new ArrayList();
        String propertyName = null;
        for (Iterator iterator = errors.properties(); iterator.hasNext();) {
            propertyName = (String)iterator.next();
            if(propertyName.startsWith(indexedListProperty +"[") && propertyName.endsWith("]."+ fieldPropertyName) ){
                indexedPropertyNames.add( propertyName );
            }
        }
        return indexedPropertyNames;
    }
}
