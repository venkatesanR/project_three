/*
 * Copyright (c) 2001-2014 Accenture LLC.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.addval.ui.UIComponentMetadata;
import com.addval.utils.Pair;
import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;

public class MetaDataUtils implements Serializable, Cloneable {
	private static final long serialVersionUID = -2730480754728094295L;

	public static void deleteUIComponentMetadata(List<UIComponentMetadata> uiCmdList, UIComponentMetadata uiCmd) {
		if (uiCmdList != null && uiCmd.getKey() != null) {
			int index = 0;
			for (UIComponentMetadata uiComponentMetadata : uiCmdList) {
				if (uiCmd.getKey().equals(uiComponentMetadata.getKey())) {
					uiCmdList.remove(index);
					break;
				}
				index++;
			}
		}
	}

	public static UIComponentMetadata findUIComponentMetadata(List<UIComponentMetadata> uiCmdList, String componentId) {
		if (uiCmdList != null && !StrUtl.isEmptyTrimmed(componentId)) {
			for (UIComponentMetadata uiComponentMetadata : uiCmdList) {
				if (componentId.equals(uiComponentMetadata.getComponentId())) {
					return uiComponentMetadata;
				}
			}
		}
		return null;
	}

	public static void updateUIComponentMetadata(List<UIComponentMetadata> uiCmdList, UIComponentMetadata uiCmd) {
		boolean match = false;
		if (uiCmdList != null && uiCmd.getKey() != null) {
			for (UIComponentMetadata uiComponentMetadata : uiCmdList) {
				if (uiCmd.getKey().equals(uiComponentMetadata.getKey())) {
					uiComponentMetadata.setJsonString(uiCmd.getJsonString());
					match = true;
				}
			}
		}
		if (!match) {
			uiCmdList.add(uiCmd);
		}
	}
}
