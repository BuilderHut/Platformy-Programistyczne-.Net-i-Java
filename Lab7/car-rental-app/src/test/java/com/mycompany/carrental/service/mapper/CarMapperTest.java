package com.mycompany.carrental.service.mapper;

import static com.mycompany.carrental.domain.CarAsserts.*;
import static com.mycompany.carrental.domain.CarTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarMapperTest {

    private CarMapper carMapper;

    @BeforeEach
    void setUp() {
        carMapper = new CarMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCarSample1();
        var actual = carMapper.toEntity(carMapper.toDto(expected));
        assertCarAllPropertiesEquals(expected, actual);
    }
}
