package com.dugx.event.domain;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A CustomerProfile: thong tin khach hang bo sung (sdt, ngay sinh, gioi tinh, so CCCD/ho chieu, dia chi),
 * tach rieng khoi User de khong dong vao entity User do JHipster tu sinh.
 */
@Entity
@Table(name = "customer_profile")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "phone")
    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    private String gender;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "address")
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdNumber() {
        return this.idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        if (!(o instanceof CustomerProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((CustomerProfile) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "CustomerProfile{" +
            "id=" +
            getId() +
            ", phone='" +
            getPhone() +
            "'" +
            ", dateOfBirth='" +
            getDateOfBirth() +
            "'" +
            ", gender='" +
            getGender() +
            "'" +
            ", idNumber='" +
            getIdNumber() +
            "'" +
            ", address='" +
            getAddress() +
            "'" +
            "}"
        );
    }
}
