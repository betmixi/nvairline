package com.dugx.event.service.mapper;

import com.dugx.event.domain.Event;
import com.dugx.event.domain.Report;
import com.dugx.event.domain.User;
import com.dugx.event.service.dto.EventDTO;
import com.dugx.event.service.dto.ReportDTO;
import com.dugx.event.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Report} and its DTO {@link ReportDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReportMapper extends EntityMapper<ReportDTO, Report> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "event", source = "event", qualifiedByName = "eventTitle")
    ReportDTO toDto(Report s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("eventTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    EventDTO toDtoEventTitle(Event event);
}
