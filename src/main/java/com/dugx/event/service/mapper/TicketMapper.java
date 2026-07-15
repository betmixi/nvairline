package com.dugx.event.service.mapper;

import com.dugx.event.domain.BookingDetail;
import com.dugx.event.domain.Ticket;
import com.dugx.event.service.dto.BookingDetailDTO;
import com.dugx.event.service.dto.TicketDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Ticket} and its DTO {@link TicketDTO}.
 */
@Mapper(componentModel = "spring")
public interface TicketMapper extends EntityMapper<TicketDTO, Ticket> {
    @Mapping(target = "bookingDetail", source = "bookingDetail", qualifiedByName = "bookingDetailId")
    TicketDTO toDto(Ticket s);

    @Named("bookingDetailId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookingDetailDTO toDtoBookingDetailId(BookingDetail bookingDetail);
}
