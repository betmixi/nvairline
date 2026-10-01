package com.dugx.event.service.dto;

import java.io.Serializable;

/** Một dòng trong giỏ hàng dịch vụ bổ trợ khi gửi yêu cầu mua. */
public class AddonCartItemDTO implements Serializable {

    private String itemCode;
    private Integer quantity;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
