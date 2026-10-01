package com.dugx.event.repository;

import com.dugx.event.domain.Showtime;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Showtime entity.
 */
@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    @Query("select s from Showtime s left join fetch s.aircraft where s.event.id = :eventId order by s.startTime")
    List<Showtime> findByEvent_Id(@Param("eventId") Long eventId);

    @Query("select s from Showtime s left join fetch s.aircraft where s.event.id in :eventIds order by s.startTime")
    List<Showtime> findByEvent_IdIn(@Param("eventIds") List<Long> eventIds);

    @Query("select min(s.basePrice) from Showtime s where s.event.id = :eventId")
    java.math.BigDecimal findMinPriceByEventId(@Param("eventId") Long eventId);

    /** Id cua cac Event co it nhat 1 giờ bay (Showtime) bat dau trong khoang [start, end). */
    @Query("select distinct s.event.id from Showtime s where s.startTime >= :start and s.startTime < :end")
    List<Long> findEventIdsByStartTimeBetween(@Param("start") Instant start, @Param("end") Instant end);

    /** Cung mot chuyen bay da co gio bay bat dau dung thoi diem nay. */
    boolean existsByEvent_IdAndStartTime(Long eventId, Instant startTime);

    /** May bay da co gio bay khac chong lan voi khoang [start, end). */
    @Query("select count(s) > 0 from Showtime s where s.aircraft.id = :aircraftId and s.startTime < :end and s.endTime > :start")
    boolean existsAircraftOverlap(@Param("aircraftId") Long aircraftId, @Param("start") Instant start, @Param("end") Instant end);
}
