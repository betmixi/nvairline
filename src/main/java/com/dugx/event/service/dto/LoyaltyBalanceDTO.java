package com.dugx.event.service.dto;

/** So diem tich luy hien co cua nguoi dung dang dang nhap. */
public class LoyaltyBalanceDTO {

    private Integer points;

    public LoyaltyBalanceDTO(Integer points) {
        this.points = points;
    }

    public Integer getPoints() {
        return points;
    }
}
