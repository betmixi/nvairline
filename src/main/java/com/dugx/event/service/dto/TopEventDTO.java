package com.dugx.event.service.dto;

import java.math.BigDecimal;

/**
 * Mot su kien trong bang xep hang: so ve da ban va doanh thu.
 */
public class TopEventDTO {

    private Long eventId;
    private String title;
    private Long ticketsSold;
    private BigDecimal revenue;

    public TopEventDTO(Long eventId, String title, Long ticketsSold, BigDecimal revenue) {
        this.eventId = eventId;
        this.title = title;
        this.ticketsSold = ticketsSold;
        this.revenue = revenue;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public Long getTicketsSold() {
        return ticketsSold;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }
}
