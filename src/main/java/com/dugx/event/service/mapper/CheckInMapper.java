package com.dugx.event.service.mapper;

import com.dugx.event.domain.CheckIn;
import com.dugx.event.domain.Ticket;
import com.dugx.event.domain.User;
import com.dugx.event.service.dto.CheckInDTO;
import com.dugx.event.service.dto.TicketDTO;
import com.dugx.event.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CheckIn} and its DTO {@link CheckInDTO}.
 */
@Mapper(componentModel = "spring", uses = { TicketMapper.class })
public interface CheckInMapper extends EntityMapper<CheckInDTO, CheckIn> {
    @Mapping(target = "ticket", source = "ticket")
    @Mapping(target = "checkedBy", source = "checkedBy", qualifiedByName = "userLogin")
    CheckInDTO toDto(CheckIn s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
