package com.dugx.event.service.dto;

import java.math.BigDecimal;

public class EventRevenueDTO {

    private Long eventId;
    private String eventName;
    private BigDecimal revenue;

    public EventRevenueDTO(Long eventId, String eventName, BigDecimal revenue) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.revenue = revenue;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }
}
