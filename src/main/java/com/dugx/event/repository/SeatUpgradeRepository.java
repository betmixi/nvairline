package com.dugx.event.repository;

import com.dugx.event.domain.SeatUpgrade;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SeatUpgrade entity.
 */
@Repository
public interface SeatUpgradeRepository extends JpaRepository<SeatUpgrade, Long> {
    @Query(
        "select su from SeatUpgrade su left join fetch su.oldShowtimeSeat left join fetch su.newShowtimeSeat left join fetch su.ticket where su.booking.id = :bookingId"
    )
    Optional<SeatUpgrade> findByBooking_Id(@Param("bookingId") Long bookingId);

    /** Danh cho man hinh admin "Khach dat ve": lay full quan he de hien thi ve goc + thong tin nang hang da mua. */
    @Query(
        """
        select su from SeatUpgrade su
        join fetch su.booking b
        join fetch b.user
        join fetch su.ticket t
        join fetch t.bookingDetail bd
        join fetch bd.showtimeSeat ss
        join fetch ss.showtime sh
        join fetch sh.event e
        join fetch su.newShowtimeSeat nss
        join fetch nss.seat
        left join fetch e.departureAirport
        left join fetch e.arrivalAirport
        """
    )
    List<SeatUpgrade> findAllWithDetailsForAdmin();
}
