package com.dugx.event.service;

import com.dugx.event.domain.*; // for static metamodels
import com.dugx.event.domain.TicketType;
import com.dugx.event.repository.TicketTypeRepository;
import com.dugx.event.service.criteria.TicketTypeCriteria;
import com.dugx.event.service.dto.TicketTypeDTO;
import com.dugx.event.service.mapper.TicketTypeMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TicketType} entities in the database.
 * The main input is a {@link TicketTypeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TicketTypeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TicketTypeQueryService extends QueryService<TicketType> {

    private static final Logger LOG = LoggerFactory.getLogger(TicketTypeQueryService.class);

    private final TicketTypeRepository ticketTypeRepository;

    private final TicketTypeMapper ticketTypeMapper;

    public TicketTypeQueryService(TicketTypeRepository ticketTypeRepository, TicketTypeMapper ticketTypeMapper) {
        this.ticketTypeRepository = ticketTypeRepository;
        this.ticketTypeMapper = ticketTypeMapper;
    }

    /**
     * Return a {@link Page} of {@link TicketTypeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TicketTypeDTO> findByCriteria(TicketTypeCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TicketType> specification = createSpecification(criteria);
        return ticketTypeRepository.findAll(specification, page).map(ticketTypeMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<TicketTypeDTO> findByCriteria(TicketTypeCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);

        final Specification<TicketType> specification = createSpecification(criteria);

        return ticketTypeRepository.findAll(specification).stream().map(ticketTypeMapper::toDto).toList();
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TicketTypeCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TicketType> specification = createSpecification(criteria);
        return ticketTypeRepository.count(specification);
    }

    /**
     * Function to convert {@link TicketTypeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TicketType> createSpecification(TicketTypeCriteria criteria) {
        Specification<TicketType> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(TicketType_.event, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), TicketType_.id),
                    buildStringSpecification(criteria.getName(), TicketType_.name),
                    buildRangeSpecification(criteria.getPrice(), TicketType_.price),
                    buildRangeSpecification(criteria.getQuantity(), TicketType_.quantity),
                    buildRangeSpecification(criteria.getRemaining(), TicketType_.remaining),
                    buildRangeSpecification(criteria.getSaleStart(), TicketType_.saleStart),
                    buildRangeSpecification(criteria.getSaleEnd(), TicketType_.saleEnd),
                    buildSpecification(criteria.getEventId(), root -> root.join(TicketType_.event, JoinType.LEFT).get(Event_.id))
                )
            );
        }
        return specification;
    }
}
