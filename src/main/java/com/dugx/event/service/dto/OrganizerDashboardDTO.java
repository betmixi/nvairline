package com.dugx.event.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class OrganizerDashboardDTO implements Serializable {

    private Long totalEvents;

    private Long publishedEvents;

    private Long totalTickets;

    private Long totalBookings;

    private BigDecimal totalRevenue;
    private String companyName;
    private List<EventDTO> latestEvents;

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

    public Long getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Long totalTickets) {
        this.totalTickets = totalTickets;
    }

    public Long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(Long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<EventDTO> getLatestEvents() {
        return latestEvents;
    }

    public void setLatestEvents(List<EventDTO> latestEvents) {
        this.latestEvents = latestEvents;
    }
}
