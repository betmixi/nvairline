package com.dugx.event.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Mua them hanh ly ky gui cho mot ve da mua, thanh toan qua VNPay.
 */
@Entity
@Table(name = "baggage_purchase")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BaggagePurchase implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "weight_kg", nullable = false)
    private Integer weightKg;

    @NotNull
    @Column(name = "price", nullable = false, precision = 21, scale = 2)
    private BigDecimal price;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_date")
    private Instant createdDate;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "bookingDetail" }, allowSetters = true)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Booking booking;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getWeightKg() {
        return this.weightKg;
    }

    public void setWeightKg(Integer weightKg) {
        this.weightKg = weightKg;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
        if (!(o instanceof BaggagePurchase)) {
            return false;
        }
        return getId() != null && getId().equals(((BaggagePurchase) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "BaggagePurchase{" +
            "id=" +
            getId() +
            ", weightKg=" +
            getWeightKg() +
            ", price=" +
            getPrice() +
            ", status='" +
            getStatus() +
            "'" +
            "}"
        );
    }
}
