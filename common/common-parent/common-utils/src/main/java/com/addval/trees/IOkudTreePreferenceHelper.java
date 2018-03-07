/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.addval.trees;


/**
 *
 * @author ravi.nandiwada
 */
public interface IOkudTreePreferenceHelper {
    public String getPrefAttribCode(Object key, OkudTreeLevel level);
    public int getPreferenceId(IOkudTreeLeafNodeData data);
    public long computePreference(OkudTreeLeafNode leafNode, IOkudTreeLeafNodeData data, OkudTreeLevel[] levels);
    public void setPreferenceAttributeCode(Object key, String levelType);
}
