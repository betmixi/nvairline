package com.dugx.event.service;

import com.dugx.event.domain.*; // for static metamodels
import com.dugx.event.domain.Favorite;
import com.dugx.event.repository.FavoriteRepository;
import com.dugx.event.service.criteria.FavoriteCriteria;
import com.dugx.event.service.dto.FavoriteDTO;
import com.dugx.event.service.mapper.FavoriteMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Favorite} entities in the database.
 * The main input is a {@link FavoriteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link FavoriteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FavoriteQueryService extends QueryService<Favorite> {

    private static final Logger LOG = LoggerFactory.getLogger(FavoriteQueryService.class);

    private final FavoriteRepository favoriteRepository;

    private final FavoriteMapper favoriteMapper;

    public FavoriteQueryService(FavoriteRepository favoriteRepository, FavoriteMapper favoriteMapper) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteMapper = favoriteMapper;
    }

    /**
     * Return a {@link Page} of {@link FavoriteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FavoriteDTO> findByCriteria(FavoriteCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Favorite> specification = createSpecification(criteria);
        return favoriteRepository.findAll(specification, page).map(favoriteMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FavoriteCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Favorite> specification = createSpecification(criteria);
        return favoriteRepository.count(specification);
    }

    /**
     * Function to convert {@link FavoriteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Favorite> createSpecification(FavoriteCriteria criteria) {
        Specification<Favorite> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Favorite_.user, JoinType.LEFT);
                root.fetch(Favorite_.event, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Favorite_.id),
                    buildSpecification(criteria.getUserId(), root -> root.join(Favorite_.user, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getEventId(), root -> root.join(Favorite_.event, JoinType.LEFT).get(Event_.id))
                )
            );
        }
        return specification;
    }
}
