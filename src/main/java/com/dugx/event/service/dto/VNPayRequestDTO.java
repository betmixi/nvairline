package com.dugx.event.service.dto;

/**
 * Yeu cau tao link thanh toan VNPay.
 *
 * Co hai cach dung:
 * - Truyen {@code bookingId} cho mot booking PENDING da tao truoc do.
 * - Hoac truyen {@code ticketTypeId} + {@code quantity} (+ {@code couponCode})
 *   de he thong tu tao booking roi sinh link thanh toan trong cung mot lan goi.
 */
public class VNPayRequestDTO {

    private Long bookingId;

    private Long ticketTypeId;

    private Integer quantity;

    private String couponCode;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(Long ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
