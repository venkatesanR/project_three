/*
 * AppData.java
 *
 * Created on December 1, 2006, 10:20 AM
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
public abstract class AppData 
{
    /**
     * When a loop holds the current object returned by the iterator.
     * The object can be retrieved by a function being executed in the loop.
     * The object will be popped at the end of each iteration. The number of
     * inner loops dictates the length of the stack.  
     * Holds StackData(loopVar, loopObj)
     */
    private Stack _loopObjects;    
    
    /**
     * the interpreter sets this flag when the execution is in a loop
     */
    private int _loopCount = 0;
    
    private boolean _endScript = false;
    
    /**
     * result from local script function
     */
    private String _localResult;
    
    /**
     * flag indicating intermediate return from a localfunc
     */
    private boolean _returnFromLocalFunc = false;
    
    /**
     * Args stack for func call
     */
    private Stack _callArgStack = new Stack();
    
    /**
     * Resume mode of execution
     */
    private boolean _resumeMode = false;
    
    /**
     * Resume label when resumeMode = true
     */
    private String _resumeLabel;
    
    /** Creates a new instance of AppData */
    public AppData() 
    {
    }
    
    /**
     * Return the object from the top of the loop stack. If none returns null.
     */
    public final Object getCurrentLoopObject()
    {
        if(_loopObjects == null || _loopObjects.empty())return null;
        return ((StackData)_loopObjects.peek()).getLoopObj();
    }   
    
    /**
     * Returns the loop object at requested depth. For example,
     * if for the same loop var when there is a loop within a loop
     * if the asked loopLevel is 0, the inner loop obj will be returned and
     * if the asked loopLevel is 1, the outer loop obj will be returned.
     * Returns null if not found 
     */
    public final Object getLoopObject(String loopVar, int loopLevel)
    {
        if(_loopObjects == null || _loopObjects.empty())return null;
        StackData data;
        int currLoopLevel = -1;
        
        for(int i = _loopObjects.size() - 1; i >= 0; i--){
            data = (StackData)_loopObjects.get(i);
            if(loopVar.equals(data.getLoopVar()))currLoopLevel++;
            if(loopLevel == currLoopLevel)return data.getLoopObj();
        }
        
        return null;
    }
    
    /**
     * if in a loop with loop var, the stack size at least 1
     */
    public final boolean inLoop()
    {
        return _loopCount > 0;
    }
    
    final void pushArgs(List args){
        _callArgStack.push(args);
    }
    
    final void popArgs(){
        _callArgStack.pop();
    }
    
    /**
     * Returns current function call arguments
     */
    public final List getArgs()
    {
        return (List)_callArgStack.peek();
    }
    
    final void incLoopCount(){
        _loopCount++;
    }
    
    final void decLoopCount(){
        _loopCount--;
    }   
    
    final void pushLoopVar(String loopVar, Object loopObj)
    {
        if(_loopObjects == null)_loopObjects = new Stack();
        _loopObjects.push(new StackData(_loopObjects.size(), loopVar, loopObj));
    } 
    
    final void popLoopVar()
    {
        if(_loopObjects != null && !_loopObjects.empty())_loopObjects.pop();
    }
    
    final boolean isEndScript() {
        return _endScript;
    }

    final void setEndScript(boolean endScript) {
        this._endScript = endScript;
    }  
    
    final String getLocalResult() {
        return _localResult;
    }

    final void setLocalResult(String localResult) {
        this._localResult = localResult;
    }   
    
    final boolean isReturnFromLocalFunc() {
        return _returnFromLocalFunc;
    }

    final void setReturnFromLocalFunc(boolean returnFromLocalFunc) {
        this._returnFromLocalFunc = returnFromLocalFunc;
    }    
    
    /** 
     * Implement this method to return a valid iterator for the loop variable. 
     * Can return an empty iterator but not null
     */
    public abstract Iterator getLoopVarIterator(String loopVar);

    final boolean isResumeMode() {
        return _resumeMode;
    }

    final void setResumeMode(boolean resumeMode) {
        this._resumeMode = resumeMode;
    }


    final String getResumeLabel() {
        return _resumeLabel;
    }

    final void setResumeLabel(String resumeLabel) {
        this._resumeLabel = resumeLabel;
    }
    
    final void resetResumeMode()
    {
        setResumeMode(false);
        setResumeLabel(null);
    }
    
    static final class StackData{
        private int _stackIdx;
        private String _loopVar;
        private Object _loopObj;
        
        StackData(int stackIdx, String loopVar, Object loopObj){
            _stackIdx = stackIdx;
            _loopVar = loopVar;
            _loopObj = loopObj;
        }
        
        public int getStackIdx(){
            return _stackIdx;
        }
        
        public String getLoopVar(){
            return _loopVar;
        }
        
        public Object getLoopObj(){
            return _loopObj;
        }        
    }
}
