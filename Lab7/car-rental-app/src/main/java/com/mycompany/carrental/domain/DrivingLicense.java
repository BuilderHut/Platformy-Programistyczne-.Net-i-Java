package com.mycompany.carrental.domain;

import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A DrivingLicense.
 */
@Table("driving_license")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "drivinglicense")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DrivingLicense implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Long)
    private Long id;

    @NotNull
    @Column("license_number")
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
    private String licenseNumber;

    @NotNull
    @Column("issue_date")
    private LocalDate issueDate;

    @NotNull
    @Column("expiration_date")
    private LocalDate expirationDate;

    @org.springframework.data.annotation.Transient
    private Customer customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DrivingLicense id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicenseNumber() {
        return this.licenseNumber;
    }

    public DrivingLicense licenseNumber(String licenseNumber) {
        this.setLicenseNumber(licenseNumber);
        return this;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public LocalDate getIssueDate() {
        return this.issueDate;
    }

    public DrivingLicense issueDate(LocalDate issueDate) {
        this.setIssueDate(issueDate);
        return this;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpirationDate() {
        return this.expirationDate;
    }

    public DrivingLicense expirationDate(LocalDate expirationDate) {
        this.setExpirationDate(expirationDate);
        return this;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        if (this.customer != null) {
            this.customer.setDrivingLicense(null);
        }
        if (customer != null) {
            customer.setDrivingLicense(this);
        }
        this.customer = customer;
    }

    public DrivingLicense customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DrivingLicense)) {
            return false;
        }
        return getId() != null && getId().equals(((DrivingLicense) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DrivingLicense{" +
            "id=" + getId() +
            ", licenseNumber='" + getLicenseNumber() + "'" +
            ", issueDate='" + getIssueDate() + "'" +
            ", expirationDate='" + getExpirationDate() + "'" +
            "}";
    }
}
