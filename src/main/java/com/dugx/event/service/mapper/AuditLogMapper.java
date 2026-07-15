package com.dugx.event.service.mapper;

import com.dugx.event.domain.AuditLog;
import com.dugx.event.domain.User;
import com.dugx.event.service.dto.AuditLogDTO;
import com.dugx.event.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AuditLog} and its DTO {@link AuditLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditLogMapper extends EntityMapper<AuditLogDTO, AuditLog> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    AuditLogDTO toDto(AuditLog s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
