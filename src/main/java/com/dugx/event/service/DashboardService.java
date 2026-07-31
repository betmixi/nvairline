package com.dugx.event.service;

import com.dugx.event.repository.*;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.DashboardDTO;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final PaymentRepository paymentRepository;
    private final CheckInRepository checkInRepository;

    public DashboardService(
        EventRepository eventRepository,
        BookingRepository bookingRepository,
        BookingDetailRepository bookingDetailRepositor,
        PaymentRepository paymentRepository,
        CheckInRepository checkInRepository
    ) {
        this.bookingDetailRepository = bookingDetailRepositor;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
        this.checkInRepository = checkInRepository;
        this.paymentRepository = paymentRepository;
    }

    public DashboardDTO getDashboard() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("Chưa đăng nhập", "dashboard", "unauthorized")
        );
        DashboardDTO dto = new DashboardDTO();
        dto.setTotalEvents(eventRepository.countByOrganizerUserLogin(login));
        dto.setPublishedEvents(eventRepository.countByOrganizerUserLoginAndStatusTrue(login));

        dto.setTotalBookings(bookingRepository.countBookingByOrganizer(login));

        dto.setTicketsSold(bookingDetailRepository.totalTicketsSold(login));

        dto.setTotalRevenue(paymentRepository.totalRevenue(login));

        dto.setCheckedIn(checkInRepository.totalCheckedIn(login));

        return dto;
    }
}
