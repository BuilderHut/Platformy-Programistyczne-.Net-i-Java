package com.mycompany.carrental.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CarTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Car getCarSample1() {
        return new Car().id(1L).brand("brand1").model("model1").productionYear(1);
    }

    public static Car getCarSample2() {
        return new Car().id(2L).brand("brand2").model("model2").productionYear(2);
    }

    public static Car getCarRandomSampleGenerator() {
        return new Car()
            .id(longCount.incrementAndGet())
            .brand(UUID.randomUUID().toString())
            .model(UUID.randomUUID().toString())
            .productionYear(intCount.incrementAndGet());
    }
}
