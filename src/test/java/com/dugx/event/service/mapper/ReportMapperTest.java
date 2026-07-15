package com.dugx.event.service.mapper;

import static com.dugx.event.domain.ReportAsserts.*;
import static com.dugx.event.domain.ReportTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportMapperTest {

    private ReportMapper reportMapper;

    @BeforeEach
    void setUp() {
        reportMapper = new ReportMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReportSample1();
        var actual = reportMapper.toEntity(reportMapper.toDto(expected));
        assertReportAllPropertiesEquals(expected, actual);
    }
}
