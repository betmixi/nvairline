package com.dugx.event.service.dto;

import java.io.Serializable;
import java.util.List;

/** Yêu cầu mua một hoặc nhiều dịch vụ bổ trợ cùng loại cho một vé. */
public class AddonPurchaseRequestDTO implements Serializable {

    private Long ticketId;
    private String addonType;
    private List<AddonCartItemDTO> items;

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getAddonType() {
        return addonType;
    }

    public void setAddonType(String addonType) {
        this.addonType = addonType;
    }

    public List<AddonCartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<AddonCartItemDTO> items) {
        this.items = items;
    }
}
