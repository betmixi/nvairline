package com.dugx.event.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EventImageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static EventImage getEventImageSample1() {
        return new EventImage().id(1L).imageUrl("imageUrl1");
    }

    public static EventImage getEventImageSample2() {
        return new EventImage().id(2L).imageUrl("imageUrl2");
    }

    public static EventImage getEventImageRandomSampleGenerator() {
        return new EventImage().id(longCount.incrementAndGet()).imageUrl(UUID.randomUUID().toString());
    }
}
