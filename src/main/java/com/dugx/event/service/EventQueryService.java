package com.dugx.event.service;

import com.dugx.event.domain.*; // for static metamodels
import com.dugx.event.domain.Event;
import com.dugx.event.repository.EventRepository;
import com.dugx.event.repository.TicketTypeRepository;
import com.dugx.event.service.criteria.EventCriteria;
import com.dugx.event.service.dto.EventDTO;
import com.dugx.event.service.mapper.EventMapper;
import com.dugx.event.service.mapper.TicketTypeMapper;
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
 * Service for executing complex queries for {@link Event} entities in the database.
 * The main input is a {@link EventCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link EventDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EventQueryService extends QueryService<Event> {

    private static final Logger LOG = LoggerFactory.getLogger(EventQueryService.class);

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketTypeMapper ticketTypeMapper;

    public EventQueryService(
        EventRepository eventRepository,
        EventMapper eventMapper,
        TicketTypeRepository ticketTypeRepository,
        TicketTypeMapper ticketTypeMapper
    ) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.ticketTypeRepository = ticketTypeRepository;
        this.ticketTypeMapper = ticketTypeMapper;
    }

    /**
     * Return a {@link Page} of {@link EventDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EventDTO> findByCriteria(EventCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Event> specification = createSpecification(criteria);
        return eventRepository.findAll(specification, page).map(event -> {
            EventDTO dto = eventMapper.toDto(event);

            dto.setTicketTypes(ticketTypeRepository.findByEventId(event.getId()).stream().map(ticketTypeMapper::toDto).toList());

            return dto;
        });
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EventCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Event> specification = createSpecification(criteria);
        return eventRepository.count(specification);
    }

    /**
     * Function to convert {@link EventCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Event> createSpecification(EventCriteria criteria) {
        Specification<Event> specification = Specification.unrestricted();
        //        specification = specification.and((root, query, builder) -> {
        //            if (Long.class != query.getResultType()) {
        //                root.fetch(Event_.category, JoinType.LEFT);
        //                root.fetch(Event_.address, JoinType.LEFT);
        //                root.fetch(Event_.organizer, JoinType.LEFT);
        //            }
        //            return null;
        //        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Event_.id),
                    buildStringSpecification(criteria.getTitle(), Event_.title),
                    buildStringSpecification(criteria.getBanner(), Event_.banner),
                    buildRangeSpecification(criteria.getStartTime(), Event_.startTime),
                    buildRangeSpecification(criteria.getEndTime(), Event_.endTime),
                    buildSpecification(criteria.getStatus(), Event_.status),
                    buildRangeSpecification(criteria.getCreatedDate(), Event_.createdDate),
                    buildSpecification(criteria.getCategoryId(), root -> root.join(Event_.category, JoinType.LEFT).get(Category_.id)),
                    buildSpecification(criteria.getAddressId(), root -> root.join(Event_.address, JoinType.LEFT).get(Address_.id)),
                    buildSpecification(criteria.getOrganizerId(), root -> root.join(Event_.organizer, JoinType.LEFT).get(Organizer_.id))
                )
            );
        }
        return specification;
    }
}
