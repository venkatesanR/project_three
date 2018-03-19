
package com.addval.springstruts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.addval.ejbutils.server.EJBSEditorMetaData;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.AVConstants;
import com.addval.utils.XRuntime;

public class CustomDisplayUtil {
    private static final String _ASC = "ASC";
    private static final String _DESC = "DESC";

	private EJBSEditorMetaData _editorMetadataServer = null;

    public CustomDisplayUtil() {
	}

	public void setEditorMetadataServer(EJBSEditorMetaData editorMetadataServer) {
		_editorMetadataServer = editorMetadataServer;
	}

	public EJBSEditorMetaData getEditorMetadataServer() {
		return _editorMetadataServer;
	}


    public void lookup(CustomDisplayForm form) throws Exception {
        EditorMetaData editorMetadata = null;
        ColumnMetaData columnMetaData  = null;

        editorMetadata  = getEditorMetadataServer().lookup( form.getEditorName(), form.getEditorType(),form.getUserId() );
        ArrayList usedColumns = new ArrayList();
        ArrayList usedColumnList = new ArrayList();
        for (Iterator iter = editorMetadata.getDisplayableColumns().iterator(); iter.hasNext();) {
            columnMetaData = ( ColumnMetaData ) iter.next();
            if ( columnMetaData.isDisplayable() && columnMetaData.getType() != ColumnDataType._CDT_LINK) {
                usedColumns.add(columnMetaData);
                usedColumnList.add(columnMetaData.getName());
            }
        }

        editorMetadata  = getEditorMetadataServer().lookup( form.getEditorName(), form.getEditorType() );
        ArrayList availableColumns = new ArrayList();
        for (Iterator iter = editorMetadata.getDisplayableColumns().iterator(); iter.hasNext();) {
            columnMetaData = ( ColumnMetaData ) iter.next();
            if (columnMetaData.isDisplayable() && columnMetaData.getType() != ColumnDataType._CDT_LINK && ! usedColumnList.contains(columnMetaData.getName()) ) {
                availableColumns.add(columnMetaData);
            }
        }

        if(usedColumns.isEmpty()) {

            usedColumns = availableColumns;
            availableColumns = new ArrayList();
        }

        form.setAvailableColumns(availableColumns);
        form.setUsedColumns(usedColumns);

        ArrayList allColumns = new ArrayList();
        allColumns.addAll(usedColumns);
        allColumns.addAll(availableColumns);

        form.setAllColumns(allColumns);
	}

    public void update(CustomDisplayForm form,HttpServletRequest request) throws XRuntime,Exception {

        EditorMetaData editorMetadata = null;
        ColumnMetaData columnMetaData  = null;

        editorMetadata  = getEditorMetadataServer().lookup( form.getEditorName(), form.getEditorType());

		String kindOfAction = form.getKindOfAction() != null ? form.getKindOfAction() : "";
        String columnName = null;
        ArrayList usedColumns = new ArrayList();
        ArrayList availableColumns = new ArrayList();

        Vector avlColumnList = new Vector();
        if(form.getAvailableColumnList() != null) {
			for(int i=0;i<form.getAvailableColumnList().length;i++) {
				columnName = form.getAvailableColumnList()[i];
				columnMetaData = editorMetadata.getColumnMetaData(columnName);
				availableColumns.add(columnMetaData);
                avlColumnList.add(columnName);
			}
		}
        Vector usedColumnList = new Vector();
        if(form.getUsedColumnList() != null) {
			for(int i=0;i<form.getUsedColumnList().length;i++) {
				columnName = form.getUsedColumnList()[i];
				columnMetaData = editorMetadata.getColumnMetaData(columnName);
				usedColumns.add(columnMetaData);
                usedColumnList.add(columnName);
		    }
		}
        form.setAvailableColumns(availableColumns);
        form.setUsedColumns(usedColumns);
        ArrayList allColumns = new ArrayList();
        allColumns.addAll(usedColumns);
        allColumns.addAll(availableColumns);
        form.setAllColumns(allColumns);

        if( kindOfAction.equals( "save"  )) {
            if(!usedColumnList.isEmpty()) {
                Vector customColumns = new Vector();
                for (Iterator iterator = usedColumnList.iterator(); iterator.hasNext();) {
                    columnName = (String)iterator.next();
                    columnMetaData = editorMetadata.getColumnMetaData(columnName);
                    setOrdering(form,columnMetaData,request);
                    customColumns.add(columnMetaData);
                }

                for (Iterator iterator = editorMetadata.getColumnsMetaData().iterator(); iterator.hasNext();) {
                    columnMetaData = (ColumnMetaData)iterator.next();
                    columnName = columnMetaData.getName();

                    if(!usedColumnList.contains( columnName )) {
                        if(avlColumnList.contains( columnName ) ) {
                            columnMetaData.setDisplayable( false );
                        }
                        setOrdering(form,columnMetaData,request);
                        customColumns.add(columnMetaData);
                    }
                }
                setSortOrderSeq(form,customColumns);
                editorMetadata.setColumnsMetaData(customColumns);
            }
        }
        if( kindOfAction.equals( "save"  ) && (form.getUsedColumnList() == null || form.getUsedColumnList().length == 0)) {
            throw new XRuntime(getClass().getName(),"You must select at least one column to be displayed (Used Columns).");
        }

        if( kindOfAction.equals( "reset"  )) {
            editorMetadata  = getEditorMetadataServer().lookup( form.getEditorName(), form.getEditorType() ,"ALL");
        }

        editorMetadata = getEditorMetadataServer().update(editorMetadata,form.getUserId());
	}

    private void setOrdering(CustomDisplayForm form,ColumnMetaData columnMetaData,HttpServletRequest request) {
        String sortOrder = null;
        int intSortOrder = AVConstants._ASC;
        sortOrder = request.getParameter( columnMetaData.getName() );
        if(sortOrder != null && sortOrder.trim().length() > 0 ) {
            intSortOrder = Integer.parseInt(sortOrder);
            columnMetaData.setOrdering(intSortOrder);
            if(form.getParentSortName() == null || form.getParentSortName().trim().length() == 0) {
                form.setParentSortName(columnMetaData.getName());
                form.setParentSortOrder(intSortOrder == AVConstants._ASC?_ASC :_DESC);
            }
            else if (form.getParentSortName().equalsIgnoreCase(columnMetaData.getName())) {
                form.setParentSortName(columnMetaData.getName());
                form.setParentSortOrder(intSortOrder == AVConstants._ASC?_ASC :_DESC);
            }
        }
	}

    private void setSortOrderSeq(CustomDisplayForm form,Vector columns) {
        String sortOrderSeq[] = form.getSortOrderSeq();
        if( sortOrderSeq != null ){
            ColumnMetaData columnMetaData  = null;
            int sortOrderSeqIndex = 0;
            for(int i=0;i<sortOrderSeq.length;i++){
                columnMetaData = (ColumnMetaData)getColumnMetaData(columns, sortOrderSeq[i] ) ;
                if(columnMetaData != null){
                    columnMetaData.setSortOrderSeq( sortOrderSeqIndex++ );
                }
            }
        }
    }

    private ColumnMetaData getColumnMetaData(Vector columns,String columnName) {
        ColumnMetaData columnMetaData  = null;
        for(int i=0;i<columns.size();i++){
            columnMetaData = (ColumnMetaData)columns.get(i);
            if( columnMetaData.isDisplayable() ){
                if( columnMetaData.getName().equalsIgnoreCase( columnName ) ) {
                    return columnMetaData;
                }
            }
        }
        return null;
    }
}
