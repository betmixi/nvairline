package com.dugx.event.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dugx.event.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookingDetailDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookingDetailDTO.class);
        BookingDetailDTO bookingDetailDTO1 = new BookingDetailDTO();
        bookingDetailDTO1.setId(1L);
        BookingDetailDTO bookingDetailDTO2 = new BookingDetailDTO();
        assertThat(bookingDetailDTO1).isNotEqualTo(bookingDetailDTO2);
        bookingDetailDTO2.setId(bookingDetailDTO1.getId());
        assertThat(bookingDetailDTO1).isEqualTo(bookingDetailDTO2);
        bookingDetailDTO2.setId(2L);
        assertThat(bookingDetailDTO1).isNotEqualTo(bookingDetailDTO2);
        bookingDetailDTO1.setId(null);
        assertThat(bookingDetailDTO1).isNotEqualTo(bookingDetailDTO2);
    }
}
