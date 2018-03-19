package com.addval.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.addval.utils.StrUtl;

public class Table implements Serializable {
	private static final long serialVersionUID = 3346108444929668676L;
	public static String newline = System.getProperty("line.separator");

	private String name = null;
	private String heading = null;
	private String headingCss = null;
	private String contentCss = null;
	private String headingKey = null;
	private Boolean discrepancyCheck = null;
	private List<Row> rows = null;

	//UI_PAGE_SEC_PROPERTY_NAMES
	private static final String  _TABLE_NAME="TABLE_NAME";
	private static final String  _TABLE_HEADING="TABLE_HEADING";
	private static final String  _HEADING_CSS="HEADING_CSS";
	private static final String  _CONTENT_CSS="CONTENT_CSS";

	public Table() {

	}

	public Table(List<UIPageSectionProperty> uiPageSectionProperties) {
		if( uiPageSectionProperties != null ){
			for( UIPageSectionProperty uiPageSectionProperty :uiPageSectionProperties ) {
				if( _TABLE_NAME.equalsIgnoreCase( uiPageSectionProperty.getPropertyName() )){
					name = uiPageSectionProperty.getPropertyValue();
				}
				else if( _TABLE_HEADING.equalsIgnoreCase( uiPageSectionProperty.getPropertyName() )){
					heading = uiPageSectionProperty.getPropertyValue();
				}
				else if( _HEADING_CSS.equalsIgnoreCase( uiPageSectionProperty.getPropertyName() )){
					headingCss = uiPageSectionProperty.getPropertyValue();
				}
				else if( _CONTENT_CSS.equalsIgnoreCase( uiPageSectionProperty.getPropertyName() )){
					contentCss = uiPageSectionProperty.getPropertyValue();
				}
			}
		}
	}

	public Table(String name, String heading, String headingCss, String contentCss) {
		setName(name);
		setHeading(heading);
		setHeadingCss(headingCss);
		setContentCss(contentCss);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public String getHeadingCss() {
		return headingCss;
	}

	public void setHeadingCss(String headingCss) {
		this.headingCss = headingCss;
	}

	public String getContentCss() {
		return contentCss;
	}

	public void setContentCss(String contentCss) {
		this.contentCss = contentCss;
	}

	public String getHeadingKey() {
		return headingKey;
	}

	public void setHeadingKey(String headingKey) {
		this.headingKey = headingKey;
	}
	
	public Boolean isDiscrepancyCheck() {
		return discrepancyCheck;
	}

	public Boolean getDiscrepancyCheck() {
		return discrepancyCheck;
	}
	
	public void setDiscrepancyCheck(Boolean discrepancyCheck) {
		this.discrepancyCheck = discrepancyCheck;
	}

	public List<Row> getRows() {
		if (rows == null) {
			rows = new ArrayList<Row>();
		}

		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	public void addRow(Row row) {
		getRows().add(row);
	}

	public Col findCol(String colName) {
		if (StrUtl.isEmptyTrimmed(colName)) {
			return null;
		}
		for (Row row : getRows()) {
			for (Col col : row.getCols()) {
				if (colName.equalsIgnoreCase(col.getColName())) {
					return col;
				}
			}
		}
		return null;
	}
	
	public String toXML(String componentId) {
		StringBuffer buf = new StringBuffer();
		buf.append("<TableLayout");
		buf.append(" name").append("=\"").append(componentId).append("\"");
		if (!StrUtl.isEmptyTrimmed(heading)) {
			buf.append(" heading").append("=\"").append(heading).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(headingCss)) {
			buf.append(" headingCss").append("=\"").append(headingCss).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(contentCss)) {
			buf.append(" contentCss").append("=\"").append(contentCss).append("\"");
		}
		buf.append(">").append(newline);
		for (Row row : getRows()) {
			buf.append("<Row");
			if (!StrUtl.isEmptyTrimmed(row.getContainerStyleClass())) {
				buf.append(" containerStyleClass").append("=\"").append(row.getContainerStyleClass()).append("\"");
			}
			buf.append(">").append(newline);
			for (Col col : row.getCols()) {
				buf.append(col.toXML());
			}
			buf.append("</Row>").append(newline);
		}
		buf.append("</TableLayout>").append(newline);
		
		return buf.toString();
	}
}
