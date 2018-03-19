package com.addval.utils;

import com.addval.utils.build.xbeans.*;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlError;
import com.addval.parser.Utils;


import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: thiyagu
 * Date: Aug 29, 2007
 * Time: 10:25:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuildUtils {
    private boolean showlogs = true;
    private static final String newLine = System.getProperty("line.separator");
    private static final String WEBLOGIC_APPS_SERVER = "weblogic";
    private static final String WEBSPHERE_APPS_SERVER = "websphere";
    private FileUtils fileUtils = null;
    public BuildUtils(){
        fileUtils = new FileUtils();
    }

    public void execute(CommandLine cl) throws XmlException, IOException,XRuntime {

        String appsServerName   = cl.hasFlag( "-a" ) ? cl.getFlagArgument( "-a" ) : null;
        String projectName      = cl.hasFlag( "-p" ) ? cl.getFlagArgument( "-p" ) : null;
        String buildFolderName  = cl.hasFlag( "-b" ) ? cl.getFlagArgument( "-b" ) : null;
        String overwritesXml    = cl.hasFlag( "-o" ) ? cl.getFlagArgument( "-o" ) : null;
        String showlog          = cl.hasFlag( "-l" ) ? cl.getFlagArgument( "-l" ) : null;

        if( StrUtl.isEmptyTrimmed( appsServerName ) || StrUtl.isEmptyTrimmed( projectName ) || StrUtl.isEmptyTrimmed( buildFolderName ) || StrUtl.isEmptyTrimmed( overwritesXml )){
            printHelp();
            return;
        }
        showlogs = "TRUE".equalsIgnoreCase( showlog );

        /************************************************************************************************
         *  Parse BuildProject Document and validate the xml.
         * *********************************************************************************************/
        BuildProjectDocument document = BuildProjectDocument.Factory.parse( new File ( overwritesXml ) );
        BuildProject projectOverwrites = document.getBuildProject();

        ArrayList validationXmlErrors = new ArrayList();
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setErrorListener( validationXmlErrors );
        boolean isValid = projectOverwrites.validate( xmlOptions );
        if( !isValid ){
            for (int i=0; i < validationXmlErrors.size(); i++ ){
                XmlError xmlError = (XmlError) validationXmlErrors.get(i);
                System.out.println( xmlError.toString() );
            }
            System.exit(1);
        }


        /************************************************************************************************
         *  MenuOverwrites
        * *********************************************************************************************/
        String menuFolder = buildFolderName + "/" + projectName+ "Ear/"+ projectName +"WebApp/js";
        MenuOverwrites menuOverwrites = projectOverwrites.getMenuOverwrites();
        new ApplyMenuOverwrites(menuOverwrites,menuFolder);

        /************************************************************************************************
         *  CacheOverwrites
         * *********************************************************************************************/
        String cacheFolder = buildFolderName + "/" + projectName+ "Ear/APP-INF/classes";
        CacheOverwrites cacheOverwrites = projectOverwrites.getCacheOverwrites();
        new ApplyCacheOverwrites(cacheOverwrites,cacheFolder);


        /************************************************************************************************
         *  PropertiesOverwrites
         * *********************************************************************************************/
        String propSearchFolder1 = buildFolderName + "/" + projectName+ "Ear/APP-INF/classes";
        String propSearchFolder2 = buildFolderName + "/" + projectName+ "CommsEar/APP-INF/classes";
        String propSearchFolder3 = buildFolderName + "/tpl";
        PropertiesOverwrites propertiesOverwrites = projectOverwrites.getPropertiesOverwrites();

        new ApplyPropertiesOverwrites(propertiesOverwrites,propSearchFolder1 + "," + propSearchFolder2 + "," + propSearchFolder3);

        /************************************************************************************************
         *  DAOOverwrites
         * *********************************************************************************************/
        String daoSearchFolder = buildFolderName + "/" + projectName+ "Ear/APP-INF/classes";

        DAOOverwrites daoOverwrites = projectOverwrites.getDAOOverwrites();
        new ApplyDAOOverwrites(daoOverwrites,daoSearchFolder);

        /************************************************************************************************
         *  EJBOverwrites
        **********************************************************************************************/
        String ejbSearchFolder1 = buildFolderName + "/" + projectName+ "Ear/" + projectName+ "Ejb/META-INF";
        String ejbSearchFolder2 = buildFolderName + "/" + projectName+ "CommsEar/" + projectName+ "CommsEjb/META-INF";
        EJBOverwrites ejbOverwrites = projectOverwrites.getEJBOverwrites();
        new ApplyEJBOverwrites(appsServerName,ejbOverwrites,ejbSearchFolder1 + "," + ejbSearchFolder2);


        /************************************************************************************************
         *  WebOverwrites
         * *********************************************************************************************/
        String webSearchFolder1 = buildFolderName + "/" + projectName+ "Ear/" + projectName+ "WebApp/WEB-INF";
        String webSearchFolder2 = buildFolderName + "/" + projectName+ "Ear/" + projectName+ "CacheWebApp/WEB-INF";
        WebOverwrites webOverwrites = projectOverwrites.getWebOverwrites();
        new ApplyWebOverwrites(appsServerName,webOverwrites,webSearchFolder1 + "," + webSearchFolder2);


    }

    public class ApplyMenuOverwrites {

        public ApplyMenuOverwrites(MenuOverwrites menuOverwrites,String menuFolder) throws IOException,XRuntime,XmlException {
            if( menuOverwrites == null){
                return;
            }

            log( "Apply Menu Overwrites" );
            MenuBuilders menuBuilders =  menuOverwrites.getMenuBuilders();
            if( menuBuilders == null ){
                return;
            }
            MenuBuilder menuBuilderArray[] = menuBuilders.getMenuBuilderArray();
            if( menuBuilderArray != null && menuBuilderArray.length > 0){
                String extensions[] = {"xml"};
                Collection list = fileUtils.listFiles(new File(menuFolder),extensions, true);
                if( list!= null ){
                    File file = null;
                    for( Iterator iterator = list.iterator(); iterator.hasNext();) {
                        file =  (File) iterator.next();
                        MenuBuilder menuBuilder = null;
                        for( int i=0;i<menuBuilderArray.length;i++){
                            menuBuilder = menuBuilderArray[i];
                            if( file.getName().equalsIgnoreCase( menuBuilder.getName() ) ) {
                                buildMenu(file,menuBuilder);
                            }
                        }
                    }
                }
            }
        }

        public void buildMenu(File file,MenuBuilder menuBuilder) throws IOException,XRuntime,XmlException {
            /* Read Product menu xml File*/
            String valueStr = fileUtils.readFileToString( file );
            MenuDocument menuDocument = MenuDocument.Factory.parse( valueStr );
            /********************************************************************/
            ArrayList validationXmlErrors = new ArrayList();
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setErrorListener( validationXmlErrors );
            boolean isValid = menuDocument.validate( xmlOptions );
            if( !isValid ){
                for (int i=0; i < validationXmlErrors.size(); i++ ){
                    XmlError xmlError = (XmlError) validationXmlErrors.get(i);
                    System.out.println( xmlError.toString() );
                }
                System.exit(1);
            }
            /********************************************************************/
            Menu menu = menuDocument.getMenu();

            /*Apply MenuOverwrites -DeleteMenuItem,ModifyMenuItem,AddMenuItem)*/
            applyMenuOverwrites(menu,menuBuilder);

            /* Generate Menu */
            StringBuffer menuBuffer = new StringBuffer();
            generateMenu(menu.getMenuItemArray(),menuBuffer, "Menu" ,true);

            /* Read TPL File */
            String tplFileName = StringUtils.replaceOnce( file.getAbsolutePath() , file.getName() , menu.getTpl() );
            File tplFile = new File( tplFileName );
            String menuJsStr = fileUtils.readFileToString( tplFile ) + menuBuffer.toString();

            /* Replace  MENU_COUNT  in TPL File */
            menuJsStr = StringUtils.replaceOnce( menuJsStr , "@MENU_COUNT@",String.valueOf( menu.sizeOfMenuItemArray() ) );

            /* Create Menu JS File  */
            StringBuffer menuJSFileName = new StringBuffer();
            if( menuBuilder.isSetRole() && !StrUtl.isEmptyTrimmed( menuBuilder.getRole() ) ){
                menuJSFileName.append( menuBuilder.getRole() ).append( "_" );
            }
            menuJSFileName.append( menu.getName().toLowerCase() ).append( "_menu_var.js" );

            String jsFileName = StringUtils.replaceOnce( file.getAbsolutePath() , file.getName() , menuJSFileName.toString() );
            File jsFile = new File(  jsFileName );
            FileUtils.writeStringToFile(jsFile,menuJsStr);
        }

        private void generateMenu(MenuItem menuItems[],StringBuffer menuBuffer,String menuPrefix,boolean addtabLeftRight){
            if( menuItems != null && menuItems.length > 0){
                MenuItem menuItem = null;
                MenuItem childMenuItems[] = null;
                for(int i=0;i<menuItems.length;i++){

                    menuItem = menuItems[i];
                    childMenuItems = menuItem.getMenuItemArray();
                    int childMenuCount = (childMenuItems != null) ? childMenuItems.length : 0;
                    String url = StrUtl.isEmptyTrimmed( menuItem.getUrl() )? "" : menuItem.getUrl();
                    String menuCaption = addtabLeftRight ?  "tableft +\"" + menuItem.getCaption() + "\"+ tabright" : "\"" + menuItem.getCaption() + "\"";

                    menuBuffer.append( menuPrefix ).append( i + 1 ).append( " = new Array(" ).append( menuCaption )
                            .append( "," )
                            .append(  "\"" ).append( url ).append( "\"" )
                            .append( "," )
                            .append( "\"\"" ) //backgroundImage
                            .append( "," )
                            .append( childMenuCount )
                            .append( "," )
                            .append( menuItem.getHeight() )
                            .append( "," )
                            .append( menuItem.getWidth() )
                            .append( ");" )
                            .append( newLine );

                    generateMenu(childMenuItems,menuBuffer,"\t" + menuPrefix + (i +1) + "_" ,false);
                }
            }
        }

        private void applyMenuOverwrites(Menu menu,MenuBuilder menuBuilder){
            //Apply DeleteMenuItem
            MenuItem menuItem = null;
            DeleteMenuItem deleteMenuItems[] = menuBuilder.getDeleteMenuItemArray();
            if( deleteMenuItems != null && deleteMenuItems.length > 0){
                DeleteMenuItem deleteMenuItem = null;

                for(int i=0;i<deleteMenuItems.length;i++){
                    deleteMenuItem = deleteMenuItems[i];
                    MenuItem menuItems[] = menu.getMenuItemArray();
                    if( menuItems != null && menuItems.length > 0 ){
                        for(int j=0;j<menuItems.length;j++){
                            menuItem = menuItems[j];
                            if( deleteMenuItem.getName().equalsIgnoreCase( menuItem.getName() ) ){
                                menu.removeMenuItem( j );
                                log( "MenuItem : " + deleteMenuItem.getName()  + " is deleted.");
                            }
                            else {
                                deleteMenuItem(menuItem,deleteMenuItem);
                            }
                        }
                    }
                }
            }
            //Apply ModifyMenuItem
            ModifyMenuItem modifyMenuItems[] = menuBuilder.getModifyMenuItemArray();
            if( modifyMenuItems != null && modifyMenuItems.length > 0){
                ModifyMenuItem modifyMenuItem = null;
                for(int i=0;i<modifyMenuItems.length;i++){
                    modifyMenuItem = modifyMenuItems[i];
                    modifyMenuItem(menu.getMenuItemArray(),modifyMenuItem);
                }
            }

            //Apply AddMenuItem
            AddMenuItem addMenuItems[] = menuBuilder.getAddMenuItemArray();
            if( addMenuItems != null && addMenuItems.length > 0){
                AddMenuItem addMenuItem = null;

                for( int i=0;i<addMenuItems.length;i++){
                    addMenuItem = addMenuItems[i];

                    if( menu.getName().equalsIgnoreCase( addMenuItem.getParent() ) ){
                        MenuItem menuItems[] = menu.getMenuItemArray();

                        if( menuItems != null && menuItems.length > 0 ){
                            int insertIndex = menuItems.length;

                            for(int j=0;j<menuItems.length;j++){
                                menuItem = menuItems[j];
                                if( menuItem.getName().equalsIgnoreCase( addMenuItem.getPreviousMenu() ) ){
                                    insertIndex = j+1;
                                    break;
                                }
                            }

                            MenuItem newMenuItems[] = addMenuItem.getMenuItemArray();
                            for(int k=newMenuItems.length-1;k>=0;k--){
                                menu.insertNewMenuItem( insertIndex );
                                menu.setMenuItemArray(insertIndex,newMenuItems[ k ]);
                                log( "MenuItem : " + newMenuItems[ k ].getName()  + " is added.");

                            }
                        }
                    }
                    else {
                        addMenuItem(menu.getMenuItemArray(),addMenuItem);
                    }
                }
            }
        }


        private void deleteMenuItem(MenuItem parentMenuItem,DeleteMenuItem deleteMenuItem){
            MenuItem childMenuItems[] = parentMenuItem.getMenuItemArray();
            if( childMenuItems != null && childMenuItems.length > 0){
                MenuItem menuItem = null;
                for(int i=0;i<childMenuItems.length;i++){
                    menuItem = childMenuItems[i];
                    if( deleteMenuItem.getName().equalsIgnoreCase( menuItem.getName() ) ){
                        parentMenuItem.removeMenuItem( i );
                        log( "MenuItem : " + deleteMenuItem.getName()  + " is deleted.");
                    }
                    else if( menuItem.getMenuItemArray() != null ){
                        deleteMenuItem( menuItem , deleteMenuItem );
                    }
                }
            }
        }


        private void modifyMenuItem(MenuItem menuItems[],ModifyMenuItem modifyMenuItem){
            if( menuItems != null && menuItems.length > 0){
                MenuItem menuItem = null;
                for(int i=0;i<menuItems.length;i++){
                    menuItem = menuItems[i];
                    if( modifyMenuItem.getName().equalsIgnoreCase( menuItem.getName() ) ){
                        if( !StrUtl.isEmptyTrimmed( modifyMenuItem.getCaption() ) ){
                            menuItem.setCaption( modifyMenuItem.getCaption() );
                        }
                        if( !StrUtl.isEmptyTrimmed( modifyMenuItem.getUrl() ) ){
                            menuItem.setUrl( modifyMenuItem.getUrl() );
                        }
                        if( modifyMenuItem.getHeight() > 0 ){
                            menuItem.setHeight( modifyMenuItem.getHeight() );
                        }
                        if( modifyMenuItem.getWidth() > 0 ){
                            menuItem.setWidth( modifyMenuItem.getWidth() );
                        }
                        log( "MenuItem : " + modifyMenuItem.getName()  + " is modified.");
                    }
                    else {
                        modifyMenuItem( menuItem.getMenuItemArray() , modifyMenuItem );
                    }
                }
            }
        }

        private void addMenuItem(MenuItem menuItems[],AddMenuItem addMenuItem){
            if( menuItems != null && menuItems.length > 0){
                MenuItem menuItem = null;
                for(int i=0;i<menuItems.length;i++){
                    menuItem = menuItems[i];
                    if( menuItem.getName().equalsIgnoreCase( addMenuItem.getParent() ) ){

                        MenuItem childMenuItems[]  = menuItem.getMenuItemArray();
                        MenuItem childMenuItem = null;

                        int insertIndex = childMenuItems.length;

                        for(int j=0;j<childMenuItems.length;j++){
                            childMenuItem = childMenuItems[j];
                            if( childMenuItem.getName().equalsIgnoreCase( addMenuItem.getPreviousMenu() ) ){
                                insertIndex = j+1;
                                break;
                            }
                        }

                        MenuItem newMenuItems[] = addMenuItem.getMenuItemArray();
                        for(int k=newMenuItems.length-1;k>=0;k--){
                            menuItem.insertNewMenuItem( insertIndex );
                            menuItem.setMenuItemArray( insertIndex,newMenuItems[ k ] );
                            log( "MenuItem : " + newMenuItems[ k ].getName()  + " is added.");
                        }

                    }
                    else {
                        addMenuItem( menuItem.getMenuItemArray() ,  addMenuItem );
                    }
                }
            }
        }
    }

    public class ApplyCacheOverwrites {

        public ApplyCacheOverwrites(CacheOverwrites cacheOverwrites,String cacheFolder)  throws IOException,XRuntime,XmlException{
            if( cacheOverwrites == null ){
                return;
            }
            log( "Apply Cache Overwrites" );
            CacheBuilders cacheBuilders =  cacheOverwrites.getCacheBuilders();
            if( cacheBuilders == null ){
                return;
            }
            CacheBuilder cacheBuilderArray[] = cacheBuilders.getCacheBuilderArray();
            String extensions[] = {"xml"};
            Collection list = fileUtils.listFiles(new File(cacheFolder),extensions, true);
            if( list!= null ){
                HashMap daoSQLStatements  = new HashMap();
                File file = null;
                StringBuffer cacheSystemNames = new StringBuffer();
                for( Iterator iterator = list.iterator(); iterator.hasNext();) {
                    file =  (File) iterator.next();
                    CacheBuilder cacheBuilder = null;
                    for( int i=0;i<cacheBuilderArray.length;i++){
                        cacheBuilder = cacheBuilderArray[i];
                        if( file.getName().equalsIgnoreCase( cacheBuilder.getName() ) ) {
                            buildCache(file,cacheBuilder,cacheSystemNames,daoSQLStatements);
                        }
                    }
                }
                if( file != null){
                    /* Read TPL File */
                    String tplFileName = StringUtils.replaceOnce( file.getAbsolutePath() , file.getName() , cacheBuilders.getCacheenginetpl() );
                    File tplFile = new File( tplFileName );
                    String cacheengineStr = fileUtils.readFileToString( tplFile );

                    /* Replace  MENU_COUNT  in TPL File */
                    cacheengineStr = StringUtils.replaceOnce( cacheengineStr , "@CACHE_SYSTEM_NAMES@", cacheSystemNames.toString() );

                    /* Create cacheengine properties  */
                    String propertiesFileName = StringUtils.replaceOnce( tplFile.getAbsolutePath() , ".tpl" , "" );
                    File propertiesFile = new File(  propertiesFileName );
                    FileUtils.writeStringToFile(propertiesFile,cacheengineStr);


                    /* Read project_*.properties File */
                    String daoPropFileName = StringUtils.replaceOnce( file.getAbsolutePath() , file.getName() , cacheBuilders.getDaoproperties() );
                    File daoPropFile= new File( daoPropFileName );
                    String daoPropFileStr = fileUtils.readFileToString( daoPropFile );


                    /* Read DAO File */
                    String daoFileName = StringUtils.replaceOnce( file.getAbsolutePath() , file.getName() , cacheBuilders.getDaoxml() );
                    File daoFile= new File( daoFileName );
                    String daoXMlStr = fileUtils.readFileToString( daoFile );

                    /* Set DAO Lookup Property */
                    StringBuffer propertiesBuffer = new StringBuffer();
                    String cacheObjectName = null;
                    String daoSQLStatement = null;
                    String daoName = null;
                    for (Iterator iterator = daoSQLStatements.keySet().iterator(); iterator.hasNext();) {
                        cacheObjectName =  (String) iterator.next();
                        daoSQLStatement = (String) daoSQLStatements.get( cacheObjectName );
                        daoSQLStatement = StringUtils.trim( daoSQLStatement );
                        daoName = StringUtils.substringBetween(daoSQLStatement,"name=\"","\"");

                        int pos = StringUtils.indexOf(daoXMlStr,"\"" + daoName +"\"");
                        String partA = StringUtils.substring( daoXMlStr, 0, pos ) ;
                        String partB = StringUtils.substring( daoXMlStr, pos );

                        int startPos = StringUtils.lastIndexOf(partA, "<DAOSQLStatement");
                        int endPos = pos + StringUtils.indexOf(partB, "</DAOSQLStatement>") + "</DAOSQLStatement>".length();
                        String matchDAO =  StringUtils.substring(daoXMlStr,startPos,endPos);

                        int countMatches = StringUtils.countMatches( daoXMlStr , matchDAO );
                        if( countMatches > 0 ) {
                            log("CacheName : " + cacheObjectName + "\t ReplacedDAO  : " + daoName );
                            daoXMlStr = StringUtils.replaceOnce( daoXMlStr , matchDAO, daoSQLStatement);
                        }
                        else {
                            pos = StringUtils.lastIndexOf(daoXMlStr,"</DAOSQLConfiguration>");
                            String partFirst = StringUtils.substring( daoXMlStr, 0, pos ) ;
                            String partLast = StringUtils.substring( daoXMlStr, pos );
                            String partMid = newLine + daoSQLStatement;
                            daoXMlStr = partFirst + partMid + partLast;
                            log("CacheName : " + cacheObjectName + "\t AddedDAO  : " + daoName );
                        }
                        propertiesBuffer.append( newLine )
                                .append( cacheObjectName )
                                .append( ".LookupSql=" ).append( daoName );
                    }

                    daoPropFileStr = daoPropFileStr + propertiesBuffer.toString();
                    FileUtils.writeStringToFile(daoPropFile,daoPropFileStr);
                    FileUtils.writeStringToFile(daoFile,daoXMlStr);
                }
            }
        }


        public void buildCache(File file,CacheBuilder cacheBuilder,StringBuffer cacheSystemNames,HashMap daoSQLStatements) throws IOException,XRuntime,XmlException {
            /* Read Product cache xml File*/
            log("Cache Overwrites on xml "+ file.getName() );
            String valueStr = fileUtils.readFileToString( file );
            CacheDocument cacheDocument = CacheDocument.Factory.parse( valueStr );
            /********************************************************************/
            ArrayList validationXmlErrors = new ArrayList();
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setErrorListener( validationXmlErrors );
            boolean isValid = cacheDocument.validate( xmlOptions );
            if( !isValid ){
                for (int i=0; i < validationXmlErrors.size(); i++ ){
                    XmlError xmlError = (XmlError) validationXmlErrors.get(i);
                    System.out.println( xmlError.toString() );
                }
                System.exit(1);
            }
            /********************************************************************/

            Cache cache = cacheDocument.getCache();

            /*Apply CacheOverwrites -AddCacheItem,ModifyCacheItem,DeleteCacheItem)*/
            applyCacheOverwrites(cache,cacheBuilder);


            /* Generate Cache */
            StringBuffer cacheBuffer = new StringBuffer();
            generateCache(cache.getCacheItemArray(),cacheBuffer,daoSQLStatements);

            /* Create cache system properties  */
            String propertiesFileName = StringUtils.replaceOnce( file.getAbsolutePath() , ".xml" , ".properties" );
            File propertiesFile = new File(  propertiesFileName );
            FileUtils.writeStringToFile(propertiesFile,cacheBuffer.toString() );

            if( cacheSystemNames.length() > 0) {
                cacheSystemNames.append(",");
            }
            String cacheSystemName = StringUtils.replaceOnce( propertiesFile.getName() , ".properties" , "" );
            cacheSystemNames.append( cacheSystemName );
        }

        private void applyCacheOverwrites(Cache cache,CacheBuilder cacheBuilder){
            //Apply DeleteCacheItem
            CacheItem cacheItem = null;
            DeleteCacheItem deleteCacheItems[] = cacheBuilder.getDeleteCacheItemArray();
            if( deleteCacheItems != null && deleteCacheItems.length > 0){
                DeleteCacheItem deleteCacheItem = null;
                for(int i=0;i<deleteCacheItems.length;i++){
                    deleteCacheItem = deleteCacheItems[i];
                    CacheItem cacheItems[] = cache.getCacheItemArray();
                    if( cacheItems != null && cacheItems.length > 0 ){
                        for(int j=0;j<cacheItems.length;j++){
                            cacheItem = cacheItems[j];
                            if( deleteCacheItem.getName().equalsIgnoreCase( cacheItem.getName() ) ){
                                cache.removeCacheItem( j );
                                log( "CacheItem : " + deleteCacheItem.getName()  + " is deleted.");
                            }
                            else {
                                deleteCacheItem(cacheItem,deleteCacheItem);
                            }
                        }
                    }
                }
            }
            //Apply ModifyCacheItem
            ModifyCacheItem modifyCacheItems[] = cacheBuilder.getModifyCacheItemArray();
            if( modifyCacheItems != null && modifyCacheItems.length > 0){
                ModifyCacheItem modifyCacheItem = null;
                for(int i=0;i<modifyCacheItems.length;i++){
                    modifyCacheItem = modifyCacheItems[i];
                    modifyCacheItem(cache.getCacheItemArray(),modifyCacheItem);
                }
            }

            //Apply AddCacheItem
            AddCacheItem addCacheItems[] = cacheBuilder.getAddCacheItemArray();
            if( addCacheItems != null && addCacheItems.length > 0){
                AddCacheItem addCacheItem = null;

                for( int i=0;i<addCacheItems.length;i++){
                    addCacheItem = addCacheItems[i];

                    if( cache.getName().equalsIgnoreCase( addCacheItem.getParent() ) ){
                        CacheItem cacheItems[] = cache.getCacheItemArray();

                        if( cacheItems != null && cacheItems.length > 0 ){
                            int insertIndex = cacheItems.length;

                            for(int j=0;j<cacheItems.length;j++){
                                cacheItem = cacheItems[j];
                                if( cacheItem.getName().equalsIgnoreCase( addCacheItem.getPreviousCache() ) ){
                                    insertIndex = j+1;
                                    break;
                                }
                            }

                            CacheItem newCacheItems[] = addCacheItem.getCacheItemArray();
                            for(int k=newCacheItems.length-1;k>=0;k--){
                                cache.insertNewCacheItem( insertIndex );
                                cache.setCacheItemArray(insertIndex,newCacheItems[ k ]);
                                log( "CacheItem : " + newCacheItems[ k ].getName()  + " is added.");

                            }
                        }
                    }
                    else {
                        addCacheItem(cache.getCacheItemArray(),addCacheItem);
                    }
                }
            }
        }

        private void deleteCacheItem(CacheItem parentCacheItem,DeleteCacheItem deleteCacheItem){
            CacheItem childCacheItems[] = parentCacheItem.getCacheItemArray();
            if( childCacheItems != null && childCacheItems.length > 0){
                CacheItem cacheItem = null;
                for(int i=0;i<childCacheItems.length;i++){
                    cacheItem = childCacheItems[i];
                    if( deleteCacheItem.getName().equalsIgnoreCase( cacheItem.getName() ) ){
                        parentCacheItem.removeCacheItem( i );
                        log( "CacheItem : " + deleteCacheItem.getName()  + " is deleted.");
                    }
                    else if( cacheItem.getCacheItemArray() != null ){
                        deleteCacheItem( cacheItem , deleteCacheItem );
                    }
                }
            }
        }

        private void modifyCacheItem(CacheItem cacheItems[],ModifyCacheItem modifyCacheItem){
            if( cacheItems != null && cacheItems.length > 0){
                CacheItem cacheItem = null;
                for(int i=0;i<cacheItems.length;i++){
                    cacheItem = cacheItems[i];
                    if( modifyCacheItem.getName().equalsIgnoreCase( cacheItem.getName() ) ){
                        if( cacheItem.getCacheItemDetail() != null ){
                            cacheItem.getCacheItemDetail().setDAO( modifyCacheItem.getDAO() );
                            log( "CacheItem : " + modifyCacheItem.getName()  + " is modified.");
                        }
                    }
                    else {
                        modifyCacheItem( cacheItem.getCacheItemArray() , modifyCacheItem );
                    }
                }
            }
        }

        private void addCacheItem(CacheItem cacheItems[],AddCacheItem addCacheItem){
            if( cacheItems != null && cacheItems.length > 0){
                CacheItem cacheItem = null;
                for(int i=0;i<cacheItems.length;i++){
                    cacheItem = cacheItems[i];
                    if( cacheItem.getName().equalsIgnoreCase( addCacheItem.getParent() ) ){

                        CacheItem childCacheItems[]  = cacheItem.getCacheItemArray();
                        CacheItem childCacheItem = null;

                        int insertIndex = childCacheItems.length;

                        for(int j=0;j<childCacheItems.length;j++){
                            childCacheItem = childCacheItems[j];
                            if( childCacheItem.getName().equalsIgnoreCase( addCacheItem.getPreviousCache() ) ){
                                insertIndex = j+1;
                                break;
                            }
                        }

                        CacheItem newCacheItems[] = addCacheItem.getCacheItemArray();
                        for(int k=newCacheItems.length-1;k>=0;k--){
                            cacheItem.insertNewCacheItem( insertIndex );
                            cacheItem.setCacheItemArray( insertIndex,newCacheItems[ k ] );
                            log( "CacheItem : " + newCacheItems[ k ].getName()  + " is added.");
                        }

                    }
                    else {
                        addCacheItem( cacheItem.getCacheItemArray() ,  addCacheItem );
                    }
                }
            }
        }

        private void generateCache(CacheItem cacheItems[],StringBuffer cacheBuffer,HashMap daoSQLStatements){
            if( cacheItems == null || cacheItems.length == 0){
                return;
            }

            CacheItem cacheItem = null;
            CacheItem childCacheItems[] = null;

            cacheBuffer.append("cache.initializers=");
            for(int i=0;i<cacheItems.length;i++){
                cacheItem = cacheItems[i];
                if(i > 0){
                    cacheBuffer.append( "," );
                }
                cacheBuffer.append( cacheItem.getName() );
            }

            for(int i=0;i<cacheItems.length;i++){
                cacheItem = cacheItems[i];
                childCacheItems = cacheItem.getCacheItemArray();

                cacheBuffer.append( newLine );
                cacheBuffer.append( newLine ).append( "cache.initializers." ).append( cacheItem.getName() ).append( ".classname=" ).append( cacheItem.getClassname() );
                cacheBuffer.append( newLine ).append( "cache.initializers." ).append( cacheItem.getName() ).append( ".objects=");

                if( cacheItem.isSetCacheItemDetail() ){
                    cacheBuffer.append( cacheItem.getCacheItemDetail().getObjectname() );
                    if( cacheItem.getCacheItemDetail().getDAO() != null ){
                        daoSQLStatements.put( cacheItem.getCacheItemDetail().getObjectname() ,cacheItem.getCacheItemDetail().getDAO() );
                    }
                }
                else if( childCacheItems != null && childCacheItems.length > 0){
                    CacheItem childCacheItem = null;
                    for(int j=0;j<childCacheItems.length;j++){
                        childCacheItem = childCacheItems[j];
                        if( childCacheItem.isSetCacheItemDetail() ){
                            if( j > 0 ){
                                cacheBuffer.append( "," );
                            }
                            cacheBuffer.append( childCacheItem.getCacheItemDetail().getObjectname() );

                            if( childCacheItem.getCacheItemDetail().getDAO() != null ){
                                daoSQLStatements.put( childCacheItem.getCacheItemDetail().getObjectname() ,childCacheItem.getCacheItemDetail().getDAO() );
                            }

                        }
                    }
                }
            }
        }
    }

    public class ApplyPropertiesOverwrites {

        public ApplyPropertiesOverwrites(PropertiesOverwrites propertiesOverwrites,String searchFolder) throws IOException,XRuntime {
            if( propertiesOverwrites == null ){
                return;
            }

            log( "Apply Properties Overwrites" );
            DeleteProperty deletePropertyArray[] = propertiesOverwrites.getDeletePropertyArray();
            ModifyProperty modifyPropertyArray[] = propertiesOverwrites.getModifyPropertyArray();
            AddProperty addPropertyArray[] = propertiesOverwrites.getAddPropertyArray();

            if ( ( deletePropertyArray != null && deletePropertyArray.length > 0 ) || ( modifyPropertyArray != null && modifyPropertyArray.length > 0 ) || ( addPropertyArray != null && addPropertyArray.length > 0 ) ){

                String extensions[] = {"properties","tpl"};
                String searchFolders[] = searchFolder.split(",");

                File file = null;
                String propFileValuesStr = null;
                int countMatches = 0;

                DeleteProperty deleteProperty = null;
                ModifyProperty modifyProperty = null;
                AddProperty addProperty = null;

                for(int i=0;i<searchFolders.length;i++){
                    Collection list = fileUtils.listFiles(new File(searchFolders[ i ]),extensions, true);
                    if( list!= null ){
                        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                            file =  (File) iterator.next();
                            if( file.getName().endsWith( ".tpl" ) && file.getName().indexOf(".properties") == -1 ){
                                continue;
                            }
                            /* Read File */
                            propFileValuesStr = fileUtils.readFileToString( file );

                            /* Apply DeleteProperty*/
                            if( deletePropertyArray != null && deletePropertyArray.length > 0 ){
                                for(int j=0;j<deletePropertyArray.length;j++){
                                    deleteProperty = deletePropertyArray[j];
                                    if( !StrUtl.isEmptyTrimmed( deleteProperty.getFilename() ) &&  !file.getName().equalsIgnoreCase( deleteProperty.getFilename() ) ){
                                        continue;
                                    }
                                    countMatches = StringUtils.countMatches( propFileValuesStr , deleteProperty.getName() );
                                    if( countMatches > 0 ) {
                                        String propertyValue = StringUtils.substringBetween( propFileValuesStr , deleteProperty.getName(),"\n");
                                        if( propertyValue != null ){
                                            String matchPropertyPair = deleteProperty.getName() + propertyValue;
                                            countMatches = StringUtils.countMatches( propFileValuesStr , matchPropertyPair );
                                            if( countMatches > 0 ) {
                                                log("\nFile             : " + file.getAbsolutePath());
                                                log( "DeleteProperty    : " + matchPropertyPair );
                                                propFileValuesStr = StringUtils.replaceOnce( propFileValuesStr , matchPropertyPair, "" );
                                            }
                                        }
                                    }
                                }
                            }

                            /* Apply ModifyProperty */
                            if( modifyPropertyArray != null && modifyPropertyArray.length > 0 ){
                                for(int j=0;j<modifyPropertyArray.length;j++){
                                    modifyProperty = modifyPropertyArray[j];
                                    if( !StrUtl.isEmptyTrimmed( modifyProperty.getFilename() ) &&  !file.getName().equalsIgnoreCase( modifyProperty.getFilename() ) ){
                                        continue;
                                    }
                                    countMatches = StringUtils.countMatches( propFileValuesStr , modifyProperty.getName() );
                                    if( countMatches > 0 ) {
                                        String propertyValue = StringUtils.substringBetween( propFileValuesStr , modifyProperty.getName(),"\n");
                                        if( propertyValue != null ){
                                            String matchPropertyPair = modifyProperty.getName() + propertyValue;
                                            countMatches = StringUtils.countMatches( propFileValuesStr , matchPropertyPair );
                                            if( countMatches > 0 ) {
                                                String replacePropertyPair = modifyProperty.getName() + "=" + modifyProperty.getValue();
                                                log("\nFile             : " + file.getAbsolutePath());
                                                log( "ModifyProperty    : " + replacePropertyPair );
                                                propFileValuesStr = StringUtils.replaceOnce( propFileValuesStr , matchPropertyPair, replacePropertyPair );
                                            }
                                        }
                                    }
                                }
                            }

                            /* Apply AddProperty */
                            if( addPropertyArray != null && addPropertyArray.length > 0 ){
                                for(int j=0;j<addPropertyArray.length;j++){
                                    addProperty = addPropertyArray[j];
                                    if( !StrUtl.isEmptyTrimmed( addProperty.getFilename() ) &&  !file.getName().equalsIgnoreCase( addProperty.getFilename() ) ){
                                        continue;
                                    }
                                    countMatches = StringUtils.countMatches( propFileValuesStr , addProperty.getName() );
                                    if( countMatches > 0 ) {
                                        throw new XRuntime("AddProperty", addProperty.getName() +" already exists in file " + file.getAbsolutePath() );
                                    }
                                    else {
                                        String addPropertyPair = addProperty.getName() + "=" + addProperty.getValue();
                                        log("\nFile         : " + file.getAbsolutePath());
                                        log( "AddProperty   : " + addPropertyPair);
                                        propFileValuesStr += newLine  + addPropertyPair;
                                    }
                                }
                            }
                            /* Write File */
                            FileUtils.writeStringToFile(file,propFileValuesStr);
                        }
                    }
                }
            }

            log( "Apply XML TPL Overwrites" );
            DeleteXmlProperty deleteXmlPropertyArray[] = propertiesOverwrites.getDeleteXmlPropertyArray();
            ModifyXmlProperty modifyXmlPropertyArray[] = propertiesOverwrites.getModifyXmlPropertyArray();
            AddXmlProperty      addXmlPropertyArray[]    = propertiesOverwrites.getAddXmlPropertyArray();
            if ( ( deleteXmlPropertyArray != null && deleteXmlPropertyArray.length > 0 ) || ( modifyXmlPropertyArray != null && modifyXmlPropertyArray.length > 0 ) || ( addXmlPropertyArray != null && addXmlPropertyArray.length > 0 ) ){
                String extensions[] = {"tpl"};
                String searchFolders[] = searchFolder.split(",");

                File file = null;
                String tplFileValuesStr = null;
                int countMatches = 0;

                DeleteXmlProperty deleteXmlProperty = null;
                ModifyXmlProperty modifyXmlProperty = null;
                AddXmlProperty addXmlProperty = null;

                for(int i=0;i<searchFolders.length;i++){
                    Collection list = fileUtils.listFiles(new File(searchFolders[ i ]),extensions, true);
                    if( list!= null ){
                        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                            file =  (File) iterator.next();
                            if( file.getName().endsWith( ".tpl" ) && file.getName().indexOf(".xml") == -1 ){
                                continue;
                            }
                            /* Read File */
                            tplFileValuesStr = fileUtils.readFileToString( file );

                            /* Apply DeleteXmlProperty*/
                            if( deleteXmlPropertyArray != null && deleteXmlPropertyArray.length > 0 ){
                                new XRuntime(getClass().getName(),"DeleteXmlProperty not implemented") ;
                            }

                            /* Apply ModifyXmlProperty */
                            if( modifyXmlPropertyArray != null && modifyXmlPropertyArray.length > 0 ){
                                new XRuntime(getClass().getName(),"ModifyXmlProperty not implemented") ;
                            }

                            /* Apply AddXmlProperty */
                            if( addXmlPropertyArray != null && addXmlPropertyArray.length > 0){
                                for(int j=0;j<addXmlPropertyArray.length;j++){
                                    addXmlProperty = addXmlPropertyArray[j];

                                    if( !StrUtl.isEmptyTrimmed( addXmlProperty.getFilename() ) &&  !file.getName().equalsIgnoreCase( addXmlProperty.getFilename() ) ){
                                        continue;
                                    }

                                    String prevEndTag = "</" + addXmlProperty.getPrevTagName() +">";
                                    countMatches = StringUtils.countMatches( tplFileValuesStr , prevEndTag);
                                    if( countMatches > 0 ){
                                        String addTagValue =  addXmlProperty.getValue();
                                        log("\nFile           : " + file.getAbsolutePath());
                                        log( "AddXmlProperty    : " +  addTagValue );
                                        tplFileValuesStr = StringUtils.replaceOnce( tplFileValuesStr , prevEndTag , prevEndTag + newLine +addTagValue);
                                    }
                                }
                            }
                            /* Write File */
                            FileUtils.writeStringToFile(file,tplFileValuesStr);
                        }
                    }
                }
            }

        }
    }

    public class ApplyDAOOverwrites{

        public ApplyDAOOverwrites(DAOOverwrites daoOverwrites,String searchFolder) throws IOException,XRuntime {
            if( daoOverwrites == null ){
                return;
            }
            log( "Apply DAO Overwrites" );
            DeleteDAO deleteDAOArray[] = daoOverwrites.getDeleteDAOArray();
            ModifyDAO modifyDAOArray[] = daoOverwrites.getModifyDAOArray();
            AddDAO addDAOArray[] = daoOverwrites.getAddDAOArray();

            if ( ( deleteDAOArray != null && deleteDAOArray.length > 0 ) || ( modifyDAOArray != null && modifyDAOArray.length > 0 ) || ( addDAOArray != null && addDAOArray.length > 0 ) ){

                String extensions[] = {"xml"};
                String searchFolders[] = searchFolder.split(",");

                File file = null;
                String daoFileValuesStr = null;
                int countMatches = 0;

                DeleteDAO deleteDAO = null;
                ModifyDAO modifyDAO = null;
                AddDAO addDAO = null;


                for(int i=0;i<searchFolders.length;i++){
                    Collection list = fileUtils.listFiles(new File(searchFolders[ i ]),extensions, true);
                    if( list!= null ){
                        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                            file =  (File) iterator.next();
                            /* Read File */
                            daoFileValuesStr = fileUtils.readFileToString( file );

                            /* Apply DeleteDAO*/
                            if( deleteDAOArray != null && deleteDAOArray.length > 0 ){
                                for(int j=0;j<deleteDAOArray.length;j++){
                                    deleteDAO = deleteDAOArray[j];
                                    if( !StrUtl.isEmptyTrimmed( deleteDAO.getFilename() ) &&  !file.getName().equalsIgnoreCase( deleteDAO.getFilename() ) ){
                                        continue;
                                    }
                                    String daoName = "\"" + deleteDAO.getName() +"\"";
                                    countMatches = StringUtils.countMatches( daoFileValuesStr , daoName );
                                    if( countMatches > 0 ) {
                                        int pos = StringUtils.indexOf(daoFileValuesStr,daoName);
                                        String partA = StringUtils.substring( daoFileValuesStr, 0, pos ) ;
                                        String partB = StringUtils.substring( daoFileValuesStr, pos );

                                        int startPos = StringUtils.lastIndexOf(partA, "<DAOSQLStatement");
                                        int endPos = pos + StringUtils.indexOf(partB, "</DAOSQLStatement>") + "</DAOSQLStatement>".length();
                                        String matchDAO =  StringUtils.substring(daoFileValuesStr,startPos,endPos);

                                        countMatches = StringUtils.countMatches( daoFileValuesStr , matchDAO );
                                        if( countMatches > 0 ) {
                                            log("\nFile        : " + file.getAbsolutePath());
                                            log( "DeleteDAO    : " + matchDAO );
                                            daoFileValuesStr = StringUtils.replaceOnce( daoFileValuesStr , matchDAO, "" );
                                        }
                                    }
                                }
                            }

                            /* Apply ModifyDAO */
                            if( modifyDAOArray != null && modifyDAOArray.length > 0 ){
                                for(int j=0;j<modifyDAOArray.length;j++){
                                    modifyDAO = modifyDAOArray[j];
                                    if( !StrUtl.isEmptyTrimmed( modifyDAO.getFilename() ) &&  !file.getName().equalsIgnoreCase( modifyDAO.getFilename() ) ){
                                        continue;
                                    }
                                    String daoName = "\"" + modifyDAO.getName() +"\"";
                                    countMatches = StringUtils.countMatches( daoFileValuesStr , daoName );
                                    if( countMatches > 0 ) {
                                        int pos = StringUtils.indexOf(daoFileValuesStr,daoName);
                                        String partA = StringUtils.substring( daoFileValuesStr, 0, pos ) ;
                                        String partB = StringUtils.substring( daoFileValuesStr, pos );

                                        int startPos = StringUtils.lastIndexOf(partA, "<DAOSQLStatement");
                                        int endPos = pos + StringUtils.indexOf(partB, "</DAOSQLStatement>") + "</DAOSQLStatement>".length();
                                        String matchDAO =  StringUtils.substring(daoFileValuesStr,startPos,endPos);

                                        countMatches = StringUtils.countMatches( daoFileValuesStr , matchDAO );
                                        if( countMatches > 0 ) {
                                            log("\nFile        : " + file.getAbsolutePath());
                                            log( "ModifyDAO    : " + modifyDAO.getValue() );
                                            daoFileValuesStr = StringUtils.replaceOnce( daoFileValuesStr , matchDAO,StringUtils.trim( modifyDAO.getValue() ) );
                                        }
                                    }
                                }
                            }

                            /* Apply AddDAO */
                            if( addDAOArray != null && addDAOArray.length > 0 ){
                                for(int j=0;j<addDAOArray.length;j++){
                                    addDAO = addDAOArray[j];
                                    if( !StrUtl.isEmptyTrimmed( addDAO.getFilename() ) &&  !file.getName().equalsIgnoreCase( addDAO.getFilename() ) ){
                                        continue;
                                    }
                                    String daoName = "\"" + addDAO.getName() +"\"";
                                    countMatches = StringUtils.countMatches( daoFileValuesStr , daoName );
                                    if( countMatches > 0 ) {
                                        throw new XRuntime("AddDAO", addDAO.getName() +" already exists in file " + file.getAbsolutePath() );
                                    }
                                    else {
                                        int pos = StringUtils.lastIndexOf(daoFileValuesStr,"</DAOSQLConfiguration>");
                                        String partFirst = StringUtils.substring( daoFileValuesStr, 0, pos ) ;
                                        String partLast = StringUtils.substring( daoFileValuesStr, pos );
                                        String partMid = newLine + StringUtils.trim( addDAO.getValue() );
                                        daoFileValuesStr = partFirst + partMid + partLast;
                                        log("\nFile    : " + file.getAbsolutePath());
                                        log( "AddDAO   : " + addDAO.getValue());
                                    }
                                }
                           }
                           /* Write File */
                           FileUtils.writeStringToFile(file,daoFileValuesStr);
                        }
                    }
                }
            }
        }
    }

    public class ApplyEJBOverwrites {

        public ApplyEJBOverwrites(String appsServerName,EJBOverwrites ejbOverwrites,String searchFolder)  throws IOException,XRuntime,XmlException{
            if( ejbOverwrites == null ){
                return;
            }
            log( "Apply EJB Overwrites" );
            EjbBuilders ejbBuilders =  ejbOverwrites.getEjbBuilders();
            if( ejbBuilders == null ){
                return;
            }
            String searchFolders[] = searchFolder.split(",");
            for(int i=0;i<searchFolders.length;i++){
                EjbBuilder ejbBuilderArray[] = ejbBuilders.getEjbBuilderArray();
                String extensions[] = {"xml"};
                Collection list = fileUtils.listFiles(new File( searchFolders[i] ),extensions, true);

                if( list!= null ){
                    File file = null;
                    for( Iterator iterator = list.iterator(); iterator.hasNext();) {
                        file =  (File) iterator.next();
                        EjbBuilder ejbBuilder = null;
                        for( int j=0;j<ejbBuilderArray.length;j++){
                            ejbBuilder = ejbBuilderArray[j];
                            if( file.getName().equalsIgnoreCase( ejbBuilder.getName() ) ) {
                                buildEjbConfig(appsServerName,file,ejbBuilder);
                            }
                        }
                    }
                }
            }
        }

        public void buildEjbConfig(String appsServerName,File file,EjbBuilder ejbBuilder) throws IOException,XRuntime,XmlException {
            /* Read Product cache xml File*/
            log("EJB Overwrites on xml "+ file.getName() );
            String valueStr = fileUtils.readFileToString( file );
            EjbContainerDocument ejbContainerDoc = EjbContainerDocument.Factory.parse( valueStr );
            /********************************************************************/
            ArrayList validationXmlErrors = new ArrayList();
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setErrorListener( validationXmlErrors );
            boolean isValid = ejbContainerDoc.validate( xmlOptions );
            if( !isValid ){
                for (int i=0; i < validationXmlErrors.size(); i++ ){
                    XmlError xmlError = (XmlError) validationXmlErrors.get(i);
                    System.out.println( xmlError.toString() );
                }
                System.exit(1);
            }
            /********************************************************************/

            EjbContainer ejbContainer = ejbContainerDoc.getEjbContainer();

            /*Apply EjbOverwrites -AddEjbBean,ModifyEjbBean,DeleteEjbBean)*/
            EjbOverwritesUtils utils = new EjbOverwritesUtils();
            utils.applyEjbBeanOverwrites(ejbContainer,ejbBuilder);

            /* Generate EJB Config */
            generateEjbConfig(appsServerName,file,ejbContainer.getEjbBeanArray());

        }


        private void generateEjbConfig(String appsServerName,File file,EjbBean ejbBeans[])  throws IOException {
            if( ejbBeans == null || ejbBeans.length == 0){
                return;
            }
            //ejb-jar.xml
            StringBuffer beansBuffer = new StringBuffer();
            HashMap transactionMap = new HashMap();

            //weblogic-ejb-jar.xml
            StringBuffer weblogicBeansBuffer = new StringBuffer();

            //ibm-ejb-jar-bnd.xmi
            StringBuffer websphereBindingBuffer = new StringBuffer();

            //ibm-ejb-jar-ext.xmi
            StringBuffer websphereExtensionBuffer = new StringBuffer();

            EjbBean ejbBean = null;
            String enterpriseBean = null;
            String ejbName = null;
            ArrayList transactions = null;
            String transAttribute = null;

            for(int i=0;i<ejbBeans.length;i++){
                ejbBean = ejbBeans[i];
                transAttribute = ejbBean.getTransAttribute().toString() ;
                StringBuffer transactionBuffer = new StringBuffer();

                if( ejbBean.isSetCopies() ) {
                    for(int j=0;j<ejbBean.getCopies();j++){
                        enterpriseBean = ejbBean.getEnterpriseBean();
                        enterpriseBean = StringUtils.replace(enterpriseBean,"@INDEX@",String.valueOf( j ));
                        beansBuffer.append( newLine ). append( StringUtils.trim( enterpriseBean  ) );

                        ejbName = StringUtils.substringBetween(enterpriseBean,"<ejb-name>","</ejb-name>");

                        transactionBuffer.append( newLine ).append( "<method>" )
                                            .append( newLine ).append( "<ejb-name>" ).append( ejbName ).append( "</ejb-name>" )
                                            .append( newLine ).append( "<method-intf>Remote</method-intf>" )
                                            .append( newLine ).append( "<method-name>*</method-name>" )
                                         .append( newLine ).append( "</method>" )   ;


                    }
                }
                else {
                    enterpriseBean = ejbBean.getEnterpriseBean();
                    beansBuffer.append( newLine ). append( StringUtils.trim( enterpriseBean  ) );

                    ejbName = StringUtils.substringBetween(enterpriseBean,"<ejb-name>","</ejb-name>");
                    transactionBuffer.append( newLine ).append( "<method>" )
                                        .append( newLine ).append( "<ejb-name>" ).append( ejbName ).append( "</ejb-name>" )
                                        .append( newLine ).append( "<method-intf>Remote</method-intf>" )
                                        .append( newLine ).append( "<method-name>*</method-name>" )
                                     .append( newLine ).append( "</method>" )   ;

                }

                transactions = (ArrayList) transactionMap.get( transAttribute );
                if(  transactions == null ){
                    transactions = new ArrayList();
                    transactionMap.put( transAttribute , transactions );
                }
                transactions.add( transactionBuffer.toString() );

                if( WEBLOGIC_APPS_SERVER.equalsIgnoreCase( appsServerName ) ){
                    if( ejbBean.isSetCopies() ) {

                        for(int j=0;j<ejbBean.getCopies();j++){
                            String weblogicBean = StringUtils.trim( ejbBean.getWeblogic() );
                            weblogicBean = StringUtils.replace(weblogicBean,"@INDEX@",String.valueOf( j ));
                            weblogicBeansBuffer.append( newLine ).append( weblogicBean );
                        }
                    }
                    else {
                        weblogicBeansBuffer.append( newLine ).append( StringUtils.trim( ejbBean.getWeblogic() ));
                    }
                }
                else if( WEBSPHERE_APPS_SERVER.equalsIgnoreCase( appsServerName ) ){
                    if( ejbBean.isSetCopies() ) {

                        for(int j=0;j<ejbBean.getCopies();j++){
                            String websphereBinding = StringUtils.trim( ejbBean.getWebsphereBinding() );
                            websphereBinding = StringUtils.replace(websphereBinding ,"@INDEX@",String.valueOf( j ));

                            String websphereExtension = StringUtils.trim( ejbBean.getWebsphereExtension() );
                            websphereExtension = StringUtils.replace(websphereExtension ,"@INDEX@",String.valueOf( j ));

                            websphereBindingBuffer.append( newLine ).append( websphereBinding );
                            websphereExtensionBuffer.append( newLine ).append( websphereExtension );
                        }
                    }
                    else {
                        websphereBindingBuffer.append( newLine ).append( StringUtils.trim( ejbBean.getWebsphereBinding() ) );
                        websphereExtensionBuffer.append( newLine ).append( StringUtils.trim( ejbBean.getWebsphereExtension() ) );
                    }
                }
            }

            /* Read ejb-jar.xml Template*/
            String ejbJarTplFileName = StringUtils.replaceOnce( file.getAbsolutePath() , file.getName() , "ejb-jar.xml.tpl" );
            File ejbJarTplFile = new File(  ejbJarTplFileName );
            String ejbJarFileStr = fileUtils.readFileToString( ejbJarTplFile );

            ejbJarFileStr = StringUtils.replaceOnce( ejbJarFileStr , "@ENTERPRISE-BEANS@", beansBuffer.toString() );

            StringBuffer transactionBuffer = new StringBuffer();
            for (Iterator iterator = transactionMap.keySet().iterator(); iterator.hasNext();) {
                transAttribute = (String) iterator.next();
                transactions = (ArrayList) transactionMap.get( transAttribute );
                if( transactions.size() > 0){
                    transactionBuffer.append( newLine ).append( "<container-transaction><!--*********** ").append( transAttribute.toUpperCase() ).append( "***********-->" );
                    for(int i=0;i<transactions.size();i++){
                        transactionBuffer.append( transactions.get( i ) );
                    }
                    transactionBuffer.append( newLine ).append( "<trans-attribute>").append( transAttribute ).append( "</trans-attribute>" )
                                     .append( newLine ).append( "</container-transaction>" );
                }
            }

            ejbJarFileStr = StringUtils.replaceOnce( ejbJarFileStr , "@CONTAINER-TRANSACTION@", transactionBuffer.toString() );

            /* Write ejb-jar.xml*/
            String ejbJarFileName = StringUtils.replaceOnce( ejbJarTplFile.getAbsolutePath() , ".tpl", "" );
            File ejbJarFile = new File(  ejbJarFileName );
            FileUtils.writeStringToFile(ejbJarFile,ejbJarFileStr);

            if( WEBLOGIC_APPS_SERVER.equalsIgnoreCase( appsServerName ) ){
                /* Read weblogic-ejb-jar.xml Template*/
                String weblogicEjbJarTplFileName = StringUtils.replaceOnce( file.getAbsolutePath() , file.getName() , "weblogic-ejb-jar.xml.tpl" );
                File weblogicEjbJarTplFile = new File(  weblogicEjbJarTplFileName );
                String weblogicEjbJarFileStr = fileUtils.readFileToString( weblogicEjbJarTplFile );

                weblogicEjbJarFileStr = StringUtils.replaceOnce( weblogicEjbJarFileStr , "@WEBLOGIC-ENTERPRISE-BEAN@", weblogicBeansBuffer.toString() );

                /* Write weblogic-ejb-jar.xml */
                String weblogicEjbJarFileName = StringUtils.replaceOnce( weblogicEjbJarTplFile.getAbsolutePath() , ".tpl", "" );
                File weblogicEjbJarFile = new File(  weblogicEjbJarFileName );
                FileUtils.writeStringToFile(weblogicEjbJarFile,weblogicEjbJarFileStr);

            }
            else if( WEBSPHERE_APPS_SERVER.equalsIgnoreCase( appsServerName ) ){
                /* Read ibm-ejb-jar-bnd.xmi Template*/
                String ibmbndTplFileName = StringUtils.replaceOnce( file.getAbsolutePath() , file.getName() , "ibm-ejb-jar-bnd.xmi.tpl" );
                File ibmbndTplFile = new File(  ibmbndTplFileName );
                String ibmbndTplFileStr = fileUtils.readFileToString( ibmbndTplFile );

                ibmbndTplFileStr = StringUtils.replaceOnce( ibmbndTplFileStr , "@EJBBINDINGS@", websphereBindingBuffer.toString() );

                /* Write ibm-ejb-jar-bnd.xmi*/
                String ibmbndFileName = StringUtils.replaceOnce( ibmbndTplFile.getAbsolutePath() , ".tpl", "" );
                File ibmbndFile = new File(  ibmbndFileName );
                FileUtils.writeStringToFile(ibmbndFile,ibmbndTplFileStr);

                /* Read ibm-ejb-jar-bnd.xmi Template*/
                String ibmextTplFileName = StringUtils.replaceOnce( file.getAbsolutePath() , file.getName() , "ibm-ejb-jar-ext.xmi.tpl" );
                File ibmextTplFile = new File(  ibmextTplFileName );
                String ibmextTplFileStr = fileUtils.readFileToString( ibmextTplFile );

                ibmextTplFileStr = StringUtils.replaceOnce( ibmextTplFileStr , "@EJBEXTENSIONS@", websphereExtensionBuffer.toString() );

                /* Write ibm-ejb-jar-ext.xmi */
                String ibmextFileName = StringUtils.replaceOnce( ibmextTplFile.getAbsolutePath() , ".tpl", "" );
                File ibmextFile = new File(  ibmextFileName );
                FileUtils.writeStringToFile(ibmextFile,ibmextTplFileStr);
            }
        }
    }

    public class EjbOverwritesUtils{

        public EjbOverwritesUtils(){

        }

        private void applyEjbBeanOverwrites(Object container,Object builder){
            WebContainer webContainer = (container instanceof WebContainer)? (WebContainer)container : null;
            EjbContainer ejbContainer = (container instanceof EjbContainer)? (EjbContainer)container : null;
            WebBuilder webBuilder = (builder instanceof WebBuilder)? (WebBuilder)builder : null;
            EjbBuilder ejbBuilder = (builder instanceof EjbBuilder)? (EjbBuilder)builder : null;

            //Apply DeleteEjbBean
            EjbBean ejbBean = null;
            DeleteEjbBean deleteEjbBeans[] = (webBuilder !=null) ? webBuilder.getDeleteEjbBeanArray() : (ejbBuilder != null) ? ejbBuilder.getDeleteEjbBeanArray() : null;
            if( deleteEjbBeans != null && deleteEjbBeans.length > 0){
                DeleteEjbBean deleteEjbBean = null;
                for(int i=0;i<deleteEjbBeans.length;i++){
                    deleteEjbBean = deleteEjbBeans[i];
                    EjbBean ejbBeans[] =  (webContainer != null)? webContainer.getEjbBeanArray() : (ejbContainer != null) ? ejbContainer.getEjbBeanArray() : null;
                    if( ejbBeans != null && ejbBeans.length > 0 ){
                        for(int j=0;j<ejbBeans.length;j++){
                            ejbBean = ejbBeans[j];
                            if( deleteEjbBean.getName().equalsIgnoreCase( ejbBean.getName() ) ){
                                if( webContainer != null ){
                                    webContainer.removeEjbBean( j );
                                }
                                if( ejbContainer != null ){
                                    ejbContainer.removeEjbBean( j );
                                }
                                log( "EjbBean : " + deleteEjbBean.getName()  + " is deleted.");
                            }
                        }
                    }
                }
            }
            //Apply ModifyEjbBean
            ModifyEjbBean modifyEjbBeans[] = (webBuilder !=null) ? webBuilder.getModifyEjbBeanArray() : (ejbBuilder != null) ? ejbBuilder.getModifyEjbBeanArray() : null;
            if( modifyEjbBeans != null && modifyEjbBeans.length > 0){
                ModifyEjbBean modifyEjbBean = null;
                for(int i=0;i<modifyEjbBeans.length;i++){
                    modifyEjbBean = modifyEjbBeans[i];
                    EjbBean ejbBeans[] =  (webContainer != null)? webContainer.getEjbBeanArray() : (ejbContainer != null) ? ejbContainer.getEjbBeanArray() : null;
                    if( ejbBeans != null && ejbBeans.length > 0 ){
                        for(int j=0;j<ejbBeans.length;j++){
                            ejbBean = ejbBeans[j];
                            if( modifyEjbBean.getName().equalsIgnoreCase( ejbBean.getName() ) ){

                                if( modifyEjbBean.isSetTransAttribute() ) {
                                    ejbBean.setTransAttribute( modifyEjbBean.getTransAttribute() );
                                }
                                if( modifyEjbBean.isSetCopies() ) {
                                    ejbBean.setCopies( modifyEjbBean.getCopies() );
                                }
                                if( modifyEjbBean.isSetEnterpriseBean() ){
                                    String newValue =  alterTag(modifyEjbBean.getEnterpriseBean(),ejbBean.getEnterpriseBean() );
                                    ejbBean.setEnterpriseBean( newValue );
                                }

                                if( modifyEjbBean.isSetWeblogic() ){
                                    String newValue =  alterTag(modifyEjbBean.getWeblogic(),ejbBean.getWeblogic() );
                                    ejbBean.setWeblogic( newValue );
                                }

                                if( modifyEjbBean.isSetWebsphereBinding() ){
                                    String newValue =  alterTag(modifyEjbBean.getWebsphereBinding(),ejbBean.getWebsphereBinding() );
                                    ejbBean.setWebsphereBinding( newValue );
                                }

                                if( modifyEjbBean.isSetWebsphereExtension() ){
                                    String newValue =  alterTag(modifyEjbBean.getWebsphereExtension(),ejbBean.getWebsphereExtension() );
                                    ejbBean.setWebsphereExtension( newValue );
                                }

                                log( "EjbBean : " + modifyEjbBean.getName()  + " is modified.");

                            }
                        }
                    }
                }
            }
            //Apply AddEjbBean
            AddEjbBean addEjbBeans[] = (webBuilder !=null) ? webBuilder.getAddEjbBeanArray() : (ejbBuilder != null) ? ejbBuilder.getAddEjbBeanArray() : null;
            if( addEjbBeans != null && addEjbBeans.length > 0){
                AddEjbBean addEjbBean = null;
                for(int i=0;i<addEjbBeans.length;i++){
                    addEjbBean = addEjbBeans[i];
                    EjbBean ejbBeans[] =  (webContainer != null)? webContainer.getEjbBeanArray() : (ejbContainer != null) ? ejbContainer.getEjbBeanArray() : null;
                    if( ejbBeans != null && ejbBeans.length > 0 ){
                        for(int j=0;j<ejbBeans.length;j++){
                            ejbBean = ejbBeans[j];
                            if( addEjbBean.getEjbBean().getName().equalsIgnoreCase( ejbBean.getName() ) ){
                                throw new XRuntime("AddEjbBean", ejbBean.getName() +" already exists" );
                            }
                        }
                    }

                    if( webContainer != null ){
                        int insertIndex = webContainer.sizeOfEjbBeanArray();
                        webContainer.insertNewEjbBean( insertIndex );
                        webContainer.setEjbBeanArray( insertIndex , addEjbBean.getEjbBean() );
                    }
                    if( ejbContainer != null ){
                        int insertIndex = ejbContainer.sizeOfEjbBeanArray();
                        ejbContainer.insertNewEjbBean( insertIndex );
                        ejbContainer.setEjbBeanArray( insertIndex , addEjbBean.getEjbBean() );
                    }
                    log( "EjbBean : " + addEjbBean.getEjbBean().getName()  + " is added.");
                }
            }
        }

        private String alterTag(AlterTag alterTag,String value){
            DeleteTag deleteTags[]  =  alterTag.getDeleteTagArray();
            ModifyTag modifyTags[]  =  alterTag.getModifyTagArray();
            AddTag    addTags[]     =  alterTag.getAddTagArray();

            DeleteTag deleteTag = null;
            ModifyTag modifyTag = null;
            AddTag    addTag = null;

            int countMatches = 0;
            String tagStartString = null;
            String tagEndString = null;
            String foundStr = null;

            /* Apply DeleteTag*/
            if( deleteTags != null && deleteTags.length > 0 ){
                for(int j=0;j<deleteTags.length;j++){
                    deleteTag = deleteTags[j];
                    tagStartString = "<" + deleteTag.getName();
                    if( !StrUtl.isEmptyTrimmed( deleteTag.getId() ) ){
                        tagStartString +=" id=\""+ deleteTag.getId() +"\"";
                    }
                    tagStartString += ">";
                    tagEndString = "</" + deleteTag.getName() +">";

                    foundStr = StringUtils.substringBetween(value,tagStartString,tagEndString);
                    if( !StrUtl.isEmptyTrimmed( foundStr ) ) {
                        foundStr = tagStartString + foundStr + tagEndString;
                        countMatches = StringUtils.countMatches( value , foundStr );
                        if( countMatches > 0 ){
                            log( "DeleteTag    : " + foundStr );
                            value = StringUtils.replaceOnce( value , foundStr, "" );
                        }

                    }
                }
            }

            /* Apply ModifyTag */
            if( modifyTags != null && modifyTags.length > 0 ){
                for(int j=0;j<modifyTags.length;j++){
                    modifyTag = modifyTags[j];
                    tagStartString = "<" + modifyTag.getName();
                    if( !StrUtl.isEmptyTrimmed( modifyTag.getId() ) ){
                        tagStartString +=" id=\""+ modifyTag.getId() +"\"";
                    }
                    tagStartString += ">";
                    tagEndString = "</" + modifyTag.getName() +">";

                    foundStr = StringUtils.substringBetween(value,tagStartString,tagEndString);
                    if( !StrUtl.isEmptyTrimmed( foundStr ) ) {
                        foundStr = tagStartString + foundStr + tagEndString;
                        countMatches = StringUtils.countMatches( value , foundStr );
                        if( countMatches > 0 ){
                            String modifyTagValue = tagStartString + modifyTag.getValue() + tagEndString;
                            log( "ModifyTag    : " +  modifyTagValue );
                            value = StringUtils.replaceOnce( value , foundStr, modifyTagValue);
                        }
                    }
                }
            }

            /* Apply AddTag */
            if( addTags != null && addTags.length > 0 ){
                for(int j=0;j<addTags.length;j++){
                    addTag = addTags[j];
                    String prevEndTag = "</" + addTag.getPrevTagName() +">";
                    countMatches = StringUtils.countMatches( value , prevEndTag);
                    if( countMatches > 0 ){
                        tagStartString = "<" + addTag.getName();
                        if( !StrUtl.isEmptyTrimmed( addTag.getId() ) ){
                            tagStartString +=" id=\""+ addTag.getId() +"\"";
                        }
                        tagStartString += ">";
                        tagEndString = "</" + addTag.getName() +">";

                        String addTagValue =  tagStartString + addTag.getValue() + tagEndString;
                        log( "AddTag    : " +  addTagValue );
                        value = StringUtils.replaceOnce( value , prevEndTag , prevEndTag + newLine +addTagValue);
                    }
                }
            }
            return value;
        }
    }

    public class ApplyWebOverwrites {

        public ApplyWebOverwrites(String appsServerName,WebOverwrites webOverwrites,String searchFolder)  throws IOException,XRuntime,XmlException{
            if( webOverwrites == null ){
                return;
            }
            log( "Apply Web Overwrites" );
            WebBuilders webBuilders =  webOverwrites.getWebBuilders();
            if( webBuilders == null ){
                return;
            }
            String searchFolders[] = searchFolder.split(",");
            for(int i=0;i<searchFolders.length;i++){
                WebBuilder webBuilderArray[] = webBuilders.getWebBuilderArray();
                String extensions[] = {"xml"};
                Collection list = fileUtils.listFiles(new File( searchFolders[i] ),extensions, true);

                if( list!= null ){
                    File file = null;
                    for( Iterator iterator = list.iterator(); iterator.hasNext();) {
                        file =  (File) iterator.next();
                        WebBuilder webBuilder = null;
                        for( int j=0;j<webBuilderArray.length;j++){
                            webBuilder = webBuilderArray[j];
                            if( file.getName().equalsIgnoreCase( webBuilder.getName() ) ) {
                                buildWebConfig(appsServerName,file,webBuilder);
                            }
                        }
                    }
                }
            }
        }

        public void buildWebConfig(String appsServerName,File file,WebBuilder webBuilder) throws IOException,XRuntime,XmlException {
            /* Read Product cache xml File*/
            log("Web Overwrites on xml "+ file.getName() );
            String valueStr = fileUtils.readFileToString( file );
            WebContainerDocument webContainerDoc = WebContainerDocument.Factory.parse( valueStr );
            /********************************************************************/
            ArrayList validationXmlErrors = new ArrayList();
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setErrorListener( validationXmlErrors );
            boolean isValid = webContainerDoc.validate( xmlOptions );
            if( !isValid ){
                for (int i=0; i < validationXmlErrors.size(); i++ ){
                    XmlError xmlError = (XmlError) validationXmlErrors.get(i);
                    System.out.println( xmlError.toString() );
                }
                System.exit(1);
            }
            /********************************************************************/

            WebContainer webContainer = webContainerDoc.getWebContainer();
            applyWebOverwrites(webContainer,webBuilder);

            /* Generate Web Config */
            generateWebConfig(appsServerName,file,webContainer);

        }

        private void applyWebOverwrites(WebContainer webContainer,WebBuilder webBuilder ){

            if( webBuilder.isSetSessionTimeout() ){
                webContainer.setSessionTimeout( webBuilder.getSessionTimeout() );
            }

            if( webBuilder.isSetLoginConfig() ){
                webContainer.setLoginConfig( webBuilder.getLoginConfig() );
            }
            //Apply DeleteStrutsConfig
            StrutsConfig strutsConfig = null;
            DeleteStrutsConfig deleteStrutsConfigs[] = webBuilder.getDeleteStrutsConfigArray();
            if( deleteStrutsConfigs != null && deleteStrutsConfigs.length > 0){
                DeleteStrutsConfig deleteStrutsConfig = null;
                for(int i=0;i<deleteStrutsConfigs.length;i++){
                    deleteStrutsConfig = deleteStrutsConfigs[i];
                    StrutsConfig strutsConfigs[] = webContainer.getStrutsConfigArray();
                    if( strutsConfigs != null && strutsConfigs.length > 0 ){
                        for(int j=0;j<strutsConfigs.length;j++){
                            strutsConfig = strutsConfigs[j];
                            if( deleteStrutsConfig.getName().equalsIgnoreCase( strutsConfig.getName() ) ){
                                webContainer.removeStrutsConfig( j );
                                log( "StrutsConfig : " + deleteStrutsConfig.getName()  + " is deleted.");
                            }
                        }
                    }
                }
            }
            //Apply ModifyStrutsConfig
            ModifyStrutsConfig modifyStrutsConfigs[] = webBuilder.getModifyStrutsConfigArray();
            if( modifyStrutsConfigs != null && modifyStrutsConfigs.length > 0){
                ModifyStrutsConfig modifyStrutsConfig = null;
                for(int i=0;i<modifyStrutsConfigs.length;i++){
                    modifyStrutsConfig = modifyStrutsConfigs[i];
                    StrutsConfig strutsConfigs[] = webContainer.getStrutsConfigArray();
                    if( strutsConfigs != null && strutsConfigs.length > 0 ){
                        for(int j=0;j<strutsConfigs.length;j++){
                            strutsConfig = strutsConfigs[j];
                            if( modifyStrutsConfig.getName().equalsIgnoreCase( strutsConfig.getName() ) ){
                                strutsConfig.setUrlPattern( modifyStrutsConfig.getUrlPattern() );
                                log( "StrutsConfig : " + modifyStrutsConfig.getName()  + " is modified.");
                            }
                        }
                    }
                }
            }
            //Apply AddStrutsConfig
            AddStrutsConfig addStrutsConfigs[] = webBuilder.getAddStrutsConfigArray();
            if( addStrutsConfigs != null && addStrutsConfigs.length > 0){
                AddStrutsConfig addStrutsConfig = null;
                for(int i=0;i<addStrutsConfigs.length;i++){
                    addStrutsConfig = addStrutsConfigs[i];
                    StrutsConfig strutsConfigs[] = webContainer.getStrutsConfigArray();
                    if( strutsConfigs != null && strutsConfigs.length > 0 ){
                        for(int j=0;j<strutsConfigs.length;j++){
                            strutsConfig = strutsConfigs[j];
                            if( addStrutsConfig.getStrutsConfig().getName().equalsIgnoreCase( strutsConfig.getName() ) ){
                                throw new XRuntime("StrutsConfig", addStrutsConfig.getStrutsConfig().getName() +" already exists" );
                            }
                        }
                    }

                    int insertIndex = webContainer.sizeOfStrutsConfigArray();
                    webContainer.insertNewStrutsConfig( insertIndex );
                    webContainer.setStrutsConfigArray( insertIndex , addStrutsConfig.getStrutsConfig() );
                    log( "StrutsConfig : " + addStrutsConfig.getStrutsConfig().getName()  + " is added.");
                }
            }

            //Apply DeleteSecurityRole
            SecurityRole securityRole = null;
            DeleteSecurityRole deleteSecurityRoles[] = webBuilder.getDeleteSecurityRoleArray();
            if( deleteSecurityRoles != null && deleteSecurityRoles.length > 0){
                DeleteSecurityRole deleteSecurityRole = null;
                for(int i=0;i<deleteSecurityRoles.length;i++){
                    deleteSecurityRole = deleteSecurityRoles[i];
                    SecurityRole securityRoles[] = webContainer.getSecurityRoleArray();
                    if( securityRoles != null && securityRoles.length > 0 ){
                        for(int j=0;j<securityRoles.length;j++){
                            securityRole = securityRoles[j];
                            if( deleteSecurityRole.getName().equalsIgnoreCase( securityRole.getName() ) ){
                                webContainer.removeSecurityRole( j );
                                log( "SecurityRole : " + deleteSecurityRole.getName()  + " is deleted.");
                            }
                        }
                    }
                }
            }
            //Apply ModifySecurityRole
            ModifySecurityRole modifySecurityRoles[] = webBuilder.getModifySecurityRoleArray();
            if( modifySecurityRoles != null && modifySecurityRoles.length > 0){
                ModifySecurityRole modifySecurityRole = null;
                for(int i=0;i<modifySecurityRoles.length;i++){
                    modifySecurityRole = modifySecurityRoles[i];
                    SecurityRole securityRoles[] = webContainer.getSecurityRoleArray();
                    if( securityRoles != null && securityRoles.length > 0 ){
                        for(int j=0;j<securityRoles.length;j++){
                            securityRole = securityRoles[j];
                            if( modifySecurityRole.getName().equalsIgnoreCase( securityRole.getName() ) ){
                                securityRole.setPrincipal( modifySecurityRole.getPrincipal() );
                                log( "SecurityRole : " + modifySecurityRole.getName()  + " is modified.");
                            }
                        }
                    }
                }
            }
            //Apply AddSecurityRole
            AddSecurityRole addSecurityRoles[] = webBuilder.getAddSecurityRoleArray();
            if( addSecurityRoles != null && addSecurityRoles.length > 0){
                AddSecurityRole addSecurityRole = null;
                for(int i=0;i<addSecurityRoles.length;i++){
                    addSecurityRole = addSecurityRoles[i];
                    SecurityRole securityRoles[] = webContainer.getSecurityRoleArray();
                    if( securityRoles != null && securityRoles.length > 0 ){
                        for(int j=0;j<securityRoles.length;j++){
                            securityRole = securityRoles[j];
                            if( addSecurityRole.getSecurityRole().getName().equalsIgnoreCase( securityRole.getName() ) ){
                                throw new XRuntime("SecurityRole", addSecurityRole.getSecurityRole().getName() +" already exists" );
                            }
                        }
                    }

                    int insertIndex = webContainer.sizeOfSecurityRoleArray();
                    webContainer.insertNewSecurityRole( insertIndex );
                    webContainer.setSecurityRoleArray( insertIndex , addSecurityRole.getSecurityRole() );
                    log( "SecurityRole : " + addSecurityRole.getSecurityRole().getName()  + " is added.");
                }
            }

            /*Apply EjbBeanOverwrites - AddEjbBean,ModifyEjbBean,DeleteEjbBean)*/
            EjbOverwritesUtils utils = new EjbOverwritesUtils();
            utils.applyEjbBeanOverwrites(webContainer,webBuilder);

        }

        private void generateWebConfig(String appsServerName,File file,WebContainer webContainer)  throws IOException {

            /* Read web.xml Template*/
            String webTplFileName = StringUtils.replaceOnce( file.getAbsolutePath() , file.getName() , "web.xml.tpl" );
            File webTplFile = new File(  webTplFileName );
            String webFileStr = fileUtils.readFileToString( webTplFile );

            String weblogicTplFileName = StringUtils.replaceOnce( file.getAbsolutePath() , file.getName() , "weblogic.xml.tpl" );
            File weblogicTplFile = new File(  weblogicTplFileName );
            String weblogicFileStr = fileUtils.readFileToString( weblogicTplFile );

            String webbndTplFileName = StringUtils.replaceOnce( file.getAbsolutePath() , file.getName() , "ibm-web-bnd.xmi.tpl" );
            File webbndTplFile = new File(  webbndTplFileName );
            String webbndFileStr = fileUtils.readFileToString( webbndTplFile );

            webFileStr = StringUtils.replaceOnce( webFileStr , "@TIMEOUT@", String.valueOf( webContainer.getSessionTimeout() ) );
            webFileStr = StringUtils.replaceOnce( webFileStr , "@LOGIN-CONFIG@", "<login-config>" + newLine + webContainer.getLoginConfig() + newLine + "</login-config>" );

            StrutsConfig strutsConfigs[] = webContainer.getStrutsConfigArray();
            if( strutsConfigs != null && strutsConfigs.length >0 ){
                StrutsConfig strutsConfig = null;
                StringBuffer strutsConfigNameBuffer = new StringBuffer();
                StringBuffer strutsConfigUrlPatternBuffer = new StringBuffer();
                for(int i=0;i<strutsConfigs.length;i++){
                    strutsConfig  = strutsConfigs[i];
                    strutsConfigNameBuffer.append( newLine ).append( "<init-param>" )
                            .append( newLine ).append( "<param-name>config/" ).append( strutsConfig.getUrlPattern() ).append( "</param-name>" )
                            .append( newLine ).append( "<param-value>/WEB-INF/struts-config-" ).append( strutsConfig.getName() ).append( ".xml</param-value>" )
                            .append( newLine ).append( "</init-param>" );

                    strutsConfigUrlPatternBuffer.append( newLine ).append( "<url-pattern>" ).append( "/" ).append( strutsConfig.getUrlPattern() ).append( "/*</url-pattern>");

                }
                webFileStr = StringUtils.replaceOnce( webFileStr , "@INIT_STRUTS_CONFIG@", strutsConfigNameBuffer.toString() );
                webFileStr = StringUtils.replaceOnce( webFileStr , "@URL-PATTERN@", strutsConfigUrlPatternBuffer.toString() );
            }

            SecurityRole securityRoles[] = webContainer.getSecurityRoleArray();
            if( securityRoles != null && securityRoles.length >0 ){
                SecurityRole securityRole = null;
                StringBuffer securityRoleBuffer = new StringBuffer();
                StringBuffer securityRoleNameBuffer = new StringBuffer();
                StringBuffer securityroleAssignmentBuffer = new StringBuffer();
                String roleNameTag = null;
                for(int i=0;i<securityRoles.length;i++){
                    securityRole  = securityRoles[i];
                    roleNameTag = newLine + "<role-name>" + securityRole.getName() + "</role-name>";
                    securityRoleNameBuffer.append( roleNameTag );
                    securityRoleBuffer.append( newLine ).append( "<security-role>"  )
                                                        .append( roleNameTag )
                                       .append( newLine ).append( "</security-role>" );

                    String principal[] = StringUtils.split(securityRole.getPrincipal(),",");
                    if( principal != null && principal.length > 0){

                        securityroleAssignmentBuffer.append( newLine ).append("<security-role-assignment>").append( roleNameTag );
                        for(int j=0;j<principal.length;j++){
                            securityroleAssignmentBuffer.append( newLine ).append( "<principal-name>"  ).append( principal[j] ).append( "</principal-name>" );
                        }
                        securityroleAssignmentBuffer.append( newLine ).append("</security-role-assignment>");
                    }
                }
                webFileStr = StringUtils.replaceOnce( webFileStr , "@ROLE_NAME@", securityRoleNameBuffer.toString() );
                webFileStr = StringUtils.replaceOnce( webFileStr , "@SECURITY-ROLE@", securityRoleBuffer.toString() );
                weblogicFileStr = StringUtils.replaceOnce( weblogicFileStr , "@SECURITY_ROLE_ASSIGNMENT@", securityroleAssignmentBuffer.toString() );
            }

            EjbBean ejbBeans[] = webContainer.getEjbBeanArray();
            if( ejbBeans != null && ejbBeans.length >0 ){
                EjbBean ejbBean = null;
                StringBuffer beansBuffer = new StringBuffer();
                StringBuffer weblogicEjbRefBuffer = new StringBuffer();
                StringBuffer websphereBndBuffer = new StringBuffer();
                for(int i=0;i<ejbBeans.length;i++){
                    ejbBean  = ejbBeans[i];
                    beansBuffer.append( newLine ). append( StringUtils.trim( ejbBean.getEnterpriseBean() ) );
                    weblogicEjbRefBuffer.append( newLine ). append( StringUtils.trim( ejbBean.getWeblogic() ) );
                    websphereBndBuffer.append( newLine ). append( StringUtils.trim( ejbBean.getWebsphereBinding() ) );
                }

                webFileStr = StringUtils.replaceOnce( webFileStr , "@EJB_REF@", beansBuffer.toString() );
                weblogicFileStr = StringUtils.replaceOnce( weblogicFileStr , "@WEB_EJB_REFERENCE@", weblogicEjbRefBuffer.toString() );
                webbndFileStr = StringUtils.replaceOnce( webbndFileStr , "@WEBBINDINGS@", websphereBndBuffer.toString() );

            }

            /* Write web.xml*/
            String webFileName = StringUtils.replaceOnce( webTplFile.getAbsolutePath() , ".tpl", "" );
            File webFile = new File(  webFileName );
            FileUtils.writeStringToFile(webFile,webFileStr);

            if( WEBLOGIC_APPS_SERVER.equalsIgnoreCase( appsServerName ) ){
                /* Write weblogic.xml*/
                String weblogicFileName = StringUtils.replaceOnce( weblogicTplFile.getAbsolutePath() , ".tpl", "" );
                File weblogicFile = new File(  weblogicFileName );
                FileUtils.writeStringToFile(weblogicFile,weblogicFileStr);
            }
            else if( WEBSPHERE_APPS_SERVER.equalsIgnoreCase( appsServerName ) ){
                /* Write ibm-web-bnd.xmi*/
                String webbndFileName = StringUtils.replaceOnce( webbndTplFile.getAbsolutePath() , ".tpl", "" );
                File webbndFile = new File(  webbndFileName );
                FileUtils.writeStringToFile(webbndFile,webbndFileStr);
            }
        }
    }

    public void log(String msg){
       if( showlogs ){
            System.out.println( msg );
       }
    }

    private static void printHelp() {
        System.out.println( "USAGE: com.addval.utils.BuildUtils -a <APPSERVER_NAME> -p <PRODUCT_NAME> -b <BUILD_FOLDER> -o <PROJECT_OVERWRITES_XML> -l <SHOW_LOG>" );
        System.out.println( "  -a:  Apps Server Name (weblogic /websphere)" );
        System.out.println( "  -p:  Product Name" );
        System.out.println( "  -b:  Build folder name" );
        System.out.println( "  -o:  Project overwrites xml file" );
        System.out.println( "  -l:  true | false" );
        System.out.println( "  -h:  help" );
    }

    public static void main(String[] args) {
        try {
            CommandLine cl = new CommandLine( args );
            System.out.println("Arguments passed: " + cl.toString());

            if (cl.hasFlag("-h") || args.length == 0) {
                printHelp();
                System.exit( 0 );
            }
            BuildUtils client = new BuildUtils();
            client.execute( cl );
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit( 1 );
        }
    }
}
