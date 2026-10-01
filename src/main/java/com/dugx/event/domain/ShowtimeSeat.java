package com.dugx.event.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A ShowtimeSeat.
 */
@Entity
@Table(name = "showtime_seat")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShowtimeSeat implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SeatStatus status;

    @Column(name = "price", precision = 21, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "event", "aircraft" }, allowSetters = true)
    private Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "aircraft" }, allowSetters = true)
    private Seat seat;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ShowtimeSeat id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SeatStatus getStatus() {
        return this.status;
    }

    public ShowtimeSeat status(SeatStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public ShowtimeSeat price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Showtime getShowtime() {
        return this.showtime;
    }

    public void setShowtime(Showtime showtime) {
        this.showtime = showtime;
    }

    public ShowtimeSeat showtime(Showtime showtime) {
        this.setShowtime(showtime);
        return this;
    }

    public Seat getSeat() {
        return this.seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public ShowtimeSeat seat(Seat seat) {
        this.setSeat(seat);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShowtimeSeat)) {
            return false;
        }
        return getId() != null && getId().equals(((ShowtimeSeat) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShowtimeSeat{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", price=" + getPrice() +
            "}";
    }
}
