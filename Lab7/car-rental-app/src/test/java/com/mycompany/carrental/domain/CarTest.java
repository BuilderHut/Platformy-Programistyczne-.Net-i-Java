package com.mycompany.carrental.domain;

import static com.mycompany.carrental.domain.CarTestSamples.*;
import static com.mycompany.carrental.domain.CategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.carrental.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CarTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Car.class);
        Car car1 = getCarSample1();
        Car car2 = new Car();
        assertThat(car1).isNotEqualTo(car2);

        car2.setId(car1.getId());
        assertThat(car1).isEqualTo(car2);

        car2 = getCarSample2();
        assertThat(car1).isNotEqualTo(car2);
    }

    @Test
    void categoriesTest() {
        Car car = getCarRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        car.addCategories(categoryBack);
        assertThat(car.getCategorieses()).containsOnly(categoryBack);

        car.removeCategories(categoryBack);
        assertThat(car.getCategorieses()).doesNotContain(categoryBack);

        car.categorieses(new HashSet<>(Set.of(categoryBack)));
        assertThat(car.getCategorieses()).containsOnly(categoryBack);

        car.setCategorieses(new HashSet<>());
        assertThat(car.getCategorieses()).doesNotContain(categoryBack);
    }
}
