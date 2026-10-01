package com.dugx.event.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the {@link com.dugx.event.domain.Event} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventDTO implements Serializable {

    private Long id;

    @NotBlank
    private String title;

    @Lob
    private String description;

    private String banner;

    private Instant startTime;

    private Instant endTime;

    private Boolean status;

    private Instant createdDate;

    private Boolean supportsOneWay;

    private Boolean supportsRoundTrip;

    private Boolean supportsMultiCity;

    private CategoryDTO category;

    private AddressDTO address;

    private AirportDTO departureAirport;

    private AirportDTO arrivalAirport;

    private BigDecimal price;
    private List<ShowtimeDTO> showtimes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getSupportsOneWay() {
        return supportsOneWay;
    }

    public void setSupportsOneWay(Boolean supportsOneWay) {
        this.supportsOneWay = supportsOneWay;
    }

    public Boolean getSupportsRoundTrip() {
        return supportsRoundTrip;
    }

    public void setSupportsRoundTrip(Boolean supportsRoundTrip) {
        this.supportsRoundTrip = supportsRoundTrip;
    }

    public Boolean getSupportsMultiCity() {
        return supportsMultiCity;
    }

    public void setSupportsMultiCity(Boolean supportsMultiCity) {
        this.supportsMultiCity = supportsMultiCity;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public AirportDTO getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(AirportDTO departureAirport) {
        this.departureAirport = departureAirport;
    }

    public AirportDTO getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(AirportDTO arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<ShowtimeDTO> getShowtimes() {
        return showtimes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventDTO)) {
            return false;
        }

        EventDTO eventDTO = (EventDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventDTO.id);
    }

    public void setShowtimes(List<ShowtimeDTO> showtimes) {
        this.showtimes = showtimes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventDTO{" +
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
            ", category=" + getCategory() +
            ", address=" + getAddress() +
            ", departureAirport=" + getDepartureAirport() +
            ", arrivalAirport=" + getArrivalAirport() +
            "}";
    }
}
