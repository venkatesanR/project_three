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

package com.addval.utils;

public interface CacheInitializer
{
    public void setObjectName(String aName);

    public String getObjectName();

    public void setObjectType(String aType);

    public String getObjectType();

    public Object populateData(CacheMgr cache, String objectName, boolean refreshFlag) throws CacheException;

    public Object populateData(CacheMgr cache, CacheRefreshDetail cacheRefreshDetail) throws CacheException;

    public Boolean isLazyCacheLoad();

    public void setLazyCacheLoad(Boolean lazyCacheLoad);
    
    public boolean isUseExternalCache();
    
    public boolean isAllowGetAllFromExternalCache();
    
}
