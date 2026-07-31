package com.dugx.event.repository;

import com.dugx.event.domain.BookingDetail;
import com.dugx.event.service.dto.TopEventDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BookingDetail entity.
 */
@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long>, JpaSpecificationExecutor<BookingDetail> {
    /** Lay tat ca dong chi tiet cua mot booking (kem loai ve). */
    @Query("select bd from BookingDetail bd left join fetch bd.ticketType where bd.booking.id = :bookingId")
    List<BookingDetail> findByBooking_Id(@Param("bookingId") Long bookingId);

    default Optional<BookingDetail> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<BookingDetail> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<BookingDetail> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select bookingDetail from BookingDetail bookingDetail left join fetch bookingDetail.ticketType",
        countQuery = "select count(bookingDetail) from BookingDetail bookingDetail"
    )
    Page<BookingDetail> findAllWithToOneRelationships(Pageable pageable);

    @Query("select bookingDetail from BookingDetail bookingDetail left join fetch bookingDetail.ticketType")
    List<BookingDetail> findAllWithToOneRelationships();

    @Query("select bookingDetail from BookingDetail bookingDetail left join fetch bookingDetail.ticketType where bookingDetail.id =:id")
    Optional<BookingDetail> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        """
        select coalesce(sum(bd.quantity), 0)
        from BookingDetail bd
        where bd.ticketType.event.organizer.user.login = :login
        """
    )
    Long totalTicketsSold(@Param("login") String login);

    @Query(
        """
        select count(bd) > 0
        from BookingDetail bd
        where bd.booking.user.login = :login
        and bd.ticketType.event.id = :eventId
        """
    )
    boolean hasPurchasedEvent(@Param("login") String login, @Param("eventId") Long eventId);

    @Query(
        """
        select coalesce(sum(b.quantity),0)
        from BookingDetail b
        where b.ticketType.event.organizer.user.login = :login
        """
    )
    Long totalTickets(@Param("login") String login);

    /** Bang xep hang su kien theo so ve da ban (chi tinh don da thanh toan). */
    @Query(
        """
        select new com.dugx.event.service.dto.TopEventDTO(
            e.id,
            e.title,
            coalesce(sum(bd.quantity), 0),
            coalesce(sum(bd.price), 0)
        )
        from BookingDetail bd
        join bd.ticketType tt
        join tt.event e
        join Payment p on p.booking = bd.booking
        where p.status = 'SUCCESS'
        group by e.id, e.title
        order by coalesce(sum(bd.quantity), 0) desc
        """
    )
    List<TopEventDTO> topEventsByTickets(Pageable pageable);

    /** Bang xep hang su kien theo doanh thu (chi tinh don da thanh toan). */
    @Query(
        """
        select new com.dugx.event.service.dto.TopEventDTO(
            e.id,
            e.title,
            coalesce(sum(bd.quantity), 0),
            coalesce(sum(bd.price), 0)
        )
        from BookingDetail bd
        join bd.ticketType tt
        join tt.event e
        join Payment p on p.booking = bd.booking
        where p.status = 'SUCCESS'
        group by e.id, e.title
        order by coalesce(sum(bd.price), 0) desc
        """
    )
    List<TopEventDTO> topEventsByRevenue(Pageable pageable);
}
