package com.techmania.designprinciples.assignments.pircecalculator.model;

import java.util.Objects;

public class Product {
    private final String name;
    private final ProductCategoryEnum category;
    private final Float cost;
    private Integer count;
    private Float discountedCost;

    private Product(String name, Float cost, int count) {
        this.name = name;
        category = ProductCategoryEnum.getCategory(name);
        this.cost = cost;
        this.count = count;
        this.discountedCost = cost;
    }

    public String getName() {
        return name;
    }

    public Float getActualCost() {
        return cost;
    }

    public int getCount() {
        return count;
    }

    public void increment(int count) {
        this.count = count;
    }

    public void setDiscountedCost(Float discountedCost) {
        this.discountedCost = discountedCost;
    }

    public Float getDiscountedPrice() {
        return discountedCost;
    }

    public ProductCategoryEnum getCategory() {
        return category;
    }

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    static class ProductBuilder {
        private String productName;
        private Float actualCost;
        private Integer count;

        public ProductBuilder setProductName(String productName) {
            this.productName = productName;
            return this;
        }

        public ProductBuilder setActualCost(Float actualCost) {
            this.actualCost = actualCost;
            return this;
        }

        public ProductBuilder setCount(Integer count) {
            this.count = count;
            return this;
        }

        public Product build() {
            Objects.requireNonNull(productName, "Product name should not be null");
            Objects.requireNonNull(actualCost, "Cost of the product should not be null");
            Objects.requireNonNull(count, "One product that need to be added");
            return new Product(productName, actualCost, count);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getCount()).
                append(" ").
                append(getName()).
                append(" at ").
                append("actual cost: ").
                append(getActualCost()).
                append(" to discounted : ").append(getDiscountedPrice());
        return builder.toString();
    }
}
