package com.dugx.event.service.dto;

import java.time.LocalDate;

/** Thong tin khach hang bo sung cua nguoi dung dang dang nhap (tu dien o trang Settings). */
public class CustomerProfileDTO {

    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String idNumber;
    private String address;

    public CustomerProfileDTO() {}

    public CustomerProfileDTO(String phone, LocalDate dateOfBirth, String gender, String idNumber, String address) {
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.idNumber = idNumber;
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
