package com.dugx.event.domain;

import static com.dugx.event.domain.EventImageTestSamples.*;
import static com.dugx.event.domain.EventTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dugx.event.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventImageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventImage.class);
        EventImage eventImage1 = getEventImageSample1();
        EventImage eventImage2 = new EventImage();
        assertThat(eventImage1).isNotEqualTo(eventImage2);

        eventImage2.setId(eventImage1.getId());
        assertThat(eventImage1).isEqualTo(eventImage2);

        eventImage2 = getEventImageSample2();
        assertThat(eventImage1).isNotEqualTo(eventImage2);
    }

    @Test
    void eventTest() {
        EventImage eventImage = getEventImageRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        eventImage.setEvent(eventBack);
        assertThat(eventImage.getEvent()).isEqualTo(eventBack);

        eventImage.event(null);
        assertThat(eventImage.getEvent()).isNull();
    }
}
