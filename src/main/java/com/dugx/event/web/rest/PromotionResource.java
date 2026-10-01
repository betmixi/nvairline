package com.dugx.event.web.rest;

import com.dugx.event.service.PromotionService;
import com.dugx.event.service.dto.PromotionDTO;
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
 * REST controller for managing {@link com.dugx.event.domain.Promotion}.
 */
@RestController
@RequestMapping("/api/promotions")
public class PromotionResource {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionResource.class);

    private static final String ENTITY_NAME = "promotion";

    @Value("${jhipster.clientApp.name:vnairlines}")
    private String applicationName;

    private final PromotionService promotionService;

    public PromotionResource(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    /** Cong khai: cac uu dai dang active, dung de hien trong flyout "Kham Pha" o trang chu. */
    @GetMapping("")
    public ResponseEntity<List<PromotionDTO>> getAllPromotions(
        @RequestParam(name = "activeOnly", required = false, defaultValue = "false") boolean activeOnly
    ) {
        List<PromotionDTO> promotions = activeOnly ? promotionService.findAllActive() : promotionService.findAll();
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionDTO> getPromotion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Promotion : {}", id);
        Optional<PromotionDTO> promotionDTO = promotionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(promotionDTO);
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PromotionDTO> createPromotion(@Valid @RequestBody PromotionDTO promotionDTO) throws URISyntaxException {
        LOG.debug("REST request to save Promotion : {}", promotionDTO);
        if (promotionDTO.getId() != null) {
            throw new BadRequestAlertException("A new promotion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        promotionDTO = promotionService.save(promotionDTO);
        return ResponseEntity.created(new URI("/api/promotions/" + promotionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, promotionDTO.getId().toString()))
            .body(promotionDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PromotionDTO> updatePromotion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PromotionDTO promotionDTO
    ) {
        LOG.debug("REST request to update Promotion : {}, {}", id, promotionDTO);
        if (promotionDTO.getId() == null || !id.equals(promotionDTO.getId())) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idinvalid");
        }
        promotionDTO = promotionService.update(promotionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, promotionDTO.getId().toString()))
            .body(promotionDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deletePromotion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Promotion : {}", id);
        promotionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
