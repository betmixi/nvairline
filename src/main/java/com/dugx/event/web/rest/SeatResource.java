package com.dugx.event.web.rest;

import com.dugx.event.service.SeatService;
import com.dugx.event.service.dto.SeatDTO;
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
 * REST controller for managing {@link com.dugx.event.domain.Seat}.
 */
@RestController
@RequestMapping("/api/seats")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class SeatResource {

    private static final Logger LOG = LoggerFactory.getLogger(SeatResource.class);

    private static final String ENTITY_NAME = "seat";

    @Value("${jhipster.clientApp.name:cgv}")
    private String applicationName;

    private final SeatService seatService;

    public SeatResource(SeatService seatService) {
        this.seatService = seatService;
    }

    @PostMapping("")
    public ResponseEntity<SeatDTO> createSeat(@Valid @RequestBody SeatDTO seatDTO) throws URISyntaxException {
        LOG.debug("REST request to save Seat : {}", seatDTO);
        if (seatDTO.getId() != null) {
            throw new BadRequestAlertException("A new seat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        seatDTO = seatService.save(seatDTO);
        return ResponseEntity.created(new URI("/api/seats/" + seatDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, seatDTO.getId().toString()))
            .body(seatDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeatDTO> updateSeat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SeatDTO seatDTO
    ) {
        LOG.debug("REST request to update Seat : {}, {}", id, seatDTO);
        if (seatDTO.getId() == null || !id.equals(seatDTO.getId())) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idinvalid");
        }
        seatDTO = seatService.update(seatDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, seatDTO.getId().toString()))
            .body(seatDTO);
    }

    @GetMapping("")
    public ResponseEntity<List<SeatDTO>> getAllSeats(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        Page<SeatDTO> page = seatService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/aircraft/{aircraftId}")
    public ResponseEntity<List<SeatDTO>> getByAircraft(@PathVariable Long aircraftId) {
        return ResponseEntity.ok(seatService.findByAircraft(aircraftId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatDTO> getSeat(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Seat : {}", id);
        Optional<SeatDTO> seatDTO = seatService.findOne(id);
        return ResponseUtil.wrapOrNotFound(seatDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeat(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Seat : {}", id);
        seatService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
