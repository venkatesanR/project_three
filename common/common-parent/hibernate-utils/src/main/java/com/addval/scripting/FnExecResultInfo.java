/*
 * FnExecResultInfo.java
 *
 * Created on February 26, 2007, 3:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.scripting;

import java.util.*;

/**
 *
 * @author ravi
 */
public class FnExecResultInfo implements java.io.Serializable
{        
    protected String _statusCode;
    protected String _funcName;
    protected String _result;
    protected String _description;
    protected List _debugTrace;
    protected long _execTime; //func exec time in ms
    protected static String _lineSep = System.getProperty("line.separator");

    /** Creates a new instance of FnExecResultInfo */
    public FnExecResultInfo()
    {
    }
    
    /** Creates a new instance of FnExecResultInfo */
    public FnExecResultInfo(String funcName)
    {
        _funcName = funcName;
    }    
    
    /** Creates a new instance of FnExecResultInfo */
    public FnExecResultInfo(String funcName, String res, String statusCode, String desc)
    {
        _funcName = funcName;
        _result = res;
        _statusCode = statusCode;
        _description = desc;
    }

    public String toString(){
        StringBuffer sb = new StringBuffer(256);
        sb.append(_lineSep);
        sb.append(_funcName).append(":").append(_result);
        sb.append("[").append("Exec time:").append(_execTime).append(" ms]");
        if(_statusCode != null)sb.append(", status:").append(_statusCode);    
        if(_description != null)sb.append(", dscr:").append(_description);
        if(_debugTrace != null){
            sb.append(", trace:").append(_lineSep);
            for(Iterator iter = _debugTrace.iterator(); iter.hasNext();){
                sb.append(iter.next()).append(_lineSep);
            }
        }

        return sb.toString();
    }

    public String getStatusCode() {
        return _statusCode;
    }

    public void setStatusCode(String statusCode) {
        this._statusCode = statusCode;
    }

    public String getFuncName() {
        return _funcName;
    }

    public void setFuncName(String funcName) {
        this._funcName = funcName;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        this._description = description;
    }    

    public String getResult() {
        return _result;
    }

    public void setResult(String result) {
        this._result = result;
    }
  
    public void addDebugTrace(Object obj){
        if(_debugTrace == null)_debugTrace = new ArrayList();
        _debugTrace.add(obj);
    }
    
    public List getDebugTrace()
    {
        return _debugTrace;
    }

    public long getExecTime() {
        return _execTime;
    }

    public void setExecTime(long execTime) {
        this._execTime = execTime;
    }
}
