package com.dugx.event.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dugx.event.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrganizerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrganizerDTO.class);
        OrganizerDTO organizerDTO1 = new OrganizerDTO();
        organizerDTO1.setId(1L);
        OrganizerDTO organizerDTO2 = new OrganizerDTO();
        assertThat(organizerDTO1).isNotEqualTo(organizerDTO2);
        organizerDTO2.setId(organizerDTO1.getId());
        assertThat(organizerDTO1).isEqualTo(organizerDTO2);
        organizerDTO2.setId(2L);
        assertThat(organizerDTO1).isNotEqualTo(organizerDTO2);
        organizerDTO1.setId(null);
        assertThat(organizerDTO1).isNotEqualTo(organizerDTO2);
    }
}
