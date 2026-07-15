package com.dugx.event.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dugx.event.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CheckInDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CheckInDTO.class);
        CheckInDTO checkInDTO1 = new CheckInDTO();
        checkInDTO1.setId(1L);
        CheckInDTO checkInDTO2 = new CheckInDTO();
        assertThat(checkInDTO1).isNotEqualTo(checkInDTO2);
        checkInDTO2.setId(checkInDTO1.getId());
        assertThat(checkInDTO1).isEqualTo(checkInDTO2);
        checkInDTO2.setId(2L);
        assertThat(checkInDTO1).isNotEqualTo(checkInDTO2);
        checkInDTO1.setId(null);
        assertThat(checkInDTO1).isNotEqualTo(checkInDTO2);
    }
}
