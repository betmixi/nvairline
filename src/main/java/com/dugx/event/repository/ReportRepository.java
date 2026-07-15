package com.dugx.event.repository;

import com.dugx.event.domain.Report;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Report entity.
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {
    @Query("select report from Report report where report.user.login = ?#{authentication.name}")
    List<Report> findByUserIsCurrentUser();

    default Optional<Report> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Report> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Report> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select report from Report report left join fetch report.user left join fetch report.event",
        countQuery = "select count(report) from Report report"
    )
    Page<Report> findAllWithToOneRelationships(Pageable pageable);

    @Query("select report from Report report left join fetch report.user left join fetch report.event")
    List<Report> findAllWithToOneRelationships();

    @Query("select report from Report report left join fetch report.user left join fetch report.event where report.id =:id")
    Optional<Report> findOneWithToOneRelationships(@Param("id") Long id);
}
