/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.addval.trees;

import org.apache.log4j.Logger;
import org.apache.commons.beanutils.WrapDynaBean;
import com.addval.utils.StrUtl;
import com.addval.utils.ListUtl;

import java.util.*;

/**
 *
 * @author ravi.nandiwada
 */
public class XmlOkudTreeHelper implements IExtendedOkudTreeHelper
{
    private static Logger _logger = Logger.getLogger(XmlOkudTreeHelper.class);
    
    protected int _nbrTreeLevels; //total number of levels including dynamic levels
    protected int _nbrBaseLevels; //non user defined

    /**
     * Gives a measure of thrashing
     */
    protected int _numberOfLeafNodesLoaded = 0;
                
    protected IOkudTreeDao _dao;
    protected IOkudTreePreferenceHelper _prefUtil;
    protected OkudTreeLevel[] _levels;
    //protected List _udfDefsForLevels; //applicable list of Udf defintion objects
    
    protected TreeDefinitionType _treeDefinition;
    protected String _treeName;
    protected MetaOkudTreeCacheUtility _metaOkudTreeCacheUtility;
    protected IUserDefinedLevelHelper _udlHelper;
    protected OkudTreeUtilities _okudUtil;

    public XmlOkudTreeHelper()
    {
    }

    public XmlOkudTreeHelper(TreeDefinitionType treeDefinition)
    {
        _treeDefinition = treeDefinition;
        this.initialize();
    }

    protected final void initialize(){
        try{
            _treeDefinition = getMetaOkudTreeCacheUtility().getTreeDefinition(_treeName);

            if(_treeDefinition == null)throw new Exception("TreeDefinitionType of tree name " + _treeName + " not found");
            
            _nbrBaseLevels = _treeDefinition.getLevels().getLevelArray().length;
            
            //sort the levels by specified level index rather than depending on sequencial specification in the XML
            Arrays.sort(_treeDefinition.getLevels().getLevelArray(),
                    new Comparator<TreeLevelType>(){

                public int compare(TreeLevelType arg0, TreeLevelType arg1) {
                    return arg0.getIndex() - arg1.getIndex();
                }

            });
            
            //if not set in spring config
            //check if it is set in the tree definition
            if(this.getUdlHelper() == null && _treeDefinition.isSetUserDefinedLevelHelper()){                  
                this.setUdlHelper((IUserDefinedLevelHelper) this.getOkudUtil().createBean(_treeDefinition.getUserDefinedLevelHelper()));
            }
            
            if(this.getPrefUtil() == null && _treeDefinition.isSetPreferenceHelper()){
                this.setPrefUtil((IOkudTreePreferenceHelper) this.getOkudUtil().createBean(_treeDefinition.getPreferenceHelper()));
            } 
            
            if(this.getDao() == null && _treeDefinition.isSetOkudTreeDao()){
                this.setDao((IOkudTreeDao) this.getOkudUtil().createBean(_treeDefinition.getOkudTreeDao()));
            }               
        }
        catch(Exception e){
            throw new RuntimeException("Error initializing XmlOkudTreeHelper", e);
        }
    }

    protected TreeDefinitionType findTreeDefinition(TreeDefinitionsDocument doc){
        for(TreeDefinitionType td : doc.getTreeDefinitions().getTreeDefinitionArray()){
            if(StrUtl.equals(td.getTreeName(), _treeName))return td;
        }

        return null;
    }

    public OkudTreeLevel[] makeLevels() 
    {                
        //if the udl helper is set, then ge those levels  
        OkudTreeLevel[] udLevels = this.getUdlHelper() != null ? this.getUdlHelper().getUserDefinedLevels(_treeDefinition) : null;
        _nbrTreeLevels = udLevels != null ? this._nbrBaseLevels + udLevels.length : this._nbrBaseLevels;
                       
        OkudTreeLevel[] levels = new OkudTreeLevel[_nbrTreeLevels];

        TreeLevelType[] xLevels = _treeDefinition.getLevels().getLevelArray();

        OkudTreeLevel level;

        for(TreeLevelType xLevel : xLevels){
            if(_treeDefinition.getUseExternalCache()){
                level = new OkudTreeLevel(xLevel.getName(), xLevel.getType(), xLevel.getUseExternalCache());
            }
            else{
                level = new OkudTreeLevel(xLevel.getName(), xLevel.getType(), 
                        (xLevel.isSetScore() ? xLevel.getScore() : 0),
                        (xLevel.isSetDiscardable()? xLevel.getDiscardable() : false));                
            }
            
            level.setLevelIndex(xLevel.getIndex());

            if(xLevel.isSetWildcard())level.setWildcard(xLevel.getWildcard());
            try{
                if(xLevel.isSetWildcardKey()){
                    level.setWildcardKey(this.getOkudUtil().createBean(xLevel.getWildcardKey()));
                    //if key class is set then validate both are of the same class type
                    if(xLevel.getKeyClass() != null &&
                            !StrUtl.equals(xLevel.getKeyClass().getClassDefinition().getClassName(), level.getWildcardKey().getClass().getName())){
                        throw new Exception("Level key class and the class of wildcard must be the same - level name = " + level.getName());
                    }
                }
            }
            catch(Exception e){
                throw new RuntimeException("Unable to create wildcard key from the XML definition", e);
            }

            if(xLevel.getKeyClass().sizeOfCtrDtoPropertyGroupArray() > 0 && xLevel.getKeyClass().getClassDefinition().getNumberOfConstructorArgs() !=
                    xLevel.getKeyClass().getCtrDtoPropertyGroupArray(0).sizeOfDtoPropertyArray()){ //just validate the first set of dto props length
                throw new RuntimeException("Level key class - # of constructor args should be the same # of DTO properties");
            }

            if(xLevel.isSetDiscardLRU())level.setDiscardLRU(xLevel.getDiscardLRU());
            if(xLevel.isSetMaxNodesAtLevel())level.setMaxNodesAtLevel(xLevel.getMaxNodesAtLevel());
            if(xLevel.isSetCreateLevelKeysOnly())level.setCreateLevelKeysOnly(xLevel.getCreateLevelKeysOnly());
            if(xLevel.isSetMultiMatch())level.setMultiMatch(xLevel.getMultiMatch());
            if(xLevel.isSetRangeSearchable())level.setRangeSearchable(xLevel.getRangeSearchable());
            
            levels[level.getLevelIndex()] = level;
        }

        //add user defined levels
        if(udLevels != null){
            for(int i = this._nbrBaseLevels; i < this._nbrTreeLevels; i++){
                levels[i] = udLevels[i - this._nbrBaseLevels];
                levels[i].setLevelIndex(i);
            }
        }

        _levels = levels;

        return levels;
    }
    
    public OkudTreeLevel[] getLevels()
    {
        return _levels;
    }

    protected TreeLevelType getXLevel(int levelIndex){
        if(levelIndex >= this._treeDefinition.getLevels().sizeOfLevelArray())return null;
        
        return this._treeDefinition.getLevels().getLevelArray(levelIndex);
    }
    
    /**
     * Return an array from the param xLevel up. For example,
     * if the level index of the xLevel is 4 then the array returned would have 
     * [4, 3, 2, 1, 0].
     * 
     * @param xLevel
     * @return 
     */
    private TreeLevelType[] getTopLevels(TreeLevelType xLevel)
    {
        //construct an array of xLevels to pass to the loader class
        TreeLevelType[] xLevels = new TreeLevelType[xLevel.getIndex() + 1];
        xLevels[0] = xLevel;
        for(int i = 1; i < xLevels.length; i++){
            xLevels[i] = this.getXLevel(xLevels.length - i - 1);
        }  
        
        return xLevels;
    }

    /**
     * The default behavior assumes a set at leaf node, override in sub class
     * @param leafNode
     * @param applicationDataObject 
     */
    public void addDataToLeafNode(OkudTreeLeafNode leafNode, Object applicationDataObject) {
        _numberOfLeafNodesLoaded++;

        Set<IOkudTreeLeafNodeData> dataset = (Set<IOkudTreeLeafNodeData>)leafNode.getLeafNodeData();
        IOkudTreeLeafNodeData data = (IOkudTreeLeafNodeData)applicationDataObject;

        if(dataset == null){
            dataset = new HashSet<IOkudTreeLeafNodeData>(2);
            dataset.add(data);
            if(this.getPrefUtil() != null)leafNode.setScore(this.getPrefUtil().computePreference(leafNode, data, this.getLevels()));
            leafNode.setLeafNodeData(dataset);
        }
        else{
            dataset.add(data);
        }        
    }         

    /**
     * Does nothing. Can be overridden
     * @param tree
     * @param node
     * @param level 
     */
    public void onCreateNode(OkudTree tree, AOkudTreeNode node, OkudTreeLevel level) {
        //do nothing
    }
    
    public void populate(OkudTree tree, OkudTreeNode node, OkudTreeLevel level)
    {
        this.populate2(tree, node, level);
    }

    private void populate2(OkudTree tree, AOkudTreeNode node, OkudTreeLevel level) 
    {
        if(this.getDao() == null){
            _logger.warn("Nothing to populate as no DAO is set in either spring configuration or OkudTree definition");
            return;
        }
        
        try{
            //UDF level cannot be a discardable level, so getXLevel will return null
            TreeLevelType xLevel = this.getXLevel(level.getLevelIndex()); //the sql is specified at the discardable level
            //if xLevel is null then set xLevel to that last xLevel as only that can be discarded
            if(xLevel == null)xLevel = this.getXLevel(this._nbrBaseLevels - 1);
            
            //construct an array of xLevels to pass to the loader class in the order[n, n-1, n-2, ...0]
            //the levels should start at the level to be populated meaning param level + 1
            TreeLevelType[] xLevels = this.getTopLevels(xLevel);

            //build a map of sql values to be used in the query
            //the list is expected to be from this node to the top
            Map<TreeLevelType, Object[]> sqlValues = this.getSqlValueMap(node, level);
            List keys = new ArrayList();
            AOkudTreeNode localNode = node;
            Object key;
            StringBuilder logMsg = new StringBuilder();
                    
            logMsg.append("[");
                        
            while(localNode != null && localNode.getKey() != null){
                key = localNode.getKey();
                keys.add(key);
                logMsg.append(key);
                localNode = localNode.getParent();
                if(localNode != null && localNode.getKey() != null)logMsg.append(", ");                
            }
            logMsg.append("]");

            //reverse keys to start from index 0
            Collections.reverse(keys);

            if(_logger.isDebugEnabled())_logger.debug("XmlOkudTreeHelper.populate() called for keys " + logMsg.toString());

            //execute all sqls and accumulate rates

            List<IOkudTreeDTO> alldtos = new ArrayList<IOkudTreeDTO>();
            List<IOkudTreeDTO> dtos;
            for(String sql : xLevel.getDaoSqlKeys().getDaoSqlKeyArray()){
                dtos = this.getDao().readData(_treeDefinition.getEntityTypes(), sql, sqlValues, xLevels);
                if(dtos != null)alldtos.addAll(dtos);
            }


            if(ListUtl.isEmpty(alldtos)){
                //_logger.warn("No data found for keys " + logMsg.toString());
                if(!(node instanceof OkudTreeLeafNode))((OkudTreeNode)node).setTerminal(true);
                return;
            }
            
            IOkudTreeLeafNodeData data;
            List<Object[]> putKeys;

            for(IOkudTreeDTO dto : alldtos){
                data = this.getDao().createData(dto);
                putKeys = this.makeKeys(dto, keys);                
                tree.putLeafNode(putKeys, data);
            }
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    
    protected Set getKeysForLevel(IOkudTreeDTO dto, TreeLevelType xLevel) throws Exception
    {
        //get dynabean for the dto to get the property values easily and efficiently
        WrapDynaBean wrappedDto = new WrapDynaBean(dto);     
        
        return this.getKeysForLevel(wrappedDto, xLevel);        
    }
    
    protected final Set getKeysForLevel(WrapDynaBean wrappedDto, TreeLevelType xLevel) throws Exception
    {
        Set keysAtLevel = null;
        DtoPropertyType[] propTypes;
        Object[] keyConstructorArgs;
        Class[] argTypes;
        
        if(xLevel.getKeyClass().getDtoProperties() != null){
            keysAtLevel = new HashSet(1);
            for(DtoPropertyType dtoProp : xLevel.getKeyClass().getDtoProperties().getDtoPropertyArray()){
                //the property directly maps to a key
                //when it is a list add all
                boolean isList = dtoProp.getIsList();                
                Object key = wrappedDto.get(dtoProp.getName());  
                if(key == null)continue; //no null key allowed
                if(!isList){                    
                    if(this.getPrefUtil() != null)this.getPrefUtil().setPreferenceAttributeCode(key, xLevel.getType());
                    keysAtLevel.add(key);
                }
                else{
                    this.setPreferenceAttrubuteCode((Collection)key, xLevel);
                    keysAtLevel.addAll((Collection)key);
                }
            }//for each dto property
        }            
        else{
            //else construct keys
            keysAtLevel = new HashSet(xLevel.getKeyClass().getCtrDtoPropertyGroupArray().length);

            for(int i = 0; i < xLevel.getKeyClass().getCtrDtoPropertyGroupArray().length; i++){
                propTypes = xLevel.getKeyClass().getCtrDtoPropertyGroupArray(i).getDtoPropertyArray();
                keyConstructorArgs = new Object[propTypes.length];
                argTypes = new Class[propTypes.length];
                for(int j = 0; j < propTypes.length; j++){
                    keyConstructorArgs[j] = wrappedDto.get(propTypes[j].getName());
                    if(!propTypes[j].getIsList() && keyConstructorArgs[j] != null){                        
                            argTypes[j] = keyConstructorArgs[j].getClass();
                    }
                    else if(propTypes[j].isSetClassName()){ //if list then a class name is expected
                        argTypes[j] = Class.forName(propTypes[j].getClassName());
                    }
                    else{
                        //default String class
                        argTypes[j] = String.class;
                    }                                            
                }
                //if not the primary propert group (meaning i > 0), we will not allow nulls in the key property
                if(i > 0 && keyConstructorArgs[0] == null){
                    continue;
                }

                List keys = createKeys(propTypes, keyConstructorArgs, argTypes, xLevel);
                this.setPreferenceAttrubuteCode(keys, xLevel);
                keysAtLevel.addAll(keys);
            }
        }
        
        return keysAtLevel;        
    }
    
    protected final void setPreferenceAttrubuteCode(Collection keys, TreeLevelType xLevel)
    {
        if(this.getPrefUtil() != null)
            for(Object levelKey : keys){
                this.getPrefUtil().setPreferenceAttributeCode(levelKey, xLevel.getType());
            }        
    }
    
    protected final List createKeys(DtoPropertyType[] propTypes, Object[] keyConstructorArgs, Class[] argTypes, TreeLevelType xLevel) throws Exception
    {
        List keys = new ArrayList(1);
        Object key = null;
        DtoPropertyType dtoProp = propTypes[0]; //we only consider the first one for list
        ClassDefinitionType classDef = xLevel.getKeyClass().getClassDefinition();
        
        //if the first arg is a list then we need to call createClass multiple times
        if(!dtoProp.getIsList()){
            key = OkudTreeUtilities.createClassInstance(classDef, keyConstructorArgs, argTypes);
            keys.add(key);
        }
        else{
            Object[] localArgs = new Object[keyConstructorArgs.length];
            //except the first one the other args remain the same
            //for(int i = 1; i < keyConstructorArgs.length; i++)localArgs[i] = keyConstructorArgs[i];
            System.arraycopy(keyConstructorArgs, 1, localArgs, 1, keyConstructorArgs.length - 1);
            for(Object o : ((Collection)keyConstructorArgs[0])){
                localArgs[0] = o;
                key = OkudTreeUtilities.createClassInstance(classDef, localArgs, argTypes);
                keys.add(key);
            }
        }
                       
        return keys;       
    }

    protected List<Object[]> makeKeys(IOkudTreeDTO dto, List topkeys) throws Exception
    {
        //get dynabean for the dto to get the property values easily and efficiently
        WrapDynaBean wrappedDto = new WrapDynaBean(dto);
        //transpose the array of lists keys to list of arrays keys
        List<Object[]> keyList = new ArrayList<Object[]>(this._nbrTreeLevels);

        int idx = 0;
        for(Object key : topkeys){
            keyList.add(new Object[]{key});
            idx++;
        }
        
        //in case leaf node data needs to be populated, you would have all the keys
        if(idx == this._nbrTreeLevels)return keyList;

        TreeLevelType xLevel = null;        
        Set keysAtLevel = null;

        while(idx < this._nbrBaseLevels){
            xLevel = this.getXLevel(idx);
            keysAtLevel = this.getKeysForLevel(wrappedDto, xLevel);            
            keyList.add(keysAtLevel.toArray());
            idx++;
        }

        if(this.getUdlHelper() != null){
            //limitation: only a singl udl key per ud level
            List udlKeys = this.getUdlHelper().makeKeysForUserDefinedLevels(dto);
            Object key;
            OkudTreeLevel level;
            
            //check to ensure that the udl keys size match the udl levels size
            if(this._nbrTreeLevels - this._nbrBaseLevels > 0 && (ListUtl.isEmpty(udlKeys) || this._nbrTreeLevels - this._nbrBaseLevels != udlKeys.size())){
                throw new Exception("Number of UDL keys does not match number of UDL levels");
            }

            for(idx = 0; idx < udlKeys.size(); idx++){
                key = udlKeys.get(idx);
                level = _levels[this._nbrBaseLevels + idx];
                this.getPrefUtil().setPreferenceAttributeCode(key, level.getLevelType());
                keyList.add(new Object[]{key});
            }        
            
        }

        return keyList;
    }
    
    /**
     * Creates keys list in the order of levels, if a level key cannot be created, 
     * that level key will be null (at the level index in the list)
     * 
     * @param dto
     * @return list of level keys in the order of tree levels where each level may contain multiple keys
     * 
     */
    public List<Object[]> makeKeys(IOkudTreeDTO dto)
    {
        try{
            //get dynabean for the dto to get the property values easily and efficiently
            WrapDynaBean wrappedDto = new WrapDynaBean(dto);
            //transpose the array of lists keys to list of arrays keys
            List<Object[]> keyList = new ArrayList<Object[]>(this._nbrTreeLevels);

            int idx = 0;

            TreeLevelType xLevel = null;        
            Set keysAtLevel = null;

            while(idx < this._nbrBaseLevels){
                xLevel = this.getXLevel(idx);
                keysAtLevel = this.getKeysForLevel(wrappedDto, xLevel);            
                if(keysAtLevel != null)keyList.add(keysAtLevel.toArray());
                else keyList.add(null);
                idx++;
            }

            if(this.getUdlHelper() != null){
                //limitation: only a singl udl key per ud level
                List udlKeys = this.getUdlHelper().makeKeysForUserDefinedLevels(dto);
                Object key;
                OkudTreeLevel level;

                //check to ensure that the udl keys size match the udl levels size
                if(this._nbrTreeLevels - this._nbrBaseLevels > 0 && (ListUtl.isEmpty(udlKeys) || this._nbrTreeLevels - this._nbrBaseLevels != udlKeys.size())){
                    throw new RuntimeException("Number of UDL keys does not match number of UDL levels");
                }

                for(idx = 0; idx < udlKeys.size(); idx++){
                    key = udlKeys.get(idx);
                    level = _levels[this._nbrBaseLevels + idx];
                    this.getPrefUtil().setPreferenceAttributeCode(key, level.getLevelType());
                    keyList.add(new Object[]{key});
                }        

            }

            return keyList;
        }
        catch(RuntimeException re){
            throw re;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }    

    public List getTopLevelKeys(OkudTreeLevel level) 
    {        
        if(this.getDao() == null){
            _logger.warn("Cannot get top level keys as no DAO is set in either spring configuration or OkudTree definition for tree " + this.getTreeName());
            return new ArrayList(0);
        }
        
        TreeLevelType xLevel = this.getXLevel(0); //top level is index 0
        
        List<IOkudTreeDTO> alldtos = new ArrayList();
        List<IOkudTreeDTO> dtos = null;
        for(String sqlKey : xLevel.getDaoSqlKeys().getDaoSqlKeyArray()){
            dtos = this.getDao().getTopLevelKeyDtos(_treeDefinition.getEntityTypes(), sqlKey, xLevel);
            if(dtos != null)alldtos.addAll(dtos);                    
        }

        //if no rate at of the rateTypeList at this time, log warning
        if(ListUtl.isEmpty(alldtos)){
            _logger.warn("No top level keys found for tree " + _treeName);
        }
        
        List allkeys = new ArrayList(alldtos.size());
        
        try{
            for(IOkudTreeDTO dto : alldtos)allkeys.addAll(this.getKeysForLevel(dto, xLevel)); 
        }
        catch(Exception e){
            throw new RuntimeException("Error creating top level keys for tree " + this.getTreeName(), e);
        }
        
        return allkeys;
    }

    /**
     * Method involves compiling the keys for all the levels above so that
     * a sql query can be executes with the correct where clause and the results
     * returned as keys
     * @param tree
     * @param node
     * @param level
     * @return
     */
    public List getLevelKeys(OkudTree tree, OkudTreeNode node, OkudTreeLevel level) 
    {
        if(this.getDao() == null){
            _logger.warn("Cannot get level keys as no DAO is set in either spring configuration or OkudTree definition for the tree " + this.getTreeName());
            return new ArrayList(0);
        }
        
        List<IOkudTreeDTO> dtos = null;
        List<IOkudTreeDTO> alldtos = new ArrayList();
        
        TreeLevelType xLevel = this.getXLevel(level.getLevelIndex()); //level keys only levels are not UDF levels
        
        //construct an array of xLevels to pass to the loader class in the order[n, n-1, n-2, ...0]
        TreeLevelType[] xLevels = this.getTopLevels(xLevel);        

        //build a list of sql values to be used in the query
        //the list is expected to be from this node to the top
        Map<TreeLevelType, Object[]> sqlValues = new HashMap<TreeLevelType, Object[]>();

        AOkudTreeNode localNode = node;
        Object key;
        int levelIdxForSqlKeys = level.getLevelIndex() - 1;
        TreeLevelType sqlKeyLevel;

        //collect keys from higher level so that they can be used for query
        while(localNode != null && localNode.getKey() != null){
            key = localNode.getKey();
            sqlKeyLevel = this.getXLevel(levelIdxForSqlKeys);
            sqlValues.put(sqlKeyLevel, this.getDao().getSqlKeyValues(sqlKeyLevel, key));
            localNode = localNode.getParent();
            levelIdxForSqlKeys--;
        }

        for(String sqlKey : xLevel.getDaoSqlKeys().getDaoSqlKeyArray()){
            dtos = this.getDao().getLevelKeyDtos(_treeDefinition.getEntityTypes(), sqlKey, xLevels, sqlValues);
            if(dtos != null)alldtos.addAll(dtos);
        }

        //ifno dtos, log warning
        //and set terminal
        if(ListUtl.isEmpty(alldtos)){
            node.setTerminal(true);
            _logger.warn("No level keys found under level key - " + node.getKey() + " for tree " + this.getTreeName());            
        }        
        
        List allkeys = new ArrayList(alldtos.size());
        
        try{
            for(IOkudTreeDTO dto : alldtos)allkeys.addAll(this.getKeysForLevel(dto, xLevel)); 
        }
        catch(Exception e){
            throw new RuntimeException("Error creating level keys for level " + xLevel.getName() + ", tree " + this.getTreeName(), e);
        }
        
        return allkeys;               
    }

    /**
     * @return the _treeDefinition
     */
    public TreeDefinitionType getTreeDefinition() {
        return _treeDefinition;
    }

    /**
     * @param treeDefinition the _treeDefinition to set
     */
    public void setTreeDefinition(TreeDefinitionType treeDefinition) {
        this._treeDefinition = treeDefinition;
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

    public OkudTree createTree()
    {
        IOkudTreeLeafNodeData  dataObject = null;
        
        try{
            //data object
            dataObject = (IOkudTreeLeafNodeData)this.getOkudUtil().createBean(_treeDefinition.getDataObjectDefinition());
        }
        catch(Exception e){
            throw new RuntimeException("Error creating data object", e);
        }
        
        if(_treeDefinition.getUseExternalCache() && !_treeDefinition.isSetExternalCacheConfig()){
            throw new RuntimeException("External cache configuration information is not provided in the tree definition");
        }
        
        String extCacheName = null;
        
        IExternalCache externalCacheBean = null;

        try{
            if(_treeDefinition.getUseExternalCache()){
                extCacheName = _treeDefinition.getExternalCacheConfig().getExternalCacheName();
                externalCacheBean = (IExternalCache) this.getOkudUtil().createBean(_treeDefinition.getExternalCacheConfig().getExternalCacheUtility());
            }
        }
        catch(Exception e){
            throw new RuntimeException("Unable to create/access external cache utility bean", e);
        }
        
        OkudTree tree = new OkudTree(
                this.makeLevels(),
                this,
                _treeDefinition.getTreeConstructorArgs().getEnforceSerializable(),
                dataObject,
                _treeDefinition.getTreeConstructorArgs().getUseKeyCache(),
                _treeDefinition.getTreeConstructorArgs().getLazyLoad(),
                _treeDefinition.getUseExternalCache(),
                extCacheName,
                externalCacheBean);
        
        if(_treeDefinition.isSetTopLevelIncrementalLoad())tree.setTopLevelIncrementalLoad(_treeDefinition.getTopLevelIncrementalLoad());
        if(_treeDefinition.isSetMaxConcurrentPopulates())tree.setMaxConcurrentPopulates(_treeDefinition.getMaxConcurrentPopulates());

        return tree;
    }
    
    /**
     * Creates search keys based on dto properties specified in the tree definition for search
     * Uses UDL helper to create keys for UDLs
     * @param dto
     * @return Returns populated OkudTreeSearch object based on dto properties
     */
    public OkudTreeSearch makeSearchCriteria(OkudTree tree, IOkudTreeDTO dto)
    {
        //get dynabean for the dto to get the property values easily and efficiently
        WrapDynaBean wrappedDto = new WrapDynaBean(dto);        
        OkudTreeSearch criteria = tree.createSearch();
        
        //check breadth first search if configured
        if(this._treeDefinition.isSetSearchBreadthFirst())criteria.setBreadthFirst(this._treeDefinition.getSearchBreadthFirst());
        if(criteria.isBreadthFirst() && this._treeDefinition.isSetWaitForPopulateMilliSec()){
            criteria.setWaitForPopulateMillSeconds(this._treeDefinition.getWaitForPopulateMilliSec());
        }
        
        TreeLevelType xLevel;
        SearchCriteriaType searchCrit;
        DtoPropertyType prop1, prop2;
        Object key1, key2;
                
        for(int i = 0; i < this._nbrBaseLevels; i++){
            xLevel = this.getXLevel(i);
            if(_logger.isDebugEnabled())_logger.debug("Making search keys for level " + xLevel.getName());

            searchCrit = xLevel.getSearchCriteria();
            if(searchCrit == null)continue;
            for(SearchProperty sp : searchCrit.getBeanPropertyArray()){
                prop1 = sp.getPropertyArray(0);
                key1 = wrappedDto.get(prop1.getName()); //key could be null

                if(sp.sizeOfPropertyArray() == 2){
                    prop2 = sp.getPropertyArray(1);
                    key2 = wrappedDto.get(prop2.getName()); //key could be null
                }
                else{
                    prop2 = null;
                    key2 = null;
                }   
                
                if(sp.getRangeSearch() && (key1 != null || key2 != null)){
                    //use both bean properties for range search
                    //at least one value in the range has to be specified                    
                    criteria.addRangeSearchKeys(i, (Comparable)key1, (Comparable)key2);
                }
                else{ //equal search
                    if(key1 != null){
                        //if isList is true on the properties, add it as search keys
                        if(prop1.getIsList())criteria.addEqualSearchKeys(i, (Collection)key1);
                        else criteria.addEqualSearchKey(i, key1);
                    }         
                    
                    if(key2 != null){
                        //if isList is true on the properties, add it as search keys
                        if(prop2.getIsList())criteria.addEqualSearchKeys(i, (Collection)key2);
                        else criteria.addEqualSearchKey(i, key2);
                    }                       
                }
            }
        }
        
        //add keys for udls
        if(this._nbrBaseLevels < this._nbrTreeLevels && this.getUdlHelper() != null){
            criteria = this.getUdlHelper().addSearchCriteria(criteria, tree, dto);
        }
        
        return criteria;
    }

    /**
     * @return the _udlHelper
     */
    public IUserDefinedLevelHelper getUdlHelper() {
        return _udlHelper;
    }

    /**
     * @param udlHelper the _udlHelper to set
     */
    public void setUdlHelper(IUserDefinedLevelHelper udlHelper) {
        this._udlHelper = udlHelper;
    }

    /**
     * @return the _prefUtil
     */
    public IOkudTreePreferenceHelper getPrefUtil() {
        return _prefUtil;
    }

    /**
     * @param prefUtil the _prefUtil to set
     */
    public void setPrefUtil(IOkudTreePreferenceHelper prefUtil) {
        this._prefUtil = prefUtil;
    }

    /**
     * @return the _dao
     */
    public IOkudTreeDao getDao() {
        return _dao;
    }

    /**
     * @param dao the _dao to set
     */
    public void setDao(IOkudTreeDao dao) {
        this._dao = dao;
    }

    /**
     * @return the _metaOkudTreeCacheUtility
     */
    public MetaOkudTreeCacheUtility getMetaOkudTreeCacheUtility() {
        return _metaOkudTreeCacheUtility;
    }

    /**
     * @param metaOkudTreeCacheUtility the _metaOkudTreeCacheUtility to set
     */
    public void setMetaOkudTreeCacheUtility(MetaOkudTreeCacheUtility metaOkudTreeCacheUtility) {
        this._metaOkudTreeCacheUtility = metaOkudTreeCacheUtility;
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

    public void populateLevelKeys(OkudTree tree, Collection<AOkudTreeNode> nodes, OkudTreeLevel level) {
        
        if(this.getDao() == null){
            _logger.warn("Nothing to populate as no DAO is set in either spring configuration or OkudTree definition");
            return;
        }
        
        TreeLevelType xLevel = this.getXLevel(level.getLevelIndex()); //level keys only levels are not UDF levels
        TreeLevelType[] xLevels = this.getTopLevels(xLevel);
        
        //build node sql value map
        Map<AOkudTreeNode, Map<TreeLevelType, Object[]>> nodeSqlValueMap = new HashMap<AOkudTreeNode, Map<TreeLevelType, Object[]>>();
        for(AOkudTreeNode node : nodes){
            nodeSqlValueMap.put(node, this.getSqlValueMap(node, level));
        }
                
        Map<AOkudTreeNode, List<IOkudTreeDTO>> levelKeyMap = 
        this.getDao().getMultiNodeLevelKeyDtos(_treeDefinition.getEntityTypes(), 
                xLevel.isSetDaoSqlKeys() ? xLevel.getDaoSqlKeys().getDaoSqlKeyArray() : null,
                xLevels,
                nodeSqlValueMap);
        
        
        if(levelKeyMap == null || levelKeyMap.isEmpty()){
            //assume no child nodes for any node and hence set terminal
            //populateLevelKeys is never called for leaf nodes
            for(AOkudTreeNode node : nodes)((OkudTreeNode)node).setTerminal(true);
        }
        
        List<IOkudTreeDTO> dtos;
        
        try{
            for(AOkudTreeNode node : nodes){
                dtos = levelKeyMap.get(node);
                if(ListUtl.isEmpty(dtos)){
                    ((OkudTreeNode)node).setTerminal(true);
                }
                else{ 
                    for(IOkudTreeDTO dto : dtos)tree.createNodesAtLevel(this.getKeysForLevel(dto, xLevel), level, (OkudTreeNode)node);
                }
            }
        }
        catch(Exception e){
            throw new RuntimeException("Error creating level keys for level " + xLevel.getName() + ", tree " + this.getTreeName(), e);
        }
                
    }

    public void populate(OkudTree tree, Collection<AOkudTreeNode> nodes, OkudTreeLevel level) {        
        if(this.getDao() == null){
            _logger.warn("Nothing to populate as no DAO is set in either spring configuration or OkudTree definition");
            return;
        }
        
        boolean isLeafLevel = nodes.iterator().next() instanceof OkudTreeLeafNode;
        
        //UDF level cannot be a discardable level, so getXLevel will return null
        TreeLevelType xLevel = this.getXLevel(level.getLevelIndex());
        //if xLevel is null then set xLevel to that last xLevel as only that can be discarded
        if(xLevel == null)xLevel = this.getXLevel(this._nbrBaseLevels - 1);
                
        //construct an array of xLevels to pass to the loader class in the order[n, n-1, n-2, ...0]
        //the levels should start at the level to be populated meaning param level
        //if leaf node needs to be populated then all the levels are involved
        TreeLevelType[] xLevels = this.getTopLevels(xLevel);
                
        //build node sql value map
        Map<AOkudTreeNode, Map<TreeLevelType, Object[]>> nodeSqlValueMap = new HashMap<AOkudTreeNode, Map<TreeLevelType, Object[]>>();
        for(AOkudTreeNode node : nodes){
            nodeSqlValueMap.put(node, this.getSqlValueMap(node, level));
        }
                
        Map<AOkudTreeNode, List<IOkudTreeDTO>>  nodeDtoMap = 
        this.getDao().readMultiNodeData(_treeDefinition.getEntityTypes(), 
                xLevel.isSetDaoSqlKeys() ? xLevel.getDaoSqlKeys().getDaoSqlKeyArray() : null,
                nodeSqlValueMap, 
                xLevels);    
        
        if(!isLeafLevel && (nodeDtoMap == null || nodeDtoMap.isEmpty())){
            //assume no child nodes for any node and hence set terminal
            for(AOkudTreeNode node : nodes)((OkudTreeNode)node).setTerminal(true);
        }
        
        List<IOkudTreeDTO> dtos;
        IOkudTreeLeafNodeData data;
        List<Object[]> putKeys;
        List parentKeys = new ArrayList();
        
        try{                
            for(AOkudTreeNode node : nodes){
                dtos = nodeDtoMap.get(node);
                if(ListUtl.isEmpty(dtos)){
                    if(!isLeafLevel)((OkudTreeNode)node).setTerminal(true);
                }
                else{
                    for(IOkudTreeDTO dto : dtos){                    
                        data = this.getDao().createData(dto);
                        putKeys = this.makeKeys(dto, this.getParentKeys(node, parentKeys));                
                        tree.putLeafNode(putKeys, data);
                    }
                }
            }
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }                
    }
    
    private Map<TreeLevelType, Object[]> getSqlValueMap(AOkudTreeNode node, OkudTreeLevel level){
        //build a list of sql values to be used in the query
        //the list is expected to be from this node to the top
        Map<TreeLevelType, Object[]> sqlValues = new HashMap<TreeLevelType, Object[]>();

        AOkudTreeNode localNode = node;
        Object key;
        int levelIdxForSqlKeys = node instanceof OkudTreeLeafNode ? level.getLevelIndex() : level.getLevelIndex() - 1;
        TreeLevelType sqlKeyLevel;

        //collect keys from higher level so that they can be used for query
        while(localNode != null && localNode.getKey() != null){
            key = localNode.getKey();
            sqlKeyLevel = this.getXLevel(levelIdxForSqlKeys);
            //for UDF levels, there is no xLevel and no sql values required
            if(sqlKeyLevel != null)sqlValues.put(sqlKeyLevel, this.getDao().getSqlKeyValues(sqlKeyLevel, key));
            localNode = localNode.getParent();
            levelIdxForSqlKeys--;
        }
        
        return sqlValues;
    }
    
    private List getParentKeys(AOkudTreeNode anode, List keys){
        keys.clear();
        AOkudTreeNode localNode = anode;        
        Object key;
        
        while(localNode != null && localNode.getKey() != null){
            key = localNode.getKey();
            keys.add(key);
            localNode = localNode.getParent();
        }

        //reverse keys to start from index 0
        Collections.reverse(keys);
        
        return keys;
    }

    public void populateLeafNode(OkudTree tree, OkudTreeLeafNode node, OkudTreeLevel level) {
        this.populate2(tree, node, level);
    }
}
