package com.dugx.event.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ReportTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Report getReportSample1() {
        return new Report().id(1L).status("status1");
    }

    public static Report getReportSample2() {
        return new Report().id(2L).status("status2");
    }

    public static Report getReportRandomSampleGenerator() {
        return new Report().id(longCount.incrementAndGet()).status(UUID.randomUUID().toString());
    }
}
