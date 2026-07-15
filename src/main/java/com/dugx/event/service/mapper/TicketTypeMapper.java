package com.dugx.event.service.mapper;

import com.dugx.event.domain.Event;
import com.dugx.event.domain.TicketType;
import com.dugx.event.service.dto.EventDTO;
import com.dugx.event.service.dto.TicketTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TicketType} and its DTO {@link TicketTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface TicketTypeMapper extends EntityMapper<TicketTypeDTO, TicketType> {
    @Mapping(target = "event", source = "event", qualifiedByName = "eventTitle")
    TicketTypeDTO toDto(TicketType s);

    @Named("eventTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    EventDTO toDtoEventTitle(Event event);
}
