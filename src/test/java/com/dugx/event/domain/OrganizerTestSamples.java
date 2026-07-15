package com.dugx.event.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OrganizerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Organizer getOrganizerSample1() {
        return new Organizer().id(1L).companyName("companyName1").taxCode("taxCode1");
    }

    public static Organizer getOrganizerSample2() {
        return new Organizer().id(2L).companyName("companyName2").taxCode("taxCode2");
    }

    public static Organizer getOrganizerRandomSampleGenerator() {
        return new Organizer()
            .id(longCount.incrementAndGet())
            .companyName(UUID.randomUUID().toString())
            .taxCode(UUID.randomUUID().toString());
    }
}
