package com.dugx.event.service.mapper;

import static com.dugx.event.domain.BookingDetailAsserts.*;
import static com.dugx.event.domain.BookingDetailTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookingDetailMapperTest {

    private BookingDetailMapper bookingDetailMapper;

    @BeforeEach
    void setUp() {
        bookingDetailMapper = new BookingDetailMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBookingDetailSample1();
        var actual = bookingDetailMapper.toEntity(bookingDetailMapper.toDto(expected));
        assertBookingDetailAllPropertiesEquals(expected, actual);
    }
}
