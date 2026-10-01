package com.dugx.event.repository;

import com.dugx.event.domain.TicketAddon;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TicketAddon entity.
 */
@Repository
public interface TicketAddonRepository extends JpaRepository<TicketAddon, Long> {
    @Query("select ta from TicketAddon ta where ta.booking.id = :bookingId")
    List<TicketAddon> findByBooking_Id(@Param("bookingId") Long bookingId);

    @Query(
        "select ta from TicketAddon ta where ta.ticket.id = :ticketId and ta.addonType = :addonType and ta.status = 'PAID' order by ta.id"
    )
    List<TicketAddon> findPaidByTicket_IdAndAddonType(@Param("ticketId") Long ticketId, @Param("addonType") String addonType);

    /**
     * Danh cho man hinh admin "Khach dat ve": lay full quan he de hien thi ve goc + tung dong dich vu da mua them.
     * Mot booking dich vu bo tro co the co nhieu dong (nhieu san pham trong gio hang), goc theo booking.id o tang service.
     */
    @Query(
        """
        select ta from TicketAddon ta
        join fetch ta.booking b
        join fetch b.user
        join fetch ta.ticket t
        join fetch t.bookingDetail bd
        join fetch bd.showtimeSeat ss
        join fetch ss.showtime sh
        join fetch sh.event e
        left join fetch e.departureAirport
        left join fetch e.arrivalAirport
        """
    )
    List<TicketAddon> findAllWithDetailsForAdmin();
}
