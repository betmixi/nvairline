package com.dugx.event.web.rest;

import com.dugx.event.repository.EventImageRepository;
import com.dugx.event.service.EventImageQueryService;
import com.dugx.event.service.EventImageService;
import com.dugx.event.service.criteria.EventImageCriteria;
import com.dugx.event.service.dto.EventImageDTO;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing {@link com.dugx.event.domain.EventImage}.
 */
@RestController
@RequestMapping("/api/event-images")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class EventImageResource {

    private static final Logger LOG = LoggerFactory.getLogger(EventImageResource.class);

    private static final String ENTITY_NAME = "eventImage";

    @Value("${jhipster.clientApp.name:dugx}")
    private String applicationName;

    private final EventImageService eventImageService;

    private final EventImageRepository eventImageRepository;

    private final EventImageQueryService eventImageQueryService;

    public EventImageResource(
        EventImageService eventImageService,
        EventImageRepository eventImageRepository,
        EventImageQueryService eventImageQueryService
    ) {
        this.eventImageService = eventImageService;
        this.eventImageRepository = eventImageRepository;
        this.eventImageQueryService = eventImageQueryService;
    }

    /**
     * {@code POST  /event-images} : Create a new eventImage.
     *
     * @param eventImageDTO the eventImageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventImageDTO, or with status {@code 400 (Bad Request)} if the eventImage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EventImageDTO> createEventImage(@Valid @RequestBody EventImageDTO eventImageDTO) throws URISyntaxException {
        LOG.debug("REST request to save EventImage : {}", eventImageDTO);
        if (eventImageDTO.getId() != null) {
            throw new BadRequestAlertException("A new eventImage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        eventImageDTO = eventImageService.save(eventImageDTO);
        return ResponseEntity.created(new URI("/api/event-images/" + eventImageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, eventImageDTO.getId().toString()))
            .body(eventImageDTO);
    }

    /**
     * {@code PUT  /event-images/:id} : Updates an existing eventImage.
     *
     * @param id the id of the eventImageDTO to save.
     * @param eventImageDTO the eventImageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventImageDTO,
     * or with status {@code 400 (Bad Request)} if the eventImageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventImageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventImageDTO> updateEventImage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EventImageDTO eventImageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EventImage : {}, {}", id, eventImageDTO);
        if (eventImageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventImageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventImageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        eventImageDTO = eventImageService.update(eventImageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eventImageDTO.getId().toString()))
            .body(eventImageDTO);
    }

    /**
     * {@code PATCH  /event-images/:id} : Partial updates given fields of an existing eventImage, field will ignore if it is null
     *
     * @param id the id of the eventImageDTO to save.
     * @param eventImageDTO the eventImageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventImageDTO,
     * or with status {@code 400 (Bad Request)} if the eventImageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the eventImageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the eventImageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EventImageDTO> partialUpdateEventImage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EventImageDTO eventImageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EventImage partially : {}, {}", id, eventImageDTO);
        if (eventImageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventImageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventImageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EventImageDTO> result = eventImageService.partialUpdate(eventImageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eventImageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /event-images} : get all the Event Images.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Event Images in body.
     */
    @GetMapping("")
    public ResponseEntity<List<EventImageDTO>> getAllEventImages(
        EventImageCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get EventImages by criteria: {}", criteria);

        Page<EventImageDTO> page = eventImageQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /event-images/count} : count all the eventImages.n
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countEventImages(EventImageCriteria criteria) {
        LOG.debug("REST request to count EventImages by criteria: {}", criteria);
        return ResponseEntity.ok().body(eventImageQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /event-images/:id} : get the "id" eventImage.
     *
     * @param id the id of the eventImageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventImageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventImageDTO> getEventImage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EventImage : {}", id);
        Optional<EventImageDTO> eventImageDTO = eventImageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventImageDTO);
    }

    /**
     * {@code DELETE  /event-images/:id} : delete the "id" eventImage.
     *
     * @param id the id of the eventImageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventImage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EventImage : {}", id);
        eventImageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
