package com.dugx.event.service.dto;

import java.util.List;

/**
 * Yeu cau dat ve. Mot chieu chi co 1 leg trong {@code legs}, khu hoi/nhieu chang co nhieu leg.
 * {@code couponCode} ap dung cho ca booking, tu dong khop voi chang co Event trung voi coupon.
 */
public class BookingRequest {

    private List<LegRequest> legs;

    private String couponCode;

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
