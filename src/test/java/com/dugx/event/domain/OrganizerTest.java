package com.dugx.event.domain;

import static com.dugx.event.domain.OrganizerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dugx.event.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrganizerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Organizer.class);
        Organizer organizer1 = getOrganizerSample1();
        Organizer organizer2 = new Organizer();
        assertThat(organizer1).isNotEqualTo(organizer2);

        organizer2.setId(organizer1.getId());
        assertThat(organizer1).isEqualTo(organizer2);

        organizer2 = getOrganizerSample2();
        assertThat(organizer1).isNotEqualTo(organizer2);
    }
}
