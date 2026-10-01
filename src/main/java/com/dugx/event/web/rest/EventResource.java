package com.dugx.event.web.rest;

import com.dugx.event.repository.EventRepository;
import com.dugx.event.repository.ShowtimeRepository;
import com.dugx.event.service.EventQueryService;
import com.dugx.event.service.EventService;
import com.dugx.event.service.criteria.EventCriteria;
import com.dugx.event.service.dto.EventDTO;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.dugx.event.domain.Event}.
 */
@RestController
@RequestMapping("/api/events")
public class EventResource {

    private static final Logger LOG = LoggerFactory.getLogger(EventResource.class);

    private static final String ENTITY_NAME = "event";

    @Value("${jhipster.clientApp.name:dugx}")
    private String applicationName;

    private final EventService eventService;

    private final EventRepository eventRepository;

    private final EventQueryService eventQueryService;

    private final ShowtimeRepository showtimeRepository;

    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    public EventResource(
        EventService eventService,
        EventRepository eventRepository,
        EventQueryService eventQueryService,
        ShowtimeRepository showtimeRepository
    ) {
        this.eventService = eventService;
        this.eventRepository = eventRepository;
        this.eventQueryService = eventQueryService;
        this.showtimeRepository = showtimeRepository;
    }

    /**
     * Neu co truyen showtimeDate (yyyy-MM-dd), rang buoc criteria.id() theo danh sach Event
     * co it nhat 1 gio bay trong ngay do (mui gio Viet Nam) - khong dung den EventCriteria/
     * EventQueryService, tan dung nguyen ven pipeline Specification hien co.
     */
    private void applyShowtimeDateFilter(EventCriteria criteria, String showtimeDate) {
        if (showtimeDate == null || showtimeDate.isBlank()) {
            return;
        }
        try {
            LocalDate date = LocalDate.parse(showtimeDate);
            var start = date.atStartOfDay(VN_ZONE).toInstant();
            var end = date.plusDays(1).atStartOfDay(VN_ZONE).toInstant();
            List<Long> matchingEventIds = showtimeRepository.findEventIdsByStartTimeBetween(start, end);
            criteria.id().setIn(matchingEventIds);
        } catch (DateTimeParseException e) {
            LOG.debug("Invalid showtimeDate param, ignoring: {}", showtimeDate);
        }
    }

    /**
     * Neu co truyen tripType (one-way | round-trip | multi-city), rang buoc criteria theo cot
     * supportsOneWay/supportsRoundTrip/supportsMultiCity tuong ung - moi Event co the duoc gioi han
     * chi ban theo mot so loai hanh trinh nhat dinh (giong hang bay that phan loai hang ve).
     */
    private void applyTripTypeFilter(EventCriteria criteria, String tripType) {
        if (tripType == null || tripType.isBlank()) {
            return;
        }
        switch (tripType) {
            case "one-way" -> criteria.supportsOneWay().setEquals(true);
            case "round-trip" -> criteria.supportsRoundTrip().setEquals(true);
            case "multi-city" -> criteria.supportsMultiCity().setEquals(true);
            default -> LOG.debug("Unknown tripType param, ignoring: {}", tripType);
        }
    }

    /**
     * San bay di va san bay den cua mot su kien khong duoc trung nhau.
     */
    private void validateDepartureArrivalAirports(EventDTO eventDTO) {
        if (
            eventDTO.getDepartureAirport() != null &&
            eventDTO.getArrivalAirport() != null &&
            Objects.equals(eventDTO.getDepartureAirport().getId(), eventDTO.getArrivalAirport().getId())
        ) {
            throw new BadRequestAlertException("Departure airport and arrival airport must be different", ENTITY_NAME, "sameairport");
        }
    }

    /**
     * Kiem tra du lieu chuyen bay: so hieu khong de trong/khong trung (khong phan biet hoa thuong) va
     * (khi tao/cap nhat day du) gio ket thuc khong som hon gio bat dau.
     */
    private void validateEvent(EventDTO eventDTO, Long excludeId, boolean fullUpdate) {
        if (eventDTO.getTitle() != null || fullUpdate) {
            String title = eventDTO.getTitle() == null ? "" : eventDTO.getTitle().trim();
            if (title.isEmpty()) {
                throw new BadRequestAlertException("Flight code must not be blank", ENTITY_NAME, "titlerequired");
            }
            eventDTO.setTitle(title);
            boolean duplicated = excludeId == null
                ? eventRepository.existsByTitleIgnoreCase(title)
                : eventRepository.existsByTitleIgnoreCaseAndIdNot(title, excludeId);
            if (duplicated) {
                throw new BadRequestAlertException("Flight code already exists", ENTITY_NAME, "titleexists");
            }
        }
        if (
            fullUpdate &&
            eventDTO.getStartTime() != null &&
            eventDTO.getEndTime() != null &&
            eventDTO.getEndTime().isBefore(eventDTO.getStartTime())
        ) {
            throw new BadRequestAlertException("End time must not be before start time", ENTITY_NAME, "invalidtime");
        }
        validateDepartureArrivalAirports(eventDTO);
    }

    /**
     * {@code POST  /events} : Create a new event.
     *
     * @param eventDTO the eventDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventDTO, or with status {@code 400 (Bad Request)} if the event has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody EventDTO eventDTO) throws URISyntaxException {
        LOG.debug("REST request to save Event : {}", eventDTO);
        if (eventDTO.getId() != null) {
            throw new BadRequestAlertException("A new event cannot already have an ID", ENTITY_NAME, "idexists");
        }
        validateEvent(eventDTO, null, true);
        eventDTO = eventService.save(eventDTO);
        return ResponseEntity.created(new URI("/api/events/" + eventDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, eventDTO.getId().toString()))
            .body(eventDTO);
    }

    /**
     * {@code PUT  /events/:id} : Updates an existing event.
     *
     * @param id the id of the eventDTO to save.
     * @param eventDTO the eventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventDTO,
     * or with status {@code 400 (Bad Request)} if the eventDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EventDTO> updateEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EventDTO eventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Event : {}, {}", id, eventDTO);
        if (eventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        validateEvent(eventDTO, id, true);

        eventDTO = eventService.update(eventDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eventDTO.getId().toString()))
            .body(eventDTO);
    }

    /**
     * {@code PATCH  /events/:id} : Partial updates given fields of an existing event, field will ignore if it is null
     *
     * @param id the id of the eventDTO to save.
     * @param eventDTO the eventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventDTO,
     * or with status {@code 400 (Bad Request)} if the eventDTO is not valid,
     * or with status {@code 404 (Not Found)} if the eventDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the eventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EventDTO> partialUpdateEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EventDTO eventDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Event partially : {}, {}", id, eventDTO);
        if (eventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        validateEvent(eventDTO, id, false);

        Optional<EventDTO> result = eventService.partialUpdate(eventDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eventDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /events} : get all the Events.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Events in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<EventDTO>> getAllEvents(
        EventCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Events by criteria: {}", criteria);

        Page<EventDTO> page = eventQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/public")
    public ResponseEntity<List<EventDTO>> getPublicEvents(
        EventCriteria criteria,
        @RequestParam(required = false) String showtimeDate,
        @RequestParam(required = false) String tripType,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        criteria.status().setEquals(true);
        applyShowtimeDateFilter(criteria, showtimeDate);
        applyTripTypeFilter(criteria, tripType);

        Page<EventDTO> page = eventQueryService.findByCriteria(criteria, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    //xem chi têtsst
    @GetMapping("/public/{id}")
    public ResponseEntity<EventDTO> getPublicEvent(@PathVariable Long id) {
        Optional<EventDTO> eventDTO = eventService.findOne(id);

        if (eventDTO.isEmpty() || !Boolean.TRUE.equals(eventDTO.get().getStatus())) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(eventDTO.get());
    }

    @GetMapping("/public/search")
    public ResponseEntity<List<EventDTO>> searchPublicEvents(
        EventCriteria criteria,
        @RequestParam(required = false) String showtimeDate,
        @RequestParam(required = false) String tripType,
        @ParameterObject Pageable pageable
    ) {
        criteria.status().setEquals(true);
        applyShowtimeDateFilter(criteria, showtimeDate);
        applyTripTypeFilter(criteria, tripType);

        Page<EventDTO> page = eventQueryService.findByCriteria(criteria, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /events/count} : count all the events.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Long> countEvents(EventCriteria criteria) {
        LOG.debug("REST request to count Events by criteria: {}", criteria);
        return ResponseEntity.ok().body(eventQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /events/:id} : get the "id" event.
     *
     * @param id the id of the eventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EventDTO> getEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Event : {}", id);
        Optional<EventDTO> eventDTO = eventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventDTO);
    }

    /**
     * {@code DELETE  /events/:id} : delete the "id" event.
     *
     * @param id the id of the eventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Event : {}", id);
        eventService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
