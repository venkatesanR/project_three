package com.addval.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.addval.dbutils.DAOSQLStatement;
import com.addval.dbutils.DAOUtils;
import com.addval.dbutils.DBPoolMgr;
import com.addval.dbutils.DBUtl;
import com.addval.ui.UIPageConfig;
import com.addval.ui.UIPageProperty;
import com.addval.ui.UIPageSectionAttribute;
import com.addval.ui.UIPageSectionAttributeDto;
import com.addval.ui.UIPageSectionConfig;
import com.addval.ui.UIPageSectionProperty;

public class UIPageConfigCacheInitializer extends NamedCacheInitializerUsingCachedDAOSQL {
	private static final String _module = "com.addval.utils.UIPageConfigCacheInitializer";
	private static transient final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(UIPageConfigCacheInitializer.class);
	protected String pagePropertiesSqlName = null;
	protected String pageSectionPropertiesSqlName = null;
	protected String pageDefaultPropertiesSqlName = null;
	protected String pageSectionDefaultPropertiesSqlName = null;

	public String getPagePropertiesSqlName() {
		return pagePropertiesSqlName;
	}

	public void setPagePropertiesSqlName(String pagePropertiesSqlName) {
		this.pagePropertiesSqlName = pagePropertiesSqlName;
	}

	public String getPageSectionPropertiesSqlName() {
		return pageSectionPropertiesSqlName;
	}

	public void setPageSectionPropertiesSqlName(String pageSectionPropertiesSqlName) {
		this.pageSectionPropertiesSqlName = pageSectionPropertiesSqlName;
	}

	public String getPageDefaultPropertiesSqlName() {
		return pageDefaultPropertiesSqlName;
	}

	public void setPageDefaultPropertiesSqlName(String pageDefaultPropertiesSqlName) {
		this.pageDefaultPropertiesSqlName = pageDefaultPropertiesSqlName;
	}

	public String getPageSectionDefaultPropertiesSqlName() {
		return pageSectionDefaultPropertiesSqlName;
	}

	public void setPageSectionDefaultPropertiesSqlName(String pageSectionDefaultPropertiesSqlName) {
		this.pageSectionDefaultPropertiesSqlName = pageSectionDefaultPropertiesSqlName;
	}

	public Object populateData(CacheMgr cache, String objectName, boolean refreshFlag) throws CacheException {
		ArrayList<UIPageProperty> uiPageProperties = populateDefaultUIPageProperties();
		Hashtable<String, ArrayList<UIPageSectionProperty>> defaultComponentTypeProperties = populateDefaultUIPageSectionProperties();
		Hashtable<String, UIPageConfig> uiPageConfigs = populateUIPageConfigs(uiPageProperties,defaultComponentTypeProperties);
		populateUIPageProperties(uiPageConfigs);
		populateUIPageSectionProperties(uiPageConfigs);
		return uiPageConfigs;
	}

	private Hashtable<String, UIPageConfig> populateUIPageConfigs(ArrayList<UIPageProperty> uiPageProperties,Hashtable<String, ArrayList<UIPageSectionProperty>> defaultComponentTypeProperties) throws CacheException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Hashtable<String, UIPageConfig> uiPageConfigs = new Hashtable<String, UIPageConfig>();

		try {
			DAOSQLStatement statement = getDAOSQLStatement();
			DAOUtils utils = new DAOUtils(statement, getServerType());

			conn = getConnection();
			pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
			rs = pstmt.executeQuery();

			UIPageSectionAttribute uiPageSectionAttribute = null;
			UIPageSectionAttributeDto uiPageSectionAttributeDto = new UIPageSectionAttributeDto();

			String currPageName = "";
			String currComponentId = "";
			UIPageConfig uiPageConfig = null;
			UIPageSectionConfig uiPageSectionConfig = null;
			ArrayList<UIPageSectionProperty> uiPageSectionProperties = null;
			while (rs.next()) {
				utils.getProperties(rs, uiPageSectionAttributeDto);
				uiPageSectionAttribute = new UIPageSectionAttribute(uiPageSectionAttributeDto);
				uiPageSectionAttribute.setPrivilegeExpr(formatGroovyExpr( uiPageSectionAttribute.getPrivilegeExpr() ));
				currPageName = uiPageSectionAttribute.getPageName();
				currComponentId = uiPageSectionAttribute.getComponentId();

				if (!uiPageConfigs.containsKey(currPageName)) {
					uiPageConfig = new UIPageConfig();
					uiPageConfig.setPageName(currPageName);
					uiPageConfig.setEditorType(uiPageSectionAttribute.getEditorType());
					uiPageConfig.setUIPageProperties((List<UIPageProperty>)GenUtl.cloneObject(uiPageProperties));
					uiPageConfigs.put(currPageName, uiPageConfig);
				}
				uiPageConfig = (UIPageConfig) uiPageConfigs.get(currPageName);

				if (!uiPageConfig.getUIPageSectionConfig().containsKey(currComponentId)) {
					uiPageSectionConfig = new UIPageSectionConfig();
					uiPageSectionConfig.setPageName(currPageName);
					uiPageSectionConfig.setEditorType(uiPageSectionAttribute.getEditorType());
					uiPageSectionConfig.setComponentId(uiPageSectionAttribute.getComponentId());
					uiPageSectionConfig.setComponentType(uiPageSectionAttribute.getComponentType());
					if(defaultComponentTypeProperties.containsKey( uiPageSectionAttribute.getComponentType() )){
						uiPageSectionProperties = defaultComponentTypeProperties.get( uiPageSectionAttribute.getComponentType() ); 
						uiPageSectionConfig.setUIPageSectionProperties( (List<UIPageSectionProperty>) GenUtl.cloneObject(uiPageSectionProperties) );
					}
					uiPageConfig.getUIPageSectionConfig().put(currComponentId, uiPageSectionConfig);
				}
				uiPageSectionConfig = (UIPageSectionConfig) uiPageConfig.getUIPageSectionConfig().get(currComponentId);

				uiPageSectionConfig.getUIPageSectionAttributes().add(uiPageSectionAttribute);
				_logger.info("UIPageConfig - UIPageSectionConfig - UIPageSectionAttribute=\t" + currPageName +"\t" + currComponentId +"\t" + uiPageSectionAttribute.getAttributeName());
			}
		}
		catch (Exception e) {
			getEnvironment().getLogFileMgr(_module).logError(e);
			throw new CacheException(_module, e);
		}
		finally {
			DBUtl.closeFinally(rs, pstmt, conn, getDbPoolMgr(), (LogFileMgr) null);
		}
		return uiPageConfigs;
	}

	private void populateUIPageProperties(Hashtable<String, UIPageConfig> uiPageConfigs) throws CacheException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			DAOSQLStatement statement = getPagePropertiesDAOSQLStatement();
			DAOUtils utils = new DAOUtils(statement, getServerType());

			conn = getConnection();
			pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
			rs = pstmt.executeQuery();

			String currPageName = "";
			UIPageConfig uiPageConfig = null;
			UIPageProperty currUIPageProperty = null;
			UIPageProperty uiPageProperty = null;
			while (rs.next()) {
				currUIPageProperty = new UIPageProperty();
				utils.getProperties(rs, currUIPageProperty);
				if(currUIPageProperty.getPropertyName().equalsIgnoreCase("ACCESS_PRIVILEGE_XPRESSION")){
					currUIPageProperty.setPropertyValue(formatGroovyExpr( currUIPageProperty.getPropertyValue() ));	
				}
				currPageName = currUIPageProperty.getPageName();
				if(uiPageConfigs.containsKey(currPageName)) {
					uiPageConfig = uiPageConfigs.get( currPageName );
					uiPageProperty = uiPageConfig.getUIUIPageProperty( currUIPageProperty.getPropertyName() );
					uiPageProperty.copy( currUIPageProperty );
				}
			}
		}
		catch (SQLException e) {
			getEnvironment().getLogFileMgr(_module).logError(e);
			throw new CacheException(_module, e);
		}
		finally {
			DBUtl.closeFinally(rs, pstmt, conn, getDbPoolMgr(), (LogFileMgr) null);
		}
	}

	private void populateUIPageSectionProperties(Hashtable<String, UIPageConfig> uiPageConfigs) throws CacheException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			DAOSQLStatement statement = getPageSectionPropertiesDAOSQLStatement();
			DAOUtils utils = new DAOUtils(statement, getServerType());

			conn = getConnection();
			pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
			rs = pstmt.executeQuery();

			String currPageName = "";
			String currComponentId = "";
			UIPageConfig uiPageConfig = null;
			UIPageSectionConfig uiPageSectionConfig = null;
			UIPageSectionProperty currUIPageSectionProperty = null;
			UIPageSectionProperty uiPageSectionProperty = null;
			while (rs.next()) {
				currUIPageSectionProperty = new UIPageSectionProperty();
				utils.getProperties(rs, currUIPageSectionProperty);
				currPageName = currUIPageSectionProperty.getPageName();
				currComponentId = currUIPageSectionProperty.getComponentId();
				if (uiPageConfigs.containsKey(currPageName)) {
					uiPageConfig = uiPageConfigs.get(currPageName);
					if (uiPageConfig.getUIPageSectionConfig().containsKey(currComponentId)) {
						uiPageSectionConfig = uiPageConfig.getUIPageSectionConfig().get(currComponentId);
						uiPageSectionProperty = uiPageSectionConfig.getUIPageSectionProperty( currUIPageSectionProperty.getPropertyName() );
						uiPageSectionProperty.copy( currUIPageSectionProperty );
					}
				}
			}
		}
		catch (SQLException e) {
			getEnvironment().getLogFileMgr(_module).logError(e);
			throw new CacheException(_module, e);
		}
		finally {
			DBUtl.closeFinally(rs, pstmt, conn, getDbPoolMgr(), (LogFileMgr) null);
		}
	}

	private ArrayList<UIPageProperty> populateDefaultUIPageProperties() throws CacheException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<UIPageProperty> uiPageProperties = new ArrayList<UIPageProperty>();
		try {
			DAOSQLStatement statement = getPageDefaultPropertiesDAOSQLStatement();
			DAOUtils utils = new DAOUtils(statement, getServerType());
			conn = getConnection();
			pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
			rs = pstmt.executeQuery();
			UIPageProperty uiPageProperty = null;
			while (rs.next()) {
				uiPageProperty = new UIPageProperty();
				utils.getProperties(rs, uiPageProperty);
				if(uiPageProperty.getPropertyName().equalsIgnoreCase("ACCESS_PRIVILEGE_XPRESSION")){
					uiPageProperty.setPropertyValue(formatGroovyExpr( uiPageProperty.getPropertyValue() ));	
				}
				uiPageProperties.add(uiPageProperty);
			}
		}
		catch (SQLException e) {
			getEnvironment().getLogFileMgr(_module).logError(e);
			throw new CacheException(_module, e);
		}
		finally {
			DBUtl.closeFinally(rs, pstmt, conn, getDbPoolMgr(), (LogFileMgr) null);
		}
		return uiPageProperties;
	}

	private Hashtable<String, ArrayList<UIPageSectionProperty>> populateDefaultUIPageSectionProperties() throws CacheException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Hashtable<String, UIPageSectionConfig> componentTypeProperties = new Hashtable<String, UIPageSectionConfig>();
		Hashtable<String, ArrayList<UIPageSectionProperty>> defaultComponentTypeProperties = new Hashtable<String, ArrayList<UIPageSectionProperty>>();
		try {
			DAOSQLStatement statement = getPageSectionDefaultPropertiesDAOSQLStatement();
			DAOUtils utils = new DAOUtils(statement, getServerType());

			conn = getConnection();
			pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
			rs = pstmt.executeQuery();

			UIPageSectionProperty uiPageSectionProperty = null;
			String currComponentType = "";
			UIPageSectionConfig uiPageSectionConfig = null;
			ArrayList<UIPageSectionProperty> uiPageSectionProperties = new ArrayList<UIPageSectionProperty>();
			while (rs.next()) {
				uiPageSectionProperty = new UIPageSectionProperty();
				utils.getProperties(rs, uiPageSectionProperty);
				currComponentType = uiPageSectionProperty.getComponentType();
				if (!componentTypeProperties.containsKey(currComponentType)) {
					uiPageSectionProperties = new ArrayList<UIPageSectionProperty>();
					
					uiPageSectionConfig = new UIPageSectionConfig();
					uiPageSectionConfig.setComponentType(currComponentType);
					uiPageSectionConfig.setUIPageSectionProperties( uiPageSectionProperties );

					componentTypeProperties.put(currComponentType, uiPageSectionConfig);
					defaultComponentTypeProperties.put(currComponentType, uiPageSectionProperties);
					
				}
				uiPageSectionConfig = componentTypeProperties.get( currComponentType );
				uiPageSectionConfig.getUIPageSectionProperties().add( uiPageSectionProperty );
			}
		}
		catch (SQLException e) {
			getEnvironment().getLogFileMgr(_module).logError(e);
			throw new CacheException(_module, e);
		}
		finally {
			DBUtl.closeFinally(rs, pstmt, conn, getDbPoolMgr(), (LogFileMgr) null);
		}
		return defaultComponentTypeProperties;
	}

	private DBPoolMgr getDbPoolMgr() {
		return getEnvironment().getDbPoolMgr();
	}

	protected String getServerType() {
		return getEnvironment().getDbPoolMgr().getServerType();
	}

	protected Connection getConnection() {
		return getEnvironment().getDbPoolMgr().getConnection();
	}

	protected void releaseConnection(Connection conn) {
		getEnvironment().getDbPoolMgr().releaseConnection(conn);
	}

	protected DAOSQLStatement getPagePropertiesDAOSQLStatement() throws CacheException {
		return getDAOSQLStatement(getPagePropertiesSqlName(), "pagePropertiesSqlName");
	}

	protected DAOSQLStatement getPageSectionPropertiesDAOSQLStatement() throws CacheException {
		return getDAOSQLStatement(getPageSectionPropertiesSqlName(), "pageSectionPropertiesSqlName");
	}

	protected DAOSQLStatement getPageDefaultPropertiesDAOSQLStatement() throws CacheException {
		return getDAOSQLStatement(getPageDefaultPropertiesSqlName(), "pageDefaultPropertiesSqlName");
	}

	protected DAOSQLStatement getPageSectionDefaultPropertiesDAOSQLStatement() throws CacheException {
		return getDAOSQLStatement(getPageSectionDefaultPropertiesSqlName(), "pageSectionDefaultPropertiesSqlName");
	}
	
	private String formatGroovyExpr(String groovyExpr){
		if(!StrUtl.isEmptyTrimmed(groovyExpr)){
			groovyExpr = StringUtils.replace(groovyExpr, "\\;", ";");
			groovyExpr = StringUtils.replace(groovyExpr, "\\;", ";" + System.getProperty("line.separator"));
			groovyExpr = StringUtils.replace(groovyExpr, "{", "{" + System.getProperty("line.separator"));
			groovyExpr = StringUtils.replace(groovyExpr, "}", "}" + System.getProperty("line.separator"));
		}
		return groovyExpr;
	}
}
