package com.mycompany.carrental.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.carrental.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DrivingLicenseDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DrivingLicenseDTO.class);
        DrivingLicenseDTO drivingLicenseDTO1 = new DrivingLicenseDTO();
        drivingLicenseDTO1.setId(1L);
        DrivingLicenseDTO drivingLicenseDTO2 = new DrivingLicenseDTO();
        assertThat(drivingLicenseDTO1).isNotEqualTo(drivingLicenseDTO2);
        drivingLicenseDTO2.setId(drivingLicenseDTO1.getId());
        assertThat(drivingLicenseDTO1).isEqualTo(drivingLicenseDTO2);
        drivingLicenseDTO2.setId(2L);
        assertThat(drivingLicenseDTO1).isNotEqualTo(drivingLicenseDTO2);
        drivingLicenseDTO1.setId(null);
        assertThat(drivingLicenseDTO1).isNotEqualTo(drivingLicenseDTO2);
    }
}
