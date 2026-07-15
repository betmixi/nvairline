package com.dugx.event.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BookingTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Booking getBookingSample1() {
        return new Booking().id(1L).status("status1");
    }

    public static Booking getBookingSample2() {
        return new Booking().id(2L).status("status2");
    }

    public static Booking getBookingRandomSampleGenerator() {
        return new Booking().id(longCount.incrementAndGet()).status(UUID.randomUUID().toString());
    }
}
