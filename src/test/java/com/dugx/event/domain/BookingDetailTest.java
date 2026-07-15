package com.dugx.event.domain;

import static com.dugx.event.domain.BookingDetailTestSamples.*;
import static com.dugx.event.domain.BookingTestSamples.*;
import static com.dugx.event.domain.TicketTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dugx.event.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookingDetailTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookingDetail.class);
        BookingDetail bookingDetail1 = getBookingDetailSample1();
        BookingDetail bookingDetail2 = new BookingDetail();
        assertThat(bookingDetail1).isNotEqualTo(bookingDetail2);

        bookingDetail2.setId(bookingDetail1.getId());
        assertThat(bookingDetail1).isEqualTo(bookingDetail2);

        bookingDetail2 = getBookingDetailSample2();
        assertThat(bookingDetail1).isNotEqualTo(bookingDetail2);
    }

    @Test
    void bookingTest() {
        BookingDetail bookingDetail = getBookingDetailRandomSampleGenerator();
        Booking bookingBack = getBookingRandomSampleGenerator();

        bookingDetail.setBooking(bookingBack);
        assertThat(bookingDetail.getBooking()).isEqualTo(bookingBack);

        bookingDetail.booking(null);
        assertThat(bookingDetail.getBooking()).isNull();
    }

    @Test
    void ticketTypeTest() {
        BookingDetail bookingDetail = getBookingDetailRandomSampleGenerator();
        TicketType ticketTypeBack = getTicketTypeRandomSampleGenerator();

        bookingDetail.setTicketType(ticketTypeBack);
        assertThat(bookingDetail.getTicketType()).isEqualTo(ticketTypeBack);

        bookingDetail.ticketType(null);
        assertThat(bookingDetail.getTicketType()).isNull();
    }
}
