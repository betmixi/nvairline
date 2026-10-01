package com.dugx.event.service.mapper;

import com.dugx.event.domain.Seat;
import com.dugx.event.domain.Showtime;
import com.dugx.event.domain.ShowtimeSeat;
import com.dugx.event.service.dto.SeatDTO;
import com.dugx.event.service.dto.ShowtimeDTO;
import com.dugx.event.service.dto.ShowtimeSeatDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link ShowtimeSeat} and its DTO {@link ShowtimeSeatDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShowtimeSeatMapper extends EntityMapper<ShowtimeSeatDTO, ShowtimeSeat> {
    @Mapping(target = "showtime", source = "showtime", qualifiedByName = "showtimeId")
    @Mapping(target = "seat", source = "seat", qualifiedByName = "seatInfo")
    ShowtimeSeatDTO toDto(ShowtimeSeat s);

    @Named("showtimeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShowtimeDTO toDtoShowtimeId(Showtime showtime);

    @Named("seatInfo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "rowLabel", source = "rowLabel")
    @Mapping(target = "seatNumber", source = "seatNumber")
    @Mapping(target = "seatType", source = "seatType")
    SeatDTO toDtoSeatInfo(Seat seat);
}
