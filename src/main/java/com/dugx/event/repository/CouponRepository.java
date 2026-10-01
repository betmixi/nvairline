package com.dugx.event.repository;

import com.dugx.event.domain.Coupon;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Coupon entity.
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long>, JpaSpecificationExecutor<Coupon> {
    default Optional<Coupon> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Coupon> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Coupon> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    Optional<Coupon> findByCode(String code);

    /** 1 coupon admin da tao san, dung muc giam gia, chua ai doi (redeemedByUser = null) va con so luong. */
    Optional<Coupon> findFirstByDiscountAndRedeemedByUserIsNullAndQuantityGreaterThanOrderByIdAsc(BigDecimal discount, Integer quantity);

    /** Tat ca coupon con dung duoc (con so luong) - dung de hien danh sach chon nhanh luc thanh toan. */
    List<Coupon> findByQuantityGreaterThan(Integer quantity);

    @Query(value = "select coupon from Coupon coupon left join fetch coupon.event", countQuery = "select count(coupon) from Coupon coupon")
    Page<Coupon> findAllWithToOneRelationships(Pageable pageable);

    @Query("select coupon from Coupon coupon left join fetch coupon.event")
    List<Coupon> findAllWithToOneRelationships();

    @Query("select coupon from Coupon coupon left join fetch coupon.event where coupon.id =:id")
    Optional<Coupon> findOneWithToOneRelationships(@Param("id") Long id);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
}
