package com.dugx.event.repository;

import com.dugx.event.domain.SupportRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SupportRequest entity.
 */
@Repository
public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {
    @Query(
        """
        select s
        from SupportRequest s
        left join fetch s.user
        where s.user.login = :login
        order by s.createdDate desc
        """
    )
    List<SupportRequest> findByUserLoginOrderByCreatedDateDesc(@Param("login") String login);

    @Query(
        """
        select s
        from SupportRequest s
        left join fetch s.user
        order by s.createdDate desc
        """
    )
    List<SupportRequest> findAllForAdmin();
}
