package com.dugx.event.repository;

import com.dugx.event.domain.Ticket;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Ticket entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    /**
     * TicketMapper.toDto() luon di sau: bookingDetail -> booking -> user,
     * va bookingDetail -> showtimeSeat -> seat / showtime -> event -> category/address.
     * Cac query fetch-join ben duoi lay san toan bo chuoi nay de tranh N+1.
     */
    String FULL_DETAILS_FETCH = """
        join fetch t.bookingDetail bd
        join fetch bd.booking b
        left join fetch b.user
        join fetch bd.showtimeSeat ss
        join fetch ss.seat
        join fetch ss.showtime sh
        join fetch sh.event e
        left join fetch e.category
        left join fetch e.address
        """;

    @Query(
        value = "select t from Ticket t " + FULL_DETAILS_FETCH + " where b.user.login = :login",
        countQuery = """
        select count(t)
        from Ticket t
        join t.bookingDetail bd
        join bd.booking b
        where b.user.login = :login
        """
    )
    Page<Ticket> findMyTickets(@Param("login") String login, Pageable pageable);

    @Query(value = "select t from Ticket t " + FULL_DETAILS_FETCH + " where bd.booking.id = :bookingId")
    List<Ticket> findByBookingId(@Param("bookingId") Long bookingId);

    @Query(value = "select t from Ticket t " + FULL_DETAILS_FETCH + " where t.id = :id")
    Optional<Ticket> findByIdWithFullDetails(@Param("id") Long id);

    Optional<Ticket> findByQrCode(String qrCode);

    @Query(value = "select t from Ticket t " + FULL_DETAILS_FETCH + " where t.qrCode = :qrCode")
    Optional<Ticket> findByQrCodeWithFullDetails(@Param("qrCode") String qrCode);

    /**
     * Lay ve cua nguoi dung kem san thong tin phim, suat chieu va ghe,
     * tranh loi N+1 khi hien thi man hinh "Ve cua toi".
     */
    @Query(
        value = """
        select t
        from Ticket t
        join fetch t.bookingDetail bd
        join fetch bd.booking b
        join fetch bd.showtimeSeat ss
        join fetch ss.seat
        join fetch ss.showtime sh
        join fetch sh.event e
        left join fetch e.address a
        where b.user.login = :login
        """,
        countQuery = """
        select count(t)
        from Ticket t
        join t.bookingDetail bd
        join bd.booking b
        where b.user.login = :login
        """
    )
    Page<Ticket> findMyTicketsWithDetails(@Param("login") String login, Pageable pageable);

    @Query(value = "select t from Ticket t " + FULL_DETAILS_FETCH + " where sh.id = :showtimeId")
    List<Ticket> findByShowtime(@Param("showtimeId") Long showtimeId);
}
