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

import java.io.Serializable;


public class CacheRefreshDetail implements Serializable
{
    private static final transient long serialVersionUID = 136285878625601349L;
    public static final transient int _ADD = 0;
    public static final transient int _UPDATE = 1;
    public static final transient int _REMOVE = 2;
    private String cacheName = null;
    private String objectName = null;
    private boolean isIncremental = true;
    private int action = -1;
    private Object key = null;
    private Object[] keys = null;

    public CacheRefreshDetail(String cacheName, String objectName)
    {
        setCacheName(cacheName);
        setObjectName(objectName);
    }

    public String getCacheName()
    {
        return cacheName;
    }

    public void setCacheName(String cacheName)
    {
        this.cacheName = cacheName;
    }

    public String getObjectName()
    {
        return objectName;
    }

    public void setObjectName(String objectName)
    {
        this.objectName = objectName;
    }

    public boolean isIncremental()
    {
        return isIncremental;
    }

    public void setIncremental(boolean isIncremental)
    {
        this.isIncremental = isIncremental;
    }

    public int getAction()
    {
        return action;
    }

    public void setAction(int action)
    {
        if(action != _ADD && action != _REMOVE && action != _UPDATE)
            throw new UnsupportedOperationException(String.format("Action should be either %d or %d or %d. %d is invalid/ unknown action.", _ADD, _REMOVE, _UPDATE, action));

        this.action = action;
    }

    public void setDeleteAction()
    {
        setAction(_REMOVE);
    }

    public void setAddAction()
    {
        setAction(_ADD);
    }

    public void setUpdateAction()
    {
        setAction(_UPDATE);
    }

    public boolean isDelete()
    {
        return getAction() == _REMOVE;
    }

    public boolean isAdd()
    {
        return getAction() == _ADD;
    }

    public boolean isUpdate()
    {
        return getAction() == _UPDATE;
    }

    public Object getKey()
    {
        return key;
    }

    public void setKey(Object key)
    {
        this.key = key;
    }

    public Object[] getKeys()
    {
        return keys;
    }

    public void setKeys(Object[] keys)
    {
        this.keys = keys;
    }
}
