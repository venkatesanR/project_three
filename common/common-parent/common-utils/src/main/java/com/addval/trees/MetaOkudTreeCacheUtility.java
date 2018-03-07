/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.addval.trees;

import com.addval.utils.*;

import com.addval.utils.trees.xbeans.*;
import java.util.*;
import java.net.URL;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlError;


/**
 * This cache is responsible for parsing all okud tree cache configuration xmls and
 * storing the information
 * Recommended cache name OkudTreeDefinitions
 * 
 * @author ravi.nandiwada
 */
public class MetaOkudTreeCacheUtility extends DefaultNamedCacheInitializer
{
    private static Logger _logger = Logger.getLogger(MetaOkudTreeCacheUtility.class);

    private Set<String> _treeDefinitionXmlFileNames;
    private String _commaSeparatedTreeDefinitionXmlFileNames;
    private Map<String, TreeDefinitionType> _treeDefinitionMap;

    /**
     * Returns a Map<tree name, TreeDefinitionType>
     * @param cache
     * @param objectName
     * @param refreshFlag
     * @return
     * @throws com.addval.utils.CacheException
     */
    @Override
	public Object populateData(CacheMgr cache, String objectName, boolean refreshFlag) throws CacheException {
		final String _source = getClass().getName() + ".populateData()";

		if (_logger.isDebugEnabled())_logger.debug("Enter " + _source);

        _treeDefinitionMap = new HashMap<String, TreeDefinitionType>();

        if(ListUtl.isEmpty(_treeDefinitionXmlFileNames) && StrUtl.isEmpty(this.getCommaSeparatedTreeDefinitionXmlFileNames())){
            _logger.warn("No okud tree definition set while defining MetaOkudTreeCacheUtility bean");
            return _treeDefinitionMap;
        }
        
        if(_treeDefinitionXmlFileNames == null)_treeDefinitionXmlFileNames = new HashSet<String>();
        if(!StrUtl.isEmpty(this.getCommaSeparatedTreeDefinitionXmlFileNames())){
            _treeDefinitionXmlFileNames.addAll(Arrays.asList(this.getCommaSeparatedTreeDefinitionXmlFileNames().trim().split(",")));
        }

        TreeDefinitionsDocument doc;
        try{
            for(Iterator<String> iter = _treeDefinitionXmlFileNames.iterator(); iter.hasNext();){
                doc = this.parseTreeDefinitionXml(iter.next());
                for(TreeDefinitionType td : doc.getTreeDefinitions().getTreeDefinitionArray()){
                    _treeDefinitionMap.put(td.getTreeName(), td);
                }
            }
        }
        catch(Exception e){
			_logger.error("Error create cache - " + this.getObjectName(), e);
			throw new CacheException(getClass().getName(), e);
        }

		if (_logger.isDebugEnabled())_logger.debug("Exit " + _source);
        
		return _treeDefinitionMap;
	}

    public Map<String, TreeDefinitionType> getTreeDefinitionMap()
    {
        //this is not the best way if the cache was registered under a different name then we are in trouble
        //but just don't register it that way, we are not going to use the magical spring injection here
        return (Map<String, TreeDefinitionType>) this.getEnvironment().getCacheMgr().get(this.getObjectName());
    }

    public TreeDefinitionType getTreeDefinition(String treeName){
        Map<String, TreeDefinitionType> map = (Map<String, TreeDefinitionType>)getTreeDefinitionMap();
        return map == null ? null : map.get(treeName);
    }

//    public TreeDefinitionType getTreeDefinitionByRateType(String rateTypeName){
//        Map<String, TreeDefinitionType> map = (Map<String, TreeDefinitionType>)getTreeDefinitionMap();
//        
//        if(map == null)return null;
//
//        //each tree def may list of rate types or rate categories
//        //get the rate cat of the rate type
//        RateType rateType = RateTypeCacheUtility.getRateType(rateTypeName);
//
//        if(rateType == null){
//            _logger.warn("No rate type definition found for rate type name " + rateTypeName);
//            return null;
//        }
//        
//        for(TreeDefinitionType td : map.values()){
//            if(td.getRateTypes().isSetRateCategory()){
//                if(StrUtl.equals(rateType.getCategory(), td.getRateTypes().getRateCategory()))return td;
//            }
//            else{
//                for(String s : td.getRateTypes().getRateTypeArray())if(StrUtl.equals(rateTypeName, s))return td;
//            }
//        }
//
//        return null;
//    }

    /**
     * @return the _treeDefinitionXmlFileNames
     */
    public Set<String> getTreeDefinitionXmlFileNames() {
        return _treeDefinitionXmlFileNames;
    }

    /**
     * @param treeDefinitionXmlFileNames the _treeDefinitionXmlFileNames to set
     */
    public void setTreeDefinitionXmlFileNames(Set<String> treeDefinitionXmlFileNames) {
        this._treeDefinitionXmlFileNames = treeDefinitionXmlFileNames;
    }

    protected TreeDefinitionsDocument parseTreeDefinitionXml(String xmlFileName) throws Exception
    {
        if(_logger.isDebugEnabled())_logger.debug("Parsing tree definition xml file - " + xmlFileName);
        
        //get the resource URL
        URL treeDefinitionUrl = MetaOkudTreeCacheUtility.class.getClassLoader().getResource(xmlFileName);
        TreeDefinitionsDocument treeDefDoc = TreeDefinitionsDocument.Factory.parse(treeDefinitionUrl);

        Collection errors = new ArrayList();
        XmlOptions validateOptions = new XmlOptions();
        validateOptions.setErrorListener(errors);
        boolean isValid = treeDefDoc.validate(validateOptions);

        if(!isValid){
            StringBuilder sb = new StringBuilder(512);
            sb.append("Invalid tree definition XML: ");
            for (Iterator iter = errors.iterator(); iter.hasNext();){
                XmlError error = (XmlError)iter.next();
                sb.append(error.getMessage()).append("] \n");
            }

            throw new Exception(sb.toString());
        }

        return treeDefDoc;
    }

    /**
     * @return the _commaSeparatedTreeDefinitionXmlFileNames
     */
    public String getCommaSeparatedTreeDefinitionXmlFileNames() {
        return _commaSeparatedTreeDefinitionXmlFileNames;
    }

    /**
     * @param commaSeparatedTreeDefinitionXmlFileNames the _commaSeparatedTreeDefinitionXmlFileNames to set
     */
    public void setCommaSeparatedTreeDefinitionXmlFileNames(String commaSeparatedTreeDefinitionXmlFileNames) {
        this._commaSeparatedTreeDefinitionXmlFileNames = commaSeparatedTreeDefinitionXmlFileNames;
    }


}
