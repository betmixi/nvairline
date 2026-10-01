package com.dugx.event.domain;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * A LoyaltyAccount: so diem tich luy hien tai cua mot nguoi dung (Lotusmiles).
 */
@Entity
@Table(name = "loyalty_account")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LoyaltyAccount implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "points")
    private Integer points;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPoints() {
        return this.points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LoyaltyAccount)) {
            return false;
        }
        return getId() != null && getId().equals(((LoyaltyAccount) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "LoyaltyAccount{" + "id=" + getId() + ", points=" + getPoints() + "}";
    }
}
