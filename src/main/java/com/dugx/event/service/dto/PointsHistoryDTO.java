package com.dugx.event.service.dto;

import java.time.Instant;

/** Mot dong lich su tich diem / doi diem cua nguoi dung. */
public class PointsHistoryDTO {

    private Long id;
    private Integer points;
    private String reason;
    private Instant createdDate;
    private Long bookingId;

    public PointsHistoryDTO(Long id, Integer points, String reason, Instant createdDate, Long bookingId) {
        this.id = id;
        this.points = points;
        this.reason = reason;
        this.createdDate = createdDate;
        this.bookingId = bookingId;
    }

    public Long getId() {
        return id;
    }

    public Integer getPoints() {
        return points;
    }

    public String getReason() {
        return reason;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Long getBookingId() {
        return bookingId;
    }
}
