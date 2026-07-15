package com.dugx.event.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Payment getPaymentSample1() {
        return new Payment().id(1L).method("method1").transactionCode("transactionCode1").status("status1");
    }

    public static Payment getPaymentSample2() {
        return new Payment().id(2L).method("method2").transactionCode("transactionCode2").status("status2");
    }

    public static Payment getPaymentRandomSampleGenerator() {
        return new Payment()
            .id(longCount.incrementAndGet())
            .method(UUID.randomUUID().toString())
            .transactionCode(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString());
    }
}
