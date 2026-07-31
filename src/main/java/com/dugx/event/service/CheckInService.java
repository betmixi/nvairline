package com.dugx.event.service;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

import com.dugx.event.domain.Booking;
import com.dugx.event.domain.CheckIn;
import com.dugx.event.domain.Ticket;
import com.dugx.event.domain.User;
import com.dugx.event.repository.CheckInRepository;
import com.dugx.event.repository.TicketRepository;
import com.dugx.event.repository.UserRepository;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.CheckInDTO;
import com.dugx.event.service.dto.CheckInRequest;
import com.dugx.event.service.mapper.CheckInMapper;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.CheckIn}.
 */
@Service
@Transactional
public class CheckInService {

    private static final Logger LOG = LoggerFactory.getLogger(CheckInService.class);

    private final CheckInRepository checkInRepository;
    private final CheckInMapper checkInMapper;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public CheckInService(
        CheckInRepository checkInRepository,
        CheckInMapper checkInMapper,
        TicketRepository ticketRepository,
        UserRepository userRepository
    ) {
        this.checkInRepository = checkInRepository;
        this.checkInMapper = checkInMapper;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save a checkIn.
     *
     * @param checkInDTO the entity to save.
     * @return the persisted entity.
     */
    public CheckInDTO save(CheckInDTO checkInDTO) {
        LOG.debug("Request to save CheckIn : {}", checkInDTO);
        CheckIn checkIn = checkInMapper.toEntity(checkInDTO);
        checkIn = checkInRepository.save(checkIn);
        return checkInMapper.toDto(checkIn);
    }

    /**
     * Update a checkIn.
     *
     * @param checkInDTO the entity to save.
     * @return the persisted entity.
     */
    public CheckInDTO update(CheckInDTO checkInDTO) {
        LOG.debug("Request to update CheckIn : {}", checkInDTO);
        CheckIn checkIn = checkInMapper.toEntity(checkInDTO);
        checkIn = checkInRepository.save(checkIn);
        return checkInMapper.toDto(checkIn);
    }

    /**
     * Partially update a checkIn.
     *
     * @param checkInDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CheckInDTO> partialUpdate(CheckInDTO checkInDTO) {
        LOG.debug("Request to partially update CheckIn : {}", checkInDTO);

        return checkInRepository
            .findById(checkInDTO.getId())
            .map(existingCheckIn -> {
                checkInMapper.partialUpdate(existingCheckIn, checkInDTO);

                return existingCheckIn;
            })
            .map(checkInRepository::save)
            .map(checkInMapper::toDto);
    }

    /**
     * Get all the checkIns with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CheckInDTO> findAllWithEagerRelationships(Pageable pageable) {
        return checkInRepository.findAllWithEagerRelationships(pageable).map(checkInMapper::toDto);
    }

    /**
     * Get one checkIn by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CheckInDTO> findOne(Long id) {
        LOG.debug("Request to get CheckIn : {}", id);
        return checkInRepository.findOneWithEagerRelationships(id).map(checkInMapper::toDto);
    }

    /**
     * Delete the checkIn by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CheckIn : {}", id);
        checkInRepository.deleteById(id);
    }

    @Transactional
    public CheckInDTO checkIn(CheckInRequest request) {
        Ticket ticket = validateTicket(request);

        User user = getCurrentUser();

        CheckIn checkIn = createCheckIn(ticket, user);

        updateTicket(ticket);

        return checkInMapper.toDto(checkIn);
    }

    private Ticket validateTicket(CheckInRequest request) {
        Ticket ticket = ticketRepository
            .findById(request.getTicketId())
            .orElseThrow(() -> new BadRequestAlertException("Ticket not found", ENTITY_NAME, "ticketnotfound"));

        if (Boolean.TRUE.equals(ticket.getCheckedIn())) {
            throw new BadRequestAlertException("Ticket already checked in", ENTITY_NAME, "alreadycheckedin");
        }

        if (ticket.getBookingDetail() == null || ticket.getBookingDetail().getBooking() == null) {
            throw new BadRequestAlertException("Invalid ticket", ENTITY_NAME, "invalidticket");
        }

        Booking booking = ticket.getBookingDetail().getBooking();
        if (!"PAID".equals(booking.getStatus())) {
            throw new BadRequestAlertException("Booking has not been paid", ENTITY_NAME, "bookingnotpaid");
        }

        return ticket;
    }

    private User getCurrentUser() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("User not logged in", ENTITY_NAME, "usernotfound")
        );
        return userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "usernotfound"));
    }

    private CheckIn createCheckIn(Ticket ticket, User user) {
        CheckIn checkIn = new CheckIn();

        checkIn.setTicket(ticket);

        checkIn.setCheckedBy(user);

        checkIn.setCheckInTime(Instant.now());

        return checkInRepository.save(checkIn);
    }

    private void updateTicket(Ticket ticket) {
        ticket.setCheckedIn(true);
        ticket.setStatus("CHECKED_IN");
        ticketRepository.save(ticket);
    }
}
