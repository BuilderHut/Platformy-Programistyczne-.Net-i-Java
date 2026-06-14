package com.mycompany.carrental.service.mapper;

import static com.mycompany.carrental.domain.DrivingLicenseAsserts.*;
import static com.mycompany.carrental.domain.DrivingLicenseTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DrivingLicenseMapperTest {

    private DrivingLicenseMapper drivingLicenseMapper;

    @BeforeEach
    void setUp() {
        drivingLicenseMapper = new DrivingLicenseMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDrivingLicenseSample1();
        var actual = drivingLicenseMapper.toEntity(drivingLicenseMapper.toDto(expected));
        assertDrivingLicenseAllPropertiesEquals(expected, actual);
    }
}
