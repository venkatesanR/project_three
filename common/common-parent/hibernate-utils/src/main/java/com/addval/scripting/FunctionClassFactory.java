/*
 * FunctionClassFactory.java
 *
 * Created on December 14, 2006, 3:35 PM
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
public final class FunctionClassFactory 
{        
    /**
     * key - Integer func id
     * value - implementing AFunctionImpl object
     */
    private Map _funcObjectMap = new HashMap();

    /** Creates a new instance of FunctionClassFactory */
    FunctionClassFactory(Map funcMap)
    {
        _funcObjectMap = funcMap;
    }

    AFunctionImpl getFunctionImpl(String funcName)
    {
        AFunctionImpl funcImpl = (AFunctionImpl)_funcObjectMap.get(funcName);

        if(!funcImpl.getFuncInfo().isReuse()){
            AFunctionImpl newFuncImpl = funcImpl.newInstance();
            newFuncImpl.setFuncInfo(funcImpl.getFuncInfo());
            funcImpl = newFuncImpl;
        }

        return funcImpl;
    }    
    
}
