package com.addval.scripting;

import java.util.Collection;
import java.util.Stack;

public abstract class ALoggedAppData extends AppData{
	
    protected boolean _includeDebugInfo = false;

    protected String _lastStatus;
    
    protected String _lastStatusDescription;
    
    protected Stack _executionResults = new Stack();
    
    public void addExecResult(FnExecResultInfo result)
    {
        //ExecResultInfo result = new ExecResultInfo(funcName, res, statusCode, desc);
        _executionResults.push(result);
        return;
    }
    
    public FnExecResultInfo getLastExecResult()
    {
        return (FnExecResultInfo)_executionResults.peek();
    }
    
    public Collection getExecResults()
    {
        return _executionResults;
    }

    public boolean isIncludeDebugInfo() {
        return _includeDebugInfo;
    }

    public void setIncludeDebugInfo(boolean includeDebugInfo) {
        this._includeDebugInfo = includeDebugInfo;
    }

    public String getLastStatus() {
        return _lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this._lastStatus = lastStatus;
    }

    public String getLastStatusDescription() {
        return _lastStatusDescription;
    }

    public void setLastStatusDescription(String lastStatusDescription) {
        this._lastStatusDescription = lastStatusDescription;
    }
    
	
	
	

}
