package com.dugx.event.domain;

import static com.dugx.event.domain.EventTestSamples.*;
import static com.dugx.event.domain.ReportTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dugx.event.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReportTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Report.class);
        Report report1 = getReportSample1();
        Report report2 = new Report();
        assertThat(report1).isNotEqualTo(report2);

        report2.setId(report1.getId());
        assertThat(report1).isEqualTo(report2);

        report2 = getReportSample2();
        assertThat(report1).isNotEqualTo(report2);
    }

    @Test
    void eventTest() {
        Report report = getReportRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        report.setEvent(eventBack);
        assertThat(report.getEvent()).isEqualTo(eventBack);

        report.event(null);
        assertThat(report.getEvent()).isNull();
    }
}
