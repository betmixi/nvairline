package com.dugx.event.service;

import com.dugx.event.domain.Coupon;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import com.dugx.event.repository.CouponRepository;
import com.dugx.event.service.dto.CouponDTO;
import com.dugx.event.service.mapper.CouponMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.Coupon}.
 */
@Service
@Transactional
public class CouponService {

    private static final Logger LOG = LoggerFactory.getLogger(CouponService.class);

    private final CouponRepository couponRepository;

    private final CouponMapper couponMapper;

    public CouponService(CouponRepository couponRepository, CouponMapper couponMapper) {
        this.couponRepository = couponRepository;
        this.couponMapper = couponMapper;
    }

    /**
     * Save a coupon.
     *
     * @param couponDTO the entity to save.
     * @return the persisted entity.
     */
    public CouponDTO save(CouponDTO couponDTO) {
        LOG.debug("Request to save Coupon : {}", couponDTO);
        validateCoupon(couponDTO, null);
        Coupon coupon = couponMapper.toEntity(couponDTO);
        coupon = couponRepository.save(coupon);
        return couponMapper.toDto(coupon);
    }

    /**
     * Update a coupon.
     *
     * @param couponDTO the entity to save.
     * @return the persisted entity.
     */
    public CouponDTO update(CouponDTO couponDTO) {
        LOG.debug("Request to update Coupon : {}", couponDTO);
        validateCoupon(couponDTO, couponDTO.getId());
        Coupon coupon = couponMapper.toEntity(couponDTO);
        coupon = couponRepository.save(coupon);
        return couponMapper.toDto(coupon);
    }

    /**
     * Partially update a coupon.
     *
     * @param couponDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CouponDTO> partialUpdate(CouponDTO couponDTO) {
        LOG.debug("Request to partially update Coupon : {}", couponDTO);

        return couponRepository
            .findById(couponDTO.getId())
            .map(existingCoupon -> {
                couponMapper.partialUpdate(existingCoupon, couponDTO);

                return existingCoupon;
            })
            .map(couponRepository::save)
            .map(couponMapper::toDto);
    }

    /**
     * Get all the coupons with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CouponDTO> findAllWithEagerRelationships(Pageable pageable) {
        return couponRepository.findAllWithEagerRelationships(pageable).map(couponMapper::toDto);
    }

    /**
     * Get one coupon by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CouponDTO> findOne(Long id) {
        LOG.debug("Request to get Coupon : {}", id);
        return couponRepository.findOneWithEagerRelationships(id).map(couponMapper::toDto);
    }

    /**
     * Delete the coupon by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Coupon : {}", id);
        couponRepository.deleteById(id);
    }

    /** Kiem tra Coupon code: khong de trong va khong trung (khong phan biet hoa thuong). */
    private void validateCoupon(CouponDTO dto, Long excludeId) {
        String value = dto.getCode() == null ? "" : dto.getCode().trim();
        if (value.isEmpty()) {
            throw new BadRequestAlertException("Coupon code must not be blank", "coupon", "coderequired");
        }
        dto.setCode(value);
        boolean duplicated = excludeId == null ? couponRepository.existsByCodeIgnoreCase(value) : couponRepository.existsByCodeIgnoreCaseAndIdNot(value, excludeId);
        if (duplicated) {
            throw new BadRequestAlertException("Coupon code already exists", "coupon", "codeexists");
        }
        if (dto.getStartDate() != null && dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BadRequestAlertException("End date must not be before start date", "coupon", "invaliddate");
        }
    }
}
