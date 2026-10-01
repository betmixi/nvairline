package com.dugx.event.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ShowtimeSeatTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static ShowtimeSeat getShowtimeSeatSample1() {
        return new ShowtimeSeat().id(1L);
    }

    public static ShowtimeSeat getShowtimeSeatSample2() {
        return new ShowtimeSeat().id(2L);
    }

    public static ShowtimeSeat getShowtimeSeatRandomSampleGenerator() {
        return new ShowtimeSeat().id(longCount.incrementAndGet());
    }
}
