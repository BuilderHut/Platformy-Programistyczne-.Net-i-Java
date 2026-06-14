package com.mycompany.carrental.service.dto;

import com.mycompany.carrental.domain.enumeration.CarStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.carrental.domain.Car} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarDTO implements Serializable {

    private Long id;

    @NotNull
    private String brand;

    @NotNull
    private String model;

    @NotNull
    @Min(value = 1990)
    private Integer productionYear;

    @NotNull
    @DecimalMin(value = "0")
    private Float dailyPrice;

    @NotNull
    private CarStatus status;

    private Set<CategoryDTO> categorieses = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(Integer productionYear) {
        this.productionYear = productionYear;
    }

    public Float getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(Float dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public Set<CategoryDTO> getCategorieses() {
        return categorieses;
    }

    public void setCategorieses(Set<CategoryDTO> categorieses) {
        this.categorieses = categorieses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CarDTO)) {
            return false;
        }

        CarDTO carDTO = (CarDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, carDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarDTO{" +
            "id=" + getId() +
            ", brand='" + getBrand() + "'" +
            ", model='" + getModel() + "'" +
            ", productionYear=" + getProductionYear() +
            ", dailyPrice=" + getDailyPrice() +
            ", status='" + getStatus() + "'" +
            ", categorieses=" + getCategorieses() +
            "}";
    }
}
