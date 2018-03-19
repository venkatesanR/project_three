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
package com.addval.parser.xmlparser;

import java.text.ParseException;
import java.util.Map;

import org.apache.xmlbeans.XmlException;

public interface LoaderHelper {
    
    /**
     * This method checks for the loader type and then proceed to the corresponding feed method.
     * @param xml
     * @param loaderName
     * @param validationType
     * @return
     * @throws Exception 
     */
    public Map<?,?>[] getLoaderMap(String xml, String loaderName, String validationType) throws XmlException;
  
    /**
     * This method populates error, if there exists an SQL exception.
     * @param xmlObject
     * @param error
     * @param loaderName
     */
    public void populateError(Object xmlObject , String error, String loaderName);

    /**
     * This method initializes the SQL queries required for parent loader.
     * @param loaderGroupName
     */
    public void initializeParentSQLStmts(String loaderGroupName );
    
    /**
     * This method fetches the parent map object of the loader.
     * @param xmlObject
     * @param loaderGroupName
     * @return
     * @throws ParseException 
     */
    public Map<?,?> fetchParentLoaderMap(Object xmlObject, String loaderGroupName) throws ParseException;
    
    /**
     * This method initializes the SQL queries required for child loader.
     * @param loaderName
     */
    public void initializeChildSQLStmts(String loaderName);
    
    /**
     * This method process the child loader and loads the data to data base.
     * @param loaderName
     * @param xmlObject
     * @param object 
     * @throws ParseException 
     */
    public void childProcessMap(String loaderName, Object object, Map<?, ?> parentObj) throws ParseException;

    /**
     * This method prints the error info (if any) in a single feed after the loader process gets completed.
     * @param loaderName
     */
    public void populateErrorFeed(String loaderName);
}
