package com.dugx.event.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/** Một mục dịch vụ bổ trợ đã mua (hoặc đang chờ thanh toán) cho một vé. */
public class PurchasedAddonDTO implements Serializable {

    private Long id;
    private String itemCode;
    private String itemLabel;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String status;

    public PurchasedAddonDTO() {}

    public PurchasedAddonDTO(Long id, String itemCode, String itemLabel, Integer quantity, BigDecimal totalPrice, String status) {
        this.id = id;
        this.itemCode = itemCode;
        this.itemLabel = itemLabel;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
