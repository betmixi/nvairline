package com.dugx.event.service.dto;

import java.io.Serializable;

/** Yêu cầu mua thêm hành lý ký gửi cho một vé. */
public class BaggageRequestDTO implements Serializable {

    private Long ticketId;
    private Integer weightKg;

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Integer getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Integer weightKg) {
        this.weightKg = weightKg;
    }
}
