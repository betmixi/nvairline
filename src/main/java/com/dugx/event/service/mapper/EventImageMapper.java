package com.dugx.event.service.mapper;

import com.dugx.event.domain.Event;
import com.dugx.event.domain.EventImage;
import com.dugx.event.service.dto.EventDTO;
import com.dugx.event.service.dto.EventImageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EventImage} and its DTO {@link EventImageDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventImageMapper extends EntityMapper<EventImageDTO, EventImage> {
    @Mapping(target = "event", source = "event", qualifiedByName = "eventTitle")
    EventImageDTO toDto(EventImage s);

    @Named("eventTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    EventDTO toDtoEventTitle(Event event);
}
