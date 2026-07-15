package com.dugx.event.web.rest;

import com.dugx.event.repository.TicketTypeRepository;
import com.dugx.event.service.TicketTypeQueryService;
import com.dugx.event.service.TicketTypeService;
import com.dugx.event.service.criteria.TicketTypeCriteria;
import com.dugx.event.service.dto.TicketTypeDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.dugx.event.domain.TicketType}.
 */
@RestController
@RequestMapping("/api/ticket-types")
public class TicketTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(TicketTypeResource.class);

    private static final String ENTITY_NAME = "ticketType";

    @Value("${jhipster.clientApp.name:dugx}")
    private String applicationName;

    private final TicketTypeService ticketTypeService;

    private final TicketTypeRepository ticketTypeRepository;

    private final TicketTypeQueryService ticketTypeQueryService;

    public TicketTypeResource(
        TicketTypeService ticketTypeService,
        TicketTypeRepository ticketTypeRepository,
        TicketTypeQueryService ticketTypeQueryService
    ) {
        this.ticketTypeService = ticketTypeService;
        this.ticketTypeRepository = ticketTypeRepository;
        this.ticketTypeQueryService = ticketTypeQueryService;
    }

    /**
     * {@code POST  /ticket-types} : Create a new ticketType.
     *
     * @param ticketTypeDTO the ticketTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ticketTypeDTO, or with status {@code 400 (Bad Request)} if the ticketType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TicketTypeDTO> createTicketType(@Valid @RequestBody TicketTypeDTO ticketTypeDTO) throws URISyntaxException {
        LOG.debug("REST request to save TicketType : {}", ticketTypeDTO);
        if (ticketTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new ticketType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ticketTypeDTO = ticketTypeService.save(ticketTypeDTO);
        return ResponseEntity.created(new URI("/api/ticket-types/" + ticketTypeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ticketTypeDTO.getId().toString()))
            .body(ticketTypeDTO);
    }

    /**
     * {@code PUT  /ticket-types/:id} : Updates an existing ticketType.
     *
     * @param id the id of the ticketTypeDTO to save.
     * @param ticketTypeDTO the ticketTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketTypeDTO,
     * or with status {@code 400 (Bad Request)} if the ticketTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ticketTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TicketTypeDTO> updateTicketType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TicketTypeDTO ticketTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TicketType : {}, {}", id, ticketTypeDTO);
        if (ticketTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ticketTypeDTO = ticketTypeService.update(ticketTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketTypeDTO.getId().toString()))
            .body(ticketTypeDTO);
    }

    /**
     * {@code PATCH  /ticket-types/:id} : Partial updates given fields of an existing ticketType, field will ignore if it is null
     *
     * @param id the id of the ticketTypeDTO to save.
     * @param ticketTypeDTO the ticketTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketTypeDTO,
     * or with status {@code 400 (Bad Request)} if the ticketTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ticketTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ticketTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TicketTypeDTO> partialUpdateTicketType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TicketTypeDTO ticketTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TicketType partially : {}, {}", id, ticketTypeDTO);
        if (ticketTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TicketTypeDTO> result = ticketTypeService.partialUpdate(ticketTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ticket-types} : get all the Ticket Types.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ticket Types in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TicketTypeDTO>> getAllTicketTypes(
        TicketTypeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TicketTypes by criteria: {}", criteria);

        Page<TicketTypeDTO> page = ticketTypeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ticket-types/count} : count all the ticketTypes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTicketTypes(TicketTypeCriteria criteria) {
        LOG.debug("REST request to count TicketTypes by criteria: {}", criteria);
        return ResponseEntity.ok().body(ticketTypeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ticket-types/:id} : get the "id" ticketType.
     *
     * @param id the id of the ticketTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ticketTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketTypeDTO> getTicketType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TicketType : {}", id);
        Optional<TicketTypeDTO> ticketTypeDTO = ticketTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ticketTypeDTO);
    }

    /**
     * {@code DELETE  /ticket-types/:id} : delete the "id" ticketType.
     *
     * @param id the id of the ticketTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TicketType : {}", id);
        ticketTypeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
