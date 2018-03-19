/*
 * AFunctionImpl.java
 *
 * Created on December 14, 2006, 2:58 PM
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
public abstract class AFunctionImpl {
        
    protected ScriptParser.Function _funcInfo;
                       
    /** Creates a new instance of AFunctionImpl */    
    public AFunctionImpl(ScriptParser.Function funcInfo) {
        _funcInfo = funcInfo;
    }
    
    /** Creates a new instance of AFunctionImpl */    
    public AFunctionImpl(AFunctionImpl otherFunc) {
        _funcInfo = otherFunc._funcInfo; 
    }
        
    /**
     * wrapper execute method that calls executeImpl
     */
    public final String execute(AppData appdata)
    {
        String retStr = this.executeImpl(appdata);                        
        return retStr;
    }
    
    ScriptParser.Function getFuncInfo() {
        return _funcInfo;
    }
    
    void setFuncInfo(ScriptParser.Function funcInfo) {
        _funcInfo = funcInfo;
    }
    
    public String getFuncName() {
        return getFuncInfo().getName();
    }
    
    public boolean isReuseInstance()
    {
        return _funcInfo.isReuse();
    }
    
    /**
     * Returns array of value strings for the param if found else
     * returns null
     */
    public Set getFuncConfigParamValues(String paramName)
    {
        Map paramMap = _funcInfo.getCfgParamMap();
        if(paramMap == null)return null;
        return (Set)paramMap.get(paramName);
    }
            
    /**
     * Actual execution method to be implemented by extending class
     */
    public abstract String executeImpl(AppData appdata);
    

    /**
     * Should return a new instance of the implemented func
     */
    public abstract AFunctionImpl newInstance();    
        
}
