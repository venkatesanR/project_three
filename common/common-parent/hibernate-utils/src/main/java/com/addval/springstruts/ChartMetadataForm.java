package com.addval.springstruts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.regexp.RE;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.StrUtl;

public class ChartMetadataForm extends ActionForm {

	private static final long serialVersionUID = 4787168410498967117L;
	private String kindOfAction = null;
	private String editorName = null;

	private int userCriteriaKey = 0;
	private int userCriteriaChartKey = 0;

	private int chartIndex = -1;
	private String chartName = null;
	private String chartType = null;
	private ArrayList userCriteriaCharts = null;
	private ArrayList chartTypes = null;
	private EditorMetaData metadata = null;
	private String chartWidth = null;
	private String chartHeight = null;

	// ColumnChart or LineChart

	private String categoryAxis = null;
	private String categoryAxisPlacement = "bottom"; //default
	private ArrayList placements = null;

	private String[] selectedSeries = null;
	private Map<String,String> selectedSeriesTypes = null;
	private Map<String,String> selectedSeriesDetail = null;
	private Map<String,String> selectedLinearAxis = null;

	private String linearAxisLabel1 = null;
	private String linearAxisMin1 = null;
	private String linearAxisMax1 = null;
	private String linearAxisInterval1 = null;
	private String linearAxisPlacement1 = null;

	private String linearAxisLabel2 = null;
	private String linearAxisMin2 = null;
	private String linearAxisMax2 = null;
	private String linearAxisInterval2 = null;
	private String linearAxisPlacement2 = null;

	private boolean showDataGrid = false;
	private String dataGridPlacement = "bottom";


	// OLAPCube
	private String[] dimensions = null;
	private String[] measures = null;
	private String selectedSubTotals[] = null;
	private String selectedMeasures[] = null;
	private String selectedRowDims[] = null;
	private String selectedColDims[] = null;
	private Map<String,String> selectedAggregators = null;
	private boolean addMeasuresToCol = true;

	private Map<String,String> selectedThresholds = null;
	private Map<String,String> selectedColors = null;

	public ChartMetadataForm() {

	}

	public int getUserCriteriaKey() {
		return userCriteriaKey;
	}

	public void setUserCriteriaKey(int userCriteriaKey) {
		this.userCriteriaKey = userCriteriaKey;
	}

	public int getUserCriteriaChartKey() {
		return userCriteriaChartKey;
	}

	public void setUserCriteriaChartKey(int userCriteriaChartKey) {
		this.userCriteriaChartKey = userCriteriaChartKey;
	}

	public String getChartName() {
		return chartName;
	}

	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

	public String getEditorName() {
		return editorName;
	}

	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

	public String getKindOfAction() {
		return kindOfAction;
	}

	public void setKindOfAction(String kindOfAction) {
		this.kindOfAction = kindOfAction;
	}

	public int getChartIndex() {
		return chartIndex;
	}

	public void setChartIndex(int chartIndex) {
		this.chartIndex = chartIndex;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public ArrayList getChartTypes() {
		return chartTypes;
	}

	public void setChartTypes(ArrayList chartTypes) {
		this.chartTypes = chartTypes;
	}


	public EditorMetaData getMetadata() {
		return metadata;
	}

	public void setMetadata(EditorMetaData metadata) {
		this.metadata = metadata;
	}

	public String[] getDimensions() {
		return dimensions;
	}

	public void setDimensions(String[] dimensions) {
		this.dimensions = dimensions;
	}

	public String[] getMeasures() {
		return measures;
	}

	public void setMeasures(String[] measures) {
		this.measures = measures;
	}

	public String[] getSelectedSubTotals() {
		return selectedSubTotals;
	}

	public void setSelectedSubTotals(String[] selectedSubTotals) {
		this.selectedSubTotals = selectedSubTotals;
	}

	public String[] getSelectedMeasures() {
		return selectedMeasures;
	}

	public void setSelectedMeasures(String[] selectedMeasures) {
		this.selectedMeasures = selectedMeasures;
	}

	public Map<String,String> getSelectedAggregators() {
		return selectedAggregators;
	}

	public void setSelectedAggregators(Map<String,String> selectedAggregators) {
		this.selectedAggregators = selectedAggregators;
	}

	public Map<String,String> getSelectedThresholds() {
		return selectedThresholds;
	}

	public void setSelectedThresholds(Map<String,String> selectedThresholds) {
		this.selectedThresholds = selectedThresholds;
	}

	public Map<String,String> getSelectedColors() {
		return selectedColors;
	}

	public void setSelectedColors(Map<String,String> selectedColors) {
		this.selectedColors = selectedColors;
	}

	public boolean isAddMeasuresToCol() {
		return addMeasuresToCol;
	}

	public void setAddMeasuresToCol(boolean addMeasuresToCol) {
		this.addMeasuresToCol = addMeasuresToCol;
	}

	public ArrayList getUserCriteriaCharts() {
		return userCriteriaCharts;
	}

	public void setUserCriteriaCharts(ArrayList userCriteriaCharts) {
		this.userCriteriaCharts = userCriteriaCharts;
	}

	public String[] getSelectedRowDims() {
		return selectedRowDims;
	}

	public void setSelectedRowDims(String[] selectedRowDims) {
		this.selectedRowDims = selectedRowDims;
	}

	public String[] getSelectedColDims() {
		return selectedColDims;
	}

	public void setSelectedColDims(String[] selectedColDims) {
		this.selectedColDims = selectedColDims;
	}

	public String getChartWidth() {
		return chartWidth;
	}

	public void setChartWidth(String chartWidth) {
		this.chartWidth = chartWidth;
	}

	public String getChartHeight() {
		return chartHeight;
	}

	public void setChartHeight(String chartHeight) {
		this.chartHeight = chartHeight;
	}

	public List<LabelValueBean> getChartDimensions() {
		List<LabelValueBean> dimensions = new ArrayList<LabelValueBean>();
		EditorMetaData editorMetaData = getMetadata();
		ColumnMetaData columnMetaData = null;
		if (editorMetaData != null) {
			Vector displayable = editorMetaData.getDisplayableColumns();
			for (int i = 0; i < displayable.size(); i++) {
				columnMetaData = (ColumnMetaData) displayable.get(i);
				if (columnMetaData.isAggregatable()) {
					continue;
				}
				dimensions.add(new LabelValueBean(columnMetaData.getCaption(), columnMetaData.getName()));
			}
		}
		return dimensions;
	}

	public List<ColumnMetaData> getChartMeasures(){
		List<ColumnMetaData> measures = new ArrayList<ColumnMetaData>();
		List<String> uniqueNames = new ArrayList<String>();

		EditorMetaData editorMetaData = getMetadata();
		ColumnMetaData columnMetaData = null;
		String columnName = null;

		if(selectedSeries != null && selectedSeries.length >0 ){
			for(int i=0;i<selectedSeries.length;i++){
				columnName =  selectedSeries[i];
				columnMetaData = editorMetaData.getColumnMetaData( columnName );
				if( !uniqueNames.contains( columnName ) && columnMetaData != null ){
					uniqueNames.add(columnName);
					measures.add(columnMetaData);
				}
			}
		}

		List aggregatableColumns = editorMetaData.getAggregatableColumns();
		if(null != aggregatableColumns)
		{
			for(int i=0;i<aggregatableColumns.size();i++) {
				columnMetaData = (ColumnMetaData) aggregatableColumns.get(i);
				columnName = columnMetaData.getName();
				if( !uniqueNames.contains( columnName )){
					uniqueNames.add(columnName);
					measures.add(columnMetaData);
				}
			}
		}
		return measures;
	}

	public List<ColumnMetaData> getTableMeasures(){
		List<ColumnMetaData> measures = new ArrayList<ColumnMetaData>();
		List<String> uniqueNames = new ArrayList<String>();

		EditorMetaData editorMetaData = getMetadata();
		ColumnMetaData columnMetaData = null;
		String columnName = null;

		if(selectedMeasures != null && selectedMeasures.length >0 ){
			for(int i=0;i<selectedMeasures.length;i++){
				columnName =  selectedMeasures[i];
				columnMetaData = editorMetaData.getColumnMetaData( columnName );
				if( !uniqueNames.contains( columnName ) && columnMetaData != null ){
					uniqueNames.add(columnName);
					measures.add(columnMetaData);
				}
			}
		}

		List aggregatableColumns = editorMetaData.getAggregatableColumns();
		if(null != aggregatableColumns) {
			for(int i=0;i<aggregatableColumns.size();i++) {
				columnMetaData = (ColumnMetaData) aggregatableColumns.get(i);
				columnName = columnMetaData.getName();
				if( !uniqueNames.contains( columnName )){
					uniqueNames.add(columnName);
					measures.add(columnMetaData);
				}
			}
		}
		return measures;
	}

	public List<ColumnMetaData> getTableDimensions(){
		List<ColumnMetaData> dimensions = new ArrayList<ColumnMetaData>();
		List<String> uniqueNames = new ArrayList<String>();

		EditorMetaData editorMetaData = getMetadata();
		ColumnMetaData columnMetaData = null;
		String columnName = null;

		if(selectedRowDims != null && selectedRowDims.length >0 ){
			for(int i=0;i<selectedRowDims.length;i++){
				columnName =  selectedRowDims[i];
				columnMetaData = editorMetaData.getColumnMetaData( columnName );
				if( !uniqueNames.contains( columnName ) && columnMetaData != null ){
					uniqueNames.add(columnName);
					dimensions.add(columnMetaData);
				}
			}
		}

		if(selectedColDims != null && selectedColDims.length >0 ){
			for(int i=0;i<selectedColDims.length;i++){
				columnName =  selectedColDims[i];
				columnMetaData = editorMetaData.getColumnMetaData( columnName );
				if( !uniqueNames.contains( columnName ) && columnMetaData != null ){
					uniqueNames.add(columnName);
					dimensions.add(columnMetaData);
				}
			}
		}

		Vector displayable = editorMetaData.getDisplayableColumns();
		for (int i = 0; i < displayable.size(); i++) {
			columnMetaData = (ColumnMetaData) displayable.get(i);
			if (columnMetaData.isAggregatable()) {
				continue;
			}
			columnName = columnMetaData.getName();
			if( !uniqueNames.contains( columnName )){
				uniqueNames.add(columnName);
				dimensions.add(columnMetaData);
			}
		}

		return dimensions;
	}



	public String getCategoryAxis() {
		return categoryAxis;
	}

	public void setCategoryAxis(String categoryAxis) {
		this.categoryAxis = categoryAxis;
	}

	public String getCategoryAxisPlacement() {
		return categoryAxisPlacement;
	}

	public void setCategoryAxisPlacement(String categoryAxisPlacement) {
		this.categoryAxisPlacement = categoryAxisPlacement;
	}

	public ArrayList getPlacements() {
		return placements;
	}

	public void setPlacements(ArrayList placements) {
		this.placements = placements;
	}


	public String[] getSelectedSeries() {
		return selectedSeries;
	}

	public void setSelectedSeries(String[] selectedSeries) {
		this.selectedSeries = selectedSeries;
	}

	public Map<String,String> getSelectedSeriesTypes() {
		return selectedSeriesTypes;
	}

	public void setSelectedSeriesTypes(Map<String,String> selectedSeriesTypes) {
		this.selectedSeriesTypes = selectedSeriesTypes;
	}

	public Map getSelectedSeriesDetail() {
		return selectedSeriesDetail;
	}

	public void setSelectedSeriesDetail(Map selectedSeriesDetail) {
		this.selectedSeriesDetail = selectedSeriesDetail;
	}

	public Map<String, String> getSelectedLinearAxis() {
		return selectedLinearAxis;
	}

	public void setSelectedLinearAxis(Map<String, String> selectedLinearAxis) {
		this.selectedLinearAxis = selectedLinearAxis;
	}

	public String getLinearAxisLabel1() {
		return linearAxisLabel1;
	}

	public void setLinearAxisLabel1(String linearAxisLabel1) {
		this.linearAxisLabel1 = linearAxisLabel1;
	}

	public String getLinearAxisMin1() {
		return linearAxisMin1;
	}

	public void setLinearAxisMin1(String linearAxisMin1) {
		this.linearAxisMin1 = linearAxisMin1;
	}

	public String getLinearAxisMax1() {
		return linearAxisMax1;
	}

	public void setLinearAxisMax1(String linearAxisMax1) {
		this.linearAxisMax1 = linearAxisMax1;
	}

	public String getLinearAxisInterval1() {
		return linearAxisInterval1;
	}

	public void setLinearAxisInterval1(String linearAxisInterval1) {
		this.linearAxisInterval1 = linearAxisInterval1;
	}

	public String getLinearAxisPlacement1() {
		return linearAxisPlacement1;
	}

	public void setLinearAxisPlacement1(String linearAxisPlacement1) {
		this.linearAxisPlacement1 = linearAxisPlacement1;
	}

	public String getLinearAxisLabel2() {
		return linearAxisLabel2;
	}

	public void setLinearAxisLabel2(String linearAxisLabel2) {
		this.linearAxisLabel2 = linearAxisLabel2;
	}

	public String getLinearAxisMin2() {
		return linearAxisMin2;
	}

	public void setLinearAxisMin2(String linearAxisMin2) {
		this.linearAxisMin2 = linearAxisMin2;
	}

	public String getLinearAxisMax2() {
		return linearAxisMax2;
	}

	public void setLinearAxisMax2(String linearAxisMax2) {
		this.linearAxisMax2 = linearAxisMax2;
	}

	public String getLinearAxisInterval2() {
		return linearAxisInterval2;
	}

	public void setLinearAxisInterval2(String linearAxisInterval2) {
		this.linearAxisInterval2 = linearAxisInterval2;
	}

	public String getLinearAxisPlacement2() {
		return linearAxisPlacement2;
	}

	public void setLinearAxisPlacement2(String linearAxisPlacement2) {
		this.linearAxisPlacement2 = linearAxisPlacement2;
	}

	public boolean isShowDataGrid() {
		return showDataGrid;
	}

	public void setShowDataGrid(boolean showDataGrid) {
		this.showDataGrid = showDataGrid;
	}

	public String getDataGridPlacement() {
		return dataGridPlacement;
	}

	public void setDataGridPlacement(String dataGridPlacement) {
		this.dataGridPlacement = dataGridPlacement;
	}


	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		if(getChartMeasures().isEmpty() && getTableMeasures().isEmpty())
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Cannot create Chart as measures are not available."));
			return errors;
		}		
		String kindOfAction = getKindOfAction() != null ? getKindOfAction() : "";
		RE matcher = null;
		String value = null;
		String values[] = null;
		if (!kindOfAction.equals("saveChart")) 
			return errors;
		EditorMetaData editorMetaData = getMetadata();
		ColumnMetaData columnMetaData = null;

		value = getChartName();
		if(StrUtl.isEmptyTrimmed( value )){
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Chart Name is required."));
		}
		else {
			matcher = new RE("^\\w([\\-\\s\\w]*)$");
			if (!matcher.match(value)) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Invalid Chart Name."));
			}
		}

		value = getChartWidth();
		if(StrUtl.isEmptyTrimmed( value )){
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Width is required."));
		}
		else {
			matcher = new RE("^(\\d){1,3}%?$");
			if (!matcher.match(value)) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Invalid Width."));
			}
		}


		value = getChartHeight();
		if(StrUtl.isEmptyTrimmed( value )){
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Height is required."));
		}
		else {
			matcher = new RE("^(\\d){1,3}%?$");
			if (!matcher.match(value)) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Invalid Height."));
			}
		}

		if ("Chart".equals(getChartType())) {

			value = getCategoryAxis();
			if (StrUtl.isEmptyTrimmed(value)) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Dimension is required."));
			}

			value = getCategoryAxisPlacement();
			if (StrUtl.isEmptyTrimmed(value)) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Dimension Placement is required."));
			}

			values = getSelectedSeries();
			if (values == null || values.length == 0) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Measures is required."));
			}

			if (values != null && values.length > 0) {
				for (int i = 0; i < values.length; i++) {
					value = values[i];

					if(!getSelectedSeriesTypes().containsKey(value)){
						columnMetaData = editorMetaData.getColumnMetaData(value);
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Type is required for " + columnMetaData.getCaption() ));
					}
					if(!getSelectedLinearAxis().containsKey(value)){
						columnMetaData = editorMetaData.getColumnMetaData(value);
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Axis is required for " + columnMetaData.getCaption() ));
					}
				}
			}

			if(getSelectedLinearAxis().containsValue("L1")){
				value = getLinearAxisPlacement1();
				if (StrUtl.isEmptyTrimmed( value )) {
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "L1 Axis Placement is required."));
				}
				value = getLinearAxisLabel1();
				matcher = new RE("^(\\w([\\-\\s\\w\\/\\%\\(\\)]*))?$");
				if (!matcher.match(value)) {
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Invalid L1 Axis Label."));
				}
			}

			value = getLinearAxisMin1();
			matcher = new RE("^(\\d)*$");
			if (!matcher.match(value)) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Invalid L1 Axis Min."));
			}

			value = getLinearAxisMax1();
//			matcher = new RE("^(\\d)*$");
			if (!matcher.match(value)) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Invalid L1 Axis Max."));
			}

			value = getLinearAxisInterval1();
//			matcher = new RE("^(\\d)*$");
			if (!matcher.match(value)) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Invalid L1 Axis Interval."));
			}

			if(getSelectedLinearAxis().containsValue("L2")){

				value = getLinearAxisPlacement2();
				if (StrUtl.isEmptyTrimmed( value )) {
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "L2 Axis Placement is required."));
				}

				value = getLinearAxisLabel2();
				matcher = new RE("^(\\w([\\-\\s\\w]*))?$");
				if (!matcher.match(value)) {
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Invalid L2 Axis Label."));
				}

			}

			value = getLinearAxisMin2();
			matcher = new RE("^(\\d)*$");
			if (!matcher.match(value)) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Invalid L2 Axis Min."));
			}

			value = getLinearAxisMax2();
//			matcher = new RE("^(\\d)*$");
			if (!matcher.match(value)) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Invalid L2 Axis Max."));
			}

			value = getLinearAxisInterval2();
//			matcher = new RE("^(\\d)*$");
			if (!matcher.match(value)) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Invalid L2 Axis Interval."));
			}
		}

		else if ("Table".equals(getChartType())) {

			int rowDimsCount = getSelectedRowDims() != null ? getSelectedRowDims().length : 0;
			int colDimsCount = getSelectedColDims() != null ? getSelectedColDims().length : 0;
			if ((rowDimsCount + colDimsCount) == 0) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Dimensions is required"));
			}

			int measureCount = getSelectedMeasures() != null ? getSelectedMeasures().length : 0;

			if (measureCount == 0) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Measures is required"));
			}
			else if (colDimsCount == 0) {
				setAddMeasuresToCol(true);
			}
			else if (rowDimsCount == 0) {
				setAddMeasuresToCol(false);
			}


			for(String columnName : selectedThresholds.keySet()){
				if(!selectedColors.containsKey(columnName)){
					columnMetaData = editorMetaData.getColumnMetaData(columnName);
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Colors required for " + columnMetaData.getCaption() ));
				}
			}

			for(String columnName : selectedColors.keySet()){
				if(!selectedThresholds.containsKey(columnName)){
					columnMetaData = editorMetaData.getColumnMetaData(columnName);
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Thresholds required for " + columnMetaData.getCaption() ));
				}
			}

			if(errors.size() == 0){
				for(String columnName : selectedThresholds.keySet()){
					if(selectedColors.containsKey(columnName)){
						columnMetaData = editorMetaData.getColumnMetaData(columnName);

						value = selectedThresholds.get(columnName);
						//eg..   0,50000,75000,1000000 or 1.3,5,6.66 or 5
						matcher = new RE("^(\\d{0,8}(\\.\\d{0,2})?)?(,(\\d{0,8}(\\.\\d{0,2})?))*$");
						if (!matcher.match(value)) {
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Invalid Thresholds for " + columnMetaData.getCaption() ));
						}

						value = selectedColors.get(columnName);
						matcher = new RE("^([A-Za-z]{1,20})?(,([A-Za-z]{1,20}))*$");
						if (!matcher.match(value)) {
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Invalid Colors for " + columnMetaData.getCaption() ));
						}

						String measureValues[] = selectedThresholds.get(columnName).split(",");
						String measureColors[] = selectedColors.get(columnName).split(",");

						if(measureValues.length >0 && measureColors.length > 0 && measureValues.length <= measureColors.length){
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Invalid Thresholds Range for " + columnMetaData.getCaption() ));
						}
					}
				}
			}
		}
		return errors;
	}

	public void initializeFromMetadata(HttpServletRequest request, ActionMapping mapping, EditorMetaData metadata) {
		setMetadata(metadata);
		setEditorName(metadata.getName());
	}

}
