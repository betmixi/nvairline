package com.dugx.event.service.dto;

import java.math.BigDecimal;

public class DashboardDTO {

    private Long totalEvents;
    private Long publishedEvents;
    private Long totalBookings;
    private Long ticketsSold;
    private BigDecimal totalRevenue;
    private Long checkedIn;

    public Long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(Long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public Long getPublishedEvents() {
        return publishedEvents;
    }

    public void setPublishedEvents(Long publishedEvents) {
        this.publishedEvents = publishedEvents;
    }

    public Long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(Long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public Long getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(Long ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(Long checkedIn) {
        this.checkedIn = checkedIn;
    }
}
