package com.dugx.event.repository;

import com.dugx.event.domain.Organizer;
import com.dugx.event.domain.OrganizerStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Organizer entity.
 */
@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, Long>, JpaSpecificationExecutor<Organizer> {
    long countByStatus(OrganizerStatus status);

    @Query(
        """
        select o
        from Organizer o
        where o.status = :status
        """
    )
    List<Organizer> findPending(@Param("status") OrganizerStatus status);

    default Optional<Organizer> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Organizer> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Organizer> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select organizer from Organizer organizer left join fetch organizer.user",
        countQuery = "select count(organizer) from Organizer organizer"
    )
    Page<Organizer> findAllWithToOneRelationships(Pageable pageable);

    @Query("select organizer from Organizer organizer left join fetch organizer.user")
    List<Organizer> findAllWithToOneRelationships();

    @Query("select organizer from Organizer organizer left join fetch organizer.user where organizer.id =:id")
    Optional<Organizer> findOneWithToOneRelationships(@Param("id") Long id);

    Optional<Organizer> findByUserLogin(String login);
}
