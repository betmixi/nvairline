package com.dugx.event.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.dugx.event.domain.BookingDetail} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookingDetailDTO implements Serializable {

    private Long id;

    private Integer quantity;

    private BigDecimal price;

    private BookingDTO booking;

    private TicketTypeDTO ticketType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BookingDTO getBooking() {
        return booking;
    }

    public void setBooking(BookingDTO booking) {
        this.booking = booking;
    }

    public TicketTypeDTO getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketTypeDTO ticketType) {
        this.ticketType = ticketType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookingDetailDTO)) {
            return false;
        }

        BookingDetailDTO bookingDetailDTO = (BookingDetailDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bookingDetailDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookingDetailDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", price=" + getPrice() +
            ", booking=" + getBooking() +
            ", ticketType=" + getTicketType() +
            "}";
    }
}
