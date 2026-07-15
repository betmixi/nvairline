package com.dugx.event.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.dugx.event.domain.CheckIn} entity. This class is used
 * in {@link com.dugx.event.web.rest.CheckInResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /check-ins?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CheckInCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter checkInTime;

    private LongFilter ticketId;

    private LongFilter checkedById;

    private Boolean distinct;

    public CheckInCriteria() {}

    public CheckInCriteria(CheckInCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.checkInTime = other.optionalCheckInTime().map(InstantFilter::copy).orElse(null);
        this.ticketId = other.optionalTicketId().map(LongFilter::copy).orElse(null);
        this.checkedById = other.optionalCheckedById().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CheckInCriteria copy() {
        return new CheckInCriteria(this);
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

    public InstantFilter getCheckInTime() {
        return checkInTime;
    }

    public Optional<InstantFilter> optionalCheckInTime() {
        return Optional.ofNullable(checkInTime);
    }

    public InstantFilter checkInTime() {
        if (checkInTime == null) {
            setCheckInTime(new InstantFilter());
        }
        return checkInTime;
    }

    public void setCheckInTime(InstantFilter checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LongFilter getTicketId() {
        return ticketId;
    }

    public Optional<LongFilter> optionalTicketId() {
        return Optional.ofNullable(ticketId);
    }

    public LongFilter ticketId() {
        if (ticketId == null) {
            setTicketId(new LongFilter());
        }
        return ticketId;
    }

    public void setTicketId(LongFilter ticketId) {
        this.ticketId = ticketId;
    }

    public LongFilter getCheckedById() {
        return checkedById;
    }

    public Optional<LongFilter> optionalCheckedById() {
        return Optional.ofNullable(checkedById);
    }

    public LongFilter checkedById() {
        if (checkedById == null) {
            setCheckedById(new LongFilter());
        }
        return checkedById;
    }

    public void setCheckedById(LongFilter checkedById) {
        this.checkedById = checkedById;
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
        final CheckInCriteria that = (CheckInCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(checkInTime, that.checkInTime) &&
            Objects.equals(ticketId, that.ticketId) &&
            Objects.equals(checkedById, that.checkedById) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, checkInTime, ticketId, checkedById, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CheckInCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCheckInTime().map(f -> "checkInTime=" + f + ", ").orElse("") +
            optionalTicketId().map(f -> "ticketId=" + f + ", ").orElse("") +
            optionalCheckedById().map(f -> "checkedById=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
