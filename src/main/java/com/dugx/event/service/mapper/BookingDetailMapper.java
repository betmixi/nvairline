package com.dugx.event.service.mapper;

import com.dugx.event.domain.Booking;
import com.dugx.event.domain.BookingDetail;
import com.dugx.event.service.dto.BookingDTO;
import com.dugx.event.service.dto.BookingDetailDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BookingDetail} and its DTO {@link BookingDetailDTO}.
 */
@Mapper(componentModel = "spring", uses = { ShowtimeSeatMapper.class })
public interface BookingDetailMapper extends EntityMapper<BookingDetailDTO, BookingDetail> {
    @Mapping(target = "booking", source = "booking", qualifiedByName = "bookingId")
    BookingDetailDTO toDto(BookingDetail s);

    @Named("bookingId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookingDTO toDtoBookingId(Booking booking);
}
