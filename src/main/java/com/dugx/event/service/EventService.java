package com.dugx.event.service;

import com.dugx.event.domain.Event;
import com.dugx.event.domain.Organizer;
import com.dugx.event.domain.OrganizerStatus;
import com.dugx.event.repository.EventRepository;
import com.dugx.event.repository.OrganizerRepository;
import com.dugx.event.repository.TicketTypeRepository;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.EventDTO;
import com.dugx.event.service.mapper.EventMapper;
import com.dugx.event.service.mapper.TicketTypeMapper;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.Event}.
 */
@Service
@Transactional
public class EventService {

    private static final Logger LOG = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;
    private final OrganizerRepository organizerRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketTypeMapper ticketTypeMapper;

    public EventService(
        EventRepository eventRepository,
        EventMapper eventMapper,
        OrganizerRepository organizerRepository,
        TicketTypeRepository ticketTypeRepository,
        TicketTypeMapper ticketTypeMapper
    ) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.organizerRepository = organizerRepository;
        this.ticketTypeRepository = ticketTypeRepository;
        this.ticketTypeMapper = ticketTypeMapper;
    }

    /**
     * Save a event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    public EventDTO save(EventDTO eventDTO) {
        LOG.debug("Request to save Event : {}", eventDTO);

        Event event = eventMapper.toEntity(eventDTO);

        Organizer organizer = getCurrentOrganizer();

        if (organizer.getStatus() != OrganizerStatus.APPROVED) {
            throw new BadRequestAlertException("Organizer chưa được phê duyệt", "event", "organizerNotApproved");
        }

        event.setOrganizer(organizer);

        event = eventRepository.save(event);

        return eventMapper.toDto(event);
    }

    /**
     * Update a event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    public EventDTO update(EventDTO eventDTO) {
        LOG.debug("Request to update Event : {}", eventDTO);

        Event existingEvent = eventRepository
            .findById(eventDTO.getId())
            .orElseThrow(() -> new BadRequestAlertException("Event không tồn tại", "event", "eventNotFound"));

        Event event = eventMapper.toEntity(eventDTO);
        event.setOrganizer(existingEvent.getOrganizer());
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    /**
     * Partially update a event.
     *
     * @param eventDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EventDTO> partialUpdate(EventDTO eventDTO) {
        LOG.debug("Request to partially update Event : {}", eventDTO);

        //checkOwnership(eventDTO.getId());

        return eventRepository
            .findById(eventDTO.getId())
            .map(existingEvent -> {
                eventMapper.partialUpdate(existingEvent, eventDTO);
                return existingEvent;
            })
            .map(eventRepository::save)
            .map(eventMapper::toDto);
    }

    /**
     * Get all the events with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<EventDTO> findAllWithEagerRelationships(Pageable pageable) {
        return eventRepository.findAllWithEagerRelationships(pageable).map(eventMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<EventDTO> findMyEvents(Pageable pageable) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("User not found"));

        return eventRepository.findMyEvents(login, pageable).map(event -> {
            EventDTO dto = eventMapper.toDto(event);

            dto.setPrice(eventRepository.findMinPrice(event.getId()));

            dto.setTicketTypes(ticketTypeRepository.findByEventId(event.getId()).stream().map(ticketTypeMapper::toDto).toList());
            return dto;
        });
    }

    /**
     * Get one event by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EventDTO> findOne(Long id) {
        LOG.debug("Request to get Event : {}", id);
        return eventRepository.findOneWithEagerRelationships(id).map(event -> {
            EventDTO dto = eventMapper.toDto(event);

            dto.setPrice(eventRepository.findMinPrice(event.getId()));

            dto.setTicketTypes(ticketTypeRepository.findByEventId(event.getId()).stream().map(ticketTypeMapper::toDto).toList());

            return dto;
        });
    }

    private Organizer getCurrentOrganizer() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("Chưa đăng nhập", "event", "unauthorized")
        );

        return organizerRepository
            .findByUserLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("Bạn chưa đăng ký Organizer", "event", "organizerNotFound"));
    }

    /**
     * Delete the event by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Event : {}", id);

        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("Chưa đăng nhập", "event", "unauthorized")
        );

        Event event = eventRepository
            .findMyEventById(id, login)
            .orElseThrow(() -> new BadRequestAlertException("Bạn không có quyền xóa Event này", "event", "forbidden"));

        ticketTypeRepository.deleteAllByEventId(id);
        eventRepository.delete(event);
    }
}
