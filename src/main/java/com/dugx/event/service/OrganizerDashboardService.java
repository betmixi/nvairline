package com.dugx.event.service;

import com.dugx.event.domain.Event;
import com.dugx.event.repository.*;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.EventDTO;
import com.dugx.event.service.dto.OrganizerDashboardDTO;
import com.dugx.event.service.mapper.EventMapper;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrganizerDashboardService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final OrganizerRepository organizerRepository;
    private final ReviewRepository reviewRepository;

    public OrganizerDashboardService(
        EventRepository eventRepository,
        EventMapper eventMapper,
        PaymentRepository paymentRepository,
        BookingRepository bookingRepository,
        BookingDetailRepository bookingDetailRepository,
        OrganizerRepository organizerRepository,
        ReviewRepository reviewRepository
    ) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.organizerRepository = organizerRepository;
        this.reviewRepository = reviewRepository;
    }

    public OrganizerDashboardDTO getDashboard() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow();

        System.out.println("======================");
        System.out.println("LOGIN = " + login);

        Long totalEvents = eventRepository.countByOrganizerUserLogin(login);
        System.out.println("TOTAL EVENTS = " + totalEvents);

        OrganizerDashboardDTO dto = new OrganizerDashboardDTO();
        var organizer = organizerRepository.findByUserLogin(login).orElseThrow();

        dto.setCompanyName(organizer.getCompanyName());

        dto.setTotalEvents(totalEvents);

        dto.setPublishedEvents(eventRepository.countByOrganizerUserLoginAndStatusTrue(login));

        dto.setTotalTickets(bookingDetailRepository.totalTickets(login));
        dto.setTotalBookings(bookingRepository.countBookingByOrganizer(login));
        dto.setTotalRevenue(paymentRepository.totalRevenue(login));

        dto.setLatestEvents(
            eventRepository.findTop3BestSellingEvents(login, PageRequest.of(0, 3)).stream().map(eventMapper::toDto).toList()
        );

        return dto;
    }
}
