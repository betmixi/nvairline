package com.dugx.event.service.mapper;

import com.dugx.event.domain.Booking;
import com.dugx.event.domain.Payment;
import com.dugx.event.service.dto.BookingDTO;
import com.dugx.event.service.dto.PaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {
    @Mapping(target = "booking", source = "booking", qualifiedByName = "bookingId")
    PaymentDTO toDto(Payment s);

    @Named("bookingId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookingDTO toDtoBookingId(Booking booking);
}
