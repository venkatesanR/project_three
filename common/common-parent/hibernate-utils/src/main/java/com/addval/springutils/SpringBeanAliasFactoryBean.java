/*
 * Copyright (c) 2001-2006 Accenture LLC.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Accenture. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Accenture.
 *
 * ACCENTURE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ACCENTURE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.addval.springutils;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;



public class SpringBeanAliasFactoryBean implements FactoryBean, BeanNameAware, BeanFactoryAware {
    private BeanFactory beanFactory;
    private String target;
    private Object targetObject;
    private String beanName;

    public Object getObject() throws Exception {
    	/*
        if(targetObject == null) {
            throw new org.springframework.beans.factory.BeanCreationException("Cannot create bean '" + getBeanName() + "'. The specified target bean '" + getTarget() + "' not found in the current bean factory");
        }
		*/
    	if(targetObject == null){
    		System.out.println("\tWARN :" +"Cannot create bean '" + getBeanName() + "'. The specified target bean '" + getTarget() + "' not found in the current bean factory");
    	}
        return targetObject;
    }

    @SuppressWarnings("rawtypes")
    public Class getObjectType() {
        return (targetObject != null) ? targetObject.getClass() : null;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
    	this.beanFactory = beanFactory;
        if(this.beanFactory != null) {
        	this.targetObject = this.beanFactory.containsBean(getTarget()) ? this.beanFactory.getBean(getTarget()) :null;
        }
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }
}
