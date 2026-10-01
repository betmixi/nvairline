package com.dugx.event.domain;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * A Seat.
 */
@Entity
@Table(name = "seat")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Seat implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "row_label")
    private String rowLabel;

    @Column(name = "seat_number")
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type")
    private SeatType seatType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_room_id")
    private Aircraft aircraft;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Seat id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRowLabel() {
        return this.rowLabel;
    }

    public Seat rowLabel(String rowLabel) {
        this.setRowLabel(rowLabel);
        return this;
    }

    public void setRowLabel(String rowLabel) {
        this.rowLabel = rowLabel;
    }

    public Integer getSeatNumber() {
        return this.seatNumber;
    }

    public Seat seatNumber(Integer seatNumber) {
        this.setSeatNumber(seatNumber);
        return this;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public SeatType getSeatType() {
        return this.seatType;
    }

    public Seat seatType(SeatType seatType) {
        this.setSeatType(seatType);
        return this;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public Aircraft getAircraft() {
        return this.aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public Seat aircraft(Aircraft aircraft) {
        this.setAircraft(aircraft);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Seat)) {
            return false;
        }
        return getId() != null && getId().equals(((Seat) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Seat{" +
            "id=" + getId() +
            ", rowLabel='" + getRowLabel() + "'" +
            ", seatNumber=" + getSeatNumber() +
            ", seatType='" + getSeatType() + "'" +
            "}";
    }
}
