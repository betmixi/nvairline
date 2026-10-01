package com.dugx.event.service.dto;

import com.dugx.event.domain.SeatType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.dugx.event.domain.Seat} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SeatDTO implements Serializable {

    private Long id;

    @NotBlank
    private String rowLabel;

    @Min(value = 1)
    private Integer seatNumber;

    private SeatType seatType;

    private AircraftDTO aircraft;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public void setRowLabel(String rowLabel) {
        this.rowLabel = rowLabel;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
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
        if (!(o instanceof SeatDTO)) {
            return false;
        }

        SeatDTO seatDTO = (SeatDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, seatDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SeatDTO{" +
            "id=" + getId() +
            ", rowLabel='" + getRowLabel() + "'" +
            ", seatNumber=" + getSeatNumber() +
            ", seatType='" + getSeatType() + "'" +
            ", aircraft=" + getAircraft() +
            "}";
    }
}
