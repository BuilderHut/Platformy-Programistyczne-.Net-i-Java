package com.mycompany.carrental.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.carrental.domain.DrivingLicense} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DrivingLicenseDTO implements Serializable {

    private Long id;

    @NotNull
    private String licenseNumber;

    @NotNull
    private LocalDate issueDate;

    @NotNull
    private LocalDate expirationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DrivingLicenseDTO)) {
            return false;
        }

        DrivingLicenseDTO drivingLicenseDTO = (DrivingLicenseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, drivingLicenseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DrivingLicenseDTO{" +
            "id=" + getId() +
            ", licenseNumber='" + getLicenseNumber() + "'" +
            ", issueDate='" + getIssueDate() + "'" +
            ", expirationDate='" + getExpirationDate() + "'" +
            "}";
    }
}
