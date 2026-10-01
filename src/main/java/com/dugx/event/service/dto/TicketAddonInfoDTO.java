package com.dugx.event.service.dto;

import java.io.Serializable;
import java.util.List;

/** Thông tin dịch vụ bổ trợ (một loại addonType) cho một vé: đã mua gì và danh mục có thể mua. */
public class TicketAddonInfoDTO implements Serializable {

    private Long ticketId;
    private String addonType;
    private boolean purchasable;
    private String reason;
    private List<PurchasedAddonDTO> purchased;
    private List<AddonCatalogItemDTO> catalog;

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

    public List<PurchasedAddonDTO> getPurchased() {
        return purchased;
    }

    public void setPurchased(List<PurchasedAddonDTO> purchased) {
        this.purchased = purchased;
    }

    public List<AddonCatalogItemDTO> getCatalog() {
        return catalog;
    }

    public void setCatalog(List<AddonCatalogItemDTO> catalog) {
        this.catalog = catalog;
    }
}
