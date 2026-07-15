package com.dugx.event.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CheckInTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static CheckIn getCheckInSample1() {
        return new CheckIn().id(1L);
    }

    public static CheckIn getCheckInSample2() {
        return new CheckIn().id(2L);
    }

    public static CheckIn getCheckInRandomSampleGenerator() {
        return new CheckIn().id(longCount.incrementAndGet());
    }
}
