package com.dugx.event.domain;

import static com.dugx.event.domain.EventTestSamples.*;
import static com.dugx.event.domain.FavoriteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dugx.event.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FavoriteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Favorite.class);
        Favorite favorite1 = getFavoriteSample1();
        Favorite favorite2 = new Favorite();
        assertThat(favorite1).isNotEqualTo(favorite2);

        favorite2.setId(favorite1.getId());
        assertThat(favorite1).isEqualTo(favorite2);

        favorite2 = getFavoriteSample2();
        assertThat(favorite1).isNotEqualTo(favorite2);
    }

    @Test
    void eventTest() {
        Favorite favorite = getFavoriteRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        favorite.setEvent(eventBack);
        assertThat(favorite.getEvent()).isEqualTo(eventBack);

        favorite.event(null);
        assertThat(favorite.getEvent()).isNull();
    }
}
