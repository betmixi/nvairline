package com.dugx.event.service.mapper;

import static com.dugx.event.domain.TicketTypeAsserts.*;
import static com.dugx.event.domain.TicketTypeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TicketTypeMapperTest {

    private TicketTypeMapper ticketTypeMapper;

    @BeforeEach
    void setUp() {
        ticketTypeMapper = new TicketTypeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTicketTypeSample1();
        var actual = ticketTypeMapper.toEntity(ticketTypeMapper.toDto(expected));
        assertTicketTypeAllPropertiesEquals(expected, actual);
    }
}
