package com.dugx.event.service.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Mot dong trong danh sach "khach dat ve": thong tin khach hang gop voi
 * chuyen bay va tong quan don dat cho cua ho.
 */
public class CustomerBookingDTO {

    private Long bookingId;
    private Instant bookingDate;
    private BigDecimal totalAmount;
    private String status;

    private Long customerId;
    private String customerLogin;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;

    private String flightTitle;
    private String departureAirportCode;
    private String arrivalAirportCode;
    private Instant showtimeStart;

    private Long ticketCount;

    /** Mo ta tung chang bay gop lai (vd khu hoi/nhieu chang), dang "HAN → SGN (12/09 08:00) | SGN → HAN (15/09 10:00)". */
    private String legSummary;

    /** So chang bay trong booking nay (1 = mot chieu, >1 = khu hoi/nhieu chang). */
    private int legCount = 1;

    /** Loai don: TICKET (dat ve moi), BAGGAGE, SEAT_UPGRADE, ADDON (mua them cho ve da co). */
    private String bookingType = "TICKET";

    /** Thoi diem thanh toan thanh cong (Payment.paymentDate) - "thoi gian mua" chinh xac hon booking.bookingDate. */
    private Instant paymentDate;

    public CustomerBookingDTO(
        Long bookingId,
        Instant bookingDate,
        BigDecimal totalAmount,
        String status,
        Long customerId,
        String customerLogin,
        String customerFirstName,
        String customerLastName,
        String customerEmail,
        String flightTitle,
        String departureAirportCode,
        String arrivalAirportCode,
        Instant showtimeStart,
        Long ticketCount
    ) {
        this.bookingId = bookingId;
        this.bookingDate = bookingDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.customerId = customerId;
        this.customerLogin = customerLogin;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerEmail = customerEmail;
        this.flightTitle = flightTitle;
        this.departureAirportCode = departureAirportCode;
        this.arrivalAirportCode = arrivalAirportCode;
        this.showtimeStart = showtimeStart;
        this.ticketCount = ticketCount;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public Instant getBookingDate() {
        return bookingDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerLogin() {
        return customerLogin;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getFlightTitle() {
        return flightTitle;
    }

    public String getDepartureAirportCode() {
        return departureAirportCode;
    }

    public String getArrivalAirportCode() {
        return arrivalAirportCode;
    }

    public Instant getShowtimeStart() {
        return showtimeStart;
    }

    public Long getTicketCount() {
        return ticketCount;
    }

    public String getLegSummary() {
        return legSummary;
    }

    public void setLegSummary(String legSummary) {
        this.legSummary = legSummary;
    }

    public int getLegCount() {
        return legCount;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public Instant getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Instant paymentDate) {
        this.paymentDate = paymentDate;
    }

    /** Gop mot chang bay khac cua cung mot booking vao dong nay (dung khi khu hoi/nhieu chang bi tach thanh nhieu dong o truy van goc). */
    public CustomerBookingDTO mergeLeg(CustomerBookingDTO other) {
        this.legSummary = this.legSummary + " | " + other.legSummary;
        this.legCount = this.legCount + other.legCount;
        this.ticketCount = this.ticketCount + other.ticketCount;

        if (other.showtimeStart != null && (this.showtimeStart == null || other.showtimeStart.isBefore(this.showtimeStart))) {
            this.showtimeStart = other.showtimeStart;
            this.flightTitle = other.flightTitle;
            this.departureAirportCode = other.departureAirportCode;
            this.arrivalAirportCode = other.arrivalAirportCode;
        }

        return this;
    }
}
