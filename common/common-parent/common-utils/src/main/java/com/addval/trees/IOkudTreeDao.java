/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.addval.trees;

import com.addval.utils.trees.xbeans.*;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ravi.nandiwada
 */
public interface IOkudTreeDao {
    /**
     * Return sql keys for the tree key at the xLevel
     * 
     * @param xLevel level for which the key is provided
     * @param key
     * @return 
     */
    Object[] getSqlKeyValues(TreeLevelType xLevel, Object key);
    
    /**
     * 
     * @param entityTypes
     * @param daoSqlName array of DAO SQL names specified in the configuration
     * @param sqlValues Map with key as the xLevel for which the sql values that are derived from the key
     * @param xLevel array of xLevels from the current level at which branches need to be added going up to the top most level (index = 0)
     * @return 
     */
    public List<IOkudTreeDTO> readData(EntityTypesType entityTypes, String daoSqlName, Map<TreeLevelType, Object[]> sqlValues, TreeLevelType[] xLevel);
    public IOkudTreeLeafNodeData createData(IOkudTreeDTO dto);
    public List<IOkudTreeDTO> getTopLevelKeyDtos(EntityTypesType entityTypes, String daoSqlName, TreeLevelType xLevel);
    public List<IOkudTreeDTO> getLevelKeyDtos(EntityTypesType entityTypes, String daoSqlName, TreeLevelType[] xLevel, Map<TreeLevelType, Object[]> sqlValues);
    
    /**
     * Implement this if depth first search is used. A simple implementation is iteratively call getLevelKeyDtos but with limited performance gains if the same
     * connection and prepared statements are used. A better implementation would reduce the # of SQL executions.
     * @param entityTypes entity types are auxiliary information provided in the tree configuration. For example, rate type, category, cost type, etc.
     * @param daoSqlName array of DAO SQL names specified in the tree configuration
     * @param xLevel array of xLevels from the current level at which branches need to be added going up to the top most level (index = 0)
     * @param nodeSqlValues
     * @return 
     */
    public Map<AOkudTreeNode, List<IOkudTreeDTO>> getMultiNodeLevelKeyDtos(EntityTypesType entityTypes, String[] daoSqlName, TreeLevelType[] xLevel, Map<AOkudTreeNode, Map<TreeLevelType, Object[]>> nodeSqlValues);
    
    /**
     * Implement this if depth first search is used. A simple implementation is iteratively call readData but with limited performance gains if the same
     * connection and prepared statements are used. A better implementation would reduce the # of SQL executions. 
     * @param entityTypes entity types are auxiliary information provided in the tree configuration. For example, rate type, category, cost type, etc.
     * @param daoSqlName array of DAO SQL names specified in the tree configuration
     * @param nodeSqlValues Map of node to Map with key as the xLevel for which the sql values that are derived from the key
     * @param xLevel array of xLevels from the current level at which branches need to be added going up to the top most level (index = 0)
     * @return 
     */
    public Map<AOkudTreeNode, List<IOkudTreeDTO>> readMultiNodeData(EntityTypesType entityTypes, String[] daoSqlName, Map<AOkudTreeNode, Map<TreeLevelType, Object[]>> nodeSqlValues, TreeLevelType[] xLevel);    
}
