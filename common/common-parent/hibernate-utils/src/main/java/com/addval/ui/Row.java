package com.addval.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.addval.utils.StrUtl;

public class Row implements Serializable{
	private static final long serialVersionUID = 180009714692616790L;
    private List<Col> cols = null;
    private String containerStyleClass = null;
    public Row() {
    }

    public List<Col> getCols() {
        if (cols == null) {
            cols = new ArrayList<Col>();
        }

        return cols;
    }

    public void setCols(List<Col> cols) {
        this.cols = cols;
    }

    public void addCol(Col col) {
        getCols().add(col);
    }
    
    public void removeCol(Col col) {
    	getCols().remove(col);
    }
	public String getContainerStyleClass() {
		return StrUtl.isEmptyTrimmed(containerStyleClass) ? "" : containerStyleClass;
	}

	public void setContainerStyleClass(String containerStyleClass) {
		this.containerStyleClass = containerStyleClass;
	}
}
