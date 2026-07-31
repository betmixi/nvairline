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
        value = "select event from Event event left join fetch event.category left join fetch event.address left join fetch event.organizer",
        countQuery = "select count(event) from Event event"
    )
    Page<Event> findAllWithToOneRelationships(Pageable pageable);

    @Query("select event from Event event left join fetch event.category left join fetch event.address left join fetch event.organizer")
    List<Event> findAllWithToOneRelationships();

    @Query(
        "select event from Event event left join fetch event.category left join fetch event.address left join fetch event.organizer where event.id =:id"
    )
    Optional<Event> findOneWithToOneRelationships(@Param("id") Long id);

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

    Long countByOrganizerUserLogin(String login);

    Long countByOrganizerUserLoginAndStatusTrue(String login);

    @Query(
        """
        select e
        from Event e
        order by e.createdDate desc
        """
    )
    Page<Event> findRecentEvents(Pageable pageable);

    @Query(
        """
        select e
        from Event e
        left join fetch e.category
        left join fetch e.address
        left join fetch e.organizer
        where e.organizer.user.login = :login
        order by e.createdDate desc
        """
    )
    Page<Event> findByOrganizerLogin(@Param("login") String login, Pageable pageable);

    @Query(
        """
        select e
        from Event e
        left join fetch e.category
        left join fetch e.address
        left join fetch e.organizer
        where e.organizer.user.login = :login
        """
    )
    List<Event> findAllByOrganizerLogin(@Param("login") String login);

    @Query(
        """
        select e
        from Event e
        left join fetch e.category
        left join fetch e.address
        left join fetch e.organizer
        where e.organizer.user.login = :login
        order by e.createdDate desc
        """
    )
    Page<Event> findMyEvents(@Param("login") String login, Pageable pageable);

    @Query(
        """
        select min(t.price)
        from TicketType t
        where t.event.id = :eventId
        """
    )
    BigDecimal findMinPrice(@Param("eventId") Long eventId);

    @Query(
        """
        select coalesce(sum(b.quantity),0)
        from BookingDetail b
        where b.ticketType.event.id = :eventId
        """
    )
    Long findTicketsSold(@Param("eventId") Long eventId);

    @Query(
        """
        select e
        from Event e
        left join fetch e.category
        left join fetch e.address
        left join fetch e.organizer
        where e.id = :id
        and e.organizer.user.login = :login
        """
    )
    Optional<Event> findMyEventById(@Param("id") Long id, @Param("login") String login);

    @Query(
        """
        select e
        from Event e
        left join fetch e.category c
        left join fetch e.address a
        left join fetch e.organizer o
        left join TicketType tt on tt.event = e
        left join BookingDetail bd on bd.ticketType = tt
        where e.organizer.user.login = :login
        group by e, c, a, o
        order by coalesce(sum(bd.quantity),0) desc
        """
    )
    List<Event> findTop3BestSellingEvents(@Param("login") String login, Pageable pageable);

    @Query(
        value = """
        select name
        from ticket_type
        where event_id = :eventId
        order by price
        limit 1
        """,
        nativeQuery = true
    )
    String findTicketTypeName(@Param("eventId") Long eventId);
}
