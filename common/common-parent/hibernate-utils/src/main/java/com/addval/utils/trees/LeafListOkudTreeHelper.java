/*
 * LeafListOkudTreeHelper.java
 *
 * Created on May 25, 2006, 9:55 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.utils.trees;

import java.util.*;

/**
 *
 * @author ravi
 */
public class LeafListOkudTreeHelper implements IOkudTreeHelper
{
    
    /** Creates a new instance of LeafListOkudTreeHelper */
    public LeafListOkudTreeHelper() 
    {
    }
    
    public void addDataToLeafNode(OkudTreeLeafNode leafNode, Object applicationDataObject)
    {
        List list = (List)leafNode.getLeafNodeData();
        if(list == null){
            list = new ArrayList();
            leafNode.setLeafNodeData(list);
        }
        list.add(applicationDataObject);
    }
    
    public void onCreateNode(OkudTree tree, AOkudTreeNode node, OkudTreeLevel level)
    {        
        long score = 0;
        if(node.getParent() != null)score = node.getParent().getScore();
        
        if(node.getKey() != null){
            if(level.getWildcardKey() == null ||  
                !level.getWildcardKey().equals(node.getKey()))score += level.getScore();
        }
        
        node.setScore(score);
    }
    
    public void populate(OkudTree tree, OkudTreeNode node, OkudTreeLevel level)
    {
        //don't do anything to as this helper doesnot support discardable levels
    }  
    
    public List getTopLevelKeys(OkudTreeLevel level)
    {
        //lazy load is not supported
        return null;
    }
    
    public List getNextLevelKeys(OkudTree tree, OkudTreeNode node, OkudTreeLevel level)
    {
        //lazy load is not supported
        return null;
    }

    public List getLevelKeys(OkudTree tree, OkudTreeNode node, OkudTreeLevel level){
        return null;        
    }


    
}
