package com.dugx.event.service;

import com.dugx.event.domain.*; // for static metamodels
import com.dugx.event.domain.Organizer;
import com.dugx.event.repository.OrganizerRepository;
import com.dugx.event.service.criteria.OrganizerCriteria;
import com.dugx.event.service.dto.OrganizerDTO;
import com.dugx.event.service.mapper.OrganizerMapper;
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
 * Service for executing complex queries for {@link Organizer} entities in the database.
 * The main input is a {@link OrganizerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link OrganizerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrganizerQueryService extends QueryService<Organizer> {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizerQueryService.class);

    private final OrganizerRepository organizerRepository;

    private final OrganizerMapper organizerMapper;

    public OrganizerQueryService(OrganizerRepository organizerRepository, OrganizerMapper organizerMapper) {
        this.organizerRepository = organizerRepository;
        this.organizerMapper = organizerMapper;
    }

    /**
     * Return a {@link Page} of {@link OrganizerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OrganizerDTO> findByCriteria(OrganizerCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Organizer> specification = createSpecification(criteria);
        return organizerRepository.findAll(specification, page).map(organizerMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrganizerCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Organizer> specification = createSpecification(criteria);
        return organizerRepository.count(specification);
    }

    /**
     * Function to convert {@link OrganizerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Organizer> createSpecification(OrganizerCriteria criteria) {
        Specification<Organizer> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Organizer_.user, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Organizer_.id),
                    buildStringSpecification(criteria.getCompanyName(), Organizer_.companyName),
                    buildStringSpecification(criteria.getTaxCode(), Organizer_.taxCode),
                    buildSpecification(criteria.getVerified(), Organizer_.verified),
                    buildSpecification(criteria.getStatus(), Organizer_.status),
                    buildSpecification(criteria.getUserId(), root -> root.join(Organizer_.user, JoinType.LEFT).get(User_.id))
                )
            );
        }
        return specification;
    }
}
