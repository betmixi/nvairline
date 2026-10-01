package com.dugx.event.repository;

import com.dugx.event.domain.SeatType;
import com.dugx.event.domain.ShowtimeSeat;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ShowtimeSeat entity.
 */
@Repository
public interface ShowtimeSeatRepository extends JpaRepository<ShowtimeSeat, Long> {
    @Query("select ss from ShowtimeSeat ss left join fetch ss.seat where ss.showtime.id = :showtimeId")
    List<ShowtimeSeat> findByShowtime_Id(@Param("showtimeId") Long showtimeId);

    List<ShowtimeSeat> findBySeat_Aircraft_Id(Long aircraftId);

    void deleteByShowtime_Id(Long showtimeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ss from ShowtimeSeat ss where ss.id in :ids")
    List<ShowtimeSeat> findAllByIdInForUpdate(@Param("ids") List<Long> ids);

    /** So ghe con trong theo hang ghe cua mot suat bay (dung de hien thi tuy chon nang hang). */
    @Query(
        "select count(ss) from ShowtimeSeat ss where ss.showtime.id = :showtimeId and ss.seat.seatType = :seatType and ss.status = com.dugx.event.domain.SeatStatus.AVAILABLE"
    )
    long countAvailableByShowtimeAndSeatType(@Param("showtimeId") Long showtimeId, @Param("seatType") SeatType seatType);

    /** Khoa mot ghe con trong theo hang de giu cho khi nang hang, tranh trung voi nguoi khac. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        "select ss from ShowtimeSeat ss where ss.showtime.id = :showtimeId and ss.seat.seatType = :seatType and ss.status = com.dugx.event.domain.SeatStatus.AVAILABLE order by ss.id"
    )
    List<ShowtimeSeat> findAvailableByShowtimeAndSeatTypeForUpdate(
        @Param("showtimeId") Long showtimeId,
        @Param("seatType") SeatType seatType
    );

    default Optional<ShowtimeSeat> findFirstAvailableByShowtimeAndSeatTypeForUpdate(Long showtimeId, SeatType seatType) {
        List<ShowtimeSeat> seats = findAvailableByShowtimeAndSeatTypeForUpdate(showtimeId, seatType);
        return seats.isEmpty() ? Optional.empty() : Optional.of(seats.get(0));
    }
}
