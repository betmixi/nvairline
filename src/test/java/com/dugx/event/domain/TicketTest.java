package com.dugx.event.domain;

import static com.dugx.event.domain.BookingDetailTestSamples.*;
import static com.dugx.event.domain.TicketTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dugx.event.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TicketTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ticket.class);
        Ticket ticket1 = getTicketSample1();
        Ticket ticket2 = new Ticket();
        assertThat(ticket1).isNotEqualTo(ticket2);

        ticket2.setId(ticket1.getId());
        assertThat(ticket1).isEqualTo(ticket2);

        ticket2 = getTicketSample2();
        assertThat(ticket1).isNotEqualTo(ticket2);
    }

    @Test
    void bookingDetailTest() {
        Ticket ticket = getTicketRandomSampleGenerator();
        BookingDetail bookingDetailBack = getBookingDetailRandomSampleGenerator();

        ticket.setBookingDetail(bookingDetailBack);
        assertThat(ticket.getBookingDetail()).isEqualTo(bookingDetailBack);

        ticket.bookingDetail(null);
        assertThat(ticket.getBookingDetail()).isNull();
    }
}
