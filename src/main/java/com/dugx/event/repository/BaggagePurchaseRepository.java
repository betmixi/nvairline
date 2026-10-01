package com.dugx.event.repository;

import com.dugx.event.domain.BaggagePurchase;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BaggagePurchase entity.
 */
@Repository
public interface BaggagePurchaseRepository extends JpaRepository<BaggagePurchase, Long> {
    @Query("select bp from BaggagePurchase bp where bp.booking.id = :bookingId")
    Optional<BaggagePurchase> findByBooking_Id(@Param("bookingId") Long bookingId);

    @Query("select bp from BaggagePurchase bp where bp.ticket.id = :ticketId and bp.status = 'PAID' order by bp.id")
    List<BaggagePurchase> findPaidByTicket_Id(@Param("ticketId") Long ticketId);

    /** Danh cho man hinh admin "Khach dat ve": lay full quan he de hien thi ve goc + thong tin hanh ly da mua them. */
    @Query(
        """
        select bp from BaggagePurchase bp
        join fetch bp.booking b
        join fetch b.user
        join fetch bp.ticket t
        join fetch t.bookingDetail bd
        join fetch bd.showtimeSeat ss
        join fetch ss.showtime sh
        join fetch sh.event e
        left join fetch e.departureAirport
        left join fetch e.arrivalAirport
        """
    )
    List<BaggagePurchase> findAllWithDetailsForAdmin();
}
