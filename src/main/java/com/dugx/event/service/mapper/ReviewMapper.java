package com.dugx.event.service.mapper;

import com.dugx.event.domain.Event;
import com.dugx.event.domain.Review;
import com.dugx.event.domain.User;
import com.dugx.event.service.dto.EventDTO;
import com.dugx.event.service.dto.ReviewDTO;
import com.dugx.event.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Review} and its DTO {@link ReviewDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReviewMapper extends EntityMapper<ReviewDTO, Review> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "event", source = "event", qualifiedByName = "eventTitle")
    ReviewDTO toDto(Review s);

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
