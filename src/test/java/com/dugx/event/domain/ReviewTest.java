package com.dugx.event.domain;

import static com.dugx.event.domain.EventTestSamples.*;
import static com.dugx.event.domain.ReviewTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dugx.event.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Review.class);
        Review review1 = getReviewSample1();
        Review review2 = new Review();
        assertThat(review1).isNotEqualTo(review2);

        review2.setId(review1.getId());
        assertThat(review1).isEqualTo(review2);

        review2 = getReviewSample2();
        assertThat(review1).isNotEqualTo(review2);
    }

    @Test
    void eventTest() {
        Review review = getReviewRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        review.setEvent(eventBack);
        assertThat(review.getEvent()).isEqualTo(eventBack);

        review.event(null);
        assertThat(review.getEvent()).isNull();
    }
}
