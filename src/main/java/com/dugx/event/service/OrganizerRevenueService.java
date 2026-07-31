package com.dugx.event.service;

import com.dugx.event.repository.EventRepository;
import com.dugx.event.repository.PaymentRepository;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.OrganizerRevenueDTO;
import java.math.BigDecimal;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class OrganizerRevenueService {

    private final PaymentRepository paymentRepository;
    private final EventRepository eventRepository;

    public OrganizerRevenueService(PaymentRepository paymentRepository, EventRepository eventRepository) {
        this.paymentRepository = paymentRepository;
        this.eventRepository = eventRepository;
    }

    public OrganizerRevenueDTO getRevenue() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow();

        OrganizerRevenueDTO dto = new OrganizerRevenueDTO();

        dto.setTotalRevenue(paymentRepository.totalRevenue(login));

        dto.setTotalEvents(eventRepository.countByOrganizerUserLogin(login));

        dto.setTicketsSold(paymentRepository.totalTicketsSold(login));

        dto.setEventRevenue(paymentRepository.revenueByEvent(login));

        dto.setRecentPayments(paymentRepository.recentPayments(login, PageRequest.of(0, 5)));

        return dto;
    }
}
