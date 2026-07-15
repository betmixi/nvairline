package com.dugx.event.service.mapper;

import com.dugx.event.domain.Organizer;
import com.dugx.event.domain.User;
import com.dugx.event.service.dto.OrganizerDTO;
import com.dugx.event.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Organizer} and its DTO {@link OrganizerDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrganizerMapper extends EntityMapper<OrganizerDTO, Organizer> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    OrganizerDTO toDto(Organizer s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
