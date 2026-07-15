package com.dugx.event.service.mapper;

import com.dugx.event.domain.Booking;
import com.dugx.event.domain.User;
import com.dugx.event.service.dto.BookingDTO;
import com.dugx.event.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Booking} and its DTO {@link BookingDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookingMapper extends EntityMapper<BookingDTO, Booking> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    BookingDTO toDto(Booking s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
