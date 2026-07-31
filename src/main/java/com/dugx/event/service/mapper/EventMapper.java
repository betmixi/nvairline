package com.dugx.event.service.mapper;

import com.dugx.event.domain.Address;
import com.dugx.event.domain.Category;
import com.dugx.event.domain.Event;
import com.dugx.event.domain.Organizer;
import com.dugx.event.service.dto.AddressDTO;
import com.dugx.event.service.dto.CategoryDTO;
import com.dugx.event.service.dto.EventDTO;
import com.dugx.event.service.dto.OrganizerDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Event} and its DTO {@link EventDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventMapper extends EntityMapper<EventDTO, Event> {
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryName")
    @Mapping(target = "address", source = "address", qualifiedByName = "addressLocation")
    @Mapping(target = "organizer", source = "organizer", qualifiedByName = "organizerCompanyName")
    EventDTO toDto(Event s);

    @Named("categoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CategoryDTO toDtoCategoryName(Category category);

    @Named("addressLocation")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "location", source = "location")
    AddressDTO toDtoAddressLocation(Address address);

    @Named("organizerCompanyName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "companyName", source = "companyName")
    OrganizerDTO toDtoOrganizerCompanyName(Organizer organizer);
}
