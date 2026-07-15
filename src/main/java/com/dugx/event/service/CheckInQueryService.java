package com.dugx.event.service;

import com.dugx.event.domain.*; // for static metamodels
import com.dugx.event.domain.CheckIn;
import com.dugx.event.repository.CheckInRepository;
import com.dugx.event.service.criteria.CheckInCriteria;
import com.dugx.event.service.dto.CheckInDTO;
import com.dugx.event.service.mapper.CheckInMapper;
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
 * Service for executing complex queries for {@link CheckIn} entities in the database.
 * The main input is a {@link CheckInCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CheckInDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CheckInQueryService extends QueryService<CheckIn> {

    private static final Logger LOG = LoggerFactory.getLogger(CheckInQueryService.class);

    private final CheckInRepository checkInRepository;

    private final CheckInMapper checkInMapper;

    public CheckInQueryService(CheckInRepository checkInRepository, CheckInMapper checkInMapper) {
        this.checkInRepository = checkInRepository;
        this.checkInMapper = checkInMapper;
    }

    /**
     * Return a {@link Page} of {@link CheckInDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CheckInDTO> findByCriteria(CheckInCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CheckIn> specification = createSpecification(criteria);
        return checkInRepository.findAll(specification, page).map(checkInMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CheckInCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CheckIn> specification = createSpecification(criteria);
        return checkInRepository.count(specification);
    }

    /**
     * Function to convert {@link CheckInCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CheckIn> createSpecification(CheckInCriteria criteria) {
        Specification<CheckIn> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(CheckIn_.ticket, JoinType.LEFT);
                root.fetch(CheckIn_.checkedBy, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), CheckIn_.id),
                    buildRangeSpecification(criteria.getCheckInTime(), CheckIn_.checkInTime),
                    buildSpecification(criteria.getTicketId(), root -> root.join(CheckIn_.ticket, JoinType.LEFT).get(Ticket_.id)),
                    buildSpecification(criteria.getCheckedById(), root -> root.join(CheckIn_.checkedBy, JoinType.LEFT).get(User_.id))
                )
            );
        }
        return specification;
    }
}
