package com.dugx.event.repository;

import com.dugx.event.domain.Venue;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Venue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
