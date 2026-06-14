package com.mycompany.carrental.domain;

import static com.mycompany.carrental.domain.CustomerTestSamples.*;
import static com.mycompany.carrental.domain.DrivingLicenseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.carrental.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DrivingLicenseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DrivingLicense.class);
        DrivingLicense drivingLicense1 = getDrivingLicenseSample1();
        DrivingLicense drivingLicense2 = new DrivingLicense();
        assertThat(drivingLicense1).isNotEqualTo(drivingLicense2);

        drivingLicense2.setId(drivingLicense1.getId());
        assertThat(drivingLicense1).isEqualTo(drivingLicense2);

        drivingLicense2 = getDrivingLicenseSample2();
        assertThat(drivingLicense1).isNotEqualTo(drivingLicense2);
    }

    @Test
    void customerTest() {
        DrivingLicense drivingLicense = getDrivingLicenseRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        drivingLicense.setCustomer(customerBack);
        assertThat(drivingLicense.getCustomer()).isEqualTo(customerBack);
        assertThat(customerBack.getDrivingLicense()).isEqualTo(drivingLicense);

        drivingLicense.customer(null);
        assertThat(drivingLicense.getCustomer()).isNull();
        assertThat(customerBack.getDrivingLicense()).isNull();
    }
}
