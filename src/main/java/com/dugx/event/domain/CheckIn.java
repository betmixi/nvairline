package com.dugx.event.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A CheckIn.
 */
@Entity
@Table(name = "check_in")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CheckIn implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "check_in_time")
    private Instant checkInTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "bookingDetail" }, allowSetters = true)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    private User checkedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CheckIn id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCheckInTime() {
        return this.checkInTime;
    }

    public CheckIn checkInTime(Instant checkInTime) {
        this.setCheckInTime(checkInTime);
        return this;
    }

    public void setCheckInTime(Instant checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public CheckIn ticket(Ticket ticket) {
        this.setTicket(ticket);
        return this;
    }

    public User getCheckedBy() {
        return this.checkedBy;
    }

    public void setCheckedBy(User user) {
        this.checkedBy = user;
    }

    public CheckIn checkedBy(User user) {
        this.setCheckedBy(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CheckIn)) {
            return false;
        }
        return getId() != null && getId().equals(((CheckIn) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CheckIn{" +
            "id=" + getId() +
            ", checkInTime='" + getCheckInTime() + "'" +
            "}";
    }
}
