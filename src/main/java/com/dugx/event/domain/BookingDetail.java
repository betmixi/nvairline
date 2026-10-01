package com.dugx.event.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A BookingDetail.
 */
@Entity
@Table(name = "booking_detail")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookingDetail implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "price", precision = 21, scale = 2)
    private BigDecimal price;

    /** So kg hanh ly ky gui tra truoc da mua kem theo ghe nay luc dat ve (co the null/0). */
    @Column(name = "extra_baggage_kg")
    private Integer extraBaggageKg;

    /** Thu tu chang bay trong booking (0 = chang dau, 1 = chang tiep theo...), dung cho khu hoi/nhieu chang. Null cho booking cu truoc khi co tinh nang nay. */
    @Column(name = "leg_index")
    private Integer legIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "showtime", "seat" }, allowSetters = true)
    private ShowtimeSeat showtimeSeat;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BookingDetail id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public BookingDetail price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getExtraBaggageKg() {
        return this.extraBaggageKg;
    }

    public void setExtraBaggageKg(Integer extraBaggageKg) {
        this.extraBaggageKg = extraBaggageKg;
    }

    public Integer getLegIndex() {
        return this.legIndex;
    }

    public void setLegIndex(Integer legIndex) {
        this.legIndex = legIndex;
    }

    public Booking getBooking() {
        return this.booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public BookingDetail booking(Booking booking) {
        this.setBooking(booking);
        return this;
    }

    public ShowtimeSeat getShowtimeSeat() {
        return this.showtimeSeat;
    }

    public void setShowtimeSeat(ShowtimeSeat showtimeSeat) {
        this.showtimeSeat = showtimeSeat;
    }

    public BookingDetail showtimeSeat(ShowtimeSeat showtimeSeat) {
        this.setShowtimeSeat(showtimeSeat);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookingDetail)) {
            return false;
        }
        return getId() != null && getId().equals(((BookingDetail) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookingDetail{" +
            "id=" + getId() +
            ", price=" + getPrice() +
            "}";
    }
}
