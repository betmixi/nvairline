package com.dugx.event.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.dugx.event.domain.Showtime} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShowtimeDTO implements Serializable {

    private Long id;

    private Instant startTime;

    private Instant endTime;

    private BigDecimal basePrice;

    private BigDecimal vipPrice;

    private BigDecimal couplePrice;

    private EventDTO event;

    private AircraftDTO aircraft;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getVipPrice() {
        return vipPrice;
    }

    public void setVipPrice(BigDecimal vipPrice) {
        this.vipPrice = vipPrice;
    }

    public BigDecimal getCouplePrice() {
        return couplePrice;
    }

    public void setCouplePrice(BigDecimal couplePrice) {
        this.couplePrice = couplePrice;
    }

    public EventDTO getEvent() {
        return event;
    }

    public void setEvent(EventDTO event) {
        this.event = event;
    }

    public AircraftDTO getAircraft() {
        return aircraft;
    }

    public void setAircraft(AircraftDTO aircraft) {
        this.aircraft = aircraft;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShowtimeDTO)) {
            return false;
        }

        ShowtimeDTO showtimeDTO = (ShowtimeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, showtimeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShowtimeDTO{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", basePrice=" + getBasePrice() +
            ", vipPrice=" + getVipPrice() +
            ", couplePrice=" + getCouplePrice() +
            ", event=" + getEvent() +
            ", aircraft=" + getAircraft() +
            "}";
    }
}
