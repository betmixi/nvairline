package com.dugx.event.repository;

import com.dugx.event.domain.PointsHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PointsHistory entity.
 */
@Repository
public interface PointsHistoryRepository extends JpaRepository<PointsHistory, Long> {
    @Query("select p from PointsHistory p where p.user.login = :login order by p.createdDate desc")
    Page<PointsHistory> findByUserLogin(@Param("login") String login, Pageable pageable);
}
