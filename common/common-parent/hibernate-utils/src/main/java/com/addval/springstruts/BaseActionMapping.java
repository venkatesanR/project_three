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

package com.addval.springstruts;

import org.apache.struts.action.ActionMapping;


/**
 * Base Action Mapping Class - For all action mappings. Derive from this instead
 * of ActionMapping class. This class provides flexibility to set the subsystem
 * property with each action tag in the struts-config.xml using the set-property
 * tag.
 * There are two methods to configuring this. In method 1, the BaseActionMapping
 * is configured in the action-mappings tag. This means that this will be the
 * default ActionMapping instance passed to all the actions within this tag unless
 * it is overriden as shown in method 2.
 * METHOD 1:
 * <action-mappings type="com.addval.struts.BaseActionMapping">
 * <action path="/lookDownXml" type="com.addval.struts.LookupXmlAction"
 * name="exactLookupForm" scope="request">
 * <set-property property="subsystem" value="Rules" />
 * </action>
 * </action-mapping>
 * </action-mappings>
 * METHOD 2:
 * <action-mappings>
 * <action path="/lookDownXml"
 * className="com.addval.struts.BaseActionMapping"
 * type="com.addval.struts.LookupXmlAction" name="exactLookupForm" scope="request">
 * <set-property property="subsystem" value="Rules" />
 * </action>
 * </action-mapping>
 * </action-mappings>
 * @author AddVal Technology Inc.
 */
public class BaseActionMapping extends ActionMapping
{
    private static final long serialVersionUID = -4585643790982819811L;
    protected String _subsystem;
    protected String _cacheToRefresh;

    /**
         * set the subsystem associated with this action mapping
         * @param aSubsystem
         * @roseuid 3DCC765F01BB
         */
    public void setSubsystem(String aSubsystem)
    {
        _subsystem = aSubsystem;
    }

    /**
     * get the subsystem associated with this action mapping
     * @return String
     * @roseuid 3DCC76800050
     */
    public String getSubsystem()
    {
        return this._subsystem;
    }

    /**
     * Access method for the _cacheToRefresh property.
     *
     * @return   the current value of the _cacheToRefresh property
     */
    public String getCacheToRefresh()
    {
        return _cacheToRefresh;
    }

    /**
     * Sets the value of the _cacheToRefresh property.
     *
     * @param aCacheToRefresh the new value of the _cacheToRefresh property
     */
    public void setCacheToRefresh(String aCacheToRefresh)
    {
        _cacheToRefresh = aCacheToRefresh;
    }
}
