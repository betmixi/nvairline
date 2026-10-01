package com.dugx.event.service.dto;

import com.dugx.event.domain.SeatStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.dugx.event.domain.ShowtimeSeat} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShowtimeSeatDTO implements Serializable {

    private Long id;

    private SeatStatus status;

    private BigDecimal price;

    private ShowtimeDTO showtime;

    private SeatDTO seat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ShowtimeDTO getShowtime() {
        return showtime;
    }

    public void setShowtime(ShowtimeDTO showtime) {
        this.showtime = showtime;
    }

    public SeatDTO getSeat() {
        return seat;
    }

    public void setSeat(SeatDTO seat) {
        this.seat = seat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShowtimeSeatDTO)) {
            return false;
        }

        ShowtimeSeatDTO showtimeSeatDTO = (ShowtimeSeatDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, showtimeSeatDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShowtimeSeatDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", price=" + getPrice() +
            "}";
    }
}
