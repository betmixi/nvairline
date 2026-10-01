package com.dugx.event.service.mapper;

import com.dugx.event.domain.Aircraft;
import com.dugx.event.domain.Event;
import com.dugx.event.domain.Showtime;
import com.dugx.event.service.dto.AircraftDTO;
import com.dugx.event.service.dto.EventDTO;
import com.dugx.event.service.dto.ShowtimeDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Showtime} and its DTO {@link ShowtimeDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShowtimeMapper extends EntityMapper<ShowtimeDTO, Showtime> {
    @Mapping(target = "event", source = "event", qualifiedByName = "eventTitle")
    @Mapping(target = "aircraft", source = "aircraft", qualifiedByName = "aircraftName")
    ShowtimeDTO toDto(Showtime s);

    @Named("eventTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "banner", source = "banner")
    EventDTO toDtoEventTitle(Event event);

    @Named("aircraftName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AircraftDTO toDtoAircraftName(Aircraft aircraft);
}
