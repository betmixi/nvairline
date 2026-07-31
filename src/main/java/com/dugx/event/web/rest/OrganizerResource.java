package com.dugx.event.web.rest;

import com.dugx.event.domain.Organizer;
import com.dugx.event.repository.OrganizerRepository;
import com.dugx.event.service.OrganizerDashboardService;
import com.dugx.event.service.OrganizerQueryService;
import com.dugx.event.service.OrganizerService;
import com.dugx.event.service.criteria.OrganizerCriteria;
import com.dugx.event.service.dto.OrganizerDTO;
import com.dugx.event.service.dto.OrganizerDashboardDTO;
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
 * REST controller for managing {@link com.dugx.event.domain.Organizer}.
 */
@RestController
@RequestMapping("/api/organizers")
public class OrganizerResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizerResource.class);

    private static final String ENTITY_NAME = "organizer";

    @Value("${jhipster.clientApp.name:dugx}")
    private String applicationName;

    private final OrganizerService organizerService;

    private final OrganizerRepository organizerRepository;

    private final OrganizerQueryService organizerQueryService;
    private final OrganizerDashboardService organizerDashboardService;

    public OrganizerResource(
        OrganizerService organizerService,
        OrganizerRepository organizerRepository,
        OrganizerQueryService organizerQueryService,
        OrganizerDashboardService organizerDashboardService
    ) {
        this.organizerService = organizerService;
        this.organizerRepository = organizerRepository;
        this.organizerQueryService = organizerQueryService;
        this.organizerDashboardService = organizerDashboardService;
    }

    @PostMapping("/register")
    public ResponseEntity<OrganizerDTO> register(@Valid @RequestBody OrganizerDTO organizerDTO) {
        OrganizerDTO result = organizerService.register(organizerDTO);

        return ResponseEntity.ok(result);
    }

    /**
     * {@code POST  /organizers} : Create a new organizer.
     *
     * @param organizerDTO the organizerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new organizerDTO, or with status {@code 400 (Bad Request)} if the organizer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<OrganizerDTO> createOrganizer(@Valid @RequestBody OrganizerDTO organizerDTO) throws URISyntaxException {
        LOG.debug("REST request to save Organizer : {}", organizerDTO);
        if (organizerDTO.getId() != null) {
            throw new BadRequestAlertException("A new organizer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        organizerDTO = organizerService.save(organizerDTO);
        return ResponseEntity.created(new URI("/api/organizers/" + organizerDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, organizerDTO.getId().toString()))
            .body(organizerDTO);
    }

    /**
     * {@code PUT  /organizers/:id} : Updates an existing organizer.
     *
     * @param id the id of the organizerDTO to save.
     * @param organizerDTO the organizerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organizerDTO,
     * or with status {@code 400 (Bad Request)} if the organizerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the organizerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrganizerDTO> updateOrganizer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OrganizerDTO organizerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Organizer : {}, {}", id, organizerDTO);
        if (organizerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, organizerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!organizerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        organizerDTO = organizerService.update(organizerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, organizerDTO.getId().toString()))
            .body(organizerDTO);
    }

    /**
     * {@code PATCH  /organizers/:id} : Partial updates given fields of an existing organizer, field will ignore if it is null
     *
     * @param id the id of the organizerDTO to save.
     * @param organizerDTO the organizerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organizerDTO,
     * or with status {@code 400 (Bad Request)} if the organizerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the organizerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the organizerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrganizerDTO> partialUpdateOrganizer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OrganizerDTO organizerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Organizer partially : {}, {}", id, organizerDTO);
        if (organizerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, organizerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!organizerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrganizerDTO> result = organizerService.partialUpdate(organizerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, organizerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /organizers} : get all the Organizers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Organizers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<OrganizerDTO>> getAllOrganizers(
        OrganizerCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        List<Organizer> list = organizerRepository.findAll();

        System.out.println("========== DB ==========");
        list.forEach(o -> System.out.println(o.getId() + " | " + o.getCompanyName() + " | " + o.getStatus()));

        Page<OrganizerDTO> page = organizerQueryService.findByCriteria(criteria, pageable);

        return ResponseEntity.ok(page.getContent());
    }

    /**
     * {@code GET  /organizers/count} : count all the organizers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countOrganizers(OrganizerCriteria criteria) {
        LOG.debug("REST request to count Organizers by criteria: {}", criteria);
        return ResponseEntity.ok().body(organizerQueryService.countByCriteria(criteria));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<OrganizerDashboardDTO> getDashboard() {
        return ResponseEntity.ok(organizerDashboardService.getDashboard());
    }

    /**
     * {@code GET  /organizers/:id} : get the "id" organizer.
     *
     * @param id the id of the organizerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the organizerDTO, or with status {@code 404 (Not Found)}.
     */

    @GetMapping("/{id}")
    public ResponseEntity<OrganizerDTO> getOrganizer(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Organizer : {}", id);
        Optional<OrganizerDTO> organizerDTO = organizerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(organizerDTO);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrganizerDTO> approveOrganizer(@PathVariable Long id) {
        OrganizerDTO result = organizerService.approve(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrganizerDTO> rejectOrganizer(@PathVariable Long id) {
        OrganizerDTO result = organizerService.reject(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-request")
    public ResponseEntity<OrganizerDTO> getMyRequest() {
        Optional<OrganizerDTO> organizerDTO = organizerService.getMyRequest();
        if (organizerDTO.isPresent()) {
            return ResponseEntity.ok(organizerDTO.get());
        }
        return ResponseEntity.ok().build();
    }

    /**
     * {@code DELETE  /organizers/:id} : delete the "id" organizer.
     *
     * @param id the id of the organizerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganizer(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Organizer : {}", id);
        organizerService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
