package com.dugx.event.repository;

import com.dugx.event.domain.CustomerProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CustomerProfile entity.
 */
@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {
    Optional<CustomerProfile> findByUser_Id(Long userId);
}
