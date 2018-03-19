package com.addval.metadata;

import java.io.Serializable;

public class UserCriteriaChart  implements Serializable {
	private static final long serialVersionUID = 290522749203770149L;
	private int userCriteriaKey = 0;
	private int userCriteriaChartKey = 0;
	private String chartName = null;
	private String chartType = null;
	private int chartSeQNo;
	private String chartMetadata = null;
	private String createdBy = null;
	private String lastUpdatedBy= null;



	//For chartType = Chart
	private boolean showDataGrid = false;
	private String dataGridPlacement = null;

	public UserCriteriaChart(){

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

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}


	public int getChartSeQNo() {
		return chartSeQNo;
	}

	public void setChartSeQNo(int chartSeQNo) {
		this.chartSeQNo = chartSeQNo;
	}

	public String getChartMetadata() {
		return chartMetadata;
	}

	public void setChartMetadata(String chartMetadata) {
		this.chartMetadata = chartMetadata;
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
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
}
