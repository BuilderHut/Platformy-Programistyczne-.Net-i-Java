package com.mycompany.carrental.service.mapper;

import com.mycompany.carrental.domain.Car;
import com.mycompany.carrental.domain.Customer;
import com.mycompany.carrental.domain.Rental;
import com.mycompany.carrental.service.dto.CarDTO;
import com.mycompany.carrental.service.dto.CustomerDTO;
import com.mycompany.carrental.service.dto.RentalDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Rental} and its DTO {@link RentalDTO}.
 */
@Mapper(componentModel = "spring")
public interface RentalMapper extends EntityMapper<RentalDTO, Rental> {
    @Mapping(target = "car", source = "car", qualifiedByName = "carId")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    RentalDTO toDto(Rental s);

    @Named("carId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CarDTO toDtoCarId(Car car);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);
}
