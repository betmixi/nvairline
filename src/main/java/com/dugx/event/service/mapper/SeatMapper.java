package com.dugx.event.service.mapper;

import com.dugx.event.domain.Aircraft;
import com.dugx.event.domain.Seat;
import com.dugx.event.service.dto.AircraftDTO;
import com.dugx.event.service.dto.SeatDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Seat} and its DTO {@link SeatDTO}.
 */
@Mapper(componentModel = "spring")
public interface SeatMapper extends EntityMapper<SeatDTO, Seat> {
    @Mapping(target = "aircraft", source = "aircraft", qualifiedByName = "aircraftName")
    SeatDTO toDto(Seat s);

    @Named("aircraftName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AircraftDTO toDtoAircraftName(Aircraft aircraft);
}
