package com.dugx.event.service.dto;

import java.util.List;

/**
 * Yeu cau tao link thanh toan VNPay.
 *
 * Co hai cach dung:
 * - Truyen {@code bookingId} cho mot booking PENDING da tao truoc do.
 * - Hoac truyen {@code legs} (+ {@code couponCode}) de he thong tu tao booking (mot chieu
 *   chi co 1 leg, khu hoi/nhieu chang co nhieu leg) roi sinh link thanh toan trong cung mot lan goi.
 */
public class VNPayRequestDTO {

    private Long bookingId;

    private List<LegRequest> legs;

    private String couponCode;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public List<LegRequest> getLegs() {
        return legs;
    }

    public void setLegs(List<LegRequest> legs) {
        this.legs = legs;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
