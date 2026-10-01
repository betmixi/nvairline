package com.dugx.event.repository;

import com.dugx.event.domain.CheckIn;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CheckIn entity.
 */
@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long>, JpaSpecificationExecutor<CheckIn> {
    @Query("select checkIn from CheckIn checkIn where checkIn.checkedBy.login = ?#{authentication.name}")
    List<CheckIn> findByCheckedByIsCurrentUser();

    default Optional<CheckIn> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CheckIn> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CheckIn> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select checkIn from CheckIn checkIn left join fetch checkIn.checkedBy",
        countQuery = "select count(checkIn) from CheckIn checkIn"
    )
    Page<CheckIn> findAllWithToOneRelationships(Pageable pageable);

    @Query("select checkIn from CheckIn checkIn left join fetch checkIn.checkedBy")
    List<CheckIn> findAllWithToOneRelationships();

    @Query("select checkIn from CheckIn checkIn left join fetch checkIn.checkedBy where checkIn.id =:id")
    Optional<CheckIn> findOneWithToOneRelationships(@Param("id") Long id);
}
