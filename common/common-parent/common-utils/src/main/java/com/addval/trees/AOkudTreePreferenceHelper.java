/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.addval.trees;

import com.addval.datapreference.APreferenceService;
import com.addval.datapreference.AttribValueInfo;
import com.addval.datapreference.PreferenceCacheUtility;
import com.addval.environment.Environment;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ravi.nandiwada
 */
public abstract class AOkudTreePreferenceHelper implements IOkudTreePreferenceHelper
{
    protected PreferenceCacheUtility _prefCacheUtility;
    
    /**
     * used for computing preference
     * key AttribDimInfo object, value - AttribValueInfo object
     * Use thread local for thread safety as the populate is no longer
     * synchronized across branches
     */    
    private static ThreadLocal _dimAttribValueMap = new ThreadLocal(){
        @Override
         protected synchronized Object initialValue() {
             return new HashMap();
         }       
    };
    
    protected Map getDimAttribValueMap()
    {
        return (Map)AOkudTreePreferenceHelper._dimAttribValueMap.get();
    }
        
    public long computePreference(OkudTreeLeafNode leafNode, IOkudTreeLeafNodeData data, XmlOkudTreeHelper treeHelper)
    {
        long preference = 0;        
        
        Map dimAttribValueMap = this.getDimAttribValueMap();
        dimAttribValueMap.clear(); //ok as it is thread local
        AOkudTreeNode node = leafNode;
        Object key;
        String prefAttribCode;
                
        int levelIdx = treeHelper.getLevels().length - 1;
        OkudTreeLevel level;
        
        while(node != null){
            key = node.getKey();
            if(key != null){
                level = treeHelper.getLevels()[levelIdx];
                prefAttribCode = this.getPrefAttribCode(key, level);
                if(prefAttribCode != null){
                    dimAttribValueMap.put(level.getName(), new AttribValueInfo(key, prefAttribCode));
                }
            }
            levelIdx--;
            node = node.getParent();
        }
        //System.out.println("Pref attri val map= " + _dimAttribValueMap);
        preference = this.calcPreference(this.getPreferenceId(data), dimAttribValueMap);
        
        //System.out.println("Returned Pref=" + preference); //test
        return preference;
    }
    
    public long calcPreference(Integer prefId, Map dimValueMap)
    {
        long pref = 0;
        
        APreferenceService prefSrv = this.getPrefCacheUtility().getDataPreferenceMap(prefId);
        if(prefSrv == null)return pref;
        pref = prefSrv.calculatePreference(dimValueMap, null);
        return pref;
    }

    /**
     * @return the _prefCacheUtility
     */
    public PreferenceCacheUtility getPrefCacheUtility() {
        return _prefCacheUtility;
    }

    /**
     * @param prefCacheUtility the _prefCacheUtility to set
     */
    public void setPrefCacheUtility(PreferenceCacheUtility prefCacheUtility) {
        this._prefCacheUtility = prefCacheUtility;
    }
        
}
