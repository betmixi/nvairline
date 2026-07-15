package com.dugx.event.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.dugx.event.domain.Ticket} entity. This class is used
 * in {@link com.dugx.event.web.rest.TicketResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tickets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter status;

    private BooleanFilter checkedIn;

    private LongFilter bookingDetailId;

    private Boolean distinct;

    public TicketCriteria() {}

    public TicketCriteria(TicketCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.checkedIn = other.optionalCheckedIn().map(BooleanFilter::copy).orElse(null);
        this.bookingDetailId = other.optionalBookingDetailId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TicketCriteria copy() {
        return new TicketCriteria(this);
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

    public StringFilter getStatus() {
        return status;
    }

    public Optional<StringFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StringFilter status() {
        if (status == null) {
            setStatus(new StringFilter());
        }
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public BooleanFilter getCheckedIn() {
        return checkedIn;
    }

    public Optional<BooleanFilter> optionalCheckedIn() {
        return Optional.ofNullable(checkedIn);
    }

    public BooleanFilter checkedIn() {
        if (checkedIn == null) {
            setCheckedIn(new BooleanFilter());
        }
        return checkedIn;
    }

    public void setCheckedIn(BooleanFilter checkedIn) {
        this.checkedIn = checkedIn;
    }

    public LongFilter getBookingDetailId() {
        return bookingDetailId;
    }

    public Optional<LongFilter> optionalBookingDetailId() {
        return Optional.ofNullable(bookingDetailId);
    }

    public LongFilter bookingDetailId() {
        if (bookingDetailId == null) {
            setBookingDetailId(new LongFilter());
        }
        return bookingDetailId;
    }

    public void setBookingDetailId(LongFilter bookingDetailId) {
        this.bookingDetailId = bookingDetailId;
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
        final TicketCriteria that = (TicketCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(checkedIn, that.checkedIn) &&
            Objects.equals(bookingDetailId, that.bookingDetailId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, checkedIn, bookingDetailId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCheckedIn().map(f -> "checkedIn=" + f + ", ").orElse("") +
            optionalBookingDetailId().map(f -> "bookingDetailId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
