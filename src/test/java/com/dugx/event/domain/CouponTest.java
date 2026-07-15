package com.dugx.event.domain;

import static com.dugx.event.domain.CouponTestSamples.*;
import static com.dugx.event.domain.EventTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dugx.event.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CouponTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Coupon.class);
        Coupon coupon1 = getCouponSample1();
        Coupon coupon2 = new Coupon();
        assertThat(coupon1).isNotEqualTo(coupon2);

        coupon2.setId(coupon1.getId());
        assertThat(coupon1).isEqualTo(coupon2);

        coupon2 = getCouponSample2();
        assertThat(coupon1).isNotEqualTo(coupon2);
    }

    @Test
    void eventTest() {
        Coupon coupon = getCouponRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        coupon.setEvent(eventBack);
        assertThat(coupon.getEvent()).isEqualTo(eventBack);

        coupon.event(null);
        assertThat(coupon.getEvent()).isNull();
    }
}
