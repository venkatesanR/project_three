/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.addval.utils.trees;

import com.addval.utils.trees.xbeans.*;
import com.addval.utils.StrUtl;
import java.util.List;
import java.util.Collections;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 *
 * @author ravi
 */
public class OkudTreeUtilities implements ApplicationContextAware
{
    private ApplicationContext _appContext;
    
    public OkudTreeUtilities(){
        
    }
    
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        _appContext = ac;
    }

    public Object createBean(BeanDefinitionType beanDef) throws Exception
    {
        Object obj = null;

        if(beanDef.isSetSpringBeanName() && !StrUtl.isEmpty(beanDef.getSpringBeanName())){
            if(_appContext == null)throw new Exception("No spring application context defined, did you define OkudTreeUtilities bean in your spring config file?");
            obj =  _appContext.getBean(beanDef.getSpringBeanName()); 
        }
        else{
            //construct arg array
            Object[] constructorArgs = null;
            Class[] argTypes = null;

            if(beanDef.getClassDefinition().getNumberOfConstructorArgs() > 0){
                constructorArgs = new Object[beanDef.getClassDefinition().getNumberOfConstructorArgs()];
                argTypes = new Class[constructorArgs.length];
                //here the arguments should be all of type string

                if(beanDef.getClassDefinition().getConstructorArgumentArray() == null ||
                        beanDef.getClassDefinition().getNumberOfConstructorArgs() != beanDef.getClassDefinition().getConstructorArgumentArray().length){
                    throw new Exception("Class definition - constructor arguments count must match");
                }

                for(int i = 0; i < beanDef.getClassDefinition().getConstructorArgumentArray().length; i++){
                    if(beanDef.getClassDefinition().getConstructorArgumentArray(i).isSetNull()){
                        //null string
                        constructorArgs[i] = null;
                    }
                    else{
                        constructorArgs[i] = beanDef.getClassDefinition().getConstructorArgumentArray(i).getValue().getStringValue();
                    }
                }
            }

            obj = createClassInstance(beanDef.getClassDefinition(), constructorArgs);
        }

        return obj;
    }
    
    /**
     * Here the arguments types are assumed to be of String type
     * 
     * @param classDef
     * @param constructorArgs
     * @return
     * @throws Exception 
     */
    public static Object createClassInstance(ClassDefinitionType classDef, Object[] constructorArgs) throws Exception    
    {        
        if(constructorArgs == null || constructorArgs.length == 0){
            return  createClassInstance(classDef, constructorArgs, null);
        }
        else{
            //construct arg classes array
            Class[] argTypes = new Class[classDef.getNumberOfConstructorArgs()];
            for(int i = 0; i < constructorArgs.length; i++){
                argTypes[i] = String.class;
            }  
            
            return  createClassInstance(classDef, constructorArgs, argTypes);
        }
    }

    /**
     * Instantiate a class based on the string args for the constructor
     * 
     * @param classDef
     * @param constructorArgs
     * @return
     * @throws java.lang.Exception
     */
    public static Object createClassInstance(ClassDefinitionType classDef, Object[] constructorArgs, Class[] argTypes) throws Exception
    {
        if((classDef.getNumberOfConstructorArgs() == 0 && constructorArgs != null && constructorArgs.length > 0) ||
                (classDef.getNumberOfConstructorArgs() > 0 &&
                    (constructorArgs == null || constructorArgs.length != classDef.getNumberOfConstructorArgs()))){
            throw new Exception("Class definition - constructor arguments count must match");
        }
                    
        Object obj = null;

        //create instance of the class
        if(classDef.getNumberOfConstructorArgs() <= 0){
            obj = Class.forName(classDef.getClassName()).newInstance();
        }
        else{
            java.lang.reflect.Constructor ctr = Class.forName(classDef.getClassName()).getConstructor(argTypes);
            obj = ctr.newInstance(constructorArgs);
        }

        return obj;
    }

    /**
     * Sorts the leaf node list in the descending order of the score using the default Comparable
     * interface of AOkudTreeNode
     * @param leafNodes list
     * @return sorted leafNodes
     */
    public static List<OkudTreeLeafNode> sortByPreference(List<OkudTreeLeafNode> leafNodes){
        Collections.sort(leafNodes);
        return leafNodes;
    }
}
