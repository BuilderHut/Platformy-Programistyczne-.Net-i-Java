package com.mycompany.carrental.domain;

import static com.mycompany.carrental.domain.CustomerTestSamples.*;
import static com.mycompany.carrental.domain.DrivingLicenseTestSamples.*;
import static com.mycompany.carrental.domain.RentalTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.carrental.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = getCustomerSample1();
        Customer customer2 = new Customer();
        assertThat(customer1).isNotEqualTo(customer2);

        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);

        customer2 = getCustomerSample2();
        assertThat(customer1).isNotEqualTo(customer2);
    }

    @Test
    void drivingLicenseTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        DrivingLicense drivingLicenseBack = getDrivingLicenseRandomSampleGenerator();

        customer.setDrivingLicense(drivingLicenseBack);
        assertThat(customer.getDrivingLicense()).isEqualTo(drivingLicenseBack);

        customer.drivingLicense(null);
        assertThat(customer.getDrivingLicense()).isNull();
    }

    @Test
    void rentalsTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Rental rentalBack = getRentalRandomSampleGenerator();

        customer.addRentals(rentalBack);
        assertThat(customer.getRentalses()).containsOnly(rentalBack);
        assertThat(rentalBack.getCustomer()).isEqualTo(customer);

        customer.removeRentals(rentalBack);
        assertThat(customer.getRentalses()).doesNotContain(rentalBack);
        assertThat(rentalBack.getCustomer()).isNull();

        customer.rentalses(new HashSet<>(Set.of(rentalBack)));
        assertThat(customer.getRentalses()).containsOnly(rentalBack);
        assertThat(rentalBack.getCustomer()).isEqualTo(customer);

        customer.setRentalses(new HashSet<>());
        assertThat(customer.getRentalses()).doesNotContain(rentalBack);
        assertThat(rentalBack.getCustomer()).isNull();
    }
}
