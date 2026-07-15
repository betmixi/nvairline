package com.dugx.event.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.dugx.event.domain.TicketType} entity. This class is used
 * in {@link com.dugx.event.web.rest.TicketTypeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ticket-types?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketTypeCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private BigDecimalFilter price;

    private IntegerFilter quantity;

    private IntegerFilter remaining;

    private InstantFilter saleStart;

    private InstantFilter saleEnd;

    private LongFilter eventId;

    private Boolean distinct;

    public TicketTypeCriteria() {}

    public TicketTypeCriteria(TicketTypeCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.price = other.optionalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.quantity = other.optionalQuantity().map(IntegerFilter::copy).orElse(null);
        this.remaining = other.optionalRemaining().map(IntegerFilter::copy).orElse(null);
        this.saleStart = other.optionalSaleStart().map(InstantFilter::copy).orElse(null);
        this.saleEnd = other.optionalSaleEnd().map(InstantFilter::copy).orElse(null);
        this.eventId = other.optionalEventId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TicketTypeCriteria copy() {
        return new TicketTypeCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public BigDecimalFilter getPrice() {
        return price;
    }

    public Optional<BigDecimalFilter> optionalPrice() {
        return Optional.ofNullable(price);
    }

    public BigDecimalFilter price() {
        if (price == null) {
            setPrice(new BigDecimalFilter());
        }
        return price;
    }

    public void setPrice(BigDecimalFilter price) {
        this.price = price;
    }

    public IntegerFilter getQuantity() {
        return quantity;
    }

    public Optional<IntegerFilter> optionalQuantity() {
        return Optional.ofNullable(quantity);
    }

    public IntegerFilter quantity() {
        if (quantity == null) {
            setQuantity(new IntegerFilter());
        }
        return quantity;
    }

    public void setQuantity(IntegerFilter quantity) {
        this.quantity = quantity;
    }

    public IntegerFilter getRemaining() {
        return remaining;
    }

    public Optional<IntegerFilter> optionalRemaining() {
        return Optional.ofNullable(remaining);
    }

    public IntegerFilter remaining() {
        if (remaining == null) {
            setRemaining(new IntegerFilter());
        }
        return remaining;
    }

    public void setRemaining(IntegerFilter remaining) {
        this.remaining = remaining;
    }

    public InstantFilter getSaleStart() {
        return saleStart;
    }

    public Optional<InstantFilter> optionalSaleStart() {
        return Optional.ofNullable(saleStart);
    }

    public InstantFilter saleStart() {
        if (saleStart == null) {
            setSaleStart(new InstantFilter());
        }
        return saleStart;
    }

    public void setSaleStart(InstantFilter saleStart) {
        this.saleStart = saleStart;
    }

    public InstantFilter getSaleEnd() {
        return saleEnd;
    }

    public Optional<InstantFilter> optionalSaleEnd() {
        return Optional.ofNullable(saleEnd);
    }

    public InstantFilter saleEnd() {
        if (saleEnd == null) {
            setSaleEnd(new InstantFilter());
        }
        return saleEnd;
    }

    public void setSaleEnd(InstantFilter saleEnd) {
        this.saleEnd = saleEnd;
    }

    public LongFilter getEventId() {
        return eventId;
    }

    public Optional<LongFilter> optionalEventId() {
        return Optional.ofNullable(eventId);
    }

    public LongFilter eventId() {
        if (eventId == null) {
            setEventId(new LongFilter());
        }
        return eventId;
    }

    public void setEventId(LongFilter eventId) {
        this.eventId = eventId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TicketTypeCriteria that = (TicketTypeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(price, that.price) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(remaining, that.remaining) &&
            Objects.equals(saleStart, that.saleStart) &&
            Objects.equals(saleEnd, that.saleEnd) &&
            Objects.equals(eventId, that.eventId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, quantity, remaining, saleStart, saleEnd, eventId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketTypeCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalPrice().map(f -> "price=" + f + ", ").orElse("") +
            optionalQuantity().map(f -> "quantity=" + f + ", ").orElse("") +
            optionalRemaining().map(f -> "remaining=" + f + ", ").orElse("") +
            optionalSaleStart().map(f -> "saleStart=" + f + ", ").orElse("") +
            optionalSaleEnd().map(f -> "saleEnd=" + f + ", ").orElse("") +
            optionalEventId().map(f -> "eventId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
