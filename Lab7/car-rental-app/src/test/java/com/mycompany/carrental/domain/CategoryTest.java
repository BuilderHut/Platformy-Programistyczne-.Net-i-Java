package com.mycompany.carrental.domain;

import static com.mycompany.carrental.domain.CarTestSamples.*;
import static com.mycompany.carrental.domain.CategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.carrental.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Category.class);
        Category category1 = getCategorySample1();
        Category category2 = new Category();
        assertThat(category1).isNotEqualTo(category2);

        category2.setId(category1.getId());
        assertThat(category1).isEqualTo(category2);

        category2 = getCategorySample2();
        assertThat(category1).isNotEqualTo(category2);
    }

    @Test
    void carsTest() {
        Category category = getCategoryRandomSampleGenerator();
        Car carBack = getCarRandomSampleGenerator();

        category.addCars(carBack);
        assertThat(category.getCarses()).containsOnly(carBack);
        assertThat(carBack.getCategorieses()).containsOnly(category);

        category.removeCars(carBack);
        assertThat(category.getCarses()).doesNotContain(carBack);
        assertThat(carBack.getCategorieses()).doesNotContain(category);

        category.carses(new HashSet<>(Set.of(carBack)));
        assertThat(category.getCarses()).containsOnly(carBack);
        assertThat(carBack.getCategorieses()).containsOnly(category);

        category.setCarses(new HashSet<>());
        assertThat(category.getCarses()).doesNotContain(carBack);
        assertThat(carBack.getCategorieses()).doesNotContain(category);
    }
}
