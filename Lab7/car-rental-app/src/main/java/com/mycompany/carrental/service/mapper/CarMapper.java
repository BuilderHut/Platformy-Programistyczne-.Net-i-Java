package com.mycompany.carrental.service.mapper;

import com.mycompany.carrental.domain.Car;
import com.mycompany.carrental.domain.Category;
import com.mycompany.carrental.service.dto.CarDTO;
import com.mycompany.carrental.service.dto.CategoryDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Car} and its DTO {@link CarDTO}.
 */
@Mapper(componentModel = "spring")
public interface CarMapper extends EntityMapper<CarDTO, Car> {
    @Mapping(target = "categorieses", source = "categorieses", qualifiedByName = "categoryNameSet")
    CarDTO toDto(Car s);

    @Mapping(target = "removeCategories", ignore = true)
    Car toEntity(CarDTO carDTO);

    @Named("categoryName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CategoryDTO toDtoCategoryName(Category category);

    @Named("categoryNameSet")
    default Set<CategoryDTO> toDtoCategoryNameSet(Set<Category> category) {
        return category.stream().map(this::toDtoCategoryName).collect(Collectors.toSet());
    }
}
