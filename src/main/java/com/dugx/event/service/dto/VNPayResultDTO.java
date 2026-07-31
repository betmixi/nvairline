package com.dugx.event.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Ket qua xu ly mot giao dich VNPay, dung cho ca return-url lan IPN.
 */
public class VNPayResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long bookingId;

    private boolean success;

    /** Ma phan hoi cua VNPay, "00" la thanh cong. */
    private String responseCode;

    /** Thong diep hien thi cho nguoi dung. */
    private String message;

    private String transactionCode;

    private BigDecimal amount;

    public VNPayResultDTO() {
        // Constructor rong cho Jackson.
    }

    public VNPayResultDTO(Long bookingId, boolean success, String responseCode, String message, String transactionCode, BigDecimal amount) {
        this.bookingId = bookingId;
        this.success = success;
        this.responseCode = responseCode;
        this.message = message;
        this.transactionCode = transactionCode;
        this.amount = amount;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return (
            "VNPayResultDTO{bookingId=" +
            bookingId +
            ", success=" +
            success +
            ", responseCode='" +
            responseCode +
            "', transactionCode='" +
            transactionCode +
            "'}"
        );
    }
}
