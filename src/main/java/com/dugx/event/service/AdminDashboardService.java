package com.dugx.event.service;

import com.dugx.event.domain.OrganizerStatus;
import com.dugx.event.repository.*;
import com.dugx.event.service.dto.AdminDashboardDTO;
import com.dugx.event.service.mapper.EventMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final PaymentRepository paymentRepository;
    private final OrganizerRepository organizerRepository;
    private final EventMapper eventMapper;

    public AdminDashboardService(
        UserRepository userRepository,
        EventRepository eventRepository,
        BookingRepository bookingRepository,
        BookingDetailRepository bookingDetailRepository,
        PaymentRepository paymentRepository,
        OrganizerRepository organizerRepository,
        EventMapper eventMapper
    ) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.paymentRepository = paymentRepository;
        this.organizerRepository = organizerRepository;
        this.eventMapper = eventMapper;
    }

    public AdminDashboardDTO getDashboard() {
        AdminDashboardDTO dto = new AdminDashboardDTO();

        dto.setTotalUsers(userRepository.count());

        dto.setTotalEvents(eventRepository.count());

        dto.setTotalBookings(bookingRepository.count());

        dto.setTotalRevenue(paymentRepository.totalRevenue());

        dto.setPendingOrganizer(organizerRepository.countByStatus(OrganizerStatus.PENDING));
        dto.setRecentEvents(eventRepository.findRecentEvents(PageRequest.of(0, 5)).stream().map(eventMapper::toDto).toList());

        dto.setTopEventsByTickets(bookingDetailRepository.topEventsByTickets(PageRequest.of(0, 5)));

        dto.setTopEventsByRevenue(bookingDetailRepository.topEventsByRevenue(PageRequest.of(0, 5)));

        return dto;
    }
}
