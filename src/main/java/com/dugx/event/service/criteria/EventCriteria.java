package com.dugx.event.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.dugx.event.domain.Event} entity. This class is used
 * in {@link com.dugx.event.web.rest.EventResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /events?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter banner;

    private InstantFilter startTime;

    private InstantFilter endTime;

    private BooleanFilter status;

    private InstantFilter createdDate;

    private BooleanFilter supportsOneWay;

    private BooleanFilter supportsRoundTrip;

    private BooleanFilter supportsMultiCity;

    private LongFilter categoryId;

    private LongFilter addressId;

    private LongFilter departureAirportId;

    private LongFilter arrivalAirportId;

    private Boolean distinct;

    public EventCriteria() {}

    public EventCriteria(EventCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.banner = other.optionalBanner().map(StringFilter::copy).orElse(null);
        this.startTime = other.optionalStartTime().map(InstantFilter::copy).orElse(null);
        this.endTime = other.optionalEndTime().map(InstantFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(BooleanFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.supportsOneWay = other.optionalSupportsOneWay().map(BooleanFilter::copy).orElse(null);
        this.supportsRoundTrip = other.optionalSupportsRoundTrip().map(BooleanFilter::copy).orElse(null);
        this.supportsMultiCity = other.optionalSupportsMultiCity().map(BooleanFilter::copy).orElse(null);
        this.categoryId = other.optionalCategoryId().map(LongFilter::copy).orElse(null);
        this.addressId = other.optionalAddressId().map(LongFilter::copy).orElse(null);
        this.departureAirportId = other.optionalDepartureAirportId().map(LongFilter::copy).orElse(null);
        this.arrivalAirportId = other.optionalArrivalAirportId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EventCriteria copy() {
        return new EventCriteria(this);
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

    public StringFilter getTitle() {
        return title;
    }

    public Optional<StringFilter> optionalTitle() {
        return Optional.ofNullable(title);
    }

    public StringFilter title() {
        if (title == null) {
            setTitle(new StringFilter());
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getBanner() {
        return banner;
    }

    public Optional<StringFilter> optionalBanner() {
        return Optional.ofNullable(banner);
    }

    public StringFilter banner() {
        if (banner == null) {
            setBanner(new StringFilter());
        }
        return banner;
    }

    public void setBanner(StringFilter banner) {
        this.banner = banner;
    }

    public InstantFilter getStartTime() {
        return startTime;
    }

    public Optional<InstantFilter> optionalStartTime() {
        return Optional.ofNullable(startTime);
    }

    public InstantFilter startTime() {
        if (startTime == null) {
            setStartTime(new InstantFilter());
        }
        return startTime;
    }

    public void setStartTime(InstantFilter startTime) {
        this.startTime = startTime;
    }

    public InstantFilter getEndTime() {
        return endTime;
    }

    public Optional<InstantFilter> optionalEndTime() {
        return Optional.ofNullable(endTime);
    }

    public InstantFilter endTime() {
        if (endTime == null) {
            setEndTime(new InstantFilter());
        }
        return endTime;
    }

    public void setEndTime(InstantFilter endTime) {
        this.endTime = endTime;
    }

    public BooleanFilter getStatus() {
        return status;
    }

    public Optional<BooleanFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public BooleanFilter status() {
        if (status == null) {
            setStatus(new BooleanFilter());
        }
        return status;
    }

    public void setStatus(BooleanFilter status) {
        this.status = status;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public Optional<InstantFilter> optionalCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            setCreatedDate(new InstantFilter());
        }
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public BooleanFilter getSupportsOneWay() {
        return supportsOneWay;
    }

    public Optional<BooleanFilter> optionalSupportsOneWay() {
        return Optional.ofNullable(supportsOneWay);
    }

    public BooleanFilter supportsOneWay() {
        if (supportsOneWay == null) {
            setSupportsOneWay(new BooleanFilter());
        }
        return supportsOneWay;
    }

    public void setSupportsOneWay(BooleanFilter supportsOneWay) {
        this.supportsOneWay = supportsOneWay;
    }

    public BooleanFilter getSupportsRoundTrip() {
        return supportsRoundTrip;
    }

    public Optional<BooleanFilter> optionalSupportsRoundTrip() {
        return Optional.ofNullable(supportsRoundTrip);
    }

    public BooleanFilter supportsRoundTrip() {
        if (supportsRoundTrip == null) {
            setSupportsRoundTrip(new BooleanFilter());
        }
        return supportsRoundTrip;
    }

    public void setSupportsRoundTrip(BooleanFilter supportsRoundTrip) {
        this.supportsRoundTrip = supportsRoundTrip;
    }

    public BooleanFilter getSupportsMultiCity() {
        return supportsMultiCity;
    }

    public Optional<BooleanFilter> optionalSupportsMultiCity() {
        return Optional.ofNullable(supportsMultiCity);
    }

    public BooleanFilter supportsMultiCity() {
        if (supportsMultiCity == null) {
            setSupportsMultiCity(new BooleanFilter());
        }
        return supportsMultiCity;
    }

    public void setSupportsMultiCity(BooleanFilter supportsMultiCity) {
        this.supportsMultiCity = supportsMultiCity;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public Optional<LongFilter> optionalCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            setCategoryId(new LongFilter());
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    public LongFilter getAddressId() {
        return addressId;
    }

    public Optional<LongFilter> optionalAddressId() {
        return Optional.ofNullable(addressId);
    }

    public LongFilter addressId() {
        if (addressId == null) {
            setAddressId(new LongFilter());
        }
        return addressId;
    }

    public void setAddressId(LongFilter addressId) {
        this.addressId = addressId;
    }

    public LongFilter getDepartureAirportId() {
        return departureAirportId;
    }

    public Optional<LongFilter> optionalDepartureAirportId() {
        return Optional.ofNullable(departureAirportId);
    }

    public LongFilter departureAirportId() {
        if (departureAirportId == null) {
            setDepartureAirportId(new LongFilter());
        }
        return departureAirportId;
    }

    public void setDepartureAirportId(LongFilter departureAirportId) {
        this.departureAirportId = departureAirportId;
    }

    public LongFilter getArrivalAirportId() {
        return arrivalAirportId;
    }

    public Optional<LongFilter> optionalArrivalAirportId() {
        return Optional.ofNullable(arrivalAirportId);
    }

    public LongFilter arrivalAirportId() {
        if (arrivalAirportId == null) {
            setArrivalAirportId(new LongFilter());
        }
        return arrivalAirportId;
    }

    public void setArrivalAirportId(LongFilter arrivalAirportId) {
        this.arrivalAirportId = arrivalAirportId;
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
        final EventCriteria that = (EventCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(banner, that.banner) &&
            Objects.equals(startTime, that.startTime) &&
            Objects.equals(endTime, that.endTime) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(supportsOneWay, that.supportsOneWay) &&
            Objects.equals(supportsRoundTrip, that.supportsRoundTrip) &&
            Objects.equals(supportsMultiCity, that.supportsMultiCity) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(addressId, that.addressId) &&
            Objects.equals(departureAirportId, that.departureAirportId) &&
            Objects.equals(arrivalAirportId, that.arrivalAirportId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            title,
            banner,
            startTime,
            endTime,
            status,
            createdDate,
            supportsOneWay,
            supportsRoundTrip,
            supportsMultiCity,
            categoryId,
            addressId,
            departureAirportId,
            arrivalAirportId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalBanner().map(f -> "banner=" + f + ", ").orElse("") +
            optionalStartTime().map(f -> "startTime=" + f + ", ").orElse("") +
            optionalEndTime().map(f -> "endTime=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalSupportsOneWay().map(f -> "supportsOneWay=" + f + ", ").orElse("") +
            optionalSupportsRoundTrip().map(f -> "supportsRoundTrip=" + f + ", ").orElse("") +
            optionalSupportsMultiCity().map(f -> "supportsMultiCity=" + f + ", ").orElse("") +
            optionalCategoryId().map(f -> "categoryId=" + f + ", ").orElse("") +
            optionalAddressId().map(f -> "addressId=" + f + ", ").orElse("") +
            optionalDepartureAirportId().map(f -> "departureAirportId=" + f + ", ").orElse("") +
            optionalArrivalAirportId().map(f -> "arrivalAirportId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
