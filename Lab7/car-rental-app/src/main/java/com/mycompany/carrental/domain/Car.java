package com.mycompany.carrental.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.carrental.domain.enumeration.CarStatus;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Car.
 */
@Table("car")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "car")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Car implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Long)
    private Long id;

    @NotNull
    @Column("brand")
    @org.springframework.data.elasticsearch.annotations.MultiField(
        mainField = @org.springframework.data.elasticsearch.annotations.Field(
            type = org.springframework.data.elasticsearch.annotations.FieldType.Text
        ),
        otherFields = {
            @org.springframework.data.elasticsearch.annotations.InnerField(
                suffix = "keyword",
                type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword,
                ignoreAbove = 256
            ),
        }
    )
    private String brand;

    @NotNull
    @Column("model")
    @org.springframework.data.elasticsearch.annotations.MultiField(
        mainField = @org.springframework.data.elasticsearch.annotations.Field(
            type = org.springframework.data.elasticsearch.annotations.FieldType.Text
        ),
        otherFields = {
            @org.springframework.data.elasticsearch.annotations.InnerField(
                suffix = "keyword",
                type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword,
                ignoreAbove = 256
            ),
        }
    )
    private String model;

    @NotNull
    @Min(value = 1990)
    @Column("production_year")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer productionYear;

    @NotNull
    @DecimalMin(value = "0")
    @Column("daily_price")
    private Float dailyPrice;

    @NotNull
    @Column("status")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private CarStatus status;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "carses" }, allowSetters = true)
    private Set<Category> categorieses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Car id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return this.brand;
    }

    public Car brand(String brand) {
        this.setBrand(brand);
        return this;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return this.model;
    }

    public Car model(String model) {
        this.setModel(model);
        return this;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getProductionYear() {
        return this.productionYear;
    }

    public Car productionYear(Integer productionYear) {
        this.setProductionYear(productionYear);
        return this;
    }

    public void setProductionYear(Integer productionYear) {
        this.productionYear = productionYear;
    }

    public Float getDailyPrice() {
        return this.dailyPrice;
    }

    public Car dailyPrice(Float dailyPrice) {
        this.setDailyPrice(dailyPrice);
        return this;
    }

    public void setDailyPrice(Float dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public CarStatus getStatus() {
        return this.status;
    }

    public Car status(CarStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public Set<Category> getCategorieses() {
        return this.categorieses;
    }

    public void setCategorieses(Set<Category> categories) {
        this.categorieses = categories;
    }

    public Car categorieses(Set<Category> categories) {
        this.setCategorieses(categories);
        return this;
    }

    public Car addCategories(Category category) {
        this.categorieses.add(category);
        return this;
    }

    public Car removeCategories(Category category) {
        this.categorieses.remove(category);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Car)) {
            return false;
        }
        return getId() != null && getId().equals(((Car) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Car{" +
            "id=" + getId() +
            ", brand='" + getBrand() + "'" +
            ", model='" + getModel() + "'" +
            ", productionYear=" + getProductionYear() +
            ", dailyPrice=" + getDailyPrice() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
