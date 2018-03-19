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

package com.addval.metadata;

import java.io.Serializable;

public final class RecordStatus implements Serializable
{
	private static final transient long serialVersionUID = -6012482409016244191L;
	public static final transient int _RMS_UNCHANGED = 0;
    public static final transient int _RMS_UPDATED = 101;
    public static final transient int _RMS_INSERTED = 100;
    public static final transient int _RMS_DELETED = 102;
    public static final transient int _RMS_NOUPDATE = 103;
    public static final transient int _RSS_ERROR = -100;
    public static final transient int _RSS_SYNC = 0;
    public static final transient int _RSS_UNSYNC = -101;
    public static final transient int _RMS_SELECT = 111;
    public static final transient String _SELECT = "SELECT";
    public static final transient String _INSERT = "INSERT";
    public static final transient String _UPDATE = "UPDATE";
    public static final transient String _DELETE = "DELETE";
}
