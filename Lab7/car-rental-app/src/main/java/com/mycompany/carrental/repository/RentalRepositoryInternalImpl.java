package com.mycompany.carrental.repository;

import com.mycompany.carrental.domain.Rental;
import com.mycompany.carrental.repository.rowmapper.CarRowMapper;
import com.mycompany.carrental.repository.rowmapper.CustomerRowMapper;
import com.mycompany.carrental.repository.rowmapper.RentalRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Rental entity.
 */
@SuppressWarnings("unused")
class RentalRepositoryInternalImpl extends SimpleR2dbcRepository<Rental, Long> implements RentalRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CarRowMapper carMapper;
    private final CustomerRowMapper customerMapper;
    private final RentalRowMapper rentalMapper;

    private static final Table entityTable = Table.aliased("rental", EntityManager.ENTITY_ALIAS);
    private static final Table carTable = Table.aliased("car", "car");
    private static final Table customerTable = Table.aliased("customer", "customer");

    public RentalRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CarRowMapper carMapper,
        CustomerRowMapper customerMapper,
        RentalRowMapper rentalMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Rental.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.carMapper = carMapper;
        this.customerMapper = customerMapper;
        this.rentalMapper = rentalMapper;
    }

    @Override
    public Flux<Rental> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Rental> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = RentalSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CarSqlHelper.getColumns(carTable, "car"));
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(carTable)
            .on(Column.create("car_id", entityTable))
            .equals(Column.create("id", carTable))
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Rental.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Rental> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Rental> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Rental process(Row row, RowMetadata metadata) {
        Rental entity = rentalMapper.apply(row, "e");
        entity.setCar(carMapper.apply(row, "car"));
        entity.setCustomer(customerMapper.apply(row, "customer"));
        return entity;
    }

    @Override
    public <S extends Rental> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
