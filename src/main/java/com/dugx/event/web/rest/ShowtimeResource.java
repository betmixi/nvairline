package com.dugx.event.web.rest;

import com.dugx.event.service.ShowtimeService;
import com.dugx.event.service.dto.ShowtimeDTO;
import com.dugx.event.service.dto.ShowtimeSeatDTO;
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
 * REST controller for managing {@link com.dugx.event.domain.Showtime}.
 */
@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeResource {

    private static final Logger LOG = LoggerFactory.getLogger(ShowtimeResource.class);

    private static final String ENTITY_NAME = "showtime";

    @Value("${jhipster.clientApp.name:cgv}")
    private String applicationName;

    private final ShowtimeService showtimeService;

    public ShowtimeResource(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ShowtimeDTO> createShowtime(@Valid @RequestBody ShowtimeDTO showtimeDTO) throws URISyntaxException {
        LOG.debug("REST request to save Showtime : {}", showtimeDTO);
        if (showtimeDTO.getId() != null) {
            throw new BadRequestAlertException("A new showtime cannot already have an ID", ENTITY_NAME, "idexists");
        }
        showtimeDTO = showtimeService.save(showtimeDTO);
        return ResponseEntity.created(new URI("/api/showtimes/" + showtimeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, showtimeDTO.getId().toString()))
            .body(showtimeDTO);
    }

    /** GET /api/showtimes/event/{eventId} : danh sach suat chieu cong khai cua mot phim. */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<ShowtimeDTO>> getByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(showtimeService.findByEvent(eventId));
    }

    /** GET /api/showtimes/{id}/seats : so do ghe cong khai cua mot suat chieu. */
    @GetMapping("/{id}/seats")
    public ResponseEntity<List<ShowtimeSeatDTO>> getSeats(@PathVariable Long id) {
        return ResponseEntity.ok(showtimeService.getSeats(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowtimeDTO> getShowtime(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Showtime : {}", id);
        Optional<ShowtimeDTO> showtimeDTO = showtimeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(showtimeDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteShowtime(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Showtime : {}", id);
        showtimeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
