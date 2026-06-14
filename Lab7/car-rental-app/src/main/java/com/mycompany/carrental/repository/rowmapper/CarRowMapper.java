package com.mycompany.carrental.repository.rowmapper;

import com.mycompany.carrental.domain.Car;
import com.mycompany.carrental.domain.enumeration.CarStatus;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Car}, with proper type conversions.
 */
@Service
public class CarRowMapper implements BiFunction<Row, String, Car> {

    private final ColumnConverter converter;

    public CarRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Car} stored in the database.
     */
    @Override
    public Car apply(Row row, String prefix) {
        Car entity = new Car();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setBrand(converter.fromRow(row, prefix + "_brand", String.class));
        entity.setModel(converter.fromRow(row, prefix + "_model", String.class));
        entity.setProductionYear(converter.fromRow(row, prefix + "_production_year", Integer.class));
        entity.setDailyPrice(converter.fromRow(row, prefix + "_daily_price", Float.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", CarStatus.class));
        return entity;
    }
}
