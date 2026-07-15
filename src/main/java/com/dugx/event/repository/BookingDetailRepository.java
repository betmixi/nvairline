package com.dugx.event.repository;

import com.dugx.event.domain.BookingDetail;
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
}
