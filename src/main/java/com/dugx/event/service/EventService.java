package com.dugx.event.service;

import com.dugx.event.domain.Event;
import com.dugx.event.repository.EventRepository;
import com.dugx.event.repository.ShowtimeRepository;
import com.dugx.event.service.dto.EventDTO;
import com.dugx.event.service.mapper.EventMapper;
import com.dugx.event.service.mapper.ShowtimeMapper;
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
    private final ShowtimeRepository showtimeRepository;
    private final ShowtimeMapper showtimeMapper;

    public EventService(
        EventRepository eventRepository,
        EventMapper eventMapper,
        ShowtimeRepository showtimeRepository,
        ShowtimeMapper showtimeMapper
    ) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.showtimeRepository = showtimeRepository;
        this.showtimeMapper = showtimeMapper;
    }

    /**
     * Save a event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    public EventDTO save(EventDTO eventDTO) {
        LOG.debug("Request to save Event : {}", eventDTO);

        // Cot NOT NULL trong DB - mac dinh ho tro ca 3 loai hanh trinh neu form tao khong chi dinh gi.
        if (eventDTO.getSupportsOneWay() == null) {
            eventDTO.setSupportsOneWay(true);
        }
        if (eventDTO.getSupportsRoundTrip() == null) {
            eventDTO.setSupportsRoundTrip(true);
        }
        if (eventDTO.getSupportsMultiCity() == null) {
            eventDTO.setSupportsMultiCity(true);
        }

        Event event = eventMapper.toEntity(eventDTO);
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

        // Cot NOT NULL trong DB - giu mac dinh true neu client PUT khong gui (vd form cu chua co truong nay).
        if (eventDTO.getSupportsOneWay() == null) {
            eventDTO.setSupportsOneWay(true);
        }
        if (eventDTO.getSupportsRoundTrip() == null) {
            eventDTO.setSupportsRoundTrip(true);
        }
        if (eventDTO.getSupportsMultiCity() == null) {
            eventDTO.setSupportsMultiCity(true);
        }

        Event event = eventMapper.toEntity(eventDTO);
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

            dto.setShowtimes(showtimeRepository.findByEvent_Id(event.getId()).stream().map(showtimeMapper::toDto).toList());

            return dto;
        });
    }

    /**
     * Delete the event by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Event : {}", id);

        Event event = eventRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Event không tồn tại", "event", "eventnotfound"));

        eventRepository.delete(event);
    }
}
