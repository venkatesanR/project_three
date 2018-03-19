package com.addval.struts;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: thiyagu
 * Date: Nov 15, 2006
 * Time: 3:23:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkQueueMonitorForm extends ActionForm {

    private Vector wqMetaDatas;

     public WorkQueueMonitorForm()
     {
         wqMetaDatas = null;
     }

     public Vector getWqMetaDatas()
     {
         return wqMetaDatas;
     }

     public void setWqMetaDatas(Vector wqMetaDatas)
     {
         this.wqMetaDatas = wqMetaDatas;
     }

     public void reset(ActionMapping actionmapping, HttpServletRequest httpservletrequest)
     {
     }
 }
