package com.dugx.event.service.mapper;

import com.dugx.event.domain.Aircraft;
import com.dugx.event.service.dto.AircraftDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Aircraft} and its DTO {@link AircraftDTO}.
 */
@Mapper(componentModel = "spring")
public interface AircraftMapper extends EntityMapper<AircraftDTO, Aircraft> {}
