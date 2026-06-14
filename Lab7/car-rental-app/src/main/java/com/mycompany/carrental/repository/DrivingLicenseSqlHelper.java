package com.mycompany.carrental.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class DrivingLicenseSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("license_number", table, columnPrefix + "_license_number"));
        columns.add(Column.aliased("issue_date", table, columnPrefix + "_issue_date"));
        columns.add(Column.aliased("expiration_date", table, columnPrefix + "_expiration_date"));

        return columns;
    }
}
