package com.dugx.event.service;

import com.dugx.event.domain.Promotion;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import com.dugx.event.repository.PromotionRepository;
import com.dugx.event.service.dto.PromotionDTO;
import com.dugx.event.service.mapper.PromotionMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.Promotion}.
 */
@Service
@Transactional
public class PromotionService {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionService.class);

    private final PromotionRepository promotionRepository;

    private final PromotionMapper promotionMapper;

    public PromotionService(PromotionRepository promotionRepository, PromotionMapper promotionMapper) {
        this.promotionRepository = promotionRepository;
        this.promotionMapper = promotionMapper;
    }

    public PromotionDTO save(PromotionDTO promotionDTO) {
        LOG.debug("Request to save Promotion : {}", promotionDTO);
        validatePromotion(promotionDTO, null);
        Promotion promotion = promotionMapper.toEntity(promotionDTO);
        promotion = promotionRepository.save(promotion);
        return promotionMapper.toDto(promotion);
    }

    public PromotionDTO update(PromotionDTO promotionDTO) {
        LOG.debug("Request to update Promotion : {}", promotionDTO);
        validatePromotion(promotionDTO, promotionDTO.getId());
        Promotion promotion = promotionMapper.toEntity(promotionDTO);
        promotion = promotionRepository.save(promotion);
        return promotionMapper.toDto(promotion);
    }

    @Transactional(readOnly = true)
    public List<PromotionDTO> findAll() {
        return promotionRepository.findAll().stream().map(promotionMapper::toDto).toList();
    }

    /** Cong khai: chi cac uu dai dang active, sap xep theo displayOrder, dung cho flyout "Kham Pha". */
    @Transactional(readOnly = true)
    public List<PromotionDTO> findAllActive() {
        return promotionRepository.findByActiveTrueOrderByDisplayOrderAsc().stream().map(promotionMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Optional<PromotionDTO> findOne(Long id) {
        LOG.debug("Request to get Promotion : {}", id);
        return promotionRepository.findById(id).map(promotionMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete Promotion : {}", id);
        promotionRepository.deleteById(id);
    }

    /** Kiem tra Promotion title: khong de trong va khong trung (khong phan biet hoa thuong). */
    private void validatePromotion(PromotionDTO dto, Long excludeId) {
        String value = dto.getTitle() == null ? "" : dto.getTitle().trim();
        if (value.isEmpty()) {
            throw new BadRequestAlertException("Promotion title must not be blank", "promotion", "titlerequired");
        }
        dto.setTitle(value);
        boolean duplicated = excludeId == null ? promotionRepository.existsByTitleIgnoreCase(value) : promotionRepository.existsByTitleIgnoreCaseAndIdNot(value, excludeId);
        if (duplicated) {
            throw new BadRequestAlertException("Promotion title already exists", "promotion", "titleexists");
        }
    }
}
