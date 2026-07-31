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
    @Query(
        """
            select t
            from Ticket t
            join t.bookingDetail bd
            join bd.booking b
            where b.user.login = :login
        """
    )
    Page<Ticket> findMyTickets(@Param("login") String login, Pageable pageable);

    @Query(
        """
            select t
            from Ticket t
            join t.bookingDetail bd
            where bd.booking.id = :bookingId
        """
    )
    List<Ticket> findByBookingId(@Param("bookingId") Long bookingId);

    Optional<Ticket> findByQrCode(String qrCode);

    /**
     * Lay ve cua nguoi dung kem san thong tin su kien va dia diem,
     * tranh loi N+1 khi hien thi man hinh "Ve cua toi".
     */
    @Query(
        value = """
        select t
        from Ticket t
        join fetch t.bookingDetail bd
        join fetch bd.booking b
        join fetch bd.ticketType tt
        join fetch tt.event e
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
}
