package com.mycompany.carrental.service.mapper;

import com.mycompany.carrental.domain.Customer;
import com.mycompany.carrental.domain.DrivingLicense;
import com.mycompany.carrental.service.dto.CustomerDTO;
import com.mycompany.carrental.service.dto.DrivingLicenseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Customer} and its DTO {@link CustomerDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper extends EntityMapper<CustomerDTO, Customer> {
    @Mapping(target = "drivingLicense", source = "drivingLicense", qualifiedByName = "drivingLicenseLicenseNumber")
    CustomerDTO toDto(Customer s);

    @Named("drivingLicenseLicenseNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "licenseNumber", source = "licenseNumber")
    DrivingLicenseDTO toDtoDrivingLicenseLicenseNumber(DrivingLicense drivingLicense);
}
