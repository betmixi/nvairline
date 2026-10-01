package com.dugx.event.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/** Một mặt hàng / gói dịch vụ bổ trợ trong danh mục (mua sắm, khách sạn & tour, bảo hiểm, dịch vụ khác). */
public class AddonCatalogItemDTO implements Serializable {

    private String itemCode;
    private String itemLabel;
    private BigDecimal unitPrice;
    private int maxQuantity;

    public AddonCatalogItemDTO() {}

    public AddonCatalogItemDTO(String itemCode, String itemLabel, BigDecimal unitPrice, int maxQuantity) {
        this.itemCode = itemCode;
        this.itemLabel = itemLabel;
        this.unitPrice = unitPrice;
        this.maxQuantity = maxQuantity;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemLabel() {
        return itemLabel;
    }

    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }
}
