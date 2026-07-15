package com.dugx.event.repository;

import com.dugx.event.domain.Event;
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
}
