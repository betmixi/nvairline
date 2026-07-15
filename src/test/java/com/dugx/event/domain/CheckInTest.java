package com.dugx.event.domain;

import static com.dugx.event.domain.CheckInTestSamples.*;
import static com.dugx.event.domain.TicketTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dugx.event.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CheckInTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CheckIn.class);
        CheckIn checkIn1 = getCheckInSample1();
        CheckIn checkIn2 = new CheckIn();
        assertThat(checkIn1).isNotEqualTo(checkIn2);

        checkIn2.setId(checkIn1.getId());
        assertThat(checkIn1).isEqualTo(checkIn2);

        checkIn2 = getCheckInSample2();
        assertThat(checkIn1).isNotEqualTo(checkIn2);
    }

    @Test
    void ticketTest() {
        CheckIn checkIn = getCheckInRandomSampleGenerator();
        Ticket ticketBack = getTicketRandomSampleGenerator();

        checkIn.setTicket(ticketBack);
        assertThat(checkIn.getTicket()).isEqualTo(ticketBack);

        checkIn.ticket(null);
        assertThat(checkIn.getTicket()).isNull();
    }
}
