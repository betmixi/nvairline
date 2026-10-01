package com.dugx.event.service.mapper;

import com.dugx.event.domain.Airport;
import com.dugx.event.service.dto.AirportDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Airport} and its DTO {@link AirportDTO}.
 */
@Mapper(componentModel = "spring")
public interface AirportMapper extends EntityMapper<AirportDTO, Airport> {}
