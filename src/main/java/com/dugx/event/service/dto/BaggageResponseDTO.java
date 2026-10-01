package com.dugx.event.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/** Kết quả tạo yêu cầu mua hành lý: booking (PENDING) để thanh toán qua VNPay. */
public class BaggageResponseDTO implements Serializable {

    private Long purchaseId;
    private Long bookingId;
    private BigDecimal price;

    public BaggageResponseDTO(Long purchaseId, Long bookingId, BigDecimal price) {
        this.purchaseId = purchaseId;
        this.bookingId = bookingId;
        this.price = price;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
