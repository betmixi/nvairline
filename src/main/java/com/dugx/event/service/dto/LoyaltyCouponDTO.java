package com.dugx.event.service.dto;

import java.math.BigDecimal;

/** Mot coupon ca nhan user da doi bang diem Lotusmiles, con dung duoc. */
public class LoyaltyCouponDTO {

    private String code;

    private String label;

    private BigDecimal discount;

    public LoyaltyCouponDTO(String code, String label, BigDecimal discount) {
        this.code = code;
        this.label = label;
        this.discount = discount;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getDiscount() {
        return discount;
    }
}
