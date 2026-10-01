package com.dugx.event.service.dto;

import java.io.Serializable;
import java.util.List;

/** Thông tin hành lý trả trước cho một vé: đã mua gì và có thể mua thêm gói nào. */
public class TicketBaggageInfoDTO implements Serializable {

    private Long ticketId;
    private boolean purchasable;
    private String reason;
    private Integer totalPurchasedKg;
    private List<PurchasedBaggageDTO> purchased;
    private List<BaggageOptionDTO> options;

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public boolean isPurchasable() {
        return purchasable;
    }

    public void setPurchasable(boolean purchasable) {
        this.purchasable = purchasable;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getTotalPurchasedKg() {
        return totalPurchasedKg;
    }

    public void setTotalPurchasedKg(Integer totalPurchasedKg) {
        this.totalPurchasedKg = totalPurchasedKg;
    }

    public List<PurchasedBaggageDTO> getPurchased() {
        return purchased;
    }

    public void setPurchased(List<PurchasedBaggageDTO> purchased) {
        this.purchased = purchased;
    }

    public List<BaggageOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<BaggageOptionDTO> options) {
        this.options = options;
    }
}
