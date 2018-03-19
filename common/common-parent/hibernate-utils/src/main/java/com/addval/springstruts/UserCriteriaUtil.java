package com.addval.springstruts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.util.LabelValueBean;

import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.server.EJBSQLBuilderUtils;
import com.addval.ejbutils.server.EJBSTableManager;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorFilter;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.UserCriteria;
import com.addval.metadata.UserCriteriaChart;
import com.addval.utils.AVConstants;
import com.addval.utils.Pair;
import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;
import com.addval.utils.flexcharts.xbeans.AggregatorType;
import com.addval.utils.flexcharts.xbeans.AxisRenderer;
import com.addval.utils.flexcharts.xbeans.CategoryAxis;
import com.addval.utils.flexcharts.xbeans.ChartCriteria;
import com.addval.utils.flexcharts.xbeans.ChartMetadata;
import com.addval.utils.flexcharts.xbeans.ChartSeries;
import com.addval.utils.flexcharts.xbeans.ChartSeriesType;
import com.addval.utils.flexcharts.xbeans.ChartType;
import com.addval.utils.flexcharts.xbeans.ColDimensions;
import com.addval.utils.flexcharts.xbeans.Columns;
import com.addval.utils.flexcharts.xbeans.DataGridColumn;
import com.addval.utils.flexcharts.xbeans.DataGridMetadata;
import com.addval.utils.flexcharts.xbeans.Dimension;
import com.addval.utils.flexcharts.xbeans.FlexCharts;
import com.addval.utils.flexcharts.xbeans.FlexChartsDocument;
import com.addval.utils.flexcharts.xbeans.FlexControl;
import com.addval.utils.flexcharts.xbeans.FlexControlDocument;
import com.addval.utils.flexcharts.xbeans.FlexControlType;
import com.addval.utils.flexcharts.xbeans.HorizontalAxis;
import com.addval.utils.flexcharts.xbeans.HorizontalAxisRenderers;
import com.addval.utils.flexcharts.xbeans.Layout;
import com.addval.utils.flexcharts.xbeans.LinearAxis;
import com.addval.utils.flexcharts.xbeans.Measure;
import com.addval.utils.flexcharts.xbeans.Measures;
import com.addval.utils.flexcharts.xbeans.OLAPCubeMetadata;
import com.addval.utils.flexcharts.xbeans.PlacementType;
import com.addval.utils.flexcharts.xbeans.RECORDSET;
import com.addval.utils.flexcharts.xbeans.RECORDSETDocument;
import com.addval.utils.flexcharts.xbeans.RowDimensions;
import com.addval.utils.flexcharts.xbeans.Series;
import com.addval.utils.flexcharts.xbeans.VBox;
import com.addval.utils.flexcharts.xbeans.VerticalAxis;
import com.addval.utils.flexcharts.xbeans.VerticalAxisRenderers;

public class UserCriteriaUtil {

	private static transient final Logger _logger = Logger.getLogger(UserCriteriaUtil.class);

	private EJBSTableManager _tableManager = null;

	public UserCriteriaUtil() {

	}

	public EJBSTableManager getTableManager() {
		return _tableManager;
	}

	public void setTableManager(EJBSTableManager tableManager) {
		_tableManager = tableManager;
	}

	public void setChartXML(UserCriteriaSearchForm ucRForm) throws Exception {
		UserCriteria userCriteria = ucRForm.getCriteria();
		EJBResultSet ejbRS = ucRForm.getResultset();
		if (userCriteria != null && userCriteria.getUserCriteriaCharts().size() > 0) {
			ArrayList ucCharts = userCriteria.getUserCriteriaCharts();
			FlexChartsDocument flexChartsDoc = FlexChartsDocument.Factory.newInstance();
			FlexCharts flexCharts = flexChartsDoc.addNewFlexCharts();

			ChartCriteria chartCriteria = flexCharts.addNewChartCriteria();
			chartCriteria.setChartName(userCriteria.getCriteriaName());
			chartCriteria.setChartDesc(userCriteria.getCriteriaDesc());
			chartCriteria.setChartFilter(removeNewLine(userCriteria.getFilter()));

			Layout layout = flexCharts.addNewLayout();
			VBox vBox = layout.addNewVBox();
			vBox.setWidth("100%");
			vBox.setHeight("100%");

			UserCriteriaChart ucChart = null;

			for (int i = 0; i < ucCharts.size(); i++) {

				ucChart = (UserCriteriaChart) ucCharts.get(i);

				FlexControlDocument flexControlDoc = FlexControlDocument.Factory.parse(ucChart.getChartMetadata());
				FlexControl flexControl = flexControlDoc.getFlexControl();
				flexControl.setRECORDSET( ucRForm.getEditorName() );

				FlexControl newFlexControl = vBox.addNewFlexControl();
				newFlexControl.set( flexControl );

			}

			String xmlData = ejbRS.toXMLData();
			xmlData = StringUtils.replace(xmlData, "<RECORDSET>", "<RECORDSET xmlns=\"http://www.addval.com/utils/flexcharts/xbeans\">");
			RECORDSETDocument rsDoc = RECORDSETDocument.Factory.parse(xmlData);
			RECORDSET recordSet = rsDoc.getRECORDSET();
			recordSet.setId(ucRForm.getEditorName());

			RECORDSET newRecordSet = flexCharts.addNewRECORDSET();
			newRecordSet.set(recordSet);

			String chartXML = flexChartsDoc.toString();
			chartXML = StringUtils.replace(chartXML, " xmlns=\"http://www.addval.com/utils/flexcharts/xbeans\"", "");

			ucRForm.setChartXML(chartXML);
			_logger.debug(chartXML);
		}
	}

	public void fromUserCriteriaChart(ChartMetadataForm cmdForm, UserCriteriaChart ucChart) throws Exception {

		cmdForm.setChartName(ucChart.getChartName());
		cmdForm.setChartType(ucChart.getChartType());

		FlexControlDocument flexControlDoc = FlexControlDocument.Factory.parse(ucChart.getChartMetadata());
		FlexControl flexControl = flexControlDoc.getFlexControl();

		cmdForm.setChartName(flexControl.getName());
		cmdForm.setChartHeight(flexControl.getHeight());
		cmdForm.setChartWidth(flexControl.getWidth());

		if ("Chart".equals(cmdForm.getChartType())) {
			ChartMetadata metadata = flexControl.getChartMetadata();
			if (metadata.getVerticalAxis() != null && metadata.getVerticalAxis().getLinearAxisArray() != null) {
				fromLinearAxis(cmdForm, metadata.getVerticalAxis().getLinearAxisArray());
			}

			if (metadata.getHorizontalAxis() != null && metadata.getHorizontalAxis().getLinearAxisArray() != null) {
				fromLinearAxis(cmdForm, metadata.getHorizontalAxis().getLinearAxisArray());
			}

			if (metadata.getVerticalAxis() != null && metadata.getVerticalAxis().getCategoryAxisArray() != null) {
				fromCategoryAxis(cmdForm, metadata.getVerticalAxis().getCategoryAxisArray());
			}

			if (metadata.getHorizontalAxis() != null && metadata.getHorizontalAxis().getCategoryAxisArray() != null) {
				fromCategoryAxis(cmdForm, metadata.getHorizontalAxis().getCategoryAxisArray());
			}

			if (metadata.getHorizontalAxisRenderers() != null ) {
				fromAxisRenderer(cmdForm,metadata.getHorizontalAxisRenderers().getAxisRendererArray());
			}

			if (metadata.getVerticalAxisRenderers() != null ) {
				fromAxisRenderer(cmdForm,metadata.getVerticalAxisRenderers().getAxisRendererArray());
			}

			fromSeries(cmdForm, metadata.getSeries());

			cmdForm.setShowDataGrid( ucChart.isShowDataGrid() );

			if(!StrUtl.isEmptyTrimmed( ucChart.getDataGridPlacement() )){
				cmdForm.setDataGridPlacement( ucChart.getDataGridPlacement() );
			}
		}
		else if ("Table".equals(ucChart.getChartType())) {

			OLAPCubeMetadata metadata = flexControl.getOLAPCubeMetadata();
			fromDimension(cmdForm, metadata);
			fromMeasure(cmdForm, metadata);
			cmdForm.setAddMeasuresToCol(metadata.getAddMeasuresToCol());
		}

	}

	public UserCriteriaChart toUserCriteriaChart(ChartMetadataForm cmdForm, HttpServletRequest request) {

		EditorMetaData editorMetaData = cmdForm.getMetadata();

		UserCriteriaChart ucChart = new UserCriteriaChart();
		if (!StrUtl.isEmptyTrimmed(cmdForm.getChartName())) {
			ucChart.setChartName(cmdForm.getChartName().trim());
		}

		ucChart.setChartType(cmdForm.getChartType());
		ucChart.setChartSeQNo(cmdForm.getChartIndex());
		ucChart.setCreatedBy(request.getUserPrincipal().getName());
		ucChart.setLastUpdatedBy(request.getUserPrincipal().getName());

		FlexControlDocument flexControlDoc = FlexControlDocument.Factory.newInstance();
		FlexControl flexControl = flexControlDoc.addNewFlexControl();

		flexControl.setName(cmdForm.getChartName());

		if (!StrUtl.isEmptyTrimmed(cmdForm.getChartHeight())) {
			flexControl.setHeight(cmdForm.getChartHeight());
		}

		if (!StrUtl.isEmptyTrimmed(cmdForm.getChartWidth())) {
			flexControl.setWidth(cmdForm.getChartWidth());
		}

		if ("Chart".equals(cmdForm.getChartType())) {
			flexControl.setType( FlexControlType.CHART );

			ChartMetadata metadata = flexControl.addNewChartMetadata();

			if(isBarChart(cmdForm)){
				metadata.setType(ChartType.BAR_CHART);
			}
			else {
				metadata.setType(ChartType.COLUMN_CHART);
			}

			HorizontalAxis hAxis = toHorizontalAxis(cmdForm);
			if(hAxis != null){
				metadata.setHorizontalAxis(hAxis);
			}

			VerticalAxis vAxis = toVerticalAxis(cmdForm);
			if(vAxis != null){
				metadata.setVerticalAxis(vAxis);
			}

			HorizontalAxisRenderers hAxisRenderers = toHorizontalAxisRenderers(cmdForm);
			if(hAxisRenderers != null){
				metadata.setHorizontalAxisRenderers(hAxisRenderers);
			}

			VerticalAxisRenderers vAxisRenderers = toVerticalAxisRenderers(cmdForm);
			if(vAxisRenderers != null){
				metadata.setVerticalAxisRenderers(vAxisRenderers);
			}

			Series series = toSeries(cmdForm);
			if (series != null) {
				metadata.setSeries(series);
			}

			ucChart.setShowDataGrid( cmdForm.isShowDataGrid() );
			ucChart.setDataGridPlacement( cmdForm.getDataGridPlacement() );

		}
		else if ("Table".equals(cmdForm.getChartType())) {
			flexControl.setType( FlexControlType.OLAP_CUBE );

			OLAPCubeMetadata metadata = flexControl.addNewOLAPCubeMetadata();

			Dimension[] rowDimensionArray = toDimension(editorMetaData, cmdForm.getSelectedRowDims(), cmdForm.getSelectedSubTotals());
			if (rowDimensionArray.length > 0) {
				RowDimensions rowDimensions = RowDimensions.Factory.newInstance();
				rowDimensions.setName("row" + ucChart.getChartName());
				rowDimensions.setDimensionArray(rowDimensionArray);
				metadata.setRowDimensions(rowDimensions);
			}

			Dimension[] colDimensionArray = toDimension(editorMetaData, cmdForm.getSelectedColDims(), cmdForm.getSelectedSubTotals());
			if (colDimensionArray.length > 0) {
				ColDimensions colDimensions = ColDimensions.Factory.newInstance();
				colDimensions.setName("col" + ucChart.getChartName());
				colDimensions.setDimensionArray(colDimensionArray);
				metadata.setColDimensions(colDimensions);
			}

			Measure[] measureArray = toMeasure(cmdForm);
			if (measureArray.length > 0) {
				Measures measures = Measures.Factory.newInstance();
				measures.setMeasureArray(measureArray);
				metadata.setMeasures(measures);
			}
			if(cmdForm.isAddMeasuresToCol()){
				metadata.setAddMeasuresToCol( true );
			}
			else {
				metadata.setAddMeasuresToRow( true );
			}
		}
		ucChart.setChartMetadata(flexControlDoc.toString());

		return ucChart;
	}

	public UserCriteria getUserCriteria(UserCriteriaSearchForm ucSForm, HttpServletRequest request) {

		ColumnMetaData columnMetaData = null;

		List displayables = new ArrayList();
		List aggregatables = new ArrayList();
		List customDisplayables = new ArrayList();

		Vector aggregatableColumns = ucSForm.getMetadata().getAggregatableColumns();
		Vector displayableColumns = ucSForm.getMetadata().getDisplayableColumns();

		for (Iterator iterator = displayableColumns.iterator(); iterator.hasNext();) {
			columnMetaData = (ColumnMetaData) iterator.next();
			displayables.add(columnMetaData.getName());
		}
		for (Iterator iterator = aggregatableColumns.iterator(); iterator.hasNext();) {
			columnMetaData = (ColumnMetaData) iterator.next();
			aggregatables.add(columnMetaData.getName());
		}

		for (int i = 0; i < displayables.size(); i++) {
			if (!aggregatables.contains(displayables.get(i)) && !customDisplayables.contains(displayables.get(i))) {
				customDisplayables.add(displayables.get(i));
			}

		}
		for (int i = 0; i < aggregatables.size(); i++) {
			if (!customDisplayables.contains(aggregatables.get(i))) {
				customDisplayables.add(aggregatables.get(i));
			}
		}

		UserCriteria criteria = new UserCriteria();
		criteria.setEditorName(ucSForm.getEditorName());
		criteria.setEditorType(ucSForm.getEditorType());
		criteria.setUserName(ucSForm.getUserId());

		criteria.setCriteriaName(null);
		criteria.setCriteriaDesc(null);
		criteria.setDimension(convertToString(convertToStringArray(displayables)));
		criteria.setMeasure(convertToString(convertToStringArray(aggregatables)));
		criteria.setFilter(null);
		criteria.setSharedAll(false);
		criteria.setSharedGroup(false);
		criteria.setOwner(ucSForm.getUserId());
		criteria.setCustomDisplayable(convertToString(convertToStringArray(customDisplayables)));
		criteria.setColumnSortOrderSeq(null);
		criteria.setColumnSortOrder(null);
		criteria.setSubTotalColumns(null);
		criteria.setShowSubtotalDetail(false);

		return criteria;
	}

	private ArrayList fromUserCriteriaCharts(QueryToolForm queryToolForm, ArrayList ucCharts) throws Exception {
		if (ucCharts == null) {
			return ucCharts;
		}
		UserCriteriaChart ucChart = null;
		List flexDimensions = new ArrayList();
		List flexMeasures = new ArrayList();
		String fieldName = null;

		for (int i = 0; i < ucCharts.size(); i++) {
			ucChart = (UserCriteriaChart) ucCharts.get(i);

			FlexControlDocument flexControlDoc = FlexControlDocument.Factory.parse(ucChart.getChartMetadata());
			FlexControl flexControl = flexControlDoc.getFlexControl();

			if ("Chart".equals(ucChart.getChartType())) {
				ChartMetadata metadata = flexControl.getChartMetadata();

				if (metadata.getHorizontalAxis() != null && metadata.getHorizontalAxis().sizeOfCategoryAxisArray() > 0) {
					CategoryAxis axis = metadata.getHorizontalAxis().getCategoryAxisArray()[0];
					fieldName = axis.getCategoryField();
					if (!flexDimensions.contains(fieldName)) {
						flexDimensions.add(fieldName);
					}
				}

				if (metadata.getVerticalAxis() != null && metadata.getVerticalAxis().sizeOfCategoryAxisArray() > 0) {
					CategoryAxis axis = metadata.getVerticalAxis().getCategoryAxisArray()[0];
					fieldName = axis.getCategoryField();
					if (!flexDimensions.contains(fieldName)) {
						flexDimensions.add(fieldName);
					}
				}
				Series series = metadata.getSeries();

				boolean isBarChart = isBarChart(series);

				if (series.sizeOfChartSeriesArray() > 0) {

					ChartSeries chartSeriesArray[] = series.getChartSeriesArray();
					ChartSeries chartSeries = null;

					for (int j = 0; j < chartSeriesArray.length; j++) {
						chartSeries = chartSeriesArray[j];
						if(ChartSeriesType.COLUMN_SERIES.equals( chartSeries.getType() )){
							fieldName = chartSeries.getYField();
						}
						else if(ChartSeriesType.BAR_SERIES.equals( chartSeries.getType() )){
							fieldName = chartSeries.getXField();
						}
						else if(ChartSeriesType.LINE_SERIES.equals( chartSeries.getType() )){
							if(isBarChart){
								fieldName = chartSeries.getXField();
							}
							else {
								fieldName = chartSeries.getYField();
							}
						}

						if (!flexMeasures.contains(fieldName)) {
							flexMeasures.add(fieldName);
						}
					}
				}
			}
			else if ("Table".equals(ucChart.getChartType())) {
				OLAPCubeMetadata metadata = flexControl.getOLAPCubeMetadata();

				Dimension[] dimensionArray = null;
				Dimension dimension = null;
				if (metadata.getRowDimensions() != null) {
					dimensionArray = metadata.getRowDimensions().getDimensionArray();
					if (dimensionArray != null) {
						for (int j = 0; j < dimensionArray.length; j++) {
							dimension = dimensionArray[j];
							fieldName = dimension.getDataField();
							if (!flexDimensions.contains(fieldName)) {
								flexDimensions.add(fieldName);
							}

						}
					}
				}
				if (metadata.getColDimensions() != null) {
					dimensionArray = metadata.getColDimensions().getDimensionArray();
					if (dimensionArray != null) {
						for (int j = 0; j < dimensionArray.length; j++) {
							dimension = dimensionArray[j];
							fieldName = dimension.getDataField();
							if (!flexDimensions.contains(fieldName)) {
								flexDimensions.add(fieldName);
							}
						}
					}
				}

				if (metadata.getMeasures() != null) {
					Measure[] measureArray = metadata.getMeasures().getMeasureArray();
					Measure measure = null;
					if (measureArray != null) {
						for (int j = 0; j < measureArray.length; j++) {
							measure = measureArray[j];
							fieldName = measure.getDataField();
							if (!flexMeasures.contains(fieldName)) {
								flexMeasures.add(fieldName);
							}
						}
					}
				}// OLAPCube Ends
			}
		}

		List displayables = new ArrayList();
		List aggregatables = new ArrayList();
		List customDisplayable = new ArrayList();

		/*
		if (queryToolForm.getSelectedDisplayables() != null) {
			List list = Arrays.asList(queryToolForm.getSelectedDisplayables());
			displayables.addAll(list);
		}

		if (queryToolForm.getSelectedAggregatables() != null) {
			List list = Arrays.asList(queryToolForm.getSelectedAggregatables());
			aggregatables.addAll(list);
		}

		if (queryToolForm.getCustomDisplayableColumns() != null) {
			List list = Arrays.asList(queryToolForm.getCustomDisplayableColumns());
			customDisplayable.addAll(list);
		}
		*/

		for (int i = 0; i < flexDimensions.size(); i++) {
			fieldName = (String) flexDimensions.get(i);
			if (!displayables.contains(fieldName)) {
				displayables.add(fieldName);

				if (!customDisplayable.contains(fieldName)) {
					customDisplayable.add(fieldName);
				}
			}
		}

		for (int i = 0; i < flexMeasures.size(); i++) {
			fieldName = (String) flexMeasures.get(i);
			if (!aggregatables.contains(fieldName)) {
				aggregatables.add(fieldName);
				if (!customDisplayable.contains(fieldName)) {
					customDisplayable.add(fieldName);
				}
			}
		}
		queryToolForm.setSelectedDisplayables(convertToStringArray(displayables));
		queryToolForm.setSelectedAggregatables(convertToStringArray(aggregatables));
		queryToolForm.setCustomDisplayableColumns(convertToStringArray(customDisplayable));

		ArrayList newucCharts = new ArrayList();
		if( ucCharts.size() > 0 ){
			int chartSeQNo = 0;
			for (int i = 0; i < ucCharts.size(); i++) {
				ucChart = (UserCriteriaChart) ucCharts.get(i);
				if ("Chart".equals(ucChart.getChartType())) {

					FlexControlDocument chartFlexControlDoc = FlexControlDocument.Factory.parse(ucChart.getChartMetadata());
					FlexControl chartFlexControl = chartFlexControlDoc.getFlexControl();

					if( ucChart.isShowDataGrid() ){

						UserCriteriaChart dataGridUCC = new UserCriteriaChart();

						dataGridUCC.setChartName( ucChart.getChartName() );
						dataGridUCC.setChartType("DataGrid");
						dataGridUCC.setChartSeQNo(ucChart.getChartSeQNo());

						FlexControlDocument dataGridFlexControlDoc = FlexControlDocument.Factory.newInstance();
						FlexControl dataGridFlexControl = dataGridFlexControlDoc.addNewFlexControl();

						dataGridFlexControl.setName(  chartFlexControl.getName() );
						dataGridFlexControl.setHeight( chartFlexControl.getHeight() );
						dataGridFlexControl.setWidth( chartFlexControl.getWidth() );
						dataGridFlexControl.setType( FlexControlType.DATA_GRID );

						DataGridMetadata metadata = dataGridFlexControl.addNewDataGridMetadata();
						DataGridColumn[] dataGridColumnArray = toDataGridColumn(queryToolForm);
						if (dataGridColumnArray.length > 0) {
							Columns columns = Columns.Factory.newInstance();
							columns.setDataGridColumnArray(dataGridColumnArray);
							metadata.setColumns(columns);
						}
						dataGridUCC.setChartMetadata( dataGridFlexControlDoc.toString() );

						if("top".equalsIgnoreCase( ucChart.getDataGridPlacement() )){
							dataGridUCC.setChartSeQNo( chartSeQNo++ );
							newucCharts.add(dataGridUCC);
							ucChart.setChartSeQNo( chartSeQNo++ );
							newucCharts.add(ucChart);
						}
						else {
							ucChart.setChartSeQNo( chartSeQNo++ );
							newucCharts.add(ucChart);
							dataGridUCC.setChartSeQNo( chartSeQNo++ );
							newucCharts.add(dataGridUCC);
						}
					}
					else {
						ucChart.setChartSeQNo( chartSeQNo++ );
						newucCharts.add(ucChart);
					}
				}
				else if ("Table".equals(ucChart.getChartType())) {
					ucChart.setChartSeQNo( chartSeQNo++ );
					newucCharts.add(ucChart);
				}
			}
		}
		return newucCharts;
	}

	public List<UserCriteriaChart> getUserCriteriaCharts(QueryToolForm queryToolForm ,ArrayList<UserCriteriaChart> ucCharts) throws Exception {
		ArrayList<UserCriteriaChart> ucChartList = new ArrayList<UserCriteriaChart>();
		if(ucCharts != null && ucCharts.size() > 0){
			queryToolForm.setReportType("Advanced");

			UserCriteriaChart ucChart = null;
			for (int i = 0; i < ucCharts.size(); i++) {
				ucChart = (UserCriteriaChart) ucCharts.get(i);
				if ("Chart".equals( ucChart.getChartType() ) ) {
					UserCriteriaChart previousChart = (i-1) >= 0 ? ucCharts.get(i-1) : null;
					UserCriteriaChart nextChart     = (i+1) < ucCharts.size() ? ucCharts.get( i+1 ) : null;

					if(previousChart != null && "DataGrid".equals(previousChart.getChartType()) && ucChart.getChartName().equalsIgnoreCase( previousChart.getChartName() ) ){
						ucChart.setShowDataGrid( true );
						ucChart.setDataGridPlacement("top");
					}

					if(nextChart != null && "DataGrid".equals(nextChart.getChartType()) && ucChart.getChartName().equalsIgnoreCase( nextChart.getChartName() ) ){
						ucChart.setShowDataGrid( true );
						ucChart.setDataGridPlacement("bottom");
					}
				}
				if (!"DataGrid".equals(ucChart.getChartType())) {
					ucChartList.add( ucChart );
				}
			}
		}
		return ucChartList;
	}

	private void setShowDataGrid(ArrayList<UserCriteriaChart> ucCharts,UserCriteriaChart dataGridUCC){
		if(ucCharts != null && ucCharts.size() > 0){
		}
	}


	public UserCriteria getUserCriteria(QueryToolForm queryToolForm, HttpServletRequest request) throws Exception {
		HttpSession currSession = request.getSession(false);
		String userCriteriaChartSessionKey = queryToolForm.getUserId() + "_" + queryToolForm.getEditorName() + "_USER_CRITERIA_CHART";
		ArrayList ucCharts = (ArrayList) currSession.getAttribute(userCriteriaChartSessionKey);

		String reportType = queryToolForm.getReportType();
		if ("Advanced".equalsIgnoreCase(reportType)) {
			queryToolForm.setShowSubtotalDetail( false );
			if (ucCharts != null) {
				// Add Dimensions and Measures from UserCriteriaChart
				ucCharts = fromUserCriteriaCharts(queryToolForm, ucCharts);
			}
		}
		else {
			ucCharts = new ArrayList();
		}

		UserCriteria criteria = new UserCriteria();

		criteria.setEditorName(queryToolForm.getEditorName());
		criteria.setEditorType(queryToolForm.getEditorType());
		criteria.setUserName(queryToolForm.getUserId());
		criteria.setReportType(reportType);

		criteria.setCriteriaName(getReportName(queryToolForm));
		criteria.setCriteriaDesc(getReportDesc(queryToolForm));
		criteria.setDimension(convertToString(queryToolForm.getSelectedDisplayables()));
		criteria.setMeasure(convertToString(queryToolForm.getSelectedAggregatables()));
		criteria.setFilter(queryToolForm.getFilters());
		criteria.setSharedAll(queryToolForm.isSharedAll());
		criteria.setSharedGroup(queryToolForm.isSharedGroup());
		criteria.setOwner(queryToolForm.getOwner());
		criteria.setCustomDisplayable(convertToString(queryToolForm.getCustomDisplayableColumns()));
		criteria.setColumnSortOrderSeq(getSortOrderColumnsSeq(queryToolForm));
		criteria.setColumnSortOrder(getSortOrderColumns(queryToolForm, request));
		criteria.setSubTotalColumns(convertToString(queryToolForm.getSelectedSubTotalColumns()));
		criteria.setShowSubtotalDetail(queryToolForm.isShowSubtotalDetail());

		setUserGroups(criteria,request);

		criteria.setUserCriteriaCharts(ucCharts);

		return criteria;
	}

	public void setUserGroups(UserCriteria criteria,HttpServletRequest request){
		HttpSession currSession = request.getSession(false);
		Hashtable userProfile = (Hashtable) currSession.getAttribute("USER_PROFILE");
		if (userProfile != null && userProfile.containsKey("USER_GROUPS")) {
			String userGroupStr = (String) userProfile.get("USER_GROUPS");
			criteria.setUserGroups(convertToList(userGroupStr));
		}
	}

	public ActionErrors validateUserCriteriaChart(EditorMetaData metadata, UserCriteriaChart chart) throws Exception {
		ActionErrors errors = new ActionErrors();
		return errors;
	}

	public ActionErrors validateUserCriteria(EditorMetaData metadata, UserCriteria userCriteria, String kindOfAction) throws Exception {
		ActionErrors errors = new ActionErrors();
		boolean isAdvanced = "Advanced".equalsIgnoreCase( userCriteria.getReportType() );

		if (kindOfAction.equals("update") || kindOfAction.equals("delete")) {
			if (!StrUtl.isEmptyTrimmed(userCriteria.getOwner()) && !userCriteria.getUserName().equalsIgnoreCase(userCriteria.getOwner())) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Edit/delete action can only be performed by criteria owner. " + userCriteria.getCriteriaName() + " criteria is owned by " + userCriteria.getOwner()));
			}
		}

		if (kindOfAction.equals("insert") || kindOfAction.equals("update")) {
			if (StrUtl.isEmptyTrimmed(userCriteria.getMeasure()) && StrUtl.isEmptyTrimmed(userCriteria.getDimension())) {
				if(isAdvanced && userCriteria.getUserCriteriaCharts().size() == 0){
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Please add atleast one chart or table before proceeding to Save"));
				}
				else {
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Please select atleast one dimensions and / or measures before proceeding to Save."));
				}
			}

			if ( !StrUtl.isEmptyTrimmed(userCriteria.getFilter() )) {
				StringBuffer selectbuf = new StringBuffer(512);

				ColumnMetaData columnMetaData = null;
				for (Iterator iterator = metadata.getColumnsMetaData().iterator(); iterator.hasNext();) {
					columnMetaData = (ColumnMetaData) iterator.next();
					if (columnMetaData.getType() != ColumnDataType._CDT_LINK)
						selectbuf.append(", ").append(columnMetaData.getName());
				}
				if (selectbuf.length() == 0) {
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Please select atleast one dimensions and / or measures before proceeding to Save."));
				}

				if (errors.size() == 0) {
					try {
						StringBuffer sqlbuf = new StringBuffer(512);
						sqlbuf.append(" SELECT ");

						if (selectbuf.length() > 0) {
							sqlbuf.append(selectbuf.toString().substring(1));
						}
						sqlbuf.append(" FROM ").append(metadata.getSource());

						StringBuffer whereClause = new StringBuffer( userCriteria.getFilter() );

						whereClause = replaceEditorFilters(metadata.getEditorFilters(),whereClause);

						sqlbuf.append(" WHERE 1=0 AND ").append( whereClause );

						_logger.debug( sqlbuf.toString() );

						getTableManager().lookup(sqlbuf.toString(), metadata);

					}
					catch (XRuntime ex) {
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", ex.getMessage()));
					}
					catch (Exception ex) {
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Invalid Search Criteria. Please check the search criteria in the filters section"));
					}
				}
			}
		}

		return errors;
	}

	private StringBuffer replaceEditorFilters(Vector<EditorFilter> editorFilters,StringBuffer whereClause){
		if(whereClause.length() == 0 || editorFilters.size() == 0 ){
			return whereClause;
		}

		String whereClauseStr = whereClause.toString();

		String filterName = null;
		String filterDesc = null;
		String filterSql = null;
		String filterValue= null;

		String replaceFrom = null;
		String replaceTo = null;

		int startPos = -1;
		int endPos =-1;

		for(EditorFilter editorFilter : editorFilters){
			filterName = editorFilter.getFilterName().toUpperCase();
			startPos = whereClauseStr.toUpperCase().indexOf( filterName );
			while(startPos != -1){
				filterValue = StringUtils.substringBetween(whereClauseStr.substring(startPos),"[","]");
				if(filterValue != null){

					filterSql = editorFilter.getFilterSql();
					filterDesc = StrUtl.isEmptyTrimmed(editorFilter.getFilterDesc()) ? "" : editorFilter.getFilterDesc();

					int filterDescSize = StringUtils.split(filterDesc ,",").length;
					int filterValueSize = StringUtils.split(filterValue ,",").length;
					int filterSqlInputSize = StringUtils.countMatches(filterSql,"?");

					if(( filterDescSize != filterValueSize ) || ( filterDescSize != filterSqlInputSize)){
						throw new XRuntime(getClass().getName(), "Pre-Defined Filter parameter count doesn't match for " + filterName + ". Expected " + filterSqlInputSize + " parameter(s)");
					}

					String filterValues[] = StringUtils.split(filterValue ,",");
					for(int i=0;i<filterValues.length;i++){
						filterSql = StringUtils.replaceOnce(filterSql, "?", filterValues[i]);
					}

					replaceTo = filterSql;
					endPos = whereClauseStr.substring(startPos).indexOf("]");
					replaceFrom = StringUtils.substring(whereClauseStr, startPos, startPos+endPos+1);
					whereClauseStr = StringUtils.replace(whereClauseStr, replaceFrom, replaceTo);
				}
				startPos = whereClauseStr.toUpperCase().indexOf( filterName );
			}
		}
		return new StringBuffer(whereClauseStr);
	}

	public void initializeForm(HttpServletRequest request, ChartMetadataForm cmdForm) throws Exception {
		String kindOfAction = cmdForm.getKindOfAction() != null ? cmdForm.getKindOfAction() : "";
		ArrayList chartTypes = new ArrayList();
		chartTypes.add(new LabelValueBean("Chart", "Chart"));
		chartTypes.add(new LabelValueBean("Table", "Table"));
		cmdForm.setChartTypes(chartTypes);

		ArrayList placements = new ArrayList();
		placements.add(new LabelValueBean("bottom", "bottom"));
		placements.add(new LabelValueBean("top", "top"));
		placements.add(new LabelValueBean("left", "left"));
		placements.add(new LabelValueBean("right", "right"));
		cmdForm.setPlacements(placements);

	}

	public void initializeForm(HttpServletRequest request, QueryToolForm queryToolForm, ArrayList userCriteriaNames) throws Exception {

		String kindOfAction = queryToolForm.getKindOfAction() != null ? queryToolForm.getKindOfAction() : "";

		ArrayList criteriaNames = new ArrayList();
		String criteriaName = null;
		for (Iterator iterator = userCriteriaNames.iterator(); iterator.hasNext();) {
			criteriaName = (String) iterator.next();
			criteriaNames.add(new LabelValueBean(criteriaName, criteriaName));
		}
		queryToolForm.setReportNames(criteriaNames);
		queryToolForm.setAggregatableColumns(queryToolForm.getMetadata().getAggregatableColumns());
		queryToolForm.setDisplayableColumns(getDisplayableColumns(queryToolForm.getMetadata()));

		ArrayList reportTypes = new ArrayList();
		reportTypes.add(new LabelValueBean("Simple", "Simple"));
		reportTypes.add(new LabelValueBean("Advanced", "Advanced"));

		queryToolForm.setReportTypes(reportTypes);

		if (kindOfAction.equals("edit") || kindOfAction.equals("addnew") || kindOfAction.equals("select")) {
			queryToolForm.setOwner(null);

			Vector aggregatablesCols = queryToolForm.getAggregatablesColumnNames();
			Vector displayablesCols = queryToolForm.getDisplayablesColumnNames();
			EditorMetaData metaData = queryToolForm.getMetadata();
			UserCriteria userCriteria = queryToolForm.getCriteria();
			ColumnMetaData columnMetaData = null;

			List displayables = new ArrayList();
			List aggregatables = new ArrayList();
			List customDisplayables = new ArrayList();
			List columnSortOrderSeq = new ArrayList();
			List columnSortOrder = new ArrayList();
			List subTotalColumns = new ArrayList();

			if (userCriteria != null) {
				queryToolForm.setFilters(userCriteria.getFilter());
				queryToolForm.setReportDesc(userCriteria.getCriteriaDesc());
				queryToolForm.setReportName(userCriteria.getCriteriaName());
				queryToolForm.setSharedGroup(userCriteria.getSharedGroup());
				queryToolForm.setSharedAll(userCriteria.getSharedAll());
				queryToolForm.setOwner(userCriteria.getOwner());
				queryToolForm.setShowSubtotalDetail(userCriteria.isShowSubtotalDetail());

				displayables = convertToList(userCriteria.getDimension(), metaData);
				aggregatables = convertToList(userCriteria.getMeasure(), metaData);
				customDisplayables = convertToList(userCriteria.getCustomDisplayable(), metaData);
				columnSortOrderSeq = convertToList(userCriteria.getColumnSortOrderSeq(), metaData);
				columnSortOrder = convertToList(userCriteria.getColumnSortOrder());
				subTotalColumns = convertToList(userCriteria.getSubTotalColumns(), metaData);
			}
			else {
				queryToolForm.setFilters(null);
				queryToolForm.setReportDesc(null);
				queryToolForm.setShowSubtotalDetail(true);

				if (queryToolForm.getDisplayableColumns() != null) {
					for (ColumnMetaData colMetaData : queryToolForm.getDisplayableColumns()) {
						displayables.add(colMetaData.getName());
					}
				}
				if (queryToolForm.getAggregatableColumns() != null) {
					for (ColumnMetaData colMetaData : queryToolForm.getAggregatableColumns()) {
						aggregatables.add(colMetaData.getName());
					}
				}
			}
			if (customDisplayables.size() == 0) {
				for (int i = 0; i < displayables.size(); i++) {
					if (!aggregatables.contains(displayables.get(i)) && !customDisplayables.contains(displayables.get(i))) {
						customDisplayables.add(displayables.get(i));
					}
				}
				for (int i = 0; i < aggregatables.size(); i++) {
					if (!customDisplayables.contains(aggregatables.get(i))) {
						customDisplayables.add(aggregatables.get(i));
					}
				}
			}

			for (int i = 0; i < displayablesCols.size(); i++) {
				if (!aggregatablesCols.contains(displayablesCols.get(i)) && !columnSortOrderSeq.contains(displayablesCols.get(i))) {
					columnSortOrderSeq.add(displayablesCols.get(i));
					columnSortOrder.add(String.valueOf(AVConstants._ASC));
				}
			}

			for (int i = 0; i < aggregatablesCols.size(); i++) {
				if (!columnSortOrderSeq.contains(aggregatablesCols.get(i))) {
					columnSortOrderSeq.add(aggregatablesCols.get(i));
					columnSortOrder.add(String.valueOf(AVConstants._ASC));
				}
			}
			queryToolForm.setSelectedDisplayables(convertToStringArray(displayables));
			queryToolForm.setSelectedAggregatables(convertToStringArray(aggregatables));
			queryToolForm.setCustomDisplayableColumns(convertToStringArray(customDisplayables));
			queryToolForm.setSortOrderColumnsSeq(convertToStringArray(columnSortOrderSeq));
			queryToolForm.setSortOrderColumns(convertToStringArray(columnSortOrder));
			queryToolForm.setSelectedSubTotalColumns(convertToStringArray(subTotalColumns));
		}
	}

	public boolean isCriteriaChanged(UserCriteria thisUserCriteria, UserCriteria thatUserCriteria) throws Exception {

		if (!StrUtl.isEmptyTrimmed(thisUserCriteria.getCriteriaName())) {
			// Compare the criteria Values and set the criteria Name.
			if (thatUserCriteria != null) {
				if (!valuesEquals(thisUserCriteria.getDimension(), thatUserCriteria.getDimension())) {
					return true;
				}
				if (!valuesEquals(thisUserCriteria.getMeasure(), thatUserCriteria.getMeasure())) {
					return true;
				}
				if (!StrUtl.equals(thisUserCriteria.getFilter(), thatUserCriteria.getFilter())) {
					return true;
				}
				if (!valuesEquals(thisUserCriteria.getCustomDisplayable(), thatUserCriteria.getCustomDisplayable())) {
					return true;
				}
				if (!valuesEquals(thisUserCriteria.getColumnSortOrderSeq(), thatUserCriteria.getColumnSortOrderSeq())) {
					return true;
				}
				if (!valuesEquals(thisUserCriteria.getColumnSortOrder(), thatUserCriteria.getColumnSortOrder())) {
					return true;
				}
			}
		}
		return false;
	}

	public void initializeForm(HttpServletRequest request, UserCriteriaSearchForm ucRForm, ArrayList userCriteriaNames) throws Exception {
		ArrayList criteriaNames = new ArrayList();
		String criteriaName = null;
		for (Iterator iterator = userCriteriaNames.iterator(); iterator.hasNext();) {
			criteriaName = (String) iterator.next();
			criteriaNames.add(new LabelValueBean(criteriaName, criteriaName));
		}
		ucRForm.setCriteriaNames(criteriaNames);
	}

	public void setResultset(UserCriteriaSearchForm ucRForm, HttpServletRequest request) throws Exception
	{
		String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "";
		String kindOfAction = ucRForm.getKindOfAction() != null ? ucRForm.getKindOfAction() : "";
		String sortName = ucRForm.getSort_Name() != null ? ucRForm.getSort_Name() : "";
		String sortOrder = ucRForm.getSort_Order() != null ? ucRForm.getSort_Order() : "";
		EJBResultSet ejbRS = null;
		UserCriteria criteria = null;
		HttpSession currSession = request.getSession(false);
		if (!kindOfAction.equals("search") && !kindOfAction.equals("apply"))
			return;

		request.setAttribute("exactSearch", new Boolean(ucRForm.isExactSearch()));
		if (kindOfAction.equals("apply"))
			criteria = (UserCriteria) currSession.getAttribute(userName + "_" + ucRForm.getMetadata().getName() + "_USER_CRITERIA");
		else if (!StrUtl.isEmptyTrimmed(ucRForm.getCriteriaName()) || (!StrUtl.isEmptyTrimmed(ucRForm.getEditorName()) && (ucRForm.getEditorName().equalsIgnoreCase("MTSCENARIO_COMPARE_DMD_RESULT") || ucRForm.getEditorName().equals("MTSCENARIO_COMPARE_DEP_LEG_RESULTS"))))
			criteria = ucRForm.getCriteria();
		else if (kindOfAction.equals("search"))
			criteria = getUserCriteria(ucRForm, request);
		// when the userCriteria is retrieved from session only, there is apossibility that the userCriteria is null. If so, use the EJB Criteria
		if (criteria == null) {
			EJBCriteria ejbCriteria = EjbUtils.getEJBCriteria(ucRForm.getMetadata(), request, true);
			ejbCriteria.orderBy(EjbUtils.setOrderBy(ucRForm.getMetadata(), request));
			ejbRS = getTableManager().lookup(ejbCriteria);
			currSession.setAttribute(userName + "_" + ucRForm.getMetadata().getName() + "_EJB_CRITERIA", ejbCriteria);
		}
		else {
			criteria.setSortName( sortName );
			criteria.setSortOrder( sortOrder );
			Pair pair = getEditorMetaDataSQLPair(ucRForm.getMetadata(), criteria, request, ucRForm.isAdditionalFilter());
			ucRForm.setMetadata((EditorMetaData) pair.getFirst());
			ejbRS = getTableManager().lookup((String) pair.getSecond(), ucRForm.getMetadata());
			currSession.setAttribute(userName + "_" + ucRForm.getMetadata().getName() + "_USER_CRITERIA", criteria);
		}
		ucRForm.setResultset(ejbRS);
	}

	public Pair getEditorMetaDataSQLPair(EditorMetaData metaData, UserCriteria userCriteria, HttpServletRequest request, boolean isAdditionalFilter) throws Exception {

		boolean useRollupFunc = request.getAttribute("USE_ROLLUP") != null ? ((Boolean)request.getAttribute( "USE_ROLLUP" )).booleanValue() : true;
		List displayables = new ArrayList();
		List aggregatables = new ArrayList();
		List customDisplayables = new ArrayList();
		List columnSortOrderSeq = new ArrayList();
		List columnSortOrder = new ArrayList();
		List subTotalColumns = new ArrayList();

		displayables = convertToList(userCriteria.getDimension(), metaData);
		aggregatables = convertToList(userCriteria.getMeasure(), metaData);
		customDisplayables = convertToList(userCriteria.getCustomDisplayable(), metaData);
		columnSortOrderSeq = convertToList(userCriteria.getColumnSortOrderSeq(), metaData);
		columnSortOrder = convertToList(userCriteria.getColumnSortOrder());
		subTotalColumns = convertToList(userCriteria.getSubTotalColumns(), metaData);
		boolean hasSubTotal = (subTotalColumns.size() >0);
		ColumnMetaData columnMetaData = null;
		ColumnMetaData groupingColumnMetaData = null;
		String orderBy = null;

		/* Build SELECT */
		/* Build GROUP BY (- add all Dimensions to group by) */
		/* Build HAVING */
		/* BUILD ORDER BY */

		StringBuffer selectbuf = new StringBuffer(512);
		StringBuffer groupbybuf = new StringBuffer(512);
		StringBuffer havingClause1 = new StringBuffer(512);
		StringBuffer orderbybuf = new StringBuffer(512);
		StringBuffer orderbybuf2 = new StringBuffer(512);

		/* Subtotal column as first in Group by Rollup function and order by */
		if(hasSubTotal && useRollupFunc){
			for (int i = 0; i < subTotalColumns.size(); i++) {
				groupbybuf.append(", ").append(subTotalColumns.get(i));
				orderbybuf.append(",").append(subTotalColumns.get(i)).append(" ").append("ASC");
			}
		}

		Vector columnsMetaData = new Vector();
		for (int i = 0; i < customDisplayables.size(); i++) {
			columnMetaData = metaData.getColumnMetaData((String) customDisplayables.get(i));

			if (columnMetaData != null && columnMetaData.getType() != ColumnDataType._CDT_LINK) {
				if (aggregatables.contains(columnMetaData.getName())) {
					if (columnMetaData.getExpression() == null) {
						selectbuf.append(",NULL AS ").append(columnMetaData.getName());
					}
					else {
						selectbuf.append(", ").append(columnMetaData.getExpression()).append(" AS ").append(columnMetaData.getName());
					}
					columnsMetaData.add(columnMetaData.clone());

				}
				else if (displayables.contains(columnMetaData.getName())) {
					selectbuf.append(", ").append(columnMetaData.getName());
					columnsMetaData.add(columnMetaData.clone());
					if(useRollupFunc){
						groupingColumnMetaData = (ColumnMetaData) columnMetaData.clone();
						if (columnMetaData.getName().length() <= 28) {
							selectbuf.append(", GROUPING(").append(columnMetaData.getName()).append(") AS ").append("G_").append(columnMetaData.getName());
							groupingColumnMetaData.getColumnInfo().setName("G_" + columnMetaData.getName());
						}
						else {
							selectbuf.append(", GROUPING(").append(columnMetaData.getName()).append(") AS ").append("G_").append(columnMetaData.getName().substring(0, 28));
							groupingColumnMetaData.getColumnInfo().setName("G_" + columnMetaData.getName().substring(0, 28));
						}
						groupingColumnMetaData.setDisplayable(false);
						groupingColumnMetaData.setSearchable(false);
						groupingColumnMetaData.getColumnInfo().setType(ColumnDataType._CDT_STRING_);
						columnsMetaData.add(groupingColumnMetaData);
						havingClause1.append("+ GROUPING(").append(columnMetaData.getName()).append(") ");
						orderbybuf2.append(",").append(groupingColumnMetaData.getName()).append(" ").append( "ASC" );

						if (!subTotalColumns.contains(columnMetaData.getName())) {
							groupbybuf.append(", ").append(columnMetaData.getName());
						}
					}
					else {
						groupbybuf.append(", ").append(columnMetaData.getName());
					}
				}
			}
		}

		appendOtherRequiredColumns(columnsMetaData,customDisplayables,selectbuf,groupbybuf, havingClause1 , metaData.getName(), request  );

		for (int i = 0; i < columnSortOrderSeq.size(); i++) {
			columnMetaData = metaData.getColumnMetaData((String) columnSortOrderSeq.get(i));
			if (columnMetaData != null && columnMetaData.getType() != ColumnDataType._CDT_LINK) {
				if (aggregatables.contains(columnMetaData.getName()) || (displayables.contains(columnMetaData.getName()))) {

					if(useRollupFunc){
						if (!subTotalColumns.contains(columnMetaData.getName())) {
							orderBy = "ASC";
							if(!hasSubTotal){
								int columnIndex = columnSortOrderSeq.indexOf( columnMetaData.getName() );
								orderBy = String.valueOf(AVConstants._ASC).equals(columnSortOrder.get( columnIndex ) )? "ASC" : "DESC";
							}
							orderbybuf.append(",").append(columnMetaData.getName()).append(" ").append( orderBy );
						}
					}
					else {
						int columnIndex = columnSortOrderSeq.indexOf( columnMetaData.getName() );
						orderBy = String.valueOf(AVConstants._ASC).equals(columnSortOrder.get( columnIndex ) )? "ASC" : "DESC";
						orderbybuf.append(",").append(columnMetaData.getName()).append(" ").append( orderBy );
					}
				}
			}
		}

		if (selectbuf.length() == 0) {
			throw new XRuntime(getClass().getName(), "Please select atleast one dimensions and / or measures before proceeding to Save");
		}

		/* Build HAVING */

		if (havingClause1.length() > 0) {
			havingClause1 = new StringBuffer(havingClause1.substring(1));
		}

		StringBuffer havingClause2 = new StringBuffer(512);
		StringBuffer havingClause3 = new StringBuffer(512);

		if(useRollupFunc){
			for (int i = 0; i < customDisplayables.size(); i++) {
				columnMetaData = metaData.getColumnMetaData((String) customDisplayables.get(i));
				if (columnMetaData != null && columnMetaData.getType() != ColumnDataType._CDT_LINK && displayables.contains(columnMetaData.getName()) && !aggregatables.contains(columnMetaData.getName())) {
					if (!subTotalColumns.contains(columnMetaData.getName())) {
						havingClause2.append("AND GROUPING(").append(columnMetaData.getName()).append(") = 1 ");
					}
					havingClause3.append("AND GROUPING(").append(columnMetaData.getName()).append(") = 1 ");
				}
			}
		}

		if (havingClause2.length() > 0) {
			havingClause2 = new StringBuffer(havingClause2.substring("AND".length()));
		}
		if (havingClause3.length() > 0) {
			havingClause3 = new StringBuffer(havingClause3.substring("AND".length()));
		}

		/* Build WHERE */
		StringBuffer whereClause = new StringBuffer(512);

		if (isAdditionalFilter) {

			EJBCriteria ejbCriteria = EjbUtils.getEJBCriteria(metaData, request, true);
			EJBResultSetMetaData rsmetaData = new EJBResultSetMetaData(metaData);
			String _serverType = "";
			final EJBSQLBuilderUtils sqlBuilder = new EJBSQLBuilderUtils(_serverType);
			String whereClauseStr = sqlBuilder.buildWhere(ejbCriteria, rsmetaData);
			if (whereClauseStr.length() > 0) {
				whereClauseStr = whereClauseStr.substring("WHERE".length());
			}
			if (!StrUtl.isEmptyTrimmed(whereClauseStr)) {
				whereClause.append(whereClauseStr);
				userCriteria.setAdditionalFilter( whereClauseStr );
			}
			else {
				userCriteria.setAdditionalFilter( null );
			}
		}
		else {
			//While schedule export isAdditionalFilter will be passed as false. We should use UserCriteria.AdditionalFilter
			if (!StrUtl.isEmptyTrimmed( userCriteria.getAdditionalFilter() )) {
				whereClause.append( userCriteria.getAdditionalFilter() );
			}
		}

		if (!StrUtl.isEmptyTrimmed(userCriteria.getFilter())) {
			if (whereClause.length() > 0) {
				whereClause.append(" AND ").append(userCriteria.getFilter());
			}
			else {
				whereClause.append(userCriteria.getFilter());
			}
		}
		whereClause = replaceEditorFilters(metaData.getEditorFilters(),whereClause);

		/* Build Select Statement */
		StringBuffer sqlbuf = new StringBuffer(512);
		sqlbuf.append(" SELECT ");
		if (selectbuf.length() > 0) {
			sqlbuf.append(selectbuf.toString().substring(1));
		}
		sqlbuf.append(" FROM ").append(metaData.getSource());

		if (whereClause.length() > 0) {
			sqlbuf.append(" WHERE ").append(whereClause);
		}

		if (groupbybuf.length() > 0) {
			if(useRollupFunc){
				sqlbuf.append(" GROUP BY ROLLUP (").append(groupbybuf.toString().substring(1)).append(") ");
			}
			else {
				sqlbuf.append(" GROUP BY (").append(groupbybuf.toString().substring(1)).append(") ");
			}
		}

		if (havingClause1.length() > 0) {
			StringBuffer havingClause = new StringBuffer(512);
			/* Subtotal is empty */
			if (StrUtl.isEmptyTrimmed(userCriteria.getSubTotalColumns())) {
				havingClause.append(" ( ").append(havingClause1).append(" = 0 ").append(" ) ");
				havingClause.append(" OR ( ").append(havingClause3).append(" ) ");
			}
			else {
				if (userCriteria.isShowSubtotalDetail() == true || havingClause2.length() == 0) {
					havingClause.append(" ( ").append(havingClause1).append(" = 0 ").append(" ) ");
				}
				if (havingClause2.length() > 0) {
					if (havingClause.length() > 0) {
						havingClause.append(" OR ");
					}
					havingClause.append(" ( ").append(havingClause2).append(" ) ");
				}
			}

			if (havingClause.length() > 0) {
				sqlbuf.append(" HAVING ( ").append(havingClause).append(" ) ");
			}
		}

		if (StrUtl.isEmptyTrimmed(userCriteria.getSubTotalColumns()) && !StrUtl.isEmptyTrimmed(userCriteria.getSortName())) {
			/* Build ORDER BY */
			if (aggregatables.contains(userCriteria.getSortName()) || displayables.contains(userCriteria.getSortName())) {
				if (columnSortOrderSeq.size() > 0) {
					int columnIndex = columnSortOrderSeq.indexOf(userCriteria.getSortName());
					if (columnIndex != -1) {
						columnSortOrderSeq.remove(columnIndex);
						if(columnIndex < columnSortOrder.size() ){
							columnSortOrder.remove(columnIndex);
						}

					}
					columnSortOrderSeq.add(0, userCriteria.getSortName());
					columnSortOrder.add(0, "DESC".equalsIgnoreCase(userCriteria.getSortOrder()) ? String.valueOf(AVConstants._DESC) : String.valueOf(AVConstants._ASC));

					orderbybuf = new StringBuffer();
					for (int i = 0; i < columnSortOrderSeq.size(); i++) {
						orderBy = (i < columnSortOrder.size() && columnSortOrder.get(i).equals(String.valueOf(AVConstants._ASC) ) )  ? "ASC" : "DESC";
						orderbybuf.append(",").append(columnSortOrderSeq.get(i)).append(" ").append(orderBy);
					}
				}
				else {
					String columnsWithOrder1[] = StringUtils.split(orderbybuf.toString(), ",");
					orderbybuf = new StringBuffer(512);
					for (int i = 0; i < columnsWithOrder1.length; i++) {
						String columnsWithOrder2[] = StringUtils.split(columnsWithOrder1[i]);
						boolean matches = false;
						for (int j = 0; j < columnsWithOrder2.length; j++) {
							if (userCriteria.getSortName().equalsIgnoreCase(columnsWithOrder2[j])) {
								matches = true;
							}
						}
						if (!matches) {
							orderbybuf.append(",").append(columnsWithOrder1[i]);
						}
					}
					orderBy = "DESC".equalsIgnoreCase(userCriteria.getSortOrder()) ? userCriteria.getSortOrder() : "ASC";
					orderbybuf.insert(0, "," + userCriteria.getSortName() + " " + orderBy);
				}
			}
		}
		if (orderbybuf.length() > 0) {
			if(!hasSubTotal && orderbybuf2.length() > 0){
				sqlbuf.append(" ORDER BY ").append(orderbybuf2.substring(1) + orderbybuf.toString() );
			}
			else {
				sqlbuf.append(" ORDER BY ").append(orderbybuf.substring(1));
			}
		}
		else if(orderbybuf2.length() > 0){
			sqlbuf.append(" ORDER BY ").append(orderbybuf2.substring(1));
		}


		// Set New EditorMetadata.
		EditorMetaData newMetaData = (EditorMetaData) (metaData.clone());
		if (columnsMetaData.size() > 0) {
			newMetaData.setColumnsMetaData(columnsMetaData);
		}
		_logger.debug(sqlbuf.toString());
		//System.out.println(sqlbuf.toString());
		return new Pair(newMetaData, sqlbuf.toString());
	}

	protected void appendOtherRequiredColumns(Vector columnsMetaData, List customDisplayables, StringBuffer selectbuf, StringBuffer groupbybuf, StringBuffer havingClause1 , String editorName, HttpServletRequest request) {
		// Not required in Base class. This will be overriden in dervied class if required.
	}

	private String getReportName(QueryToolForm queryToolForm) {
		String reportName = null;
		if (!StrUtl.isEmptyTrimmed(queryToolForm.getReportName())) {
			reportName = queryToolForm.getReportName();
		}
		else if (!StrUtl.isEmptyTrimmed(queryToolForm.getNewReportName())) {
			reportName = queryToolForm.getNewReportName().trim();
		}
		return reportName;
	}

	private String getReportDesc(QueryToolForm queryToolForm) {
		String reportDesc = null;
		if (!StrUtl.isEmptyTrimmed(queryToolForm.getReportDesc())) {
			reportDesc = queryToolForm.getReportDesc();
		}
		return reportDesc;
	}

	private String convertToString(String values[]) {
		String valueStr = null;
		if (values != null) {
			valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr += "," + values[i];
			}
			valueStr = (valueStr.length() > 0) ? valueStr.substring(1) : null;
		}
		return valueStr;
	}

	private List convertToList(String valueStr, EditorMetaData metaData) {
		List newList = new ArrayList();
		if (!StrUtl.isEmptyTrimmed(valueStr)) {
			String values[] = valueStr.split(",");
			List list = Arrays.asList(values);
			String columnName = null;
			for (int i = 0; i < list.size(); i++) {
				columnName = (String) list.get(i);
				if (metaData.getColumnMetaData(columnName) != null) {
					newList.add(columnName);
				}
			}

		}
		return newList;
	}

	private List convertToList(String valueStr) {
		List newList = new ArrayList();
		if (!StrUtl.isEmptyTrimmed(valueStr)) {
			String values[] = valueStr.split(",");
			List list = Arrays.asList(values);
			newList.addAll(list);
		}
		return newList;
	}

	private String[] convertToStringArray(List values) {
		String[] valuesArray = new String[values.size()];
		valuesArray = (String[]) values.toArray(valuesArray);
		return valuesArray;
	}

	private String getSortOrderColumnsSeq(QueryToolForm queryToolForm) {
		List customDisplayable = new ArrayList();
		String[] selectedDisplayables = queryToolForm.getCustomDisplayableColumns();
		if (selectedDisplayables != null) {
			customDisplayable = Arrays.asList(selectedDisplayables);
		}

		String sortOrderColumnsSeqStr = "";
		String sortOrderColumnsSeq[] = queryToolForm.getSortOrderColumnsSeq();
		if (sortOrderColumnsSeq != null) {
			for (int i = 0; i < sortOrderColumnsSeq.length; i++) {
				if (customDisplayable.contains(sortOrderColumnsSeq[i])) {
					sortOrderColumnsSeqStr += "," + sortOrderColumnsSeq[i];
				}
			}
			sortOrderColumnsSeqStr = (sortOrderColumnsSeqStr.length() > 0) ? sortOrderColumnsSeqStr.substring(1) : null;
		}
		return sortOrderColumnsSeqStr;
	}

	private String getSortOrderColumns(QueryToolForm queryToolForm, HttpServletRequest request) {
		List customDisplayable = new ArrayList();
		String[] selectedDisplayables = queryToolForm.getCustomDisplayableColumns();
		if (selectedDisplayables != null) {
			customDisplayable = Arrays.asList(selectedDisplayables);
		}

		String sortOrder = null;
		String sortOrderColumnsStr = "";
		String sortOrderColumnsSeq[] = queryToolForm.getSortOrderColumnsSeq();
		if (sortOrderColumnsSeq != null) {
			for (int i = 0; i < sortOrderColumnsSeq.length; i++) {
				if (customDisplayable.contains(sortOrderColumnsSeq[i])) {
					sortOrder = request.getParameter(sortOrderColumnsSeq[i]);
					if (!StrUtl.isEmptyTrimmed(sortOrder)) {
						sortOrderColumnsStr += "," + Integer.parseInt(sortOrder);
					}
					else {
						sortOrderColumnsStr += "," + AVConstants._ASC;
					}
				}
			}
			sortOrderColumnsStr = (sortOrderColumnsStr.length() > 0) ? sortOrderColumnsStr.substring(1) : null;
		}
		return sortOrderColumnsStr;
	}

	public Vector getDisplayableColumns(EditorMetaData metadata) {
		Vector columnNames = new Vector();
		ColumnMetaData columnMetaData = null;
		if (metadata.getAggregatableColumns() != null) {
			for (Iterator iterator = metadata.getAggregatableColumns().iterator(); iterator.hasNext();) {
				columnMetaData = (ColumnMetaData) iterator.next();
				columnNames.add(columnMetaData.getName());
			}
		}
		Vector columnsMetaData = new Vector();
		if (metadata.getDisplayableColumns() != null) {
			for (Iterator iterator = metadata.getDisplayableColumns().iterator(); iterator.hasNext();) {
				columnMetaData = (ColumnMetaData) iterator.next();
				if (!columnNames.contains(columnMetaData.getName()))
					columnsMetaData.add(columnMetaData);
			}
		}
		return columnsMetaData.size() > 0 ? columnsMetaData : null;
	}

	private boolean valuesEquals(String thisStr, String thatStr) {
		if (thisStr == null) {
			return (thatStr == null);
		}
		if (thatStr == null) {
			return (thisStr == null);
		}
		List thisList = Arrays.asList(thisStr.split(","));
		List thatList = Arrays.asList(thatStr.split(","));

		String value = null;
		for (int i = 0; i < thisList.size(); i++) {
			value = (String) thisList.get(i);
			if (!thatList.contains(value)) {
				return false;
			}
		}
		for (int i = 0; i < thatList.size(); i++) {
			value = (String) thatList.get(i);
			if (!thisList.contains(value)) {
				return false;
			}
		}
		return true;
	}

	private String removeNewLine(String value) {
		if (value == null) {
			return "";
		}
		Pattern PATTERN_NEWLINE = Pattern.compile("(\\s)+", Pattern.CASE_INSENSITIVE);
		return PATTERN_NEWLINE.matcher(value).replaceAll(" ");
	}

	private boolean isContains(String values[], String value) {
		boolean retVal = false;
		if (values != null && value != null) {
			for (int i = 0; i < values.length; i++) {
				if (values[i].equals(value)) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}

	// FORM to XML
	private Dimension[] toDimension(EditorMetaData editorMetaData, String dimensions[], String subTotals[]) {
		List dimensionList = new ArrayList();

		ColumnMetaData columnMetaData = null;
		Dimension dimension = null;
		String columnName = null;

		if (dimensions != null) {
			for (int i = 0; i < dimensions.length; i++) {
				columnName = dimensions[i];
				columnMetaData = editorMetaData.getColumnMetaData(columnName);
				if (columnMetaData != null) {
					dimension = Dimension.Factory.newInstance();
					dimension.setDataField(columnMetaData.getName());
					dimension.setDisplayName(columnMetaData.getCaption());
					dimension.setSubtotal(isContains(subTotals, columnName));
					dimensionList.add(dimension);
				}
			}
		}
		Dimension[] dimensionArray = new Dimension[dimensionList.size()];
		dimensionArray = (Dimension[]) dimensionList.toArray(dimensionArray);
		return dimensionArray;
	}

	private Measure[] toMeasure(ChartMetadataForm cmdForm) {

		EditorMetaData editorMetaData = cmdForm.getMetadata();
		String[] measures = cmdForm.getSelectedMeasures();
		Map<String,String> aggregators = cmdForm.getSelectedAggregators();
		Map<String,String> thresholds = cmdForm.getSelectedThresholds();
		Map<String,String> colors = cmdForm.getSelectedColors();

		List measureList = new ArrayList();

		ColumnMetaData columnMetaData = null;
		Measure measure = null;
		String columnName = null;

		if (measures != null) {
			for (int i = 0; i < measures.length; i++) {
				columnName = measures[i];
				columnMetaData = editorMetaData.getColumnMetaData(columnName);
				if (columnMetaData != null) {
					measure = Measure.Factory.newInstance();
					measure.setDataField(columnMetaData.getName());
					measure.setDisplayName(columnMetaData.getCaption());
					if (aggregators.containsKey(columnName)) {
						measure.setAggregator(AggregatorType.Enum.forString(aggregators.get(columnName)));
					}

					if (thresholds.containsKey(columnName)) {
						measure.setThresholds(thresholds.get(columnName));
					}

					if (colors.containsKey(columnName)) {
						measure.setColors(colors.get(columnName));
					}
					measure.setFormat(columnMetaData.getFormat());
					measureList.add(measure);
				}
			}
		}
		Measure[] measureArray = new Measure[measureList.size()];
		measureArray = (Measure[]) measureList.toArray(measureArray);
		return measureArray;
	}

	private DataGridColumn[] toDataGridColumn(QueryToolForm queryToolForm) {
		EditorMetaData editorMetaData = queryToolForm.getMetadata();

		List dataGridColumns = new ArrayList();
		ColumnMetaData columnMetaData = null;
		DataGridColumn dataGridColumn = null;

		String customDisplayable[] = queryToolForm.getCustomDisplayableColumns();
		if (customDisplayable != null) {
			String columnName = null;
			for (int i = 0; i < customDisplayable.length; i++) {
				columnName = customDisplayable[i];
				columnMetaData = editorMetaData.getColumnMetaData(columnName);

				if (columnMetaData != null) {
					dataGridColumn = DataGridColumn.Factory.newInstance();

					dataGridColumn.setDataField(columnMetaData.getName());
					dataGridColumn.setDisplayName(columnMetaData.getCaption());

					if (ColumnDataType._CDT_INT == columnMetaData.getType() || ColumnDataType._CDT_LONG == columnMetaData.getType() || ColumnDataType._CDT_DOUBLE == columnMetaData.getType() || ColumnDataType._CDT_FLOAT == columnMetaData.getType()) {
						dataGridColumn.setTextAlign("right");
					}
					else {
						dataGridColumn.setTextAlign("left");
					}
					dataGridColumns.add(dataGridColumn);
				}
			}
		}
		DataGridColumn[] dataGridColumnArray = new DataGridColumn[dataGridColumns.size()];
		dataGridColumnArray = (DataGridColumn[]) dataGridColumns.toArray(dataGridColumnArray);
		return dataGridColumnArray;
	}


	private HorizontalAxis toHorizontalAxis(ChartMetadataForm cmdForm){
		HorizontalAxis hAxis = HorizontalAxis.Factory.newInstance();
		if(isBarChart(cmdForm)){
			LinearAxis linearAxis = toLinearAxis1(cmdForm);
			if(linearAxis != null){
				LinearAxis newLinearAxis = hAxis.addNewLinearAxis();
				newLinearAxis.set(linearAxis);
			}
			linearAxis = toLinearAxis2(cmdForm);
			if(linearAxis != null){
				LinearAxis newLinearAxis = hAxis.addNewLinearAxis();
				newLinearAxis.set(linearAxis);
			}
		}
		else {
			CategoryAxis categoryAxis = toCategoryAxis( cmdForm );
			if(categoryAxis != null){
				CategoryAxis newCategoryAxis = hAxis.addNewCategoryAxis();
				newCategoryAxis.set(categoryAxis);
			}
		}
		return hAxis;
	}

	private VerticalAxis toVerticalAxis(ChartMetadataForm cmdForm){
		VerticalAxis vAxis = VerticalAxis.Factory.newInstance();
		if(isBarChart(cmdForm)){
			CategoryAxis categoryAxis = toCategoryAxis( cmdForm );
			if(categoryAxis != null){
				CategoryAxis newCategoryAxis = vAxis.addNewCategoryAxis();
				newCategoryAxis.set(categoryAxis);
			}
		}
		else {
			LinearAxis linearAxis = toLinearAxis1(cmdForm);
			if(linearAxis != null){
				LinearAxis newLinearAxis = vAxis.addNewLinearAxis();
				newLinearAxis.set(linearAxis);
			}
			linearAxis = toLinearAxis2(cmdForm);
			if(linearAxis != null){
				LinearAxis newLinearAxis = vAxis.addNewLinearAxis();
				newLinearAxis.set(linearAxis);
			}
		}
		return vAxis;
	}

	private HorizontalAxisRenderers toHorizontalAxisRenderers(ChartMetadataForm cmdForm){
		HorizontalAxisRenderers hAxisRenderers = HorizontalAxisRenderers.Factory.newInstance();
		if(isBarChart(cmdForm)){
			if(cmdForm.getSelectedLinearAxis().containsValue("L1")){
				AxisRenderer axisRenderer = hAxisRenderers.addNewAxisRenderer();
				axisRenderer.setAxis("L1");

				if (!StrUtl.isEmptyTrimmed(  cmdForm.getLinearAxisPlacement1() ) ) {
					axisRenderer.setPlacement(PlacementType.Enum.forString( cmdForm.getLinearAxisPlacement1() ));
				}
			}
			if(cmdForm.getSelectedLinearAxis().containsValue("L2")){
				AxisRenderer axisRenderer = hAxisRenderers.addNewAxisRenderer();
				axisRenderer.setAxis("L2");

				if (!StrUtl.isEmptyTrimmed(  cmdForm.getLinearAxisPlacement2() ) ) {
					axisRenderer.setPlacement(PlacementType.Enum.forString( cmdForm.getLinearAxisPlacement2() ));
				}
			}
		}
		else {
			AxisRenderer axisRenderer = hAxisRenderers.addNewAxisRenderer();
			axisRenderer.setAxis("C1");

			if (!StrUtl.isEmptyTrimmed(  cmdForm.getCategoryAxisPlacement() ) ) {
				axisRenderer.setPlacement(PlacementType.Enum.forString( cmdForm.getCategoryAxisPlacement() ));
			}
		}
		return hAxisRenderers;
	}

	private VerticalAxisRenderers toVerticalAxisRenderers(ChartMetadataForm cmdForm){
		VerticalAxisRenderers vAxisRenderers = VerticalAxisRenderers.Factory.newInstance();
		if(isBarChart(cmdForm)){
			AxisRenderer axisRenderer = vAxisRenderers.addNewAxisRenderer();
			axisRenderer.setAxis("C1");

			if (!StrUtl.isEmptyTrimmed(  cmdForm.getCategoryAxisPlacement() ) ) {
				axisRenderer.setPlacement(PlacementType.Enum.forString( cmdForm.getCategoryAxisPlacement() ));
			}
		}
		else {
			if(cmdForm.getSelectedLinearAxis().containsValue("L1")){
				AxisRenderer axisRenderer = vAxisRenderers.addNewAxisRenderer();
				axisRenderer.setAxis("L1");

				if (!StrUtl.isEmptyTrimmed(  cmdForm.getLinearAxisPlacement1() ) ) {
					axisRenderer.setPlacement(PlacementType.Enum.forString( cmdForm.getLinearAxisPlacement1() ));
				}
			}
			if(cmdForm.getSelectedLinearAxis().containsValue("L2")){
				AxisRenderer axisRenderer = vAxisRenderers.addNewAxisRenderer();
				axisRenderer.setAxis("L2");

				if (!StrUtl.isEmptyTrimmed(  cmdForm.getLinearAxisPlacement2() ) ) {
					axisRenderer.setPlacement(PlacementType.Enum.forString( cmdForm.getLinearAxisPlacement2() ));
				}
			}
		}
		return vAxisRenderers;
	}

	private LinearAxis toLinearAxis1(ChartMetadataForm cmdForm) {
		LinearAxis linearAxis = null;
		if(cmdForm.getSelectedLinearAxis().containsValue("L1")){
			linearAxis = LinearAxis.Factory.newInstance();
			linearAxis.setId("L1");
			if (!StrUtl.isEmptyTrimmed( cmdForm.getLinearAxisLabel1() ) ) {
				linearAxis.setTitle( cmdForm.getLinearAxisLabel1() );
			}
			if (!StrUtl.isEmptyTrimmed( cmdForm.getLinearAxisMin1() ) ) {
				linearAxis.setMinimum(Integer.parseInt( cmdForm.getLinearAxisMin1() ));
			}
			if (!StrUtl.isEmptyTrimmed( cmdForm.getLinearAxisMax1() ) ) {
				linearAxis.setMaximum(Integer.parseInt( cmdForm.getLinearAxisMax1() ));
			}
			if (!StrUtl.isEmptyTrimmed( cmdForm.getLinearAxisInterval1() ) ) {
				linearAxis.setInterval(Integer.parseInt( cmdForm.getLinearAxisInterval1() ));
			}
		}
		return linearAxis;
	}

	private LinearAxis toLinearAxis2(ChartMetadataForm cmdForm) {
		LinearAxis linearAxis = null;
		if(cmdForm.getSelectedLinearAxis().containsValue("L2")){
			linearAxis = LinearAxis.Factory.newInstance();
			linearAxis.setId("L2");
			if (!StrUtl.isEmptyTrimmed( cmdForm.getLinearAxisLabel2() ) ) {
				linearAxis.setTitle( cmdForm.getLinearAxisLabel2() );
			}
			if (!StrUtl.isEmptyTrimmed( cmdForm.getLinearAxisMin2() ) ) {
				linearAxis.setMinimum(Integer.parseInt( cmdForm.getLinearAxisMin2() ));
			}
			if (!StrUtl.isEmptyTrimmed( cmdForm.getLinearAxisMax2() ) ) {
				linearAxis.setMaximum(Integer.parseInt( cmdForm.getLinearAxisMax2() ));
			}
			if (!StrUtl.isEmptyTrimmed( cmdForm.getLinearAxisInterval2() ) ) {
				linearAxis.setInterval(Integer.parseInt( cmdForm.getLinearAxisInterval2() ));
			}
		}
		return linearAxis;
	}

	private CategoryAxis toCategoryAxis(ChartMetadataForm cmdForm) {

		EditorMetaData editorMetaData = cmdForm.getMetadata();
		ColumnMetaData columnMetaData = null;
		CategoryAxis categoryAxis = null;

		String categoryAxisColumn = cmdForm.getCategoryAxis();

		if (!StrUtl.isEmptyTrimmed(categoryAxisColumn)) {
			columnMetaData = editorMetaData.getColumnMetaData(categoryAxisColumn);
			if (columnMetaData != null) {
				categoryAxis = CategoryAxis.Factory.newInstance();
				categoryAxis.setId("C1");
				categoryAxis.setCategoryField(columnMetaData.getName());
				categoryAxis.setTitle(columnMetaData.getCaption());
			}
		}
		return categoryAxis;
	}


	private Series toSeries(ChartMetadataForm cmdForm) {
		Series series = Series.Factory.newInstance();

		EditorMetaData editorMetaData = cmdForm.getMetadata();
		String[] selectedSeries = cmdForm.getSelectedSeries();
		Map selectedSeriesTypes = cmdForm.getSelectedSeriesTypes();
		Map selectedSeriesDetail = cmdForm.getSelectedSeriesDetail();
		String categoryAxis = cmdForm.getCategoryAxis();
		ColumnMetaData columnMetaData = null;
		String columnName = null;
		String seriesType = null;
		String form = null;
		String type = null;

		if (selectedSeries != null) {
			for (int i = 0; i < selectedSeries.length; i++) {
				columnName = selectedSeries[i];
				columnMetaData = editorMetaData.getColumnMetaData(columnName);
				if (columnMetaData != null) {
					seriesType = (String) selectedSeriesTypes.get(columnName);
					if ("ColumnSeries".equals(seriesType)) {
						type = (String) selectedSeriesDetail.get(columnName);
						if(isBarChart(cmdForm)){
							ChartSeries chartSeries = series.addNewChartSeries();
							chartSeries.setType( ChartSeriesType.BAR_SERIES );
							chartSeries.setVerticalAxis("C1");
							chartSeries.setHorizontalAxis(cmdForm.getSelectedLinearAxis().get( columnMetaData.getName() ));
							chartSeries.setYField(categoryAxis);
							chartSeries.setXField(columnMetaData.getName());
							chartSeries.setDisplayName(columnMetaData.getCaption());
							if (!StrUtl.isEmptyTrimmed(type)) {
								chartSeries.setSeriesType(type.toLowerCase());
							}
						}
						else {
							ChartSeries chartSeries = series.addNewChartSeries();
							chartSeries.setType( ChartSeriesType.COLUMN_SERIES  );

							chartSeries.setHorizontalAxis("C1");
							chartSeries.setVerticalAxis(cmdForm.getSelectedLinearAxis().get( columnMetaData.getName() ));
							chartSeries.setXField(categoryAxis);
							chartSeries.setYField(columnMetaData.getName());
							chartSeries.setDisplayName(columnMetaData.getCaption());
							if (!StrUtl.isEmptyTrimmed(type)) {
								chartSeries.setSeriesType(type.toLowerCase());
							}
						}
					}
					else if ("LineSeries".equals(seriesType)) {
						ChartSeries chartSeries = series.addNewChartSeries();
						chartSeries.setType( ChartSeriesType.LINE_SERIES  );

						if(isBarChart(cmdForm)){
							chartSeries.setVerticalAxis("C1");
							chartSeries.setHorizontalAxis(cmdForm.getSelectedLinearAxis().get( columnMetaData.getName() ));
							chartSeries.setYField(categoryAxis);
							chartSeries.setXField(columnMetaData.getName());
						}
						else {
							chartSeries.setHorizontalAxis("C1");
							chartSeries.setVerticalAxis(cmdForm.getSelectedLinearAxis().get( columnMetaData.getName() ));
							chartSeries.setXField(categoryAxis);
							chartSeries.setYField(columnMetaData.getName());
						}
						chartSeries.setDisplayName(columnMetaData.getCaption());

						form = (String) selectedSeriesDetail.get(columnName);
						if (!StrUtl.isEmptyTrimmed(form)) {
							chartSeries.setSeriesType(form.toLowerCase());
						}
					}
				}
			}
		}
		return series;
	}

	// XML to FORM
	private void fromDimension(ChartMetadataForm cmdForm, OLAPCubeMetadata metadata) {
		List rowDimensions = new ArrayList();
		List colDimensions = new ArrayList();
		List subTotals = new ArrayList();

		Dimension[] dimensionArray = null;
		Dimension dimension = null;
		if (metadata.getRowDimensions() != null) {
			dimensionArray = metadata.getRowDimensions().getDimensionArray();
			if (dimensionArray != null) {
				for (int i = 0; i < dimensionArray.length; i++) {
					dimension = dimensionArray[i];
					rowDimensions.add(dimension.getDataField());
					if (dimension.getSubtotal()) {
						subTotals.add(dimension.getDataField());
					}
				}
			}
		}
		if (metadata.getColDimensions() != null) {
			dimensionArray = metadata.getColDimensions().getDimensionArray();
			if (dimensionArray != null) {
				for (int i = 0; i < dimensionArray.length; i++) {
					dimension = dimensionArray[i];
					colDimensions.add(dimension.getDataField());
					if (dimension.getSubtotal()) {
						subTotals.add(dimension.getDataField());
					}
				}
			}
		}

		String[] selectedRowDims = new String[rowDimensions.size()];
		selectedRowDims = (String[]) rowDimensions.toArray(selectedRowDims);
		cmdForm.setSelectedRowDims(selectedRowDims);

		String[] selectedColDims = new String[colDimensions.size()];
		selectedColDims = (String[]) colDimensions.toArray(selectedColDims);
		cmdForm.setSelectedColDims(selectedColDims);

		String[] selectedSubTotals = new String[subTotals.size()];
		selectedSubTotals = (String[]) subTotals.toArray(selectedSubTotals);
		cmdForm.setSelectedSubTotals(selectedSubTotals);

	}

	private void fromMeasure(ChartMetadataForm cmdForm, OLAPCubeMetadata metadata) {
		List measures = new ArrayList();
		Map<String,String> aggregators = new HashMap();
		Map<String,String> thresholds  = new HashMap();
		Map<String,String> colors  = new HashMap();
		if (metadata.getMeasures() != null) {
			Measure[] measureArray = metadata.getMeasures().getMeasureArray();
			Measure measure = null;
			if (measureArray != null) {
				for (int i = 0; i < measureArray.length; i++) {
					measure = measureArray[i];
					measures.add(measure.getDataField());
					if (measure.getAggregator() != null) {
						aggregators.put(measure.getDataField(), measure.getAggregator().toString());
					}
					if (measure.getThresholds()!= null) {
						thresholds.put(measure.getDataField(), measure.getThresholds());
					}

					if (measure.getColors() != null) {
						colors.put(measure.getDataField(), measure.getColors());
					}
				}
			}
		}
		cmdForm.setSelectedAggregators(aggregators);

		String[] selectedMeasures = new String[measures.size()];
		selectedMeasures = (String[]) measures.toArray(selectedMeasures);
		cmdForm.setSelectedMeasures(selectedMeasures);
		cmdForm.setSelectedThresholds(thresholds);
		cmdForm.setSelectedColors(colors);
	}

	private void fromLinearAxis(ChartMetadataForm cmdForm, LinearAxis[] linearAxisArray) {
		if (linearAxisArray != null) {
			if(linearAxisArray.length > 0){
				LinearAxis linearAxis = linearAxisArray[0];
				cmdForm.setLinearAxisLabel1( linearAxis.getTitle());
				if( linearAxis.isSetMinimum()){
					cmdForm.setLinearAxisMin1(String.valueOf(linearAxis.getMinimum()));
				}
				if( linearAxis.isSetMaximum()){
					cmdForm.setLinearAxisMax1(String.valueOf(linearAxis.getMaximum()));
				}
				if( linearAxis.isSetInterval()){
					cmdForm.setLinearAxisInterval1(String.valueOf(linearAxis.getInterval()));
				}
			}
			if(linearAxisArray.length > 1){
				LinearAxis linearAxis = linearAxisArray[1];
				cmdForm.setLinearAxisLabel2( linearAxis.getTitle());
				if( linearAxis.isSetMinimum()){
					cmdForm.setLinearAxisMin2(String.valueOf(linearAxis.getMinimum()));
				}
				if( linearAxis.isSetMaximum()){
					cmdForm.setLinearAxisMax2(String.valueOf(linearAxis.getMaximum()));
				}
				if( linearAxis.isSetInterval()){
					cmdForm.setLinearAxisInterval2(String.valueOf(linearAxis.getInterval()));
				}
			}
		}
	}

	private void fromCategoryAxis(ChartMetadataForm cmdForm, CategoryAxis[] categoryAxisArray) {
		if (categoryAxisArray != null && categoryAxisArray.length > 0) {
			CategoryAxis categoryAxis = categoryAxisArray[0];
			cmdForm.setCategoryAxis(categoryAxis.getCategoryField());
		}
	}

	private void fromAxisRenderer(ChartMetadataForm cmdForm, AxisRenderer[] axisRendererArray) {
		if (axisRendererArray != null) {

			if(axisRendererArray.length > 0){
				AxisRenderer axisRenderer = axisRendererArray[0];
				if("C1".equalsIgnoreCase(axisRenderer.getAxis())){
					cmdForm.setCategoryAxisPlacement( axisRenderer.getPlacement().toString() );
				}
				if("L1".equalsIgnoreCase(axisRenderer.getAxis())){
					cmdForm.setLinearAxisPlacement1( axisRenderer.getPlacement().toString() );
				}
				if("L2".equalsIgnoreCase(axisRenderer.getAxis())){
					cmdForm.setLinearAxisPlacement2( axisRenderer.getPlacement().toString() );
				}
			}

			if(axisRendererArray.length > 1){
				AxisRenderer axisRenderer = axisRendererArray[1];

				if("L1".equalsIgnoreCase(axisRenderer.getAxis())){
					cmdForm.setLinearAxisPlacement1( axisRenderer.getPlacement().toString() );
				}
				if("L2".equalsIgnoreCase(axisRenderer.getAxis())){
					cmdForm.setLinearAxisPlacement2( axisRenderer.getPlacement().toString() );
				}
			}
		}
	}

	private void fromSeries(ChartMetadataForm cmdForm, Series series) {
		List seriesList = new ArrayList();
		Map<String,String> selectedSeriesTypes = new HashMap<String,String>();
		Map<String,String> selectedSeriesDetail = new HashMap<String,String>();
		Map<String,String> selectedLinearAxis = new HashMap<String,String>();

		String fieldName = null;

		if (series.sizeOfChartSeriesArray() > 0) {
			boolean isBarChart = isBarChart(series);
			ChartSeries chartSeriesArray[] = series.getChartSeriesArray();
			ChartSeries chartSeries = null;

			for (int i = 0; i < chartSeriesArray.length; i++) {
				chartSeries = chartSeriesArray[i];
				if(ChartSeriesType.COLUMN_SERIES.equals( chartSeries.getType() )){
					fieldName = chartSeries.getYField();

					if (chartSeries.getVerticalAxis() != null) {
						selectedLinearAxis.put(fieldName, chartSeries.getVerticalAxis());
					}

					selectedSeriesTypes.put(fieldName, ChartSeriesType.COLUMN_SERIES.toString() );
				}
				else if(ChartSeriesType.BAR_SERIES.equals( chartSeries.getType() )){
					fieldName = chartSeries.getXField();

					if (chartSeries.getHorizontalAxis() != null) {
						selectedLinearAxis.put(fieldName, chartSeries.getHorizontalAxis());
					}

					selectedSeriesTypes.put(fieldName, ChartSeriesType.COLUMN_SERIES.toString() );
				}
				else if(ChartSeriesType.LINE_SERIES.equals( chartSeries.getType() )){

					if(isBarChart){
						fieldName = chartSeries.getXField();
						if (chartSeries.getHorizontalAxis() != null) {
							selectedLinearAxis.put(fieldName, chartSeries.getHorizontalAxis());
						}
					}
					else {
						fieldName = chartSeries.getYField();
						if (chartSeries.getVerticalAxis() != null) {
							selectedLinearAxis.put(fieldName, chartSeries.getVerticalAxis());
						}
					}
					selectedSeriesTypes.put(fieldName, ChartSeriesType.LINE_SERIES.toString() );
				}

				if (chartSeries.getSeriesType() != null) {
					selectedSeriesDetail.put(fieldName, chartSeries.getSeriesType().toUpperCase());
				}

				seriesList.add( fieldName );
			}
		}
		String[] selectedSeries = new String[seriesList.size()];
		selectedSeries = (String[]) seriesList.toArray(selectedSeries);
		cmdForm.setSelectedSeries(selectedSeries);

		cmdForm.setSelectedSeriesTypes(selectedSeriesTypes);
		cmdForm.setSelectedSeriesDetail(selectedSeriesDetail);
		cmdForm.setSelectedLinearAxis(selectedLinearAxis);


	}
	private boolean isBarChart(ChartMetadataForm cmdForm){
		String categoryAxisPlacement = cmdForm.getCategoryAxisPlacement();
		return "left".equalsIgnoreCase(categoryAxisPlacement) || "right".equalsIgnoreCase(categoryAxisPlacement);
	}
	private boolean isBarChart(Series series){
		boolean hasBarSeries = false;
		if (series.sizeOfChartSeriesArray() > 0) {
			ChartSeries chartSeriesArray[] = series.getChartSeriesArray();
			ChartSeries chartSeries = null;
			for (int i = 0; i < chartSeriesArray.length; i++) {
				chartSeries = chartSeriesArray[i];
				if ("C1".equalsIgnoreCase( chartSeries.getVerticalAxis() ) ) {
					hasBarSeries = true;
					break;
				}
			}
		}
		return hasBarSeries;
	}
}
