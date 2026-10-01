package com.dugx.event.repository;

import com.dugx.event.domain.Event;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Event entity.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    default Optional<Event> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Event> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Event> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select event from Event event left join fetch event.category left join fetch event.address left join fetch event.departureAirport left join fetch event.arrivalAirport",
        countQuery = "select count(event) from Event event"
    )
    Page<Event> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select event from Event event left join fetch event.category left join fetch event.address left join fetch event.departureAirport left join fetch event.arrivalAirport"
    )
    List<Event> findAllWithToOneRelationships();

    @Query(
        "select event from Event event left join fetch event.category left join fetch event.address left join fetch event.departureAirport left join fetch event.arrivalAirport where event.id =:id"
    )
    Optional<Event> findOneWithToOneRelationships(@Param("id") Long id);

    /** Kiem tra trung so hieu chuyen bay (khong phan biet hoa thuong). */
    boolean existsByTitleIgnoreCase(String title);

    boolean existsByTitleIgnoreCaseAndIdNot(String title, Long id);

    // Tìm kiếm theo từ khóa
    @Query(
        """
            SELECT e
            FROM Event e
            WHERE LOWER(e.title)
                  LIKE LOWER(CONCAT('%', :keyword, '%'))
        """
    )
    Page<Event> search(@Param("keyword") String keyword, Pageable pageable);

    @Query(
        """
        select e
        from Event e
        left join fetch e.category
        left join fetch e.address
        order by e.createdDate desc
        """
    )
    Page<Event> findRecentEvents(Pageable pageable);

    @Query(
        """
        select min(s.basePrice)
        from Showtime s
        where s.event.id = :eventId
        """
    )
    BigDecimal findMinPrice(@Param("eventId") Long eventId);

    @Query(
        """
        select count(b)
        from BookingDetail b
        where b.showtimeSeat.showtime.event.id = :eventId
        """
    )
    Long findTicketsSold(@Param("eventId") Long eventId);
}
