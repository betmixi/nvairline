package com.dugx.event.web.rest;

import com.dugx.event.service.AircraftService;
import com.dugx.event.service.dto.AircraftDTO;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
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
 * REST controller for managing {@link com.dugx.event.domain.Aircraft}.
 */
@RestController
@RequestMapping("/api/aircraft")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AircraftResource {

    private static final Logger LOG = LoggerFactory.getLogger(AircraftResource.class);

    private static final String ENTITY_NAME = "aircraft";

    @Value("${jhipster.clientApp.name:cgv}")
    private String applicationName;

    private final AircraftService aircraftService;

    public AircraftResource(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    @PostMapping("")
    public ResponseEntity<AircraftDTO> createAircraft(@Valid @RequestBody AircraftDTO aircraftDTO) throws URISyntaxException {
        LOG.debug("REST request to save Aircraft : {}", aircraftDTO);
        if (aircraftDTO.getId() != null) {
            throw new BadRequestAlertException("A new aircraft cannot already have an ID", ENTITY_NAME, "idexists");
        }
        aircraftDTO = aircraftService.save(aircraftDTO);
        return ResponseEntity.created(new URI("/api/aircraft/" + aircraftDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, aircraftDTO.getId().toString()))
            .body(aircraftDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AircraftDTO> updateAircraft(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AircraftDTO aircraftDTO
    ) {
        LOG.debug("REST request to update Aircraft : {}, {}", id, aircraftDTO);
        if (aircraftDTO.getId() == null || !id.equals(aircraftDTO.getId())) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idinvalid");
        }
        aircraftDTO = aircraftService.update(aircraftDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, aircraftDTO.getId().toString()))
            .body(aircraftDTO);
    }

    @GetMapping("")
    public ResponseEntity<List<AircraftDTO>> getAllAircraft(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        Page<AircraftDTO> page = aircraftService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftDTO> getAircraft(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Aircraft : {}", id);
        Optional<AircraftDTO> aircraftDTO = aircraftService.findOne(id);
        return ResponseUtil.wrapOrNotFound(aircraftDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAircraft(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Aircraft : {}", id);
        aircraftService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
