package com.dugx.event.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A TicketType.
 */
@Entity
@Table(name = "ticket_type")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketType implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "remaining", nullable = false)
    private Integer remaining;

    @Column(name = "sale_start")
    private Instant saleStart;

    @Column(name = "sale_end")
    private Instant saleEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "category", "address", "organizer" }, allowSetters = true)
    private Event event;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TicketType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public TicketType name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public TicketType price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public TicketType quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getRemaining() {
        return this.remaining;
    }

    public TicketType remaining(Integer remaining) {
        this.setRemaining(remaining);
        return this;
    }

    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }

    public Instant getSaleStart() {
        return this.saleStart;
    }

    public TicketType saleStart(Instant saleStart) {
        this.setSaleStart(saleStart);
        return this;
    }

    public void setSaleStart(Instant saleStart) {
        this.saleStart = saleStart;
    }

    public Instant getSaleEnd() {
        return this.saleEnd;
    }

    public TicketType saleEnd(Instant saleEnd) {
        this.setSaleEnd(saleEnd);
        return this;
    }

    public void setSaleEnd(Instant saleEnd) {
        this.saleEnd = saleEnd;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public TicketType event(Event event) {
        this.setEvent(event);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TicketType)) {
            return false;
        }
        return getId() != null && getId().equals(((TicketType) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketType{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", price=" + getPrice() +
            ", quantity=" + getQuantity() +
            ", remaining=" + getRemaining() +
            ", saleStart='" + getSaleStart() + "'" +
            ", saleEnd='" + getSaleEnd() + "'" +
            "}";
    }
}
