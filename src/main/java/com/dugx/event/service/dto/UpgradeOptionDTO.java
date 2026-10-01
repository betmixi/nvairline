package com.dugx.event.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Mot hang ghe co the nang len cho mot ve, kem gia va so tien can tra them.
 */
public class UpgradeOptionDTO implements Serializable {

    private String seatType;
    private String seatTypeLabel;
    private BigDecimal price;
    private BigDecimal priceDifference;
    private boolean available;

    public UpgradeOptionDTO() {}

    public UpgradeOptionDTO(String seatType, String seatTypeLabel, BigDecimal price, BigDecimal priceDifference, boolean available) {
        this.seatType = seatType;
        this.seatTypeLabel = seatTypeLabel;
        this.price = price;
        this.priceDifference = priceDifference;
        this.available = available;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public String getSeatTypeLabel() {
        return seatTypeLabel;
    }

    public void setSeatTypeLabel(String seatTypeLabel) {
        this.seatTypeLabel = seatTypeLabel;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPriceDifference() {
        return priceDifference;
    }

    public void setPriceDifference(BigDecimal priceDifference) {
        this.priceDifference = priceDifference;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
