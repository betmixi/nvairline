package com.dugx.event.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BookingDetailTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static BookingDetail getBookingDetailSample1() {
        return new BookingDetail().id(1L).quantity(1);
    }

    public static BookingDetail getBookingDetailSample2() {
        return new BookingDetail().id(2L).quantity(2);
    }

    public static BookingDetail getBookingDetailRandomSampleGenerator() {
        return new BookingDetail().id(longCount.incrementAndGet()).quantity(intCount.incrementAndGet());
    }
}
