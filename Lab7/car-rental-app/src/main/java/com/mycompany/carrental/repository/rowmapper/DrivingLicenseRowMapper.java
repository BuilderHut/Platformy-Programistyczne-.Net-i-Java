package com.mycompany.carrental.repository.rowmapper;

import com.mycompany.carrental.domain.DrivingLicense;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link DrivingLicense}, with proper type conversions.
 */
@Service
public class DrivingLicenseRowMapper implements BiFunction<Row, String, DrivingLicense> {

    private final ColumnConverter converter;

    public DrivingLicenseRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link DrivingLicense} stored in the database.
     */
    @Override
    public DrivingLicense apply(Row row, String prefix) {
        DrivingLicense entity = new DrivingLicense();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLicenseNumber(converter.fromRow(row, prefix + "_license_number", String.class));
        entity.setIssueDate(converter.fromRow(row, prefix + "_issue_date", LocalDate.class));
        entity.setExpirationDate(converter.fromRow(row, prefix + "_expiration_date", LocalDate.class));
        return entity;
    }
}
