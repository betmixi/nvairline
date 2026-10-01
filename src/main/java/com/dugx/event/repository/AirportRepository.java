package com.dugx.event.repository;

import com.dugx.event.domain.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Airport entity.
 */
@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
}
