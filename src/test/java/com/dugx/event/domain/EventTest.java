package com.dugx.event.domain;

import static com.dugx.event.domain.AddressTestSamples.*;
import static com.dugx.event.domain.CategoryTestSamples.*;
import static com.dugx.event.domain.EventTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dugx.event.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Event.class);
        Event event1 = getEventSample1();
        Event event2 = new Event();
        assertThat(event1).isNotEqualTo(event2);

        event2.setId(event1.getId());
        assertThat(event1).isEqualTo(event2);

        event2 = getEventSample2();
        assertThat(event1).isNotEqualTo(event2);
    }

    @Test
    void categoryTest() {
        Event event = getEventRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        event.setCategory(categoryBack);
        assertThat(event.getCategory()).isEqualTo(categoryBack);

        event.category(null);
        assertThat(event.getCategory()).isNull();
    }

    @Test
    void addressTest() {
        Event event = getEventRandomSampleGenerator();
        Address addressBack = getAddressRandomSampleGenerator();

        event.setAddress(addressBack);
        assertThat(event.getAddress()).isEqualTo(addressBack);

        event.address(null);
        assertThat(event.getAddress()).isNull();
    }
}
