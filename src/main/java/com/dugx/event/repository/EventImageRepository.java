package com.dugx.event.repository;

import com.dugx.event.domain.EventImage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EventImage entity.
 */
@Repository
public interface EventImageRepository extends JpaRepository<EventImage, Long>, JpaSpecificationExecutor<EventImage> {
    default Optional<EventImage> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<EventImage> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<EventImage> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select eventImage from EventImage eventImage left join fetch eventImage.event",
        countQuery = "select count(eventImage) from EventImage eventImage"
    )
    Page<EventImage> findAllWithToOneRelationships(Pageable pageable);

    @Query("select eventImage from EventImage eventImage left join fetch eventImage.event")
    List<EventImage> findAllWithToOneRelationships();

    @Query("select eventImage from EventImage eventImage left join fetch eventImage.event where eventImage.id =:id")
    Optional<EventImage> findOneWithToOneRelationships(@Param("id") Long id);
}
