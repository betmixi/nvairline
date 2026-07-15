package com.dugx.event.service.mapper;

import com.dugx.event.domain.Booking;
import com.dugx.event.domain.BookingDetail;
import com.dugx.event.domain.TicketType;
import com.dugx.event.service.dto.BookingDTO;
import com.dugx.event.service.dto.BookingDetailDTO;
import com.dugx.event.service.dto.TicketTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BookingDetail} and its DTO {@link BookingDetailDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookingDetailMapper extends EntityMapper<BookingDetailDTO, BookingDetail> {
    @Mapping(target = "booking", source = "booking", qualifiedByName = "bookingId")
    @Mapping(target = "ticketType", source = "ticketType", qualifiedByName = "ticketTypeName")
    BookingDetailDTO toDto(BookingDetail s);

    @Named("bookingId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookingDTO toDtoBookingId(Booking booking);

    @Named("ticketTypeName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TicketTypeDTO toDtoTicketTypeName(TicketType ticketType);
}
