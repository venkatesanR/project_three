/*
 * AggregatableListProcessAction.java
 *
 * Created on August 12, 2003, 7:01 PM
 */

package com.addval.springstruts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.ColumnMetaData;
import com.addval.ejbutils.server.EJBSTableManager;
import com.addval.utils.StrUtl;
import com.addval.environment.Environment;
import com.addval.ejbutils.dbutils.EJBResultSet;
import java.util.Vector;
import java.util.StringTokenizer;
import org.apache.struts.util.LabelValueBean;


/**
 *
 * @author  ravi
 */
public class AggregatableListProcessAction extends BaseAction
{

	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(AggregatableListProcessAction.class);

	/** Creates a new instance of AggregatableListProcessAction */
	public AggregatableListProcessAction()
	{
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception
	{

		BaseActionMapping baseMapping = (BaseActionMapping) mapping;

		_logger.trace( "execute.traceEnter" );

		try{
			super.execute(mapping, form, req, res);
			AggregatableListForm listForm = (AggregatableListForm) form;
			EditorMetaData   editorMetaData = listForm.getMetadata();

			String filters = listForm.getFilters();
			Vector where = new Vector();

			StringTokenizer st = new StringTokenizer(filters,"#");
			while (st.hasMoreTokens()) {
				String filter = st.nextToken();
				where.add(new LabelValueBean( filter, filter ));
			}

			listForm.setSelectedFilters(where);

			//get fields and build sql

			StringBuffer sql = new StringBuffer(512);
			StringBuffer groupBy = new StringBuffer(128);


			sql.append("SELECT ");

			ColumnMetaData colMetaData = null;
			String colExpr;


			//select group by
			String[] cols = listForm.getGroupByColumns();

			if(cols != null && cols.length > 0){
				groupBy.append(" GROUP BY ");

				for(int i = 0; i < cols.length; i++){
					colMetaData = editorMetaData.getColumnMetaData(cols[i]);
					if(colMetaData == null)continue;

					sql.append(colMetaData.getName());
					if(listForm.getRollupOption()){
						groupBy.append("ROLLUP(").append(colMetaData.getName()).append(")");
					}
					else{
						groupBy.append(colMetaData.getName());
					}

					if(i < cols.length - 1){
						sql.append(", ");
						groupBy.append(", ");
					}
				}
			}

			//select measures
			cols = listForm.getMeasures();
			if(cols != null && cols.length > 0) {
				sql.append(", ");
				for(int i = 0; i < cols.length; i++){
					colMetaData = editorMetaData.getColumnMetaData(cols[i]);
					if(colMetaData == null)continue;
					colExpr = colMetaData.getExpression();
					if(StrUtl.isEmptyTrimmed(colExpr)){
						sql.append(colMetaData.getName());
					}
					else{
						sql.append(colExpr).append(" ").append(colMetaData.getName());
					}
					if(i < cols.length - 1)sql.append(", ");
				}
			}

			//from table
			sql.append(" FROM ").append(editorMetaData.getSource());

			if(!StrUtl.isEmptyTrimmed(listForm.getWhereClause())){
				sql.append(" WHERE ").append(listForm.getWhereClause());
			}

			sql.append(groupBy.toString());

			//order by ??

			String  sortName = req.getParameter("SORT_NAME" ) != null ? req.getParameter( "SORT_NAME" ) :"";

			if (sortName.equals(""))
				sortName = req.getParameter("sort_Name" ) != null ? req.getParameter( "sort_Name" ) :"";

			if (sortName.equals(""))
				sortName = (String) req.getAttribute( "SORT_NAME" ) != null ? (String) req.getAttribute( "SORT_NAME" ) : "";


			String sortOrder = req.getParameter("SORT_ORDER") != null ? req.getParameter( "SORT_ORDER" ) :"";

			if (sortOrder.equals(""))
				sortOrder = req.getParameter("sort_Order" ) != null ? req.getParameter( "sort_Order" ) :"ASC";

			if (sortOrder.equals(""))
				sortOrder = (String) req.getAttribute( "SORT_ORDER" ) != null ? (String) req.getAttribute( "SORT_ORDER" ) : "";

			if (! (sortName.equals("")) ) {
				sql.append(" ORDER BY ").append(sortName).append("  ").append(sortOrder);
			}

			if(listForm.getMetadata() == null){
				listForm.reset(mapping, req);
			}

			EJBResultSet ejbrs = getTableManager().lookup(sql.toString(), listForm.getMetadata());
			//listForm.setMetadata(ejbrs.getEJBResultSetMetaData().getEditorMetaData());
			listForm.setResultset(ejbrs);
			listForm.setSearch("true");




		}
		catch(Exception e) {
			_logger.error("Error looking up data in AggregatableListProcessAction");
			_logger.error(e);
			ActionErrors	errors = new ActionErrors();
			String error = null;
			if( e.getMessage().indexOf("ORA") != -1 ) {
				int ind = e.getMessage().indexOf(":",e.getMessage().indexOf("ORA"));
				error = e.getMessage().substring(ind+1,e.getMessage().length()-2);
			}
			if(error==null) {
				error = "Critical System Error - Contact System Administrator";
			}
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.invalid", error));
			saveErrors( req, errors );
			return mapping.findForward( ListAction._SEARCH_FORWARD );

		}

		_logger.trace( "execute.traceExit" );

		return mapping.findForward(ListAction._SEARCH_FORWARD);

	}

}
