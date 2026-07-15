package com.dugx.event.service;

import com.dugx.event.domain.*; // for static metamodels
import com.dugx.event.domain.EventImage;
import com.dugx.event.repository.EventImageRepository;
import com.dugx.event.service.criteria.EventImageCriteria;
import com.dugx.event.service.dto.EventImageDTO;
import com.dugx.event.service.mapper.EventImageMapper;
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
 * Service for executing complex queries for {@link EventImage} entities in the database.
 * The main input is a {@link EventImageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link EventImageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EventImageQueryService extends QueryService<EventImage> {

    private static final Logger LOG = LoggerFactory.getLogger(EventImageQueryService.class);

    private final EventImageRepository eventImageRepository;

    private final EventImageMapper eventImageMapper;

    public EventImageQueryService(EventImageRepository eventImageRepository, EventImageMapper eventImageMapper) {
        this.eventImageRepository = eventImageRepository;
        this.eventImageMapper = eventImageMapper;
    }

    /**
     * Return a {@link Page} of {@link EventImageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EventImageDTO> findByCriteria(EventImageCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<EventImage> specification = createSpecification(criteria);
        return eventImageRepository.findAll(specification, page).map(eventImageMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EventImageCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<EventImage> specification = createSpecification(criteria);
        return eventImageRepository.count(specification);
    }

    /**
     * Function to convert {@link EventImageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<EventImage> createSpecification(EventImageCriteria criteria) {
        Specification<EventImage> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(EventImage_.event, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), EventImage_.id),
                    buildStringSpecification(criteria.getImageUrl(), EventImage_.imageUrl),
                    buildSpecification(criteria.getEventId(), root -> root.join(EventImage_.event, JoinType.LEFT).get(Event_.id))
                )
            );
        }
        return specification;
    }
}
