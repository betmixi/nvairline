package com.dugx.event.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.dugx.event.domain.CheckIn} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CheckInDTO implements Serializable {

    private Long id;

    private Instant checkInTime;

    private TicketDTO ticket;

    private UserDTO checkedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Instant checkInTime) {
        this.checkInTime = checkInTime;
    }

    public TicketDTO getTicket() {
        return ticket;
    }

    public void setTicket(TicketDTO ticket) {
        this.ticket = ticket;
    }

    public UserDTO getCheckedBy() {
        return checkedBy;
    }

    public void setCheckedBy(UserDTO checkedBy) {
        this.checkedBy = checkedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CheckInDTO)) {
            return false;
        }

        CheckInDTO checkInDTO = (CheckInDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, checkInDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CheckInDTO{" +
            "id=" + getId() +
            ", checkInTime='" + getCheckInTime() + "'" +
            ", ticket=" + getTicket() +
            ", checkedBy=" + getCheckedBy() +
            "}";
    }
}
