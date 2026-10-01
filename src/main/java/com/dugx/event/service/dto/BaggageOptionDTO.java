package com.dugx.event.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/** Một gói hành lý ký gửi trả trước có thể mua thêm cho một vé. */
public class BaggageOptionDTO implements Serializable {

    private Integer weightKg;
    private BigDecimal price;

    public BaggageOptionDTO() {}

    public BaggageOptionDTO(Integer weightKg, BigDecimal price) {
        this.weightKg = weightKg;
        this.price = price;
    }

    public Integer getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Integer weightKg) {
        this.weightKg = weightKg;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
