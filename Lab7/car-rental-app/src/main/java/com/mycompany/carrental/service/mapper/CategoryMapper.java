package com.mycompany.carrental.service.mapper;

import com.mycompany.carrental.domain.Car;
import com.mycompany.carrental.domain.Category;
import com.mycompany.carrental.service.dto.CarDTO;
import com.mycompany.carrental.service.dto.CategoryDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Category} and its DTO {@link CategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper extends EntityMapper<CategoryDTO, Category> {
    @Mapping(target = "carses", source = "carses", qualifiedByName = "carIdSet")
    CategoryDTO toDto(Category s);

    @Mapping(target = "carses", ignore = true)
    @Mapping(target = "removeCars", ignore = true)
    Category toEntity(CategoryDTO categoryDTO);

    @Named("carId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CarDTO toDtoCarId(Car car);

    @Named("carIdSet")
    default Set<CarDTO> toDtoCarIdSet(Set<Car> car) {
        return car.stream().map(this::toDtoCarId).collect(Collectors.toSet());
    }
}
