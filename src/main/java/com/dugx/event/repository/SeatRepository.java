package com.dugx.event.repository;

import com.dugx.event.domain.Seat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Seat entity.
 */
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByAircraft_Id(Long aircraftId);

    boolean existsByAircraft_IdAndRowLabelIgnoreCaseAndSeatNumber(Long aircraftId, String rowLabel, Integer seatNumber);

    boolean existsByAircraft_IdAndRowLabelIgnoreCaseAndSeatNumberAndIdNot(Long aircraftId, String rowLabel, Integer seatNumber, Long id);
}
