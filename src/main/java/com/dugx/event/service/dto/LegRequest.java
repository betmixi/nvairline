package com.dugx.event.service.dto;

import java.util.List;
import java.util.Map;

/**
 * Mot chang bay trong yeu cau dat ve (mot chieu chi co 1 leg, khu hoi/nhieu chang co nhieu leg).
 */
public class LegRequest {

    private Long showtimeId;

    private List<Long> seatIds;

    /** So kg hanh ly ky gui tra truoc chon kem theo tung ghe, khoa la seatId. */
    private Map<Long, Integer> baggageBySeat;

    public Long getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Long showtimeId) {
        this.showtimeId = showtimeId;
    }

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {
        this.seatIds = seatIds;
    }

    public Map<Long, Integer> getBaggageBySeat() {
        return baggageBySeat;
    }

    public void setBaggageBySeat(Map<Long, Integer> baggageBySeat) {
        this.baggageBySeat = baggageBySeat;
    }
}
