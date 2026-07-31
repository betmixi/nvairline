package com.dugx.event.service.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrganizerRevenueDTO {

    private BigDecimal totalRevenue;

    private Long totalEvents;

    private Long ticketsSold;

    private List<EventRevenueDTO> eventRevenue;

    private List<RecentPaymentDTO> recentPayments;

    public OrganizerRevenueDTO() {}

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(Long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public Long getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(Long ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public List<EventRevenueDTO> getEventRevenue() {
        return eventRevenue;
    }

    public void setEventRevenue(List<EventRevenueDTO> eventRevenue) {
        this.eventRevenue = eventRevenue;
    }

    public List<RecentPaymentDTO> getRecentPayments() {
        return recentPayments;
    }

    public void setRecentPayments(List<RecentPaymentDTO> recentPayments) {
        this.recentPayments = recentPayments;
    }
}
