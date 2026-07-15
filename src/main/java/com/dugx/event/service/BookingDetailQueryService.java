package com.dugx.event.service;

import com.dugx.event.domain.*; // for static metamodels
import com.dugx.event.domain.BookingDetail;
import com.dugx.event.repository.BookingDetailRepository;
import com.dugx.event.service.criteria.BookingDetailCriteria;
import com.dugx.event.service.dto.BookingDetailDTO;
import com.dugx.event.service.mapper.BookingDetailMapper;
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
 * Service for executing complex queries for {@link BookingDetail} entities in the database.
 * The main input is a {@link BookingDetailCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BookingDetailDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookingDetailQueryService extends QueryService<BookingDetail> {

    private static final Logger LOG = LoggerFactory.getLogger(BookingDetailQueryService.class);

    private final BookingDetailRepository bookingDetailRepository;

    private final BookingDetailMapper bookingDetailMapper;

    public BookingDetailQueryService(BookingDetailRepository bookingDetailRepository, BookingDetailMapper bookingDetailMapper) {
        this.bookingDetailRepository = bookingDetailRepository;
        this.bookingDetailMapper = bookingDetailMapper;
    }

    /**
     * Return a {@link Page} of {@link BookingDetailDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BookingDetailDTO> findByCriteria(BookingDetailCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BookingDetail> specification = createSpecification(criteria);
        return bookingDetailRepository.findAll(specification, page).map(bookingDetailMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BookingDetailCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<BookingDetail> specification = createSpecification(criteria);
        return bookingDetailRepository.count(specification);
    }

    /**
     * Function to convert {@link BookingDetailCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BookingDetail> createSpecification(BookingDetailCriteria criteria) {
        Specification<BookingDetail> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(BookingDetail_.booking, JoinType.LEFT);
                root.fetch(BookingDetail_.ticketType, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), BookingDetail_.id),
                    buildRangeSpecification(criteria.getQuantity(), BookingDetail_.quantity),
                    buildRangeSpecification(criteria.getPrice(), BookingDetail_.price),
                    buildSpecification(criteria.getBookingId(), root -> root.join(BookingDetail_.booking, JoinType.LEFT).get(Booking_.id)),
                    buildSpecification(criteria.getTicketTypeId(), root ->
                        root.join(BookingDetail_.ticketType, JoinType.LEFT).get(TicketType_.id)
                    )
                )
            );
        }
        return specification;
    }
}
