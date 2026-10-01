package com.dugx.event.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Yeu cau nang hang ghe cho mot ve da mua: doi tu ghe hang thap sang ghe
 * hang cao hon trong cung suat bay, thu them phan chenh lech gia qua VNPay.
 */
@Entity
@Table(name = "seat_upgrade")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SeatUpgrade implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "price_difference", nullable = false, precision = 21, scale = 2)
    private BigDecimal priceDifference;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_date")
    private Instant createdDate;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "bookingDetail" }, allowSetters = true)
    private Ticket ticket;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "seat", "showtime" }, allowSetters = true)
    private ShowtimeSeat oldShowtimeSeat;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "seat", "showtime" }, allowSetters = true)
    private ShowtimeSeat newShowtimeSeat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Booking booking;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPriceDifference() {
        return this.priceDifference;
    }

    public void setPriceDifference(BigDecimal priceDifference) {
        this.priceDifference = priceDifference;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public ShowtimeSeat getOldShowtimeSeat() {
        return this.oldShowtimeSeat;
    }

    public void setOldShowtimeSeat(ShowtimeSeat oldShowtimeSeat) {
        this.oldShowtimeSeat = oldShowtimeSeat;
    }

    public ShowtimeSeat getNewShowtimeSeat() {
        return this.newShowtimeSeat;
    }

    public void setNewShowtimeSeat(ShowtimeSeat newShowtimeSeat) {
        this.newShowtimeSeat = newShowtimeSeat;
    }

    public Booking getBooking() {
        return this.booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SeatUpgrade)) {
            return false;
        }
        return getId() != null && getId().equals(((SeatUpgrade) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "SeatUpgrade{" +
            "id=" +
            getId() +
            ", priceDifference=" +
            getPriceDifference() +
            ", status='" +
            getStatus() +
            "'" +
            ", createdDate='" +
            getCreatedDate() +
            "'" +
            "}"
        );
    }
}
