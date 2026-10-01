package com.dugx.event.repository;

import com.dugx.event.domain.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Aircraft entity.
 */
@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {}
