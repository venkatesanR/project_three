package com.addval.metadata;

import com.addval.environment.Environment;
import com.addval.ejbutils.server.EJBSEditorMetaDataHome;
import com.addval.ejbutils.server.EJBSEditorMetaDataRemote;
import com.addval.environment.EJBEnvironment;
import com.addval.ejbutils.server.EJBSTableManagerHome;
import com.addval.ejbutils.server.EJBSTableManagerRemote;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import java.util.Vector;
import java.util.Iterator;
import com.addval.ejbutils.dbutils.EJBResultSet;
import java.util.Hashtable;
import java.util.ArrayList;

import com.addval.utils.XRuntime;
import com.addval.ejbutils.dbutils.EJBCriteria;
import javax.naming.NamingException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class UserCriteriaMgr
{
	private String _subsystem = null;
	private static final String _EDITOR_NAME = "USER_CRITERIA";
	private static final String _EDITOR_TYPE = "DEFAULT";
	private EditorMetaData _metadata = null;

	/**
	 * @param subsystem
	 * @throws java.lang.Exception
	 * @roseuid 3FC8F9B302D9
	 */
	public UserCriteriaMgr(String subsystem) throws Exception
	{
		try {
			setSubsystem(subsystem);
			_metadata = getEditorMetaData();
		}
		catch(Exception ex) {
			throw ex;
		}
	}

	/**
	 * @param param
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @throws java.lang.Exception
	 * @roseuid 3FC8F8210187
	 */
	public EJBResultSet lookup(Hashtable param,Hashtable operator) throws Exception
	{
		EJBResultSet ejbRS = null;
		EJBCriteria ejbCriteria = null;
		try {
			EJBSTableManagerHome   tableManagerHome   = getTableManagerHome(getSubsystem());
			EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();
			ejbCriteria = getEJBCriteriaForLookup( _metadata , param, operator );
			ejbRS = tableManagerRemote.lookup( ejbCriteria );
			return ejbRS;
		}
		catch(Exception ex) {
			throw ex;
		}
	}

    public UserCriteria lookup(String criteriaName,String editorName, String editorType, String userId) throws Exception {
        ArrayList userGroups = null;
        UserCriteria criteria = null;
        EJBResultSet ejbRS = lookup(criteriaName,editorName,editorType,userId,userGroups);
        if(ejbRS != null) {
            ResultSet rs = ( ResultSet ) ejbRS;
            ResultSetMetaData rsMetaData	= rs.getMetaData();
            int				size 	   		= rsMetaData.getColumnCount();
            Hashtable		cols	   		= new Hashtable();

             if(rs.next()) {
				boolean sharedAll	= 	rs.getString("SHARED_WITH_ALL") != null && rs.getString("SHARED_WITH_ALL").equalsIgnoreCase("1") ? true : false;
                boolean sharedGroup	=	rs.getString("SHARED_WITH_GROUP") != null && rs.getString("SHARED_WITH_GROUP").equalsIgnoreCase("1") ? true : false;
                criteria = new UserCriteria( editorName,
                                             editorType,
                                             userId,
                                             rs.getString("CRITERIA_NAME"),
                                             rs.getString("CRITERIA_DESC"),
                                             rs.getString("DIMENSIONS"),
                                             rs.getString("MEASURES"),
                                             rs.getString("FILTER"),
                                             sharedAll,
                                             sharedGroup,
                                             rs.getString("USER_NAME"),
                                             rs.getString("DISPLAYABLE"),
                                             rs.getString("SORT_ORDER_SEQ"),
                                             rs.getString("SORT_ORDER"),
                                             rs.getString("SUBTOTAL"),
                                             rs.getInt("SHOW_SUBTOTAL_DETAIL") == 0 ? false : true
                                            );
				try {
            		criteria.setChartType( rs.getString("CHART_TYPE") );
            		criteria.setChartJs( rs.getString("CHART_JS") );
				}
				catch(Exception e) {
					System.out.println( "Warining: CHART_TYPE/ CHART_JS columns not available in ResultSet."  );
				}

            }
        }
        return criteria;
    }

    public EJBResultSet lookup(String editorName, String editorType, String userId, ArrayList userGroups) throws Exception {
        String criteriaName = null;
        return lookup(criteriaName,editorName,editorType,userId,userGroups);
    }

	private EJBResultSet lookup(String criteriaName,String editorName, String editorType, String userId, ArrayList userGroups) throws Exception {
        String userGroup = "";
        if((userGroups != null) && !userGroups.isEmpty())
        {
            int size = userGroups.size();
            for(int i = 0; i < size; i++)
                userGroup = userGroup + "," + " '" + userGroups.get(i) + "'";
        }

        if(!userGroup.equals(""))
            userGroup = userGroup.substring(1);

        String sql = null;
        if(criteriaName != null)
        {
            sql = "select * from User_criteria where user_name = '" + userId + "' and editor_name = '" + editorName + "' and editor_type = '" + editorType + "' and criteria_name = '" + criteriaName + "'" + " UNION " + " select * from User_criteria where NVL(shared_with_all,0) = 1  and user_name <> '" + userId + "' and editor_name = '" + editorName + "' and editor_type = '" + editorType + "' and criteria_name = '" + criteriaName + "'";
            if(!userGroup.equals(""))
                sql += (" UNION " + " select * from User_criteria where NVL(shared_with_group,0) = 1 and user_name <> '" + userId + "' and editor_name = '" + editorName + "' and editor_type = '" + editorType + "' and criteria_name = '" + criteriaName + "'" + " and user_name in (select user_name from group_user where group_name in (" + userGroup + "))");
        }
        else
        {
            sql = "select * from User_criteria where user_name = '" + userId + "' and editor_name = '" + editorName + "' and editor_type = '" + editorType + "'" + " UNION " + " select * from User_criteria where NVL(shared_with_all,0) = 1  and user_name <> '" + userId + "' and editor_name = '" + editorName + "' and editor_type = '" + editorType + "'";
            if(!userGroup.equals(""))
                sql += (" UNION " + " select * from User_criteria where NVL(shared_with_group,0) = 1 and user_name <> '" + userId + "' and editor_name = '" + editorName + "' and editor_type = '" + editorType + "'" + " and user_name in (select user_name from group_user where group_name in (" + userGroup + "))");
        }

        EJBSTableManagerHome tableManagerHome = getTableManagerHome(getSubsystem());
        EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();
        return tableManagerRemote.lookup(sql, _metadata);
	}

	/**
	 * Access method for the _subsystem property.
	 * @return   the current value of the _subsystem property
	 * @roseuid 3FC8F9B4000A
	 */
	public String getSubsystem()
	{
		return _subsystem;
	}

	/**
	 * Sets the value of the _subsystem property.
	 * @param aSubsystem the new value of the _subsystem property
	 * @roseuid 3FC8F9B40050
	 */
	public void setSubsystem(String aSubsystem)
	{
		_subsystem = aSubsystem;
	}

	/**
	 * @return com.addval.metadata.EditorMetaData
	 * @throws java.lang.Exception
	 * @roseuid 3FC8F9B400FA
	 */
	private EditorMetaData getEditorMetaData() throws Exception
	{
		String homeName = Environment.getInstance(getSubsystem()).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSEditorMetaDataBeanName","");
		EJBSEditorMetaDataHome   metaDataHome   = ( EJBSEditorMetaDataHome ) EJBEnvironment.lookupEJBInterface( getSubsystem(), homeName ,EJBSEditorMetaDataHome.class );
		EJBSEditorMetaDataRemote metaDataRemote = metaDataHome.create( );

		return  metaDataRemote.lookup( _EDITOR_NAME, _EDITOR_TYPE );
	}

	/**
	 * @param criteria
	 * @throws com.addval.utils.XRuntime
	 * @roseuid 3FC8F9B4033F
	 */
	public void insert(UserCriteria criteria) throws XRuntime
	{
		try {

			EJBResultSet ejbRS = new EJBResultSet ( new EJBResultSetMetaData( _metadata ) );
			ejbRS.insertRow();
			ejbRS.updateString("EDITOR_NAME", criteria.getEditorName());
			ejbRS.updateString("EDITOR_TYPE", criteria.getEditorType());
			ejbRS.updateString("USER_NAME", criteria.getUserName());
			ejbRS.updateString("CRITERIA_NAME", criteria.getCriteriaName());
			ejbRS.updateString("CRITERIA_DESC", criteria.getCriteriaDesc());
			ejbRS.updateString("DIMENSIONS", criteria.getDimension());
			ejbRS.updateString("MEASURES", criteria.getMeasure());
            ejbRS.updateString("FILTER", criteria.getFilter());
            ejbRS.updateString("SHARED_WITH_ALL", criteria.getSharedAll() ? "1" : "0");
            ejbRS.updateString("SHARED_WITH_GROUP", criteria.getSharedGroup() ? "1" : "0");
            ejbRS.updateString("DISPLAYABLE", criteria.getCustomDisplayable() );
            ejbRS.updateString("SORT_ORDER_SEQ", criteria.getColumnSortOrderSeq() );
            ejbRS.updateString("SORT_ORDER", criteria.getColumnSortOrder() );
            ejbRS.updateString("SUBTOTAL", criteria.getSubTotalColumns() );
            ejbRS.updateInt("SHOW_SUBTOTAL_DETAIL", criteria.isShowSubtotalDetail()?1:0);
            ejbRS.updateString("CHART_TYPE", criteria.getChartType());
        	ejbRS.updateString("CHART_JS", criteria.getChartJs());

            EJBSTableManagerHome   tableManagerHome   = getTableManagerHome( getSubsystem() );
			EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();
			ejbRS = tableManagerRemote.updateTransaction ( ejbRS );
			if (ejbRS == null)
				throw new XRuntime( "UserCriteriaMgr.insert()", "Insert Fails");
		}
		catch(Exception ex) {
			throw new XRuntime( "UserCriteriaMgr.insert()", ex.getMessage());
		}
	}

	/**
	 * @param criteria
	 * @throws com.addval.utils.XRuntime
	 * @roseuid 3FC8F9B500F1
	 */
	public void update(UserCriteria criteria) throws XRuntime
	{
		try {
			EJBResultSet ejbRS = new EJBResultSet ( new EJBResultSetMetaData( _metadata ) );
			ejbRS.updateRow();
			ejbRS.updateString("EDITOR_NAME", criteria.getEditorName());
			ejbRS.updateString("EDITOR_TYPE", criteria.getEditorType());
			ejbRS.updateString("USER_NAME", criteria.getUserName());
			ejbRS.updateString("CRITERIA_NAME", criteria.getCriteriaName());
			ejbRS.updateString("CRITERIA_DESC", criteria.getCriteriaDesc());
			ejbRS.updateString("DIMENSIONS", criteria.getDimension());
			ejbRS.updateString("MEASURES", criteria.getMeasure());
			ejbRS.updateString("FILTER", criteria.getFilter());
			ejbRS.updateString( "SHARED_WITH_ALL", criteria.getSharedAll() ? "1" : "0");
			ejbRS.updateString( "SHARED_WITH_GROUP", criteria.getSharedGroup() ? "1" : "0");
            ejbRS.updateString("DISPLAYABLE", criteria.getCustomDisplayable() );
            ejbRS.updateString("SORT_ORDER_SEQ", criteria.getColumnSortOrderSeq() );
            ejbRS.updateString("SORT_ORDER", criteria.getColumnSortOrder() );
            ejbRS.updateString("SUBTOTAL", criteria.getSubTotalColumns() );
            ejbRS.updateInt("SHOW_SUBTOTAL_DETAIL", criteria.isShowSubtotalDetail() ? 1 : 0 );
        	ejbRS.updateString("CHART_TYPE", criteria.getChartType());
        	ejbRS.updateString("CHART_JS", criteria.getChartJs());

            EJBSTableManagerHome   tableManagerHome   = getTableManagerHome( getSubsystem() );
			EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();
			ejbRS = tableManagerRemote.updateTransaction ( ejbRS );
		}
		catch(Exception ex) {
			throw new XRuntime( "UserCriteriaMgr.update()", ex.getMessage());
		}
	}

	/**
	 * @param criteria
	 * @throws com.addval.utils.XRuntime
	 * @roseuid 3FC8F9B50296
	 */
	public void delete(UserCriteria criteria) throws XRuntime
	{
		try {
			EJBResultSet ejbRS = new EJBResultSet ( new EJBResultSetMetaData( _metadata ) );
			ejbRS.deleteRow();
			ejbRS.updateString("EDITOR_NAME", criteria.getEditorName());
			ejbRS.updateString("EDITOR_TYPE", criteria.getEditorType());
			ejbRS.updateString("USER_NAME", criteria.getUserName());
			ejbRS.updateString("CRITERIA_NAME", criteria.getCriteriaName());

			EJBSTableManagerHome   tableManagerHome   = getTableManagerHome( getSubsystem() );
			EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();
			ejbRS = tableManagerRemote.updateTransaction ( ejbRS );
		}
		catch(Exception ex) {
			throw new XRuntime( "UserCriteriaMgr.delete()", ex.getMessage());
		}
	}

	/**
	 * @param metadata
	 * @param param
	 * @return com.addval.ejbutils.dbutils.EJBCriteria
	 * @roseuid 3FC8F9B60053
	 */
	private EJBCriteria getEJBCriteriaForLookup(EditorMetaData metadata, Hashtable param, Hashtable operator)
	{
    	EJBCriteria ejbCriteria = ( new EJBResultSetMetaData( metadata ) ).getNewCriteria();
		ejbCriteria.where ( param, operator );
	    Vector orderby = new Vector( );
	    for ( Iterator iter = metadata.getDisplayableColumns().iterator( ); iter.hasNext( ); ) {
	        ColumnMetaData colInfo = ( ColumnMetaData ) iter.next( );
	        if ( colInfo.getType() != ColumnDataType._CDT_LINK )
	    		orderby.add ( colInfo.getName() );
	    }
        ejbCriteria.orderBy ( orderby );
        return ejbCriteria;
	}

	/**
	 * @param subsystem
	 * @return com.addval.ejbutils.server.EJBSTableManagerHome
	 * @throws javax.naming.NamingException
	 * @throws java.rmi.RemoteException
	 * @throws java.lang.Exception
	 * @roseuid 3FC8F9B601D9
	 */
	private EJBSTableManagerHome getTableManagerHome(String subsystem) throws NamingException, RemoteException, Exception
	{
		String homeName = Environment.getInstance(subsystem).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSTableManagerBeanName", "EJBSTableManager");
        try {
			EJBSTableManagerHome   tableManagerHome   = ( EJBSTableManagerHome ) EJBEnvironment.lookupEJBInterface( subsystem, homeName , EJBSTableManagerHome.class );
			return tableManagerHome;
		}
		catch (javax.naming.NamingException ex) {
			throw ex;
		}
		catch (java.lang.Exception ex) {
			throw ex;
       	}
	}
}
