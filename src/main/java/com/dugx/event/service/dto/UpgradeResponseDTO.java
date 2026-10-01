package com.dugx.event.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/** Ket qua tao yeu cau nang hang: booking (PENDING) de thu tien chenh lech qua VNPay. */
public class UpgradeResponseDTO implements Serializable {

    private Long upgradeId;
    private Long bookingId;
    private BigDecimal priceDifference;

    public UpgradeResponseDTO(Long upgradeId, Long bookingId, BigDecimal priceDifference) {
        this.upgradeId = upgradeId;
        this.bookingId = bookingId;
        this.priceDifference = priceDifference;
    }

    public Long getUpgradeId() {
        return upgradeId;
    }

    public void setUpgradeId(Long upgradeId) {
        this.upgradeId = upgradeId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public BigDecimal getPriceDifference() {
        return priceDifference;
    }

    public void setPriceDifference(BigDecimal priceDifference) {
        this.priceDifference = priceDifference;
    }
}
