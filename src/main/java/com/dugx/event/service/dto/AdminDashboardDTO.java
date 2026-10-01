package com.dugx.event.service.dto;

import java.math.BigDecimal;
import java.util.List;

public class AdminDashboardDTO {

    private long totalUsers;

    private long totalEvents;

    private long totalBookings;

    private BigDecimal totalRevenue;

    private List<EventDTO> recentEvents;

    private List<TopEventDTO> topEventsByTickets;

    private List<TopEventDTO> topEventsByRevenue;

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public List<EventDTO> getRecentEvents() {
        return recentEvents;
    }

    public void setRecentEvents(List<EventDTO> recentEvents) {
        this.recentEvents = recentEvents;
    }

    public List<TopEventDTO> getTopEventsByTickets() {
        return topEventsByTickets;
    }

    public void setTopEventsByTickets(List<TopEventDTO> topEventsByTickets) {
        this.topEventsByTickets = topEventsByTickets;
    }

    public List<TopEventDTO> getTopEventsByRevenue() {
        return topEventsByRevenue;
    }

    public void setTopEventsByRevenue(List<TopEventDTO> topEventsByRevenue) {
        this.topEventsByRevenue = topEventsByRevenue;
    }
}
