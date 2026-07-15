package com.dugx.event.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dugx.event.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventImageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventImageDTO.class);
        EventImageDTO eventImageDTO1 = new EventImageDTO();
        eventImageDTO1.setId(1L);
        EventImageDTO eventImageDTO2 = new EventImageDTO();
        assertThat(eventImageDTO1).isNotEqualTo(eventImageDTO2);
        eventImageDTO2.setId(eventImageDTO1.getId());
        assertThat(eventImageDTO1).isEqualTo(eventImageDTO2);
        eventImageDTO2.setId(2L);
        assertThat(eventImageDTO1).isNotEqualTo(eventImageDTO2);
        eventImageDTO1.setId(null);
        assertThat(eventImageDTO1).isNotEqualTo(eventImageDTO2);
    }
}
