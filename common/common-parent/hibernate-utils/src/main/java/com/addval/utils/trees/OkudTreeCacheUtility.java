/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.addval.utils.trees;

import org.apache.log4j.Logger;

import com.addval.utils.*;
import com.addval.utils.trees.xbeans.*;


/**
 *
 * @author ravi.nandiwada
 */
public class OkudTreeCacheUtility extends DefaultNamedCacheInitializer
{
    protected IExtendedOkudTreeHelper _treeHelper;
    protected String _treeName;
    protected MetaOkudTreeCacheUtility _metaOkudTreeCacheUtility;    
    protected OkudTreeUtilities _okudUtil;
    private static Logger _logger = LogMgr.getLogger(OkudTreeCacheUtility.class);;

    @Override
    public Object populateData(CacheMgr cache, String objectName, boolean refreshFlag) throws CacheException {
        final String _source = getClass().getName() + ".populateData()";

        if (_logger.isDebugEnabled())_logger.debug("Enter " + _source);

        OkudTree tree = null;

        try{
            if(_treeHelper == null){
                TreeDefinitionType treeDefinition = this._metaOkudTreeCacheUtility.getTreeDefinition(_treeName);
                if(treeDefinition == null)throw new Exception("TreeDefinitionType of tree name " + _treeName + " not found");
                _treeHelper = (IExtendedOkudTreeHelper)this.getOkudUtil().createBean(treeDefinition.getTreeHelperDefinition());
            }

            tree = this.getTreeHelper().createTree();
        }
        catch (Exception e) {
            _logger.error("Error creating cache - " + this.getObjectName(), e);
            throw new CacheException(getClass().getName(), e);
        }

        if (_logger.isDebugEnabled())_logger.debug("Exit " + _source);

        return tree;
    }


    public OkudTree getRateTree() {
        return (OkudTree) this.getEnvironment().getCacheMgr().get(this.getObjectName());
    }

    /**
     * @return the _treeHelper
     */
    public IExtendedOkudTreeHelper getTreeHelper() {
        return _treeHelper;
    }

    /**
     * @param treeHelper the _treeHelper to set
     */
    public void setTreeHelper(IExtendedOkudTreeHelper treeHelper) {
        this._treeHelper = treeHelper;
    }

    /**
     * @return the _treeName
     */
    public String getTreeName() {
        return _treeName;
    }

    /**
     * @param treeName the _treeName to set
     */
    public void setTreeName(String treeName) {
        this._treeName = treeName;
    }

    /**
     * @return the _MetaOkudTreeCacheUtility
     */
    public MetaOkudTreeCacheUtility getMetaOkudTreeCacheUtility() {
        return _metaOkudTreeCacheUtility;
    }

    /**
     * @param MetaOkudTreeCacheUtility the _MetaOkudTreeCacheUtility to set
     */
    public void setMetaOkudTreeCacheUtility(MetaOkudTreeCacheUtility MetaOkudTreeCacheUtility) {
        this._metaOkudTreeCacheUtility = MetaOkudTreeCacheUtility;
    }
    
    /**
     * @return the _okudUtil
     */
    public OkudTreeUtilities getOkudUtil() {
        return _okudUtil;
    }

    /**
     * @param okudUtil the _okudUtil to set
     */
    public void setOkudUtil(OkudTreeUtilities okudUtil) {
        this._okudUtil = okudUtil;
    }    
}
