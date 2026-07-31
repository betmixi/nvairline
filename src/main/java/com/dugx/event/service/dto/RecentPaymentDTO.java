package com.dugx.event.service.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class RecentPaymentDTO {

    private Instant paymentDate;
    private String buyer;
    private BigDecimal amount;

    public RecentPaymentDTO(Instant paymentDate, String buyer, BigDecimal amount) {
        this.paymentDate = paymentDate;
        this.buyer = buyer;
        this.amount = amount;
    }

    public Instant getPaymentDate() {
        return paymentDate;
    }

    public String getBuyer() {
        return buyer;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
