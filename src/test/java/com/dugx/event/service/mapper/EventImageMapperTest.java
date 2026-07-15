package com.dugx.event.service.mapper;

import static com.dugx.event.domain.EventImageAsserts.*;
import static com.dugx.event.domain.EventImageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventImageMapperTest {

    private EventImageMapper eventImageMapper;

    @BeforeEach
    void setUp() {
        eventImageMapper = new EventImageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEventImageSample1();
        var actual = eventImageMapper.toEntity(eventImageMapper.toDto(expected));
        assertEventImageAllPropertiesEquals(expected, actual);
    }
}
