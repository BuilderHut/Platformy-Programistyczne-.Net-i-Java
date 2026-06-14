package com.mycompany.carrental.domain;

import static com.mycompany.carrental.domain.CarTestSamples.*;
import static com.mycompany.carrental.domain.CustomerTestSamples.*;
import static com.mycompany.carrental.domain.RentalTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.carrental.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RentalTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Rental.class);
        Rental rental1 = getRentalSample1();
        Rental rental2 = new Rental();
        assertThat(rental1).isNotEqualTo(rental2);

        rental2.setId(rental1.getId());
        assertThat(rental1).isEqualTo(rental2);

        rental2 = getRentalSample2();
        assertThat(rental1).isNotEqualTo(rental2);
    }

    @Test
    void carTest() {
        Rental rental = getRentalRandomSampleGenerator();
        Car carBack = getCarRandomSampleGenerator();

        rental.setCar(carBack);
        assertThat(rental.getCar()).isEqualTo(carBack);

        rental.car(null);
        assertThat(rental.getCar()).isNull();
    }

    @Test
    void customerTest() {
        Rental rental = getRentalRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        rental.setCustomer(customerBack);
        assertThat(rental.getCustomer()).isEqualTo(customerBack);

        rental.customer(null);
        assertThat(rental.getCustomer()).isNull();
    }
}
