package com.addval.springstruts;

import org.apache.struts.action.ActionForm;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;
import com.addval.metadata.ColumnMetaData;

public class CustomDisplayForm extends ActionForm
{

    private String subSystem = null;
	private String editorName = null;
	private String editorType = null;
    private String userId = null;
    private ArrayList availableColumns = null;
    private ArrayList usedColumns = null;
    private ArrayList allColumns = null;
    private String[] availableColumnList = null;
    private String[] usedColumnList = null;
	private String kindOfAction = null;
    private String parentSortName = null;
    private String parentSortOrder = null;
    private String[] sortOrderSeq = null;

    public CustomDisplayForm() {
        this.setSubSystem( null );
        this.setEditorName( null );
        this.setEditorType( null );
        this.setUserId( null );
        this.setParentSortName( null );
        this.setParentSortOrder( null );
        this.setAvailableColumns( new ArrayList() );
        this.setUsedColumns( new ArrayList() );
        this.setAllColumns( new ArrayList() );
        this.setKindOfAction( null );

	}
    public void reset(ActionMapping mapping, HttpServletRequest request) {

	}
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
        if(this.getEditorName() ==null || this.getEditorName().trim().length() == 0 )
        	errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.required", "Editor Name" ) );
		return errors;
	}

    public String getSubSystem() {
        return subSystem;
    }

    public void setSubSystem(String subSystem) {
        this.subSystem = subSystem;
    }

    public String getEditorName() {
        return editorName;
    }

    public void setEditorName(String editorName) {
        this.editorName = editorName;
    }
    public String getEditorType() {
        if(editorType == null ||editorType.trim().length() == 0)
            editorType = "DEFAULT";
        return editorType;
    }

    public void setEditorType(String editorType) {
        this.editorType = editorType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList getAvailableColumns() {
        return availableColumns;
    }

    public void setAvailableColumns(ArrayList availableColumns) {
        this.availableColumns = availableColumns;
    }

    public ArrayList getUsedColumns() {
        return usedColumns;
    }

    public void setUsedColumns(ArrayList usedColumns) {
        this.usedColumns = usedColumns;
    }

    public String getKindOfAction() {
        return kindOfAction;
    }

    public void setKindOfAction(String kindOfAction) {
        this.kindOfAction = kindOfAction;
    }

    public String[] getAvailableColumnList() {
        return availableColumnList;
    }

    public void setAvailableColumnList(String[] availableColumnList) {
        this.availableColumnList = availableColumnList;
    }

    public String[] getUsedColumnList() {
        return usedColumnList;
    }

    public void setUsedColumnList(String[] usedColumnList) {
        this.usedColumnList = usedColumnList;
    }
    public ArrayList getAllColumns() {
        return allColumns;
    }

    public void setAllColumns(ArrayList allColumns) {
        this.allColumns = allColumns;
    }
    public String getParentSortName() {
        return parentSortName;
    }

    public void setParentSortName(String parentSortName) {
        this.parentSortName = parentSortName;
    }

    public String getParentSortOrder() {
        return parentSortOrder;
    }

    public void setParentSortOrder(String parentSortOrder) {
        this.parentSortOrder = parentSortOrder;
    }

    public ColumnMetaData[] getColumsBySortOrderSeq() {
        ColumnMetaData columnMetaDatas[]  = new ColumnMetaData[ allColumns.size() ];
        ColumnMetaData columnMetaData  = null;
        ArrayList unAssigned = new ArrayList();
        for(int i=0;i<allColumns.size();i++) {
            columnMetaData = (ColumnMetaData) allColumns.get(i);
            if( columnMetaData.getSortOrderSeq() > -1 ){
                columnMetaDatas[ columnMetaData.getSortOrderSeq() ] = columnMetaData;
            }
            else {
                unAssigned.add( columnMetaData );
            }
        }
        Iterator iterator = unAssigned.iterator();

        for(int i=0;i<columnMetaDatas.length;i++) {
            if( columnMetaDatas[i] == null ){
                columnMetaDatas[i] = (ColumnMetaData) iterator.next();
            }
        }
        return columnMetaDatas;
    }

    public String[] getSortOrderSeq() {
        return sortOrderSeq;
    }

    public void setSortOrderSeq(String[] sortOrderSeq) {
        this.sortOrderSeq = sortOrderSeq;
    }

}
