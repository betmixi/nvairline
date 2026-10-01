package com.dugx.event.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class BookingDetailTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static BookingDetail getBookingDetailSample1() {
        return new BookingDetail().id(1L);
    }

    public static BookingDetail getBookingDetailSample2() {
        return new BookingDetail().id(2L);
    }

    public static BookingDetail getBookingDetailRandomSampleGenerator() {
        return new BookingDetail().id(longCount.incrementAndGet());
    }
}
