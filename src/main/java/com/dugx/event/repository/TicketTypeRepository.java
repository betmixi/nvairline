package com.dugx.event.repository;

import com.dugx.event.domain.TicketType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TicketType entity.
 */
@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, Long>, JpaSpecificationExecutor<TicketType> {
    default Optional<TicketType> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TicketType> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TicketType> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select ticketType from TicketType ticketType left join fetch ticketType.event",
        countQuery = "select count(ticketType) from TicketType ticketType"
    )
    Page<TicketType> findAllWithToOneRelationships(Pageable pageable);

    @Query("select ticketType from TicketType ticketType left join fetch ticketType.event")
    List<TicketType> findAllWithToOneRelationships();

    @Query("select ticketType from TicketType ticketType left join fetch ticketType.event where ticketType.id =:id")
    Optional<TicketType> findOneWithToOneRelationships(@Param("id") Long id);

    @Modifying
    @Query(
        """
        delete from TicketType t
        where t.event.id = :eventId
        """
    )
    void deleteAllByEventId(@Param("eventId") Long eventId);

    List<TicketType> findByEventId(Long eventId);
}
