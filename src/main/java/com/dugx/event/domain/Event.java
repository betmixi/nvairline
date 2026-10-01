package com.dugx.event.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Event.
 */
@Entity
@Table(name = "event")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Event implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "banner")
    private String banner;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "supports_one_way")
    private Boolean supportsOneWay;

    @Column(name = "supports_round_trip")
    private Boolean supportsRoundTrip;

    @Column(name = "supports_multi_city")
    private Boolean supportsMultiCity;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    private Airport departureAirport;

    @ManyToOne(fetch = FetchType.LAZY)
    private Airport arrivalAirport;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Event id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Event title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Event description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBanner() {
        return this.banner;
    }

    public Event banner(String banner) {
        this.setBanner(banner);
        return this;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public Event startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public Event endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public Event status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Event createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getSupportsOneWay() {
        return this.supportsOneWay;
    }

    public Event supportsOneWay(Boolean supportsOneWay) {
        this.setSupportsOneWay(supportsOneWay);
        return this;
    }

    public void setSupportsOneWay(Boolean supportsOneWay) {
        this.supportsOneWay = supportsOneWay;
    }

    public Boolean getSupportsRoundTrip() {
        return this.supportsRoundTrip;
    }

    public Event supportsRoundTrip(Boolean supportsRoundTrip) {
        this.setSupportsRoundTrip(supportsRoundTrip);
        return this;
    }

    public void setSupportsRoundTrip(Boolean supportsRoundTrip) {
        this.supportsRoundTrip = supportsRoundTrip;
    }

    public Boolean getSupportsMultiCity() {
        return this.supportsMultiCity;
    }

    public Event supportsMultiCity(Boolean supportsMultiCity) {
        this.setSupportsMultiCity(supportsMultiCity);
        return this;
    }

    public void setSupportsMultiCity(Boolean supportsMultiCity) {
        this.supportsMultiCity = supportsMultiCity;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Event category(Category category) {
        this.setCategory(category);
        return this;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Event address(Address address) {
        this.setAddress(address);
        return this;
    }

    public Airport getDepartureAirport() {
        return this.departureAirport;
    }

    public void setDepartureAirport(Airport departureAirport) {
        this.departureAirport = departureAirport;
    }

    public Event departureAirport(Airport departureAirport) {
        this.setDepartureAirport(departureAirport);
        return this;
    }

    public Airport getArrivalAirport() {
        return this.arrivalAirport;
    }

    public void setArrivalAirport(Airport arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public Event arrivalAirport(Airport arrivalAirport) {
        this.setArrivalAirport(arrivalAirport);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        return getId() != null && getId().equals(((Event) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Event{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", banner='" + getBanner() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", supportsOneWay='" + getSupportsOneWay() + "'" +
            ", supportsRoundTrip='" + getSupportsRoundTrip() + "'" +
            ", supportsMultiCity='" + getSupportsMultiCity() + "'" +
            "}";
    }
}
