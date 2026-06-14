package com.mycompany.carrental.service.mapper;

import com.mycompany.carrental.domain.DrivingLicense;
import com.mycompany.carrental.service.dto.DrivingLicenseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DrivingLicense} and its DTO {@link DrivingLicenseDTO}.
 */
@Mapper(componentModel = "spring")
public interface DrivingLicenseMapper extends EntityMapper<DrivingLicenseDTO, DrivingLicense> {}
