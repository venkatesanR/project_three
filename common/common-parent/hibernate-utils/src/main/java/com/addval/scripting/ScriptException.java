/*
 * ScriptException.java
 *
 * Created on December 1, 2006, 11:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.scripting;

/**
 *
 * @author ravi
 */
public class ScriptException extends Exception
{
    
    /** Creates a new instance of ScriptException */
    public ScriptException() 
    {
        super();
    }
    
    /** Creates a new instance of ScriptException */
    public ScriptException(String msg) 
    {
        super(msg);
    }    
        
    /** Creates a new instance of ScriptException */
    public ScriptException(String msg, Exception e) 
    {
        super(msg, e);
    }    
}
