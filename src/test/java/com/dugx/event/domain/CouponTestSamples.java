package com.dugx.event.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CouponTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Coupon getCouponSample1() {
        return new Coupon().id(1L).code("code1").quantity(1);
    }

    public static Coupon getCouponSample2() {
        return new Coupon().id(2L).code("code2").quantity(2);
    }

    public static Coupon getCouponRandomSampleGenerator() {
        return new Coupon().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).quantity(intCount.incrementAndGet());
    }
}
