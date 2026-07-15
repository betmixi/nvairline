package com.dugx.event.service.mapper;

import static com.dugx.event.domain.CheckInAsserts.*;
import static com.dugx.event.domain.CheckInTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CheckInMapperTest {

    private CheckInMapper checkInMapper;

    @BeforeEach
    void setUp() {
        checkInMapper = new CheckInMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCheckInSample1();
        var actual = checkInMapper.toEntity(checkInMapper.toDto(expected));
        assertCheckInAllPropertiesEquals(expected, actual);
    }
}
