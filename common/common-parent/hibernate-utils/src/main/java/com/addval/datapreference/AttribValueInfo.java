/*
 * AttribValueInfo.java
 *
 * Created on January 8, 2008, 1:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.datapreference;

/**
 * This class should be used construct the value map for dim key
 * to compute preference/score.
 *
 * @author ravi
 */
public class AttribValueInfo implements java.io.Serializable
{
    protected Object _value;
    protected String _attribCode;
    
    /** Creates a new instance of AttribValueInfo */
    public AttribValueInfo() 
    {
    }
    
    /** Creates a new instance of AttribValueInfo */
    public AttribValueInfo(Object value, String attribCode) 
    {
        setValue(value);
        setAttribCode(attribCode);
    }    

    public Object getValue() {
        return _value;
    }

    public void setValue(Object value) {
        this._value = value;
    }

    public String getAttribCode() {
        return _attribCode;
    }

    public void setAttribCode(String attribCode) {
        this._attribCode = attribCode;
    }
    
    public String toString()
    {
        return "AttribValueInfo[" + _value + ", " + _attribCode + "]";
    }
    
}
