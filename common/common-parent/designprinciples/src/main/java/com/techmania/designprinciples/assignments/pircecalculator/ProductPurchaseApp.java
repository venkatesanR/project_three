package com.techmania.designprinciples.assignments.pircecalculator;

import com.techmania.designprinciples.assignments.pircecalculator.cache.DiscountCache;
import com.techmania.designprinciples.assignments.pircecalculator.cache.ICacheConstructor;
import com.techmania.designprinciples.assignments.pircecalculator.model.ProductCategoryEnum;
import com.techmania.designprinciples.assignments.pircecalculator.model.PurchaseRequest;
import com.techmania.designprinciples.assignments.pircecalculator.model.PurchaseResponse;
import com.techmania.designprinciples.assignments.pircecalculator.service.DiscountServiceImpl;
import com.techmania.designprinciples.assignments.pircecalculator.service.IDiscountService;

public class ProductPurchaseApp {
    private static final String CLEARANCE_IDENTIFIER = "CLEARANCE";
    private IDiscountService service;
    private DiscountCache cacheConstructor;

    public IDiscountService getService() {
        return service;
    }

    public void init() {
        cacheConstructor = new DiscountCache();
        loadData(cacheConstructor);
        service = new DiscountServiceImpl(CLEARANCE_IDENTIFIER, cacheConstructor);
    }

    public void destroy() {
        cacheConstructor.clear();
    }

    private void loadData(ICacheConstructor cacheConstructor) {
        cacheConstructor.add(ProductCategoryEnum.STATIONARY.name(), 0.05f);
        cacheConstructor.add(ProductCategoryEnum.FOOD.name(), 0.05f);
        cacheConstructor.add(ProductCategoryEnum.CLOTHES.name(), 0.20f);
        cacheConstructor.add(ProductCategoryEnum.OTHERS.name(), 0.03f);
        cacheConstructor.add(CLEARANCE_IDENTIFIER, 0.20f);
    }

    private PurchaseRequest preparePurchaseRequest() {
        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.addProduct(1, "book", 14.49f);
        purchaseRequest.addProduct(1, "shirt", 19.99f);
        purchaseRequest.addProduct(1, "chocolate", 1.00f);
        purchaseRequest.addProduct(1, "clearance chocolate", 2.00f);

        return purchaseRequest;
    }

    public static void main(String[] args) {
        ProductPurchaseApp purchaseApp = new ProductPurchaseApp();
        purchaseApp.init();

        PurchaseRequest purchaseRequest = purchaseApp.preparePurchaseRequest();
        PurchaseResponse response = purchaseApp.getService().purchase(purchaseRequest);

        System.out.println(response.getTotal());
        response.getProducts().
                stream().forEach(product -> System.out.println(product));
        purchaseApp.destroy();
    }
}
