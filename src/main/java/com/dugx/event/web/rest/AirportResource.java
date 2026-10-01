package com.dugx.event.web.rest;

import com.dugx.event.service.AirportService;
import com.dugx.event.service.dto.AirportDTO;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.dugx.event.domain.Airport}.
 */
@RestController
@RequestMapping("/api/airports")
public class AirportResource {

    private static final Logger LOG = LoggerFactory.getLogger(AirportResource.class);

    private static final String ENTITY_NAME = "airport";

    @Value("${jhipster.clientApp.name:vnairlines}")
    private String applicationName;

    private final AirportService airportService;

    public AirportResource(AirportService airportService) {
        this.airportService = airportService;
    }

    /** Cong khai: form tim chuyen bay can danh sach san bay de do dropdown di/den. */
    @GetMapping("")
    public ResponseEntity<List<AirportDTO>> getAllAirports() {
        return ResponseEntity.ok(airportService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirportDTO> getAirport(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Airport : {}", id);
        Optional<AirportDTO> airportDTO = airportService.findOne(id);
        return ResponseUtil.wrapOrNotFound(airportDTO);
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AirportDTO> createAirport(@Valid @RequestBody AirportDTO airportDTO) throws URISyntaxException {
        LOG.debug("REST request to save Airport : {}", airportDTO);
        if (airportDTO.getId() != null) {
            throw new BadRequestAlertException("A new airport cannot already have an ID", ENTITY_NAME, "idexists");
        }
        airportDTO = airportService.save(airportDTO);
        return ResponseEntity.created(new URI("/api/airports/" + airportDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, airportDTO.getId().toString()))
            .body(airportDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AirportDTO> updateAirport(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AirportDTO airportDTO
    ) {
        LOG.debug("REST request to update Airport : {}, {}", id, airportDTO);
        if (airportDTO.getId() == null || !id.equals(airportDTO.getId())) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idinvalid");
        }
        airportDTO = airportService.update(airportDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, airportDTO.getId().toString()))
            .body(airportDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAirport(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Airport : {}", id);
        airportService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
