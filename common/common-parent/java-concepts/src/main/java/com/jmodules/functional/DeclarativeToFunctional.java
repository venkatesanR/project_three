package com.jmodules.functional;

import java.math.BigDecimal;
import java.util.List;

public class DeclarativeToFunctional {
    public BigDecimal discountedPrice(List<BigDecimal> actualPrice) {
        return actualPrice.stream()
                .filter(price -> price.compareTo(BigDecimal.valueOf(20)) > 0)
                .map(price -> price.multiply(BigDecimal.valueOf(0.9)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
