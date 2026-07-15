package com.dugx.event.web.rest;

import com.dugx.event.repository.BookingDetailRepository;
import com.dugx.event.service.BookingDetailQueryService;
import com.dugx.event.service.BookingDetailService;
import com.dugx.event.service.criteria.BookingDetailCriteria;
import com.dugx.event.service.dto.BookingDetailDTO;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.dugx.event.domain.BookingDetail}.
 */
@RestController
@RequestMapping("/api/booking-details")
public class BookingDetailResource {

    private static final Logger LOG = LoggerFactory.getLogger(BookingDetailResource.class);

    private static final String ENTITY_NAME = "bookingDetail";

    @Value("${jhipster.clientApp.name:dugx}")
    private String applicationName;

    private final BookingDetailService bookingDetailService;

    private final BookingDetailRepository bookingDetailRepository;

    private final BookingDetailQueryService bookingDetailQueryService;

    public BookingDetailResource(
        BookingDetailService bookingDetailService,
        BookingDetailRepository bookingDetailRepository,
        BookingDetailQueryService bookingDetailQueryService
    ) {
        this.bookingDetailService = bookingDetailService;
        this.bookingDetailRepository = bookingDetailRepository;
        this.bookingDetailQueryService = bookingDetailQueryService;
    }

    /**
     * {@code POST  /booking-details} : Create a new bookingDetail.
     *
     * @param bookingDetailDTO the bookingDetailDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookingDetailDTO, or with status {@code 400 (Bad Request)} if the bookingDetail has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BookingDetailDTO> createBookingDetail(@RequestBody BookingDetailDTO bookingDetailDTO) throws URISyntaxException {
        LOG.debug("REST request to save BookingDetail : {}", bookingDetailDTO);
        if (bookingDetailDTO.getId() != null) {
            throw new BadRequestAlertException("A new bookingDetail cannot already have an ID", ENTITY_NAME, "idexists");
        }
        bookingDetailDTO = bookingDetailService.save(bookingDetailDTO);
        return ResponseEntity.created(new URI("/api/booking-details/" + bookingDetailDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, bookingDetailDTO.getId().toString()))
            .body(bookingDetailDTO);
    }

    /**
     * {@code PUT  /booking-details/:id} : Updates an existing bookingDetail.
     *
     * @param id the id of the bookingDetailDTO to save.
     * @param bookingDetailDTO the bookingDetailDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookingDetailDTO,
     * or with status {@code 400 (Bad Request)} if the bookingDetailDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookingDetailDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookingDetailDTO> updateBookingDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BookingDetailDTO bookingDetailDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update BookingDetail : {}, {}", id, bookingDetailDTO);
        if (bookingDetailDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookingDetailDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookingDetailRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        bookingDetailDTO = bookingDetailService.update(bookingDetailDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookingDetailDTO.getId().toString()))
            .body(bookingDetailDTO);
    }

    /**
     * {@code PATCH  /booking-details/:id} : Partial updates given fields of an existing bookingDetail, field will ignore if it is null
     *
     * @param id the id of the bookingDetailDTO to save.
     * @param bookingDetailDTO the bookingDetailDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookingDetailDTO,
     * or with status {@code 400 (Bad Request)} if the bookingDetailDTO is not valid,
     * or with status {@code 404 (Not Found)} if the bookingDetailDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookingDetailDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BookingDetailDTO> partialUpdateBookingDetail(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BookingDetailDTO bookingDetailDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BookingDetail partially : {}, {}", id, bookingDetailDTO);
        if (bookingDetailDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookingDetailDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookingDetailRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookingDetailDTO> result = bookingDetailService.partialUpdate(bookingDetailDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookingDetailDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /booking-details} : get all the Booking Details.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Booking Details in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BookingDetailDTO>> getAllBookingDetails(
        BookingDetailCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get BookingDetails by criteria: {}", criteria);

        Page<BookingDetailDTO> page = bookingDetailQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /booking-details/count} : count all the bookingDetails.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBookingDetails(BookingDetailCriteria criteria) {
        LOG.debug("REST request to count BookingDetails by criteria: {}", criteria);
        return ResponseEntity.ok().body(bookingDetailQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /booking-details/:id} : get the "id" bookingDetail.
     *
     * @param id the id of the bookingDetailDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookingDetailDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingDetailDTO> getBookingDetail(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BookingDetail : {}", id);
        Optional<BookingDetailDTO> bookingDetailDTO = bookingDetailService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookingDetailDTO);
    }

    /**
     * {@code DELETE  /booking-details/:id} : delete the "id" bookingDetail.
     *
     * @param id the id of the bookingDetailDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookingDetail(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BookingDetail : {}", id);
        bookingDetailService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
