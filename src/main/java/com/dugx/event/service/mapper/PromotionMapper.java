package com.dugx.event.service.mapper;

import com.dugx.event.domain.Promotion;
import com.dugx.event.service.dto.PromotionDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Promotion} and its DTO {@link PromotionDTO}.
 */
@Mapper(componentModel = "spring")
public interface PromotionMapper extends EntityMapper<PromotionDTO, Promotion> {}
