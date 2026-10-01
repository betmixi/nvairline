package com.dugx.event.service.dto;

import java.io.Serializable;

/** Yeu cau nang hang ghe cho mot ve. */
public class UpgradeRequestDTO implements Serializable {

    private Long ticketId;
    private String targetSeatType;

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getTargetSeatType() {
        return targetSeatType;
    }

    public void setTargetSeatType(String targetSeatType) {
        this.targetSeatType = targetSeatType;
    }
}
