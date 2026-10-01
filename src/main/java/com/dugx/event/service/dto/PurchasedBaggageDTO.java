package com.dugx.event.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/** Một gói hành lý đã mua (hoặc đang chờ thanh toán) cho một vé. */
public class PurchasedBaggageDTO implements Serializable {

    private Long id;
    private Integer weightKg;
    private BigDecimal price;
    private String status;

    public PurchasedBaggageDTO() {}

    public PurchasedBaggageDTO(Long id, Integer weightKg, BigDecimal price, String status) {
        this.id = id;
        this.weightKg = weightKg;
        this.price = price;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
