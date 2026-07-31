package com.dugx.event.repository;

import com.dugx.event.domain.Payment;
import com.dugx.event.service.dto.EventRevenueDTO;
import com.dugx.event.service.dto.RecentPaymentDTO;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Payment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    /** Dung de tranh ghi trung khi VNPay goi ca return-url lan IPN. */
    java.util.Optional<Payment> findByTransactionCode(String transactionCode);

    @Query(
        """
        select coalesce(sum(p.amount), 0)
        from Payment p
        join BookingDetail bd on bd.booking = p.booking
        where bd.ticketType.event.organizer.user.login = :login
          and p.status = 'SUCCESS'
        """
    )
    BigDecimal totalRevenue(@Param("login") String login);

    @Query(
        """
        select coalesce(sum(p.amount),0)
        from Payment p
        where p.status='SUCCESS'
        """
    )
    BigDecimal totalRevenue();

    @Query(
        """
        select new com.dugx.event.service.dto.EventRevenueDTO(
            e.id,
            e.title,
            coalesce(sum(bd.price),0)
        )
        from Payment p
        join BookingDetail bd on bd.booking = p.booking
        join bd.ticketType tt
        join tt.event e
        where e.organizer.user.login = :login
        and p.status='SUCCESS'
        group by e.id,e.title
        order by coalesce(sum(bd.price),0) desc
        """
    )
    List<EventRevenueDTO> revenueByEvent(@Param("login") String login);

    @Query(
        """
        select new com.dugx.event.service.dto.RecentPaymentDTO(
            p.paymentDate,
            coalesce(
                nullif(trim(concat(coalesce(p.booking.user.firstName, ''), ' ', coalesce(p.booking.user.lastName, ''))), ''),
                p.booking.user.login
            ),
            p.amount
        )
        from Payment p
        join BookingDetail bd on bd.booking=p.booking
        where bd.ticketType.event.organizer.user.login=:login
        and p.status='SUCCESS'
        order by p.paymentDate desc
        """
    )
    List<RecentPaymentDTO> recentPayments(@Param("login") String login, Pageable pageable);

    @Query(
        """
        select coalesce(sum(bd.quantity),0)
        from BookingDetail bd
        join bd.ticketType tt
        join tt.event e
        join Payment p on p.booking=bd.booking
        where e.organizer.user.login=:login
        and p.status='SUCCESS'
        """
    )
    Long totalTicketsSold(@Param("login") String login);
}
