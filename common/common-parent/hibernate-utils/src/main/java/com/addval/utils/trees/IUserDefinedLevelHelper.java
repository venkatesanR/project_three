/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.addval.utils.trees;

import com.addval.utils.trees.xbeans.TreeDefinitionType;
import java.util.List;

/**
 *
 * @author ravi.nandiwada
 */
public interface IUserDefinedLevelHelper {
    /**
     * These levels will be added at the end of levels defined in the xml
     * @param treeDefinition
     * @return 
     */
    public OkudTreeLevel[] getUserDefinedLevels(TreeDefinitionType treeDefinition);    
    
    /**
     * Return ordered list of keys in the order of created user defined levels
     * 
     * @param dto
     * @return ordered list of keys
     */
    public List makeKeysForUserDefinedLevels(IOkudTreeDTO dto);
    
    
    /**
     * Add UDL search criteria
     * @param criteria
     * @param tree
     * @param dto
     * @return 
     */
    public OkudTreeSearch addSearchCriteria(OkudTreeSearch criteria, OkudTree tree, IOkudTreeDTO dto);
    
}
