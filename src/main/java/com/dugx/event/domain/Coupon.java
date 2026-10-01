package com.dugx.event.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A Coupon.
 */
@Entity
@Table(name = "coupon")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Coupon implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "discount", precision = 21, scale = 2)
    private BigDecimal discount;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "category", "address" }, allowSetters = true)
    private Event event;

    /** User da doi coupon nay bang diem Lotusmiles (null = coupon khuyen mai chung do admin tao). */
    @ManyToOne(fetch = FetchType.LAZY)
    private User redeemedByUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Coupon id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Coupon code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getDiscount() {
        return this.discount;
    }

    public Coupon discount(BigDecimal discount) {
        this.setDiscount(discount);
        return this;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public Coupon startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public Coupon endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public Coupon quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Coupon event(Event event) {
        this.setEvent(event);
        return this;
    }

    public User getRedeemedByUser() {
        return this.redeemedByUser;
    }

    public void setRedeemedByUser(User redeemedByUser) {
        this.redeemedByUser = redeemedByUser;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Coupon)) {
            return false;
        }
        return getId() != null && getId().equals(((Coupon) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Coupon{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", discount=" + getDiscount() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", quantity=" + getQuantity() +
            "}";
    }
}
