package com.dugx.event.service.mapper;

import com.dugx.event.domain.Event;
import com.dugx.event.domain.Favorite;
import com.dugx.event.domain.User;
import com.dugx.event.service.dto.EventDTO;
import com.dugx.event.service.dto.FavoriteDTO;
import com.dugx.event.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Favorite} and its DTO {@link FavoriteDTO}.
 */
@Mapper(componentModel = "spring")
public interface FavoriteMapper extends EntityMapper<FavoriteDTO, Favorite> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "event", source = "event", qualifiedByName = "eventTitle")
    FavoriteDTO toDto(Favorite s);

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
