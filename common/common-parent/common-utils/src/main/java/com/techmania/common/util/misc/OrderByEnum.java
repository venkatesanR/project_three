package com.techmania.common.util.misc;

import java.io.Serializable;

public enum OrderByEnum implements Serializable {

    ASC("Asc"), DESC("Desc");

    private String orderBy;

    OrderByEnum(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

}
