package com.dugx.event.repository;

import com.dugx.event.domain.Payment;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
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
        select coalesce(sum(p.amount),0)
        from Payment p
        where p.status='SUCCESS'
        """
    )
    BigDecimal totalRevenue();

    /** Thoi diem thanh toan thanh cong gan nhat cho tung booking - dung cho man hinh admin "Khach dat ve". */
    @Query(
        """
        select p.booking.id as bookingId, max(p.paymentDate) as paymentDate
        from Payment p
        where p.status = 'SUCCESS' and p.booking.id in :bookingIds
        group by p.booking.id
        """
    )
    List<BookingPaymentDate> findSuccessPaymentDatesByBookingIds(@Param("bookingIds") Collection<Long> bookingIds);

    interface BookingPaymentDate {
        Long getBookingId();
        Instant getPaymentDate();
    }
}
