package com.techmania.designprinciples.assignments.pircecalculator.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PurchaseRequest {
    private Map<String, Product> productDetails = null;

    public PurchaseRequest() {
        productDetails = new ConcurrentHashMap<>();
    }

    public void addProduct(int count, String productName, Float cost) {
        if (productDetails.containsKey(productName)) {
            productDetails.get(productName).increment(count);
            return;
        }
        Product product = Product.builder().
                setProductName(productName).
                setActualCost(cost).
                setCount(count).
                build();
        productDetails.put(productName, product);
    }

    public Map<String, Product> getProductDetails() {
        return productDetails;
    }

}
