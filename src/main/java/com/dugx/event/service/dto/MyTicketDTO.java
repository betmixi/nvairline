package com.dugx.event.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Mot ve trong "vi ve" cua nguoi dung.
 *
 * DTO nay lam phang du lieu tu Ticket -> BookingDetail -> ShowtimeSeat -> Showtime -> Event
 * de man hinh Angular khong phai lan qua nhieu tang quan he.
 */
public class MyTicketDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /** Ma ve duy nhat, cung la noi dung ma hoa vao QR. */
    private String qrCode;

    private String status;

    private Boolean checkedIn;

    private Long bookingId;

    /** Thu tu chang bay trong booking (0 = chang dau...), null neu booking khong co nhieu chang. Dung de hien thi tag "Khứ hồi - chặng x/y". */
    private Integer legIndex;

    private String bookingStatus;

    private Instant bookingDate;

    private Long showtimeId;

    private Instant showtimeStartTime;

    /** So hieu/ten may bay khai thac chuyen bay, vi du "Airbus A321 - VN-A612". */
    private String aircraftName;

    /** Ho ten khach hang dat ve (chu tai khoan booking). */
    private String passengerName;

    private String seatLabel;

    private String seatType;

    private BigDecimal price;

    private Long eventId;

    private String eventTitle;

    private String eventBanner;

    private Instant eventStartTime;

    private Instant eventEndTime;

    /** Ten dia diem, vi du "Nha hat Hoa Binh". */
    private String location;

    private String address;

    private String city;

    public MyTicketDTO() {
        // Constructor rong cho Jackson.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(Boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getLegIndex() {
        return legIndex;
    }

    public void setLegIndex(Integer legIndex) {
        this.legIndex = legIndex;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Instant getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Instant bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Long getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Long showtimeId) {
        this.showtimeId = showtimeId;
    }

    public Instant getShowtimeStartTime() {
        return showtimeStartTime;
    }

    public void setShowtimeStartTime(Instant showtimeStartTime) {
        this.showtimeStartTime = showtimeStartTime;
    }

    public String getAircraftName() {
        return aircraftName;
    }

    public void setAircraftName(String aircraftName) {
        this.aircraftName = aircraftName;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getSeatLabel() {
        return seatLabel;
    }

    public void setSeatLabel(String seatLabel) {
        this.seatLabel = seatLabel;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventBanner() {
        return eventBanner;
    }

    public void setEventBanner(String eventBanner) {
        this.eventBanner = eventBanner;
    }

    public Instant getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(Instant eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public Instant getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(Instant eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "MyTicketDTO{id=" + id + ", eventTitle='" + eventTitle + "', status='" + status + "'}";
    }
}
