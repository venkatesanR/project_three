package com.techmania.designprinciples.assignments.pircecalculator.model;

public enum ProductCategoryEnum {
    STATIONARY("BOOK"),
    CLOTHES("CLOTHES SHIRT"),
    FOOD("FOOD DRINKS"),
    OTHERS("OTHERS");

    private String product;

    ProductCategoryEnum(String product) {
        this.product = product;
    }

    public static ProductCategoryEnum getCategory(String product) {
        for (ProductCategoryEnum productMap : ProductCategoryEnum.values()) {
            if (productMap.product.contains(product.toUpperCase())) {
                return productMap;
            }
        }
        return OTHERS;
    }
}
