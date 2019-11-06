package com.techmania.designprinciples.assignments.pircecalculator.service;

import com.techmania.designprinciples.assignments.pircecalculator.model.PurchaseRequest;
import com.techmania.designprinciples.assignments.pircecalculator.model.PurchaseResponse;

public interface IDiscountService {
    public PurchaseResponse purchase(PurchaseRequest request);
}