package com.dugx.event.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/** Kết quả tạo yêu cầu mua dịch vụ bổ trợ: booking (PENDING) để thanh toán qua VNPay. */
public class AddonPurchaseResponseDTO implements Serializable {

    private Long bookingId;
    private BigDecimal totalPrice;

    public AddonPurchaseResponseDTO(Long bookingId, BigDecimal totalPrice) {
        this.bookingId = bookingId;
        this.totalPrice = totalPrice;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
