//Source file: D:\\projects\\COMMON\\src\\com\\addval\\struts\\UserCriteriaUtil.java

package com.addval.struts;

import com.addval.metadata.UserCriteria;
import com.addval.metadata.UserCriteriaMgr;

import java.sql.ResultSet;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.metadata.ColumnMetaData;
import org.apache.struts.util.LabelValueBean;
import org.apache.commons.lang.StringUtils;
import com.addval.metadata.ColumnDataType;
import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.dbutils.EJBCriteriaColumn;
import com.addval.environment.Environment;
import com.addval.environment.EJBEnvironment;
import com.addval.ejbutils.server.*;
import com.addval.metadata.EditorMetaData;
import javax.servlet.http.HttpSession;

import com.addval.utils.*;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

public class UserCriteriaUtil
{
	private String _subsystem = null;

	public UserCriteriaUtil(String subsystem)
	{
		_subsystem = subsystem;
	}

	public String getSubsystem()
	{
		return _subsystem;
	}

    public void setSubsystem(String aSubsystem)
	{
		_subsystem = aSubsystem;
	}

	public void populateData(UserCriteriaForm form) throws Exception
	{
		Vector displayables = new Vector();
		Vector aggregatables = new Vector();
        Vector customDisplayables = new Vector();
        Vector columnSortOrderSeq = new Vector();
        Vector columnSortOrder = new Vector();
        Vector subTotalColumns = new Vector();

        ColumnMetaData 	columnMetaData	= null;

        UserCriteria criteria = form.getCriteria();
		EditorMetaData metaData = form.getMetadata();
        form.setOwner( null );
        if( criteria != null)  {

			StringTokenizer tokens  = null;
			String token = null;
			if(criteria.getDimension() != null) {
				tokens  = new StringTokenizer(criteria.getDimension(),",",false);
				while(tokens.hasMoreTokens()) {
					token = tokens.nextToken();
					if(metaData.getColumnMetaData(token)!= null)
						displayables.add(token);
				}
			}

			if(criteria.getMeasure() != null) {
				tokens  = new StringTokenizer(criteria.getMeasure(),",",false);
				while(tokens.hasMoreTokens()) {
					token = tokens.nextToken();
					if(metaData.getColumnMetaData(token)!= null)
						aggregatables.add(token);
				}
			}

            if(criteria.getCustomDisplayable() != null) {
                tokens  = new StringTokenizer(criteria.getCustomDisplayable(),",",false);
                while(tokens.hasMoreTokens()) {
                    token = tokens.nextToken();
					if(metaData.getColumnMetaData(token)!= null)
                    	customDisplayables.add(token);
                }
            }

            if(criteria.getColumnSortOrderSeq() != null) {
                tokens  = new StringTokenizer(criteria.getColumnSortOrderSeq(),",",false);
                while(tokens.hasMoreTokens()) {
                    token = tokens.nextToken();
					if(metaData.getColumnMetaData(token)!= null)
                    	columnSortOrderSeq.add(token);
                }
            }

            if(criteria.getColumnSortOrder() != null) {
                tokens  = new StringTokenizer(criteria.getColumnSortOrder(),",",false);
                while(tokens.hasMoreTokens()) {
                    token = tokens.nextToken();
					if(metaData.getColumnMetaData(token)!= null)
                    	columnSortOrder.add( token );
                }
            }

            if(criteria.getSubTotalColumns() != null) {
                tokens  = new StringTokenizer(criteria.getSubTotalColumns(),",",false);
                while(tokens.hasMoreTokens()) {
                    token = tokens.nextToken();
					if(metaData.getColumnMetaData(token)!= null)
                    	subTotalColumns.add( token );
                }
            }

            form.setSearchCriteria(criteria.getFilter());
			form.setCriteriaDesc(criteria.getCriteriaDesc());
			form.setCriteriaName(criteria.getCriteriaName());
			form.setChartType(criteria.getChartType());
			form.setChartJs(criteria.getChartJs());
			form.setSharedGroup( criteria.getSharedGroup());
			form.setSharedAll( criteria.getSharedAll() );
            form.setOwner( criteria.getOwner() );
            form.setShowSubtotalDetail( criteria.isShowSubtotalDetail() );
        }
		else {

			if(form.getDisplayables() != null) {
				for(Iterator iterator = form.getDisplayables().iterator();iterator.hasNext();) {
					columnMetaData = (ColumnMetaData)iterator.next();
					if(metaData.getColumnMetaData(columnMetaData.getName())!= null)
						displayables.add(columnMetaData.getName());
				}
			}
			if(form.getAggregatables() != null) {
				for(Iterator iterator = form.getAggregatables().iterator();iterator.hasNext();) {
					columnMetaData = (ColumnMetaData)iterator.next();
					if(metaData.getColumnMetaData(columnMetaData.getName())!= null)
						aggregatables.add(columnMetaData.getName());
				}
			}
			form.setSearchCriteria(null);
			form.setCriteriaDesc(null);
            form.setShowSubtotalDetail( true );
        }

        if( customDisplayables.size() == 0 ){
            for(int i=0;i<displayables.size();i++){
                if( !aggregatables.contains(displayables.get( i ) ) && !customDisplayables.contains( displayables.get( i )) ) {
                        customDisplayables.add(displayables.get(i));
                }

            }
            for(int i=0;i<aggregatables.size();i++){
                if(!customDisplayables.contains(aggregatables.get( i )) ) {
                    customDisplayables.add(aggregatables.get(i));
                }
            }
        }

        Vector  aggregatablesCols = form.getAggregatablesColumnNames();
        Vector  displayablesCols = form.getDisplayablesColumnNames();

        for(int i=0;i<displayablesCols.size();i++){
            if( !aggregatablesCols.contains( displayablesCols.get( i ) ) && !columnSortOrderSeq.contains( displayablesCols.get( i )) ) {
                columnSortOrderSeq.add( displayablesCols.get( i ));
                columnSortOrder.add(String.valueOf( AVConstants._ASC ) );
            }
        }

        for(int i=0;i<aggregatablesCols.size();i++){
            if(!columnSortOrderSeq.contains( aggregatablesCols.get( i ) ) ) {
                columnSortOrderSeq.add( aggregatablesCols.get( i ));
                columnSortOrder.add( String.valueOf( AVConstants._ASC ) );
            }
        }

        String[] displayableList = new String[ displayables.size() ];
		displayableList = ( String[] ) displayables.toArray( displayableList );

		String[] aggregatableList = new String[ aggregatables.size() ];
		aggregatableList= ( String[] ) aggregatables.toArray( aggregatableList );

        String[] customDisplayableList = new String[ customDisplayables.size() ];
        customDisplayableList = ( String[] ) customDisplayables.toArray( customDisplayableList );

        String[] columnSortOrderSeqList = new String[ columnSortOrderSeq.size() ];
        columnSortOrderSeqList = ( String[] ) columnSortOrderSeq.toArray( columnSortOrderSeqList );

        String[] columnSortOrderList = new String[ columnSortOrder.size() ];
        columnSortOrderList = ( String[] ) columnSortOrder.toArray( columnSortOrderList );

        String[] subTotalColumnsList = new String[ subTotalColumns.size() ];
        subTotalColumnsList = ( String[] ) subTotalColumns.toArray( subTotalColumnsList );

        form.setSelectedDisplayables(displayableList);
        form.setSelectedSubTotalColumns( subTotalColumnsList );
        form.setSelectedAggregatables(aggregatableList);
        form.setCustomDisplayableList( customDisplayableList );
        form.setColumnSortOrderSeqList( columnSortOrderSeqList );
        form.setColumnSortOrderList( columnSortOrderList );
    }

	public UserCriteria getUserCriteria(UserCriteriaForm form,HttpServletRequest request)
	{
		UserCriteria criteria = new UserCriteria(form.getEditorName(),
												 form.getEditorType(),
												 form.getUserId(),
												 getCriteriaName(form),
												 getCriteriaDesc(form),
												 getSelectedDisplayables(form),
												 getSelectedAggregatables(form),
												 form.getSearchCriteria(),
												 form.getSharedAll(),
												 form.getSharedGroup(),
                                                 form.getOwner(),
                                                 getCustomDisplayables(form),
                                                 getColumnSortOrderSeqList(form),
                                                 getColumnSortOrderList(form,request),
                                                 getSubTotalColumns(form),
                                                 form.isShowSubtotalDetail()
                                            );
		criteria.setChartType( form.getChartType() );
		criteria.setChartJs( form.getChartJs() );
		
		return criteria;
	}

	public String getCriteriaName(UserCriteriaForm form)
	{
		String criteriaName = null;
		if(form.getNewCriteriaName() != null && form.getNewCriteriaName().length() > 0)
			criteriaName = form.getNewCriteriaName().trim();
		else if(form.getCriteriaName() != null)
			criteriaName = form.getCriteriaName();
		return criteriaName;
	}

	public String getCriteriaDesc(UserCriteriaForm form)
	{
		String criteriaDesc = null;
		if(form.getNewCriteriaName() != null && form.getNewCriteriaName().length() > 0 &&
		   form.getNewCriteriaDesc() != null && form.getNewCriteriaDesc().length() > 0 )
			criteriaDesc = form.getNewCriteriaDesc().trim();
		else if(form.getCriteriaDesc() != null)
			criteriaDesc = form.getCriteriaDesc();
		return criteriaDesc;
	}

	public String getSelectedDisplayables(UserCriteriaForm form)
	{
		String displayables = null;
		if(form.getSelectedDisplayables() != null){
			displayables = "";
			for(int i=0;i<form.getSelectedDisplayables().length;i++)
				displayables += "," +  form.getSelectedDisplayables()[i];

			displayables = (displayables.length() > 0) ? displayables.substring(1):null;
		}
		return displayables;
	}

	public String getSelectedAggregatables(UserCriteriaForm form)
	{
		String aggregatables = null;
		if(form.getSelectedAggregatables() != null){
			aggregatables = "";
			for(int i=0;i<form.getSelectedAggregatables().length;i++)
				aggregatables += "," +  form.getSelectedAggregatables()[i];

			aggregatables = (aggregatables.length() > 0) ? aggregatables.substring(1):null;
		}
		return aggregatables;
	}

    public String getCustomDisplayables(UserCriteriaForm form)
    {
        String customdisplayables = null;
        if(form.getCustomDisplayableList() != null){
            customdisplayables = "";
            for(int i=0;i<form.getCustomDisplayableList().length;i++)
                customdisplayables += "," +  form.getCustomDisplayableList()[i];

            customdisplayables = (customdisplayables.length() > 0) ? customdisplayables.substring(1):null;
        }
        return customdisplayables;
    }

    public String getSubTotalColumns(UserCriteriaForm form)
    {
        String subTotalColumns = null;
        if(form.getSelectedSubTotalColumns() != null){
            subTotalColumns = "";
            for(int i=0;i<form.getSelectedSubTotalColumns().length;i++)
                subTotalColumns += "," +  form.getSelectedSubTotalColumns()[i];

            subTotalColumns = (subTotalColumns.length() > 0) ? subTotalColumns.substring(1):null;
        }
        return subTotalColumns;
    }

    public String getColumnSortOrderSeqList(UserCriteriaForm form)
    {
        Vector customDisplayable = new Vector();
        if(form.getCustomDisplayableList() != null){
            for(int i=0;i<form.getCustomDisplayableList().length;i++) {
                customDisplayable.add( form.getCustomDisplayableList()[i] );
            }
        }
        String columnSortOrderSeqStr = "";
        String columnSortOrderSeqList[] = form.getColumnSortOrderSeqList();
        if( columnSortOrderSeqList != null ){
            for(int i=0;i<columnSortOrderSeqList.length;i++){
                if(customDisplayable.contains( columnSortOrderSeqList[i] ) ) {
                    columnSortOrderSeqStr += "," +  columnSortOrderSeqList[i];
                }
            }
            columnSortOrderSeqStr = (columnSortOrderSeqStr.length() > 0) ? columnSortOrderSeqStr.substring(1):null;
        }
        return columnSortOrderSeqStr;
    }

    public String getColumnSortOrderList(UserCriteriaForm form,HttpServletRequest request)
    {
        Vector customDisplayable = new Vector();
        if(form.getCustomDisplayableList() != null){
            for(int i=0;i<form.getCustomDisplayableList().length;i++) {
                customDisplayable.add( form.getCustomDisplayableList()[i] );
            }
        }
        String sortOrder = null;
        String columnSortOrderStr = "";
        String columnSortOrderSeqList[] = form.getColumnSortOrderSeqList();
        if( columnSortOrderSeqList != null ){
            for(int i=0;i<columnSortOrderSeqList.length;i++){
                if(customDisplayable.contains( columnSortOrderSeqList[i] ) ) {
                    sortOrder = request.getParameter( columnSortOrderSeqList[i] );
                    if( !StrUtl.isEmptyTrimmed( sortOrder ) ) {
                        columnSortOrderStr += "," +  Integer.parseInt( sortOrder );
                    }
                    else {
                        columnSortOrderStr += "," +   AVConstants._ASC;
                    }
                }
            }
            columnSortOrderStr = (columnSortOrderStr.length() > 0) ? columnSortOrderStr.substring(1):null;

            Vector columnSortOrder = new Vector();
            for(int i=0;i<columnSortOrderSeqList.length;i++){
                sortOrder = request.getParameter( columnSortOrderSeqList[i] );
                if( !StrUtl.isEmptyTrimmed( sortOrder ) ) {
                    columnSortOrder.add( sortOrder );
                }
                else {
                    columnSortOrder.add( String.valueOf( AVConstants._ASC ) );
                }
            }
            String[] columnSortOrderList = new String[ columnSortOrder.size() ];
            columnSortOrderList = ( String[] ) columnSortOrder.toArray( columnSortOrderList );
            form.setColumnSortOrderList( columnSortOrderList );
        }
        return columnSortOrderStr;
    }


    public void setCriteriaNames(UserCriteriaForm form) throws XRuntime
	{
//		ArrayList defCriterias = getCriterias(form.getEditorName(),form.getEditorType(),"all", form.getUserGroups());
		ArrayList criterias = getCriterias(form.getEditorName(),form.getEditorType(),form.getUserId(),form.getUserGroups());
//		criterias.addAll(0,defCriterias.subList(0,defCriterias.size()));
//		ArrayList list = addCriteriaNames( form.getMetadata().getName(),form.getMetadata().getType(),form.getUserId(),form.getUserGroups());
//		criterias.addAll(0,list.subList(0,list.size()));
		UserCriteria criteria = null;
		ArrayList criteriaNames = new ArrayList();
		for(Iterator iterator = criterias.iterator();iterator.hasNext(); ) {

			criteria = (UserCriteria) iterator.next();
			criteriaNames.add( new LabelValueBean( criteria.getCriteriaName(), criteria.getCriteriaName() ) );
			if(form.getCriteriaName() != null && criteria.getCriteriaName().equals( form.getCriteriaName() ) ){
                form.setCriteria(criteria);
           }

		}
		form.setCriteriaNames(criteriaNames);
	}

	public void setCriteriaNames(UserCriteriaSearchForm form) throws XRuntime
	{
//		ArrayList defCriterias = getCriterias(form.getMetadata().getName(),form.getMetadata().getType(),"all",form.getUserGroups());
		ArrayList criterias = getCriterias(form.getMetadata().getName(),form.getMetadata().getType(),form.getUserId(),form.getUserGroups());
//		criterias.addAll(0,defCriterias.subList(0,defCriterias.size()));
//		ArrayList list = addCriteriaNames( form.getMetadata().getName(),form.getMetadata().getType(),form.getUserId(),form.getUserGroups());
//		criterias.addAll(0,list.subList(0,list.size()));
		UserCriteria criteria = null;
		ArrayList criteriaNames = new ArrayList();
        form.setCriteriaDesc( "" );

        for(Iterator iterator = criterias.iterator();iterator.hasNext(); ) {
			criteria = (UserCriteria) iterator.next();
			criteriaNames.add( new LabelValueBean( criteria.getCriteriaName(), criteria.getCriteriaName() ) );
			if(form.getCriteriaName() != null && criteria.getCriteriaName().equals( form.getCriteriaName() ) ) {
                form.setCriteria(criteria);
                form.setCriteriaDesc(criteria.getCriteriaDesc());
			}
		}
		form.setCriteriaNames(criteriaNames);
	}

	public ArrayList getCriterias(String editorName, String editorType, String userId, ArrayList userGroups) throws XRuntime
	{
		try {

			Hashtable param = new Hashtable();
			param.put("EDITOR_NAME",editorName);
			param.put("EDITOR_TYPE",editorType);
			param.put("USER_NAME",userId);
			return setCriteriaList(param,null,editorName,editorType,userId, userGroups);
		}
		catch(Exception ex) {
			throw new XRuntime(getSubsystem(),ex.getMessage());
		}
	}
	public ArrayList addCriteriaNames( String editorName, String editorType, String userId, ArrayList userGroups)
	{
		try {
			Hashtable param = new Hashtable();
			Hashtable operators = new Hashtable();
			param.put("EDITOR_NAME",editorName);
			param.put("EDITOR_TYPE",editorType);
			param.put("SHARED_WITH_ALL","1");
			param.put("USER_NAME",userId);
			operators.put("USER_NAME",String.valueOf(AVConstants._NOT_EQUAL));
			return setCriteriaList(param,operators,editorName,editorType,userId,userGroups);
		}
		catch(Exception ex) {
			throw new XRuntime(getSubsystem(),ex.getMessage());
		}
	}
	public void setResultset(UserCriteriaSearchForm form, HttpServletRequest request,String userName) throws XRuntime
	{
		try {
			String kindOfAction = form.getKindOfAction() != null ? form.getKindOfAction() : "";
			String homeName = Environment.getInstance(getSubsystem()).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSTableManagerBeanName", "EJBSTableManager");
			EJBSTableManagerHome   tableManagerHome   = ( EJBSTableManagerHome ) EJBEnvironment.lookupEJBInterface(  getSubsystem(), homeName , EJBSTableManagerHome.class );
			EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();
            String  sortName = form.getSort_Name() != null ? form.getSort_Name() : "";
            String  sortOrder = form.getSort_Order() != null ? form.getSort_Order() :"";
			EJBResultSet ejbRS = null;
            UserCriteria criteria = null;
            HttpSession currSession = request.getSession( false );
            if( kindOfAction.equals("search") || kindOfAction.equals("apply") ) {

                request.setAttribute("exactSearch",new Boolean( form.isExactSearch() ) );

                if(form.getCriteriaName() != null && form.getCriteriaName().length() > 0 ) {
                    criteria = form.getCriteria();
					if (criteria != null) {
                    	criteria.setSortName( sortName );
                    	criteria.setSortOrder( sortOrder );

                        Pair pair = getEditorMetaDataSQLPair(form.getMetadata(),criteria ,request,form.isAdditionalFilter());
                    	form.setMetadata( ( EditorMetaData ) pair.getFirst() );
						ejbRS = tableManagerRemote.lookup( (String)pair.getSecond() , form.getMetadata() );
                    	currSession.setAttribute(userName + "_" + form.getMetadata().getName() + "_USER_CRITERIA",criteria);
					}
					else {
						//EJBCriteria ejbCriteria = ( new EJBResultSetMetaData( form.getMetadata() ) ).getNewCriteria();
						EJBCriteria ejbCriteria = EjbUtils.getEJBCriteria ( form.getMetadata(), request, true );
						ejbCriteria.orderBy ( EjbUtils.setOrderBy( form.getMetadata() , request )  );
						ejbRS = tableManagerRemote.lookup( ejbCriteria );
						currSession.setAttribute(userName + "_" + form.getMetadata().getName() + "_EJB_CRITERIA",ejbCriteria);
					}
    			}
				else {
                    criteria = (UserCriteria) currSession.getAttribute(userName + "_" + form.getMetadata().getName() + "_USER_CRITERIA");
					if ( criteria != null ) {
                        criteria.setSortName( sortName );
                        criteria.setSortOrder( sortOrder );
                        form.setCriteria( criteria );
                        Pair pair = getEditorMetaDataSQLPair(form.getMetadata(),form.getCriteria(),request,form.isAdditionalFilter());
                        form.setMetadata( ( EditorMetaData ) pair.getFirst() );
						ejbRS = tableManagerRemote.lookup( (String)pair.getSecond() , form.getMetadata() );
                        currSession.setAttribute(userName + "_" + form.getMetadata().getName() + "_USER_CRITERIA",criteria);
					}
					else {
						//EJBCriteria ejbCriteria = ( new EJBResultSetMetaData( form.getMetadata() ) ).getNewCriteria();
						EJBCriteria ejbCriteria = EjbUtils.getEJBCriteria ( form.getMetadata(), request, true );
						ejbCriteria.orderBy ( EjbUtils.setOrderBy( form.getMetadata() , request )  );
						ejbRS = tableManagerRemote.lookup( ejbCriteria );
                        currSession.setAttribute(userName + "_" + form.getMetadata().getName() + "_EJB_CRITERIA",ejbCriteria);
					}
				}
			}
			form.setResultset(ejbRS);
		}
		catch(Exception ex) {
			throw new XRuntime(getSubsystem(),ex.getMessage());
		}
	}

    public Pair getEditorMetaDataSQLPair(EditorMetaData metaData, UserCriteria userCriteria,HttpServletRequest request,boolean isAdditionalFilter) throws XRuntime
	{
        StringTokenizer tokens  = null;
		String columnName = null;

		Vector displayables = new Vector();
		Vector aggregatables = new Vector();
        Vector customDisplayables = new Vector();
        Vector subTotalColumns = new Vector();
        Vector columnSortOrderSeq = new Vector();
        Vector columnSortOrder = new Vector();

        if( !StrUtl.isEmptyTrimmed( userCriteria.getColumnSortOrderSeq() )  ) {

            StringTokenizer sortOrderTokens  = new StringTokenizer(userCriteria.getColumnSortOrder() ,",",false);
            StringTokenizer sortOrderSeqTokens  = new StringTokenizer(userCriteria.getColumnSortOrderSeq() ,",",false);
            String sortOrder = "";
            if( sortOrderTokens.countTokens() == sortOrderSeqTokens.countTokens() ) {
                while(sortOrderSeqTokens.hasMoreTokens()) {
                    columnName = sortOrderSeqTokens.nextToken();
                    sortOrder = sortOrderTokens.nextToken();
                    if( metaData.getColumnMetaData(columnName) != null){
                        columnSortOrderSeq.add( columnName );
                        columnSortOrder.add( sortOrder );
                    }
                }
            }

        }

        if( !StrUtl.isEmptyTrimmed( userCriteria.getSubTotalColumns() )  ) {
            tokens  = new StringTokenizer( userCriteria.getSubTotalColumns() ,",",false);
            while(tokens.hasMoreTokens()) {
                columnName = tokens.nextToken();
				if(metaData.getColumnMetaData(columnName) != null)
                	subTotalColumns.add(columnName);
            }
        }

        if(userCriteria.getMeasure() != null) {
			tokens  = new StringTokenizer(userCriteria.getMeasure(),",",false);
			while(tokens.hasMoreTokens()) {
				columnName = tokens.nextToken();
				if(metaData.getColumnMetaData(columnName) != null)
					aggregatables.add(columnName);
			}
		}
		if(userCriteria.getDimension() != null) {
			tokens  = new StringTokenizer(userCriteria.getDimension(),",",false);
			while(tokens.hasMoreTokens()) {
				columnName = tokens.nextToken();
				if(metaData.getColumnMetaData(columnName) != null)
					displayables.add(columnName);
			}
		}
        // Arrange the column based on Custom Display.
        if( !StrUtl.isEmptyTrimmed( userCriteria.getCustomDisplayable() )  ) {
            tokens  = new StringTokenizer(userCriteria.getCustomDisplayable(),",",false);
            while(tokens.hasMoreTokens()) {
                columnName = tokens.nextToken();
				if(metaData.getColumnMetaData(columnName) != null)
                	customDisplayables.add( columnName );
            }
        }

        if(customDisplayables.size() == 0){
            for(int i=0;i<displayables.size();i++){
                if( !aggregatables.contains( displayables.get( i ) ) && !customDisplayables.contains( displayables.get( i )) ) {
					if(metaData.getColumnMetaData(String.valueOf(displayables.get( i ))) != null)
                    	customDisplayables.add( displayables.get( i ));
                }
            }
            for(int i=0;i<aggregatables.size();i++){
                if(!customDisplayables.contains( aggregatables.get( i ) ) ) {
                    customDisplayables.add( aggregatables.get( i ));
                }
            }
        }
        ColumnMetaData 	columnMetaData	= null;
        ColumnMetaData 	groupingColumnMetaData	= null;

/* Build SELECT */
/* Build GROUP BY (- add all Dimensions to group by) */
/* Build HAVING */
/* BUILD ORDER BY*/

        StringBuffer selectbuf = new StringBuffer(512);
        StringBuffer groupbybuf = new StringBuffer(512);
        StringBuffer havingClause1 = new StringBuffer(512);
        StringBuffer orderbybuf = new StringBuffer(512);

        /* Subtotal column as first in Group by Rollup function and order by */
        for(int i=0;i<subTotalColumns.size();i++){
            groupbybuf.append( ", " ).append( subTotalColumns.get( i ) );
            orderbybuf.append(",").append( subTotalColumns.get( i ) ).append( " " ).append( "ASC" );
        }

        Vector columnsMetaData = new Vector();
        for(int i=0;i<customDisplayables.size();i++){
            columnMetaData = metaData.getColumnMetaData( (String) customDisplayables.get(i) ) ;

            if (columnMetaData != null && columnMetaData.getType() != ColumnDataType._CDT_LINK) {
                if( aggregatables.contains( columnMetaData.getName() ) ) {
                    if (columnMetaData.getExpression() == null){
                        selectbuf.append( ",NULL AS ").append( columnMetaData.getName() );
                    }
                    else{
                        selectbuf.append( ", " ).append( columnMetaData.getExpression() ).append( " AS " ).append( columnMetaData.getName() );
                    }
                    columnsMetaData.add( columnMetaData.clone() );
                }
                else if( displayables.contains( columnMetaData.getName() ) ) {
                    selectbuf.append( ", " ).append( columnMetaData.getName() );
                    columnsMetaData.add( columnMetaData.clone() );
                    groupingColumnMetaData = (ColumnMetaData) columnMetaData.clone();
                    if ( columnMetaData.getName().length() <= 28 ) {
						selectbuf.append( ", GROUPING(" ).append( columnMetaData.getName() ).append(") AS ").append( "G_" ).append( columnMetaData.getName() );
                    	groupingColumnMetaData.getColumnInfo().setName( "G_" + columnMetaData.getName() );

					}
                    else {
                    	selectbuf.append( ", GROUPING(" ).append( columnMetaData.getName() ).append(") AS ").append( "G_" ).append(columnMetaData.getName().substring( 0 , 28 ) );
                    	groupingColumnMetaData.getColumnInfo().setName( "G_" + columnMetaData.getName().substring( 0 , 28 ));
					}
                    groupingColumnMetaData.setDisplayable( false );
                    groupingColumnMetaData.setSearchable( false );
                    groupingColumnMetaData.getColumnInfo().setType( ColumnDataType._CDT_STRING_ );
                    columnsMetaData.add( groupingColumnMetaData );

                    havingClause1.append( "+ GROUPING(" ).append( columnMetaData.getName() ).append(") " );

                    if( !subTotalColumns.contains( columnMetaData.getName() ) ) {
                        groupbybuf.append( ", " ).append( columnMetaData.getName() );
                        orderbybuf.append(",").append( columnMetaData.getName() ).append( " " ).append( "ASC" );
                    }
                }
            }
        }

        if( selectbuf.length() ==  0 ){
			throw new XRuntime(getSubsystem(),"Please Select any Displayable [ OR ] Aggregatable Column(s).");
        }

/* Build HAVING */

        if( havingClause1.length() > 0 ) {
            havingClause1 = new StringBuffer( havingClause1.substring(1) );
        }

        StringBuffer havingClause2 = new StringBuffer(512);
        StringBuffer havingClause3 = new StringBuffer(512);

        for(int i=0;i<customDisplayables.size();i++){
            columnMetaData = metaData.getColumnMetaData( (String) customDisplayables.get(i) ) ;

            if(  columnMetaData != null && columnMetaData.getType() != ColumnDataType._CDT_LINK
                    && displayables.contains( columnMetaData.getName() )
                    && !aggregatables.contains( columnMetaData.getName() ) ) {

                    if(!subTotalColumns.contains( columnMetaData.getName() ) ){
                        havingClause2.append( "AND GROUPING(" ).append( columnMetaData.getName() ).append(") = 1 " );
                    }
                    havingClause3.append( "AND GROUPING(" ).append( columnMetaData.getName() ).append(") = 1 " );
            }
        }
        if( havingClause2.length() > 0 ) {
            havingClause2 = new StringBuffer( havingClause2.substring( "AND".length() ) );
        }
        if( havingClause3.length() > 0 ) {
            havingClause3 = new StringBuffer( havingClause3.substring( "AND".length() ) );
        }

/* Build WHERE */
        StringBuffer whereClause = new StringBuffer(512);

        if( isAdditionalFilter ){

            EJBCriteria ejbCriteria = EjbUtils.getEJBCriteria ( metaData, request, true );
            EJBResultSetMetaData rsmetaData = new EJBResultSetMetaData( metaData );
            String _serverType ="";
            final EJBSQLBuilderUtils sqlBuilder  = new EJBSQLBuilderUtils( _serverType );
            String whereClauseStr = sqlBuilder.buildWhere( ejbCriteria , rsmetaData );
            if( whereClauseStr.length() > 0 ){
                whereClauseStr = whereClauseStr.substring( "WHERE".length() );
            }
            if( !StrUtl.isEmptyTrimmed( whereClauseStr ) ) {
                whereClause.append( whereClauseStr );
            }
        }

        if( !StrUtl.isEmptyTrimmed( userCriteria.getFilter() ) ) {
            if( whereClause.length() > 0) {
                whereClause.append( " AND " ).append( userCriteria.getFilter() );
            }
            else{
                whereClause.append( userCriteria.getFilter() );
            }
        }
/* Build Select Statement */
        StringBuffer sqlbuf = new StringBuffer( 512 );
        sqlbuf.append( " SELECT ");
        if( selectbuf.length() > 0 ){
        	sqlbuf.append( selectbuf.toString().substring(1) );
        }
        sqlbuf.append( " FROM " ).append( metaData.getSource() );

        if( whereClause.length() > 0) {
            sqlbuf.append( " WHERE " ).append( whereClause );
        }

        if( groupbybuf.length() > 0 ){
            sqlbuf.append( " GROUP BY ROLLUP (" ).append( groupbybuf.toString().substring( 1 ) ).append( ") " );
        }


        if( havingClause1.length() > 0 ){
            StringBuffer havingClause = new StringBuffer( 512 );
            /* Subtotal is empty */
            if( StrUtl.isEmptyTrimmed( userCriteria.getSubTotalColumns() ) ){
                havingClause.append(" ( ").append( havingClause1 ).append( " = 0 ").append( " ) " );
                havingClause.append(" OR ( ").append( havingClause3 ).append (" ) ");
            }
            else {
                if( userCriteria.isShowSubtotalDetail() == true || havingClause2.length() == 0 ) {
                    havingClause.append(" ( ").append( havingClause1 ).append( " = 0 ").append( " ) " );
                }
                if( havingClause2.length() > 0 ) {
                    if(havingClause.length() > 0){
                        havingClause.append(" OR ");
                    }
                    havingClause.append(" ( ").append( havingClause2 ).append (" ) ");
                }
            }

            if(havingClause.length() > 0){
                sqlbuf.append( " HAVING ( " ).append( havingClause ).append( " ) " );
            }
        }

        if( StrUtl.isEmptyTrimmed( userCriteria.getSubTotalColumns() ) && !StrUtl.isEmptyTrimmed( userCriteria.getSortName() )  ) {
            /* Build ORDER BY */
            if( aggregatables.contains( userCriteria.getSortName() ) || displayables.contains( userCriteria.getSortName() ) ) {
                if( columnSortOrderSeq.size() > 0 ){
                    int columnIndex = columnSortOrderSeq.indexOf( userCriteria.getSortName() );
                    if( columnIndex != -1 ) {
                        columnSortOrderSeq.remove( columnIndex );
                        columnSortOrder.remove( columnIndex );
                    }
                    columnSortOrderSeq.add(0, userCriteria.getSortName() );
                    columnSortOrder.add(0,"DESC".equalsIgnoreCase( userCriteria.getSortOrder() ) ? String.valueOf( AVConstants._DESC ) : String.valueOf( AVConstants._ASC ) );

                    orderbybuf = new StringBuffer();
                    String orderBy = null;
                    for(int i=0;i<columnSortOrderSeq.size();i++){
                        orderBy = columnSortOrder.get( i ).equals( String.valueOf( AVConstants._ASC ) ) ? "ASC" : "DESC";
                        orderbybuf.append(",").append( columnSortOrderSeq.get(i) ).append( " " ).append( orderBy );
                    }
                }
                else {
                    String columnsWithOrder1[] = StringUtils.split(orderbybuf.toString(),",");
                    orderbybuf = new StringBuffer(512);
                    for(int i=0;i<columnsWithOrder1.length;i++){
                        String columnsWithOrder2[] = StringUtils.split( columnsWithOrder1[i] );
                        boolean matches = false;
                        for(int j=0;j<columnsWithOrder2.length;j++){
                            if( userCriteria.getSortName().equalsIgnoreCase( columnsWithOrder2[j] ) ) {
                                matches = true;
                            }
                        }
                        if( !matches ){
                            orderbybuf.append(",").append( columnsWithOrder1[i] );
                        }
                    }
                    String orderBy = "DESC".equalsIgnoreCase( userCriteria.getSortOrder() ) ? userCriteria.getSortOrder() : "ASC";
                    orderbybuf.insert(0,"," + userCriteria.getSortName() + " " + orderBy);
                }
            }
        }

        if( orderbybuf.length()  > 0) {
            sqlbuf.append( " ORDER BY " ).append( orderbybuf.substring(1) );
        }

//Set New EditorMetadata.
        EditorMetaData newMetaData = (EditorMetaData)(metaData.clone());
        if(columnsMetaData.size() > 0) {
            newMetaData.setColumnsMetaData( columnsMetaData );
        }
        return new Pair( newMetaData, sqlbuf.toString() );
	}
/*
	public void validateUser(EditorMetaData metadata, UserCriteria userCriteria, String userName) throws XRuntime
	{
		try {
			Hashtable hash = new Hashtable();
			hash.put( "EDITOR_NAME", userCriteria.getEditorName() );
			hash.put( "EDITOR_TYPE", userCriteria.getEditorType() );
			hash.put( "USER_NAME", userCriteria.getUserName() );
			hash.put( "CRITERIA_NAME", userCriteria.getCriteriaName() );

			String homeName = Environment.getInstance(getSubsystem()).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSEditorMetaDataBeanName","");
			EJBSEditorMetaDataHome   metaDataHome   = ( EJBSEditorMetaDataHome ) EJBEnvironment.lookupEJBInterface( getSubsystem(), homeName ,EJBSEditorMetaDataHome.class );
			EJBSEditorMetaDataRemote metaDataRemote = metaDataHome.create( );
			EditorMetaData editorMetaData = metaDataRemote.lookup( "USER_CRITERIA", "DEFAULT" );
			homeName = Environment.getInstance(getSubsystem()).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSTableManagerBeanName", "EJBSTableManager");
			EJBSTableManagerHome   tableManagerHome   = ( EJBSTableManagerHome ) EJBEnvironment.lookupEJBInterface( getSubsystem(), homeName , EJBSTableManagerHome.class );
			EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();
			EJBCriteria ejbCriteria = ( new EJBResultSetMetaData( editorMetaData ) ).getNewCriteria();
			ejbCriteria.where ( hash, null );
			EJBResultSet ejbRS = tableManagerRemote.lookup( ejbCriteria );

			if (ejbRS == null || !ejbRS.next()) {
				throw new XRuntime(getSubsystem(),"This Criteria is shared by some other user.  You can't Edit/Delete this criteria" );
			}
		}
		catch(XRuntime xr) {
			throw new XRuntime(getSubsystem(),xr.getMessage() );
		}
		catch(Exception ex) {
			throw new XRuntime(getSubsystem(),"Invalid Search Criteria. Please check the Criteria." );
		}
	}
*/
    public void validateUser(UserCriteria userCriteria) throws XRuntime
    {
        if( !StrUtl.isEmptyTrimmed( userCriteria.getOwner() ) && !userCriteria.getUserName().equalsIgnoreCase( userCriteria.getOwner() ) ) {
            throw new XRuntime(getSubsystem(), "Edit/delete action can only be performed by criteria owner. "+ userCriteria.getCriteriaName() + " criteria is owned by " + userCriteria.getOwner() );
        }
    }

    public void validateWhereClause(EditorMetaData metadata, UserCriteria userCriteria) throws XRuntime
	{
		try {
			if(userCriteria.getFilter() == null || userCriteria.getFilter().length() == 0 ) {
				return;
            }

			StringBuffer selectbuf = new StringBuffer(512);

			ColumnMetaData 	columnMetaData	= null;
			for(Iterator iterator = metadata.getColumnsMetaData().iterator();iterator.hasNext();) {

				columnMetaData = (ColumnMetaData) iterator.next();
				if (columnMetaData.getType() != ColumnDataType._CDT_LINK)
					selectbuf.append( ", " ).append( columnMetaData.getName() );
			}

			StringBuffer sqlbuf = new StringBuffer(512);
			sqlbuf.append( " SELECT ");

        	if( selectbuf.length() > 0 )
        		sqlbuf.append( selectbuf.toString().substring(1) );
        	else
        		throw new XRuntime(getSubsystem(),"Please Select any Displayable [ OR ] Aggregatable Column(s).");

			sqlbuf.append( " FROM ").append( metadata.getSource() );
			sqlbuf.append( " WHERE 1=0 AND ").append( userCriteria.getFilter() );

			String homeName = Environment.getInstance(getSubsystem()).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSTableManagerBeanName", "EJBSTableManager");
			EJBSTableManagerHome   tableManagerHome   = ( EJBSTableManagerHome ) EJBEnvironment.lookupEJBInterface(  getSubsystem(), homeName , EJBSTableManagerHome.class );
			EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();
			tableManagerRemote.lookup( sqlbuf.toString(), metadata  );
		}
		catch(Exception ex) {
			throw new XRuntime(getSubsystem(),"Invalid Search Criteria. Please check the Criteria.");
		}
	}

	public Vector getDisplayableColumns(EditorMetaData metadata)
	{
		Vector columnNames = new Vector();
		ColumnMetaData 	columnMetaData	= null;
		if(metadata.getAggregatableColumns() != null) {
			for(Iterator iterator =metadata.getAggregatableColumns().iterator();iterator.hasNext(); ) {
				columnMetaData = (ColumnMetaData)iterator.next();
				columnNames.add(columnMetaData.getName());
			}
		}
		Vector columnsMetaData = new Vector();
		if(metadata.getDisplayableColumns() != null) {
			for(Iterator iterator = metadata.getDisplayableColumns().iterator();iterator.hasNext(); ) {
				columnMetaData = (ColumnMetaData)iterator.next();
				if(!columnNames.contains(columnMetaData.getName()))
					columnsMetaData.add( columnMetaData );
			}
		}
		return columnsMetaData.size() > 0 ? columnsMetaData : null;
	}

	public ArrayList setCriteriaList(Hashtable param,Hashtable operators,String editorName, String editorType, String userId,ArrayList userGroups) throws Exception
	{
		UserCriteriaMgr manager = new UserCriteriaMgr(getSubsystem());
//		EJBResultSet ejbRS = manager.lookup( param,operators );
		EJBResultSet ejbRS = manager.lookup( editorName, editorType, userId, userGroups );
		UserCriteria criteria = null;
		ArrayList criterias = new ArrayList();
		if(ejbRS != null) {

			ResultSet rs = ( ResultSet ) ejbRS;
			while(rs.next()) {
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
            	criteria.setChartType( rs.getString("CHART_TYPE") );
            	criteria.setChartJs( rs.getString("CHART_JS") );

            	criterias.add(criteria);

			}
		}
		return criterias;
	}

    public boolean isCriteriaChanged(UserCriteria userCriteria) throws Exception {
        if( !StrUtl.isEmptyTrimmed( userCriteria.getCriteriaName() ) ) {
            //Compare the criteria Values and set the criteria Name.
            UserCriteriaMgr manager = new UserCriteriaMgr(getSubsystem());
            UserCriteria userCriteriaDB  = manager.lookup( userCriteria.getCriteriaName(),userCriteria.getEditorName(),userCriteria.getEditorType(),userCriteria.getUserName() );
            if( userCriteriaDB != null){
                if( !stringEquals(userCriteria.getDimension(),userCriteriaDB.getDimension() ) ) return true;
                if( !stringEquals(userCriteria.getMeasure(),userCriteriaDB.getMeasure() ) ) return true;
                if( !StrUtl.equals(userCriteria.getFilter(),userCriteriaDB.getFilter() ) ) return true;
                if( !stringEquals(userCriteria.getCustomDisplayable() ,userCriteriaDB.getCustomDisplayable() ) ) return true;
                if( !stringEquals(userCriteria.getColumnSortOrderSeq() ,userCriteriaDB.getColumnSortOrderSeq() ) ) return true;
                if( !stringEquals(userCriteria.getColumnSortOrder() ,userCriteriaDB.getColumnSortOrder() ) ) return true;
            }
        }
        return false;
    }

    private boolean stringEquals(String thisStr,String thatStr){
        if(thisStr == null){
            return (thatStr == null);
        }
        if(thatStr == null){
            return (thisStr == null);
        }

        List thisList = new ArrayList();
        String thisStrArray[] = thisStr.split(",");
        for(int i=0;i<thisStrArray.length;i++){
            thisList.add(thisStrArray[i]);
        }

        List thatList = new ArrayList();
        String thatStrArray[] = thatStr.split(",");
        for(int i=0;i<thatStrArray.length;i++){
            thatList.add( thatStrArray[i] );
        }

        String value = null;
        for(int i=0;i<thisList.size();i++){
            value = (String) thisList.get( i );
            if(!thatList.contains( value ) ) {
                return false;
            }
        }
        for(int i=0;i<thatList.size();i++){
            value = (String) thatList.get( i );
            if(!thisList.contains( value ) ) {
                return false;
            }
        }
        return true;
    }
}
