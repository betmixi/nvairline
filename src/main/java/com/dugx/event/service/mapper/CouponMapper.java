package com.dugx.event.service.mapper;

import com.dugx.event.domain.Coupon;
import com.dugx.event.domain.Event;
import com.dugx.event.service.dto.CouponDTO;
import com.dugx.event.service.dto.EventDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Coupon} and its DTO {@link CouponDTO}.
 */
@Mapper(componentModel = "spring")
public interface CouponMapper extends EntityMapper<CouponDTO, Coupon> {
    @Mapping(target = "event", source = "event", qualifiedByName = "eventTitle")
    CouponDTO toDto(Coupon s);

    @Named("eventTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    EventDTO toDtoEventTitle(Event event);
}
