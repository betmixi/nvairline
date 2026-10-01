package com.dugx.event.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Thong tin nang hang ghe cho mot ve: hang hien tai va cac hang co the nang len.
 */
public class TicketUpgradeInfoDTO implements Serializable {

    private Long ticketId;
    private String currentSeatType;
    private String currentSeatTypeLabel;
    private BigDecimal currentPrice;
    private boolean maxTier;
    private boolean upgradable;
    private String reason;
    private List<UpgradeOptionDTO> options;

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getCurrentSeatType() {
        return currentSeatType;
    }

    public void setCurrentSeatType(String currentSeatType) {
        this.currentSeatType = currentSeatType;
    }

    public String getCurrentSeatTypeLabel() {
        return currentSeatTypeLabel;
    }

    public void setCurrentSeatTypeLabel(String currentSeatTypeLabel) {
        this.currentSeatTypeLabel = currentSeatTypeLabel;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public boolean isMaxTier() {
        return maxTier;
    }

    public void setMaxTier(boolean maxTier) {
        this.maxTier = maxTier;
    }

    public boolean isUpgradable() {
        return upgradable;
    }

    public void setUpgradable(boolean upgradable) {
        this.upgradable = upgradable;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<UpgradeOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<UpgradeOptionDTO> options) {
        this.options = options;
    }
}
