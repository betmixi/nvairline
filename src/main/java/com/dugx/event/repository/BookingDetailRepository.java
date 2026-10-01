package com.dugx.event.repository;

import com.dugx.event.domain.BookingDetail;
import com.dugx.event.service.dto.CustomerBookingDTO;
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
    /** Lay tat ca dong chi tiet cua mot booking (kem ghe). */
    @Query("select bd from BookingDetail bd left join fetch bd.showtimeSeat where bd.booking.id = :bookingId")
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
        value = "select bookingDetail from BookingDetail bookingDetail left join fetch bookingDetail.showtimeSeat",
        countQuery = "select count(bookingDetail) from BookingDetail bookingDetail"
    )
    Page<BookingDetail> findAllWithToOneRelationships(Pageable pageable);

    @Query("select bookingDetail from BookingDetail bookingDetail left join fetch bookingDetail.showtimeSeat")
    List<BookingDetail> findAllWithToOneRelationships();

    @Query("select bookingDetail from BookingDetail bookingDetail left join fetch bookingDetail.showtimeSeat where bookingDetail.id =:id")
    Optional<BookingDetail> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        """
        select count(bd) > 0
        from BookingDetail bd
        where bd.booking.user.login = :login
        and bd.showtimeSeat.showtime.event.id = :eventId
        """
    )
    boolean hasPurchasedEvent(@Param("login") String login, @Param("eventId") Long eventId);

    /** Bang xep hang su kien theo so ve da ban (chi tinh don da thanh toan). */
    @Query(
        """
        select new com.dugx.event.service.dto.TopEventDTO(
            e.id,
            e.title,
            count(bd),
            coalesce(sum(bd.price), 0)
        )
        from BookingDetail bd
        join bd.showtimeSeat ss
        join ss.showtime sh
        join sh.event e
        join Payment p on p.booking = bd.booking
        where p.status = 'SUCCESS'
        group by e.id, e.title
        order by count(bd) desc
        """
    )
    List<TopEventDTO> topEventsByTickets(Pageable pageable);

    /** Bang xep hang su kien theo doanh thu (chi tinh don da thanh toan). */
    @Query(
        """
        select new com.dugx.event.service.dto.TopEventDTO(
            e.id,
            e.title,
            count(bd),
            coalesce(sum(bd.price), 0)
        )
        from BookingDetail bd
        join bd.showtimeSeat ss
        join ss.showtime sh
        join sh.event e
        join Payment p on p.booking = bd.booking
        where p.status = 'SUCCESS'
        group by e.id, e.title
        order by coalesce(sum(bd.price), 0) desc
        """
    )
    List<TopEventDTO> topEventsByRevenue(Pageable pageable);

    /** Danh sach khach hang da dat ve, gop theo booking kem thong tin chuyen bay. */
    @Query(
        """
        select new com.dugx.event.service.dto.CustomerBookingDTO(
            b.id,
            b.bookingDate,
            b.totalAmount,
            b.status,
            u.id,
            u.login,
            u.firstName,
            u.lastName,
            u.email,
            e.title,
            da.code,
            aa.code,
            sh.startTime,
            count(bd)
        )
        from BookingDetail bd
        join bd.booking b
        join b.user u
        join bd.showtimeSeat ss
        join ss.showtime sh
        join sh.event e
        left join e.departureAirport da
        left join e.arrivalAirport aa
        group by b.id, b.bookingDate, b.totalAmount, b.status, u.id, u.login, u.firstName, u.lastName, u.email,
            e.title, da.code, aa.code, sh.startTime
        order by b.bookingDate desc
        """
    )
    List<CustomerBookingDTO> findAllCustomerBookings();
}
