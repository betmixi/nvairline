package com.dugx.event.service;

import com.dugx.event.domain.Address;
import com.dugx.event.domain.Booking;
import com.dugx.event.domain.BookingDetail;
import com.dugx.event.domain.Event;
import com.dugx.event.domain.Ticket;
import com.dugx.event.domain.TicketType;
import com.dugx.event.repository.BookingRepository;
import com.dugx.event.repository.TicketRepository;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.MyTicketDTO;
import com.dugx.event.service.dto.TicketDTO;
import com.dugx.event.service.mapper.TicketMapper;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.Ticket}.
 */
@Service
@Transactional
public class TicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;

    private final TicketMapper ticketMapper;
    private final BookingRepository bookingRepository;
    private final QrCodeService qrCodeService;

    public TicketService(
        TicketRepository ticketRepository,
        TicketMapper ticketMapper,
        BookingRepository bookingRepository,
        QrCodeService qrCodeService
    ) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.bookingRepository = bookingRepository;
        this.qrCodeService = qrCodeService;
    }

    /**
     * Lay "vi ve" cua nguoi dung dang dang nhap: moi ve kem san thong tin
     * su kien, loai ve va dia diem.
     *
     * @param pageable thong tin phan trang.
     * @return trang ve da lam phang.
     */
    @Transactional(readOnly = true)
    public Page<MyTicketDTO> getMyTicketWallet(Pageable pageable) {
        String login = currentLogin();

        return ticketRepository.findMyTicketsWithDetails(login, pageable).map(this::toMyTicketDto);
    }

    /**
     * Sinh anh QR cho mot ve, sau khi kiem tra ve do thuoc ve nguoi dang dang nhap.
     *
     * @param ticketId id cua ve.
     * @return chuoi data URI cua anh PNG.
     */
    @Transactional(readOnly = true)
    public String getQrImage(Long ticketId) {
        Ticket ticket = ticketRepository
            .findById(ticketId)
            .orElseThrow(() -> new BadRequestAlertException("Ticket not found", "ticket", "ticketnotfound"));

        assertOwnedByCurrentUser(ticket);

        if (ticket.getQrCode() == null || ticket.getQrCode().isBlank()) {
            throw new BadRequestAlertException("Ticket has no QR code", "ticket", "missingqrcode");
        }

        return qrCodeService.generatePngDataUri(ticket.getQrCode());
    }

    /** Lam phang Ticket -> BookingDetail -> TicketType -> Event -> Address. */
    private MyTicketDTO toMyTicketDto(Ticket ticket) {
        MyTicketDTO dto = new MyTicketDTO();

        dto.setId(ticket.getId());
        dto.setQrCode(ticket.getQrCode());
        dto.setStatus(ticket.getStatus());
        dto.setCheckedIn(ticket.getCheckedIn());

        BookingDetail detail = ticket.getBookingDetail();

        if (detail == null) {
            return dto;
        }

        dto.setPrice(detail.getPrice());

        if (detail.getBooking() != null) {
            dto.setBookingId(detail.getBooking().getId());
            dto.setBookingStatus(detail.getBooking().getStatus());
            dto.setBookingDate(detail.getBooking().getBookingDate());
        }

        TicketType ticketType = detail.getTicketType();

        if (ticketType == null) {
            return dto;
        }

        dto.setTicketTypeId(ticketType.getId());
        dto.setTicketTypeName(ticketType.getName());

        Event event = ticketType.getEvent();

        if (event == null) {
            return dto;
        }

        dto.setEventId(event.getId());
        dto.setEventTitle(event.getTitle());
        dto.setEventBanner(event.getBanner());
        dto.setEventStartTime(event.getStartTime());
        dto.setEventEndTime(event.getEndTime());

        Address address = event.getAddress();

        if (address != null) {
            dto.setLocation(address.getLocation());
            dto.setAddress(address.getAddress());
            dto.setCity(address.getCity());
        }

        return dto;
    }

    private String currentLogin() {
        return SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("User not logged in", "ticket", "usernotfound")
        );
    }

    /** Chan nguoi dung xem ve cua nguoi khac. */
    private void assertOwnedByCurrentUser(Ticket ticket) {
        String login = currentLogin();

        boolean owned =
            ticket.getBookingDetail() != null &&
            ticket.getBookingDetail().getBooking() != null &&
            ticket.getBookingDetail().getBooking().getUser() != null &&
            login.equals(ticket.getBookingDetail().getBooking().getUser().getLogin());

        if (!owned) {
            throw new BadRequestAlertException("You do not own this ticket", "ticket", "accessdenied");
        }
    }

    /**
     * Save a ticket.
     *
     * @param ticketDTO the entity to save.
     * @return the persisted entity.
     */
    public TicketDTO save(TicketDTO ticketDTO) {
        LOG.debug("Request to save Ticket : {}", ticketDTO);
        Ticket ticket = ticketMapper.toEntity(ticketDTO);
        ticket = ticketRepository.save(ticket);
        return ticketMapper.toDto(ticket);
    }

    /**
     * Update a ticket.
     *
     * @param ticketDTO the entity to save.
     * @return the persisted entity.
     */
    public TicketDTO update(TicketDTO ticketDTO) {
        LOG.debug("Request to update Ticket : {}", ticketDTO);
        Ticket ticket = ticketMapper.toEntity(ticketDTO);
        ticket = ticketRepository.save(ticket);
        return ticketMapper.toDto(ticket);
    }

    /**
     * Partially update a ticket.
     *
     * @param ticketDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TicketDTO> partialUpdate(TicketDTO ticketDTO) {
        LOG.debug("Request to partially update Ticket : {}", ticketDTO);

        return ticketRepository
            .findById(ticketDTO.getId())
            .map(existingTicket -> {
                ticketMapper.partialUpdate(existingTicket, ticketDTO);

                return existingTicket;
            })
            .map(ticketRepository::save)
            .map(ticketMapper::toDto);
    }

    /**
     * Get one ticket by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TicketDTO> findOne(Long id) {
        LOG.debug("Request to get Ticket : {}", id);
        return ticketRepository.findById(id).map(ticketMapper::toDto);
    }

    /**
     * Delete the ticket by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Ticket : {}", id);
        ticketRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<TicketDTO> getMyTickets(Pageable pageable) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("User not logged in", "ticket", "usernotfound")
        );
        return ticketRepository.findMyTickets(login, pageable).map(ticketMapper::toDto);
    }

    @Transactional(readOnly = true)
    public TicketDTO getTicket(Long id) {
        Ticket ticket = ticketRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Ticket not found", "ticket", "ticketnotfound"));
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("User not found", "ticket", "usernotfound")
        );
        if (!ticket.getBookingDetail().getBooking().getUser().getLogin().equals(login)) {
            throw new BadRequestAlertException("You do not own this ticket", "ticket", "accessdenied");
        }
        return ticketMapper.toDto(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketDTO> getTicketsByBooking(Long bookingId) {
        Booking booking = bookingRepository
            .findById(bookingId)
            .orElseThrow(() -> new BadRequestAlertException("Booking not found", "ticket", "bookingnotfound"));

        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("User not found", "ticket", "usernotfound")
        );

        if (!booking.getUser().getLogin().equals(login)) {
            throw new BadRequestAlertException("Access denied", "ticket", "accessdenied");
        }

        return ticketRepository.findByBookingId(bookingId).stream().map(ticketMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public TicketDTO getTicketByQr(String qrCode) {
        Ticket ticket = ticketRepository
            .findByQrCode(qrCode)
            .orElseThrow(() -> new BadRequestAlertException("Ticket not found", "ticket", "ticketnotfound"));

        return ticketMapper.toDto(ticket);
    }
}
