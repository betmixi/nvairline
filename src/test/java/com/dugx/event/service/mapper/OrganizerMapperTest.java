package com.dugx.event.service.mapper;

import static com.dugx.event.domain.OrganizerAsserts.*;
import static com.dugx.event.domain.OrganizerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrganizerMapperTest {

    private OrganizerMapper organizerMapper;

    @BeforeEach
    void setUp() {
        organizerMapper = new OrganizerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOrganizerSample1();
        var actual = organizerMapper.toEntity(organizerMapper.toDto(expected));
        assertOrganizerAllPropertiesEquals(expected, actual);
    }
}
