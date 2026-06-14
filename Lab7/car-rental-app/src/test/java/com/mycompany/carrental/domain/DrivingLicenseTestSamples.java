package com.mycompany.carrental.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DrivingLicenseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static DrivingLicense getDrivingLicenseSample1() {
        return new DrivingLicense().id(1L).licenseNumber("licenseNumber1");
    }

    public static DrivingLicense getDrivingLicenseSample2() {
        return new DrivingLicense().id(2L).licenseNumber("licenseNumber2");
    }

    public static DrivingLicense getDrivingLicenseRandomSampleGenerator() {
        return new DrivingLicense().id(longCount.incrementAndGet()).licenseNumber(UUID.randomUUID().toString());
    }
}
