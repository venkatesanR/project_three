/*
 * ScriptInterpreter.java
 *
 * Created on December 14, 2006, 2:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.scripting;

import java.util.*;
import com.addval.utils.*;

/**
 *
 * @author ravi
 */
public final class ScriptInterpreter implements java.io.Serializable  
{      
    private FunctionClassFactory _funcClassFactory;
    private Set _loopVars;
    private ScriptParser.StepList _steps;
    private Map _funcMap;
    
    /** Creates a new instance of ScriptInterpreter */
    ScriptInterpreter(FunctionClassFactory factory, Set loopVars, Map funcMap, ScriptParser.StepList steps) {
        _funcClassFactory = factory;
        _loopVars = loopVars;
        _funcMap = funcMap;
        _steps = steps;        
    }
        
    public void execute(AppData data) throws ScriptException
    {   
        data.resetResumeMode();
        executeSteps(_steps, data);        
    } 

    /**
     * 
     * if resumeLabel is null/empty it is equivalent to calling execute
     * @param resumeLabel
     * @param data
     * @throws com.addval.scripting.ScriptException
     */
    public void executeResume(String resumeLabel, AppData data) throws ScriptException
    {   
        if(!StrUtl.isEmptyTrimmed(resumeLabel)){
            data.setResumeMode(true);
            data.setResumeLabel(resumeLabel);
        }
        else data.resetResumeMode();
        
        executeSteps(_steps, data);
        
        data.resetResumeMode();
    } 
        
    private boolean executeSteps(ScriptParser.StepList steps, AppData data) throws ScriptException
    {
        boolean ret = false;
        if(steps.isEmpty())return ret;
        
        for(Iterator iter = steps.iterator(); iter.hasNext();){
            ret = executeStep((ScriptParser.Step)iter.next(), data);
            if(ret || data.isEndScript() || data.isReturnFromLocalFunc())break;
        }        
        
        return ret;
    }
    
    private boolean executeStep(ScriptParser.Step step, AppData data) throws ScriptException
    {
        boolean ret = false;
        
        //while in resume mode do not execute steps until resume label is reached
        //only forward looking
        if(step.isLabel()){
            if(data.isResumeMode() && step.getLabelName().equals(data.getResumeLabel())){
                data.setResumeMode(false);
                data.setResumeLabel(null);
            }
        }        
        else if(data.isResumeMode()){ //no op
        }        
        else if(step.isBreak()){
            ret = true;
        }
        else if(step.isEnd()){
            data.setEndScript(true);
        }
        else if(step.isReturn()){
            data.setLocalResult(step.getReturnValue());
            data.setReturnFromLocalFunc(true);
        }
        else if(!step.isLoop()){
            ret = executeFunc(step.getFunc(), data);
        }
        else executeLoop(step.getLoop(), data);
        
        return ret;
    }
    
    private boolean executeLocalFunc(ScriptParser.Function funcDef, ScriptParser.FunctionCall func, AppData data) throws ScriptException
    {
        executeSteps(funcDef.getSteps(), data);
        boolean ret = false;
        String res = data.getLocalResult();
        data.setLocalResult(null);
        data.setReturnFromLocalFunc(false);        
        ret = executeResult(func.getResultMap(), res, data);
        return ret;
    }
    
    /**
     * return true if break
     */
    private boolean executeFunc(ScriptParser.FunctionCall func, AppData data) throws ScriptException
    {   
        //check for local function
        ScriptParser.Function funcDef = (ScriptParser.Function)_funcMap.get(func.getName());
        if(funcDef.isLocalFunction())return executeLocalFunc(funcDef, func, data);
        
        //get function and execute
        AFunctionImpl funcImpl = this._funcClassFactory.getFunctionImpl(func.getName());
        if(funcImpl == null)throw new ScriptException(func.getName() + " is not a declared function");

        boolean ret = false;
        //push args on to the arg stack
        data.pushArgs(func.getArgs());
        String res = funcImpl.execute(data);
        //pop args
        data.popArgs();
        
        ret = executeResult(func.getResultMap(), res, data);
        return ret;
    }
    
    private boolean executeResult(Map results, String res, AppData data) throws ScriptException
    {
        boolean ret = false;
        
        if(results == null || results.isEmpty())return ret;
        ScriptParser.Result result;

        for(Iterator iter = results.values().iterator(); iter.hasNext();){
            result = (ScriptParser.Result)iter.next();
            if(StrUtl.equals(res, result.getValue())){
                ret = executeSteps(result.getSteps(), data);                                    
                break; //break from result matching loop
            }
        }
        
        return ret;        
    }
    
    /**
     * A loop can be with or without a loop var. If with loop var
     * an iterator is expected.
     */
    private void executeLoop(ScriptParser.Loop loop, AppData data) throws ScriptException
    {
        boolean hasLoopVar = !StrUtl.isEmpty(loop.getLoopVar());
        if(hasLoopVar && !_loopVars.contains(loop.getLoopVar())){
            throw new ScriptException("Undeclared Loop Variable: " + loop.getLoopVar());
        }
        
        boolean ret;
        
        data.incLoopCount();
        
        if(!hasLoopVar){            
            while(true){
                ret = executeSteps(loop.getSteps(), data);
                if(ret || data.isEndScript() || data.isReturnFromLocalFunc())break; //break from the loop
            }
        }
        else{                
            for(Iterator iter = data.getLoopVarIterator(loop.getLoopVar()); iter.hasNext();){
                data.pushLoopVar(loop.getLoopVar(), iter.next());
                ret = executeSteps(loop.getSteps(), data);
                data.popLoopVar();
                if(ret || data.isEndScript() || data.isReturnFromLocalFunc())break; //break from the loop
            }    
        }
        
        data.decLoopCount();
    }            
}
