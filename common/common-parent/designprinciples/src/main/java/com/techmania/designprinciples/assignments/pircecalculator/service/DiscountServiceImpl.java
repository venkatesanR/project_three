package com.techmania.designprinciples.assignments.pircecalculator.service;

import com.techmania.common.exceptions.InvalidInputException;
import com.techmania.designprinciples.assignments.pircecalculator.cache.DiscountCache;
import com.techmania.designprinciples.assignments.pircecalculator.model.Product;
import com.techmania.designprinciples.assignments.pircecalculator.model.PurchaseRequest;
import com.techmania.designprinciples.assignments.pircecalculator.model.PurchaseResponse;

import java.math.BigDecimal;
import java.util.function.Function;

public class DiscountServiceImpl implements IDiscountService {
    private final String clearanceIdentifier;
    private DiscountCache discountCache;


    public DiscountServiceImpl(String clearanceIdentifier, DiscountCache discountCache) {
        this.discountCache = discountCache;
        this.clearanceIdentifier = clearanceIdentifier;
    }

    @Override
    public PurchaseResponse purchase(PurchaseRequest request) {
        if (request == null
                || request.getProductDetails() == null
                || request.getProductDetails().isEmpty()) {
            throw new InvalidInputException("Please provide product details to process");
        }

        PurchaseResponse response = new PurchaseResponse();

        request.getProductDetails().values().stream()
                .map(applyDiscount())
                .forEach((product) -> {
                    response.setTotal(Float.sum(response.getTotal(), product.getDiscountedPrice()));
                    response.setActual(Float.sum(response.getActual(), product.getActualCost()));
                    response.getProducts().add(product);
                });

        return response;
    }

    private Function<Product, Product> applyDiscount() {
        return product -> {
            product.setDiscountedCost(calculateDiscount(product.getActualCost(),
                    discountCache.get(product.getCategory().name())));
            if (hasClearance(product.getName())) {
                product.setDiscountedCost(calculateDiscount(product.getDiscountedPrice(),
                        discountCache.get(clearanceIdentifier)));
            }
            return product;
        };
    }

    //utils
    private float calculateDiscount(Float actual, Float discount) {
        return round(actual - (actual * discount), 2);
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private boolean hasClearance(String product) {
        return product.toUpperCase().contains(clearanceIdentifier);
    }
}