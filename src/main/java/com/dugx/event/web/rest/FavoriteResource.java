package com.dugx.event.web.rest;

import com.dugx.event.repository.FavoriteRepository;
import com.dugx.event.service.FavoriteQueryService;
import com.dugx.event.service.FavoriteService;
import com.dugx.event.service.criteria.FavoriteCriteria;
import com.dugx.event.service.dto.EventDTO;
import com.dugx.event.service.dto.FavoriteDTO;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
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
 * REST controller for managing {@link com.dugx.event.domain.Favorite}.
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteResource {

    private static final Logger LOG = LoggerFactory.getLogger(FavoriteResource.class);

    private static final String ENTITY_NAME = "favorite";

    @Value("${jhipster.clientApp.name:dugx}")
    private String applicationName;

    private final FavoriteService favoriteService;

    private final FavoriteRepository favoriteRepository;

    private final FavoriteQueryService favoriteQueryService;

    public FavoriteResource(
        FavoriteService favoriteService,
        FavoriteRepository favoriteRepository,
        FavoriteQueryService favoriteQueryService
    ) {
        this.favoriteService = favoriteService;
        this.favoriteRepository = favoriteRepository;
        this.favoriteQueryService = favoriteQueryService;
    }

    /**
     * {@code POST  /favorites} : Create a new favorite.
     *
     * @param favoriteDTO the favoriteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new favoriteDTO, or with status {@code 400 (Bad Request)} if the favorite has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<FavoriteDTO> createFavorite(@RequestBody FavoriteDTO favoriteDTO) throws URISyntaxException {
        LOG.debug("REST request to save Favorite : {}", favoriteDTO);
        if (favoriteDTO.getId() != null) {
            throw new BadRequestAlertException("A new favorite cannot already have an ID", ENTITY_NAME, "idexists");
        }
        favoriteDTO = favoriteService.save(favoriteDTO);
        return ResponseEntity.created(new URI("/api/favorites/" + favoriteDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, favoriteDTO.getId().toString()))
            .body(favoriteDTO);
    }

    /**
     * {@code PUT  /favorites/:id} : Updates an existing favorite.
     *
     * @param id the id of the favoriteDTO to save.
     * @param favoriteDTO the favoriteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated favoriteDTO,
     * or with status {@code 400 (Bad Request)} if the favoriteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the favoriteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<FavoriteDTO> updateFavorite(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FavoriteDTO favoriteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Favorite : {}, {}", id, favoriteDTO);
        if (favoriteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, favoriteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!favoriteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        favoriteDTO = favoriteService.update(favoriteDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, favoriteDTO.getId().toString()))
            .body(favoriteDTO);
    }

    /**
     * {@code PATCH  /favorites/:id} : Partial updates given fields of an existing favorite, field will ignore if it is null
     *
     * @param id the id of the favoriteDTO to save.
     * @param favoriteDTO the favoriteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated favoriteDTO,
     * or with status {@code 400 (Bad Request)} if the favoriteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the favoriteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the favoriteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<FavoriteDTO> partialUpdateFavorite(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FavoriteDTO favoriteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Favorite partially : {}, {}", id, favoriteDTO);
        if (favoriteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, favoriteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!favoriteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FavoriteDTO> result = favoriteService.partialUpdate(favoriteDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, favoriteDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /favorites} : get all the Favorites.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Favorites in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<FavoriteDTO>> getAllFavorites(
        FavoriteCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Favorites by criteria: {}", criteria);

        Page<FavoriteDTO> page = favoriteQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /favorites/count} : count all the favorites.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Long> countFavorites(FavoriteCriteria criteria) {
        LOG.debug("REST request to count Favorites by criteria: {}", criteria);
        return ResponseEntity.ok().body(favoriteQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /favorites/:id} : get the "id" favorite.
     *
     * @param id the id of the favoriteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the favoriteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<FavoriteDTO> getFavorite(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Favorite : {}", id);
        Optional<FavoriteDTO> favoriteDTO = favoriteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(favoriteDTO);
    }

    /**
     * {@code DELETE  /favorites/:id} : delete the "id" favorite.
     *
     * @param id the id of the favoriteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteFavorite(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Favorite : {}", id);
        favoriteService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /** Bat/tat yeu thich mot su kien cho nguoi dung dang dang nhap. */
    @PostMapping("/events/{eventId}/toggle")
    public ResponseEntity<Map<String, Boolean>> toggleFavorite(@PathVariable Long eventId) {
        boolean favorited = favoriteService.toggleFavorite(eventId);
        return ResponseEntity.ok(Map.of("favorited", favorited));
    }

    /** Kiem tra su kien co dang duoc nguoi dung hien tai yeu thich khong. */
    @GetMapping("/events/{eventId}")
    public ResponseEntity<Map<String, Boolean>> isFavorited(@PathVariable Long eventId) {
        return ResponseEntity.ok(Map.of("favorited", favoriteService.isFavorited(eventId)));
    }

    /** Danh sach su kien da luu cua nguoi dung dang dang nhap. */
    @GetMapping("/my")
    public ResponseEntity<List<EventDTO>> myFavorites() {
        return ResponseEntity.ok(favoriteService.getMyFavoriteEvents());
    }
}
