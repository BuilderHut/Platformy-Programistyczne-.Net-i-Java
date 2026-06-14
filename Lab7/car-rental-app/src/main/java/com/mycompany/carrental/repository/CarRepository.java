package com.mycompany.carrental.repository;

import com.mycompany.carrental.domain.Car;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Car entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CarRepository extends ReactiveCrudRepository<Car, Long>, CarRepositoryInternal {
    Flux<Car> findAllBy(Pageable pageable);

    @Override
    Mono<Car> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Car> findAllWithEagerRelationships();

    @Override
    Flux<Car> findAllWithEagerRelationships(Pageable page);

    @Query(
        "SELECT entity.* FROM car entity JOIN rel_car__categories joinTable ON entity.id = joinTable.categories_id WHERE joinTable.categories_id = :id"
    )
    Flux<Car> findByCategories(Long id);

    @Override
    <S extends Car> Mono<S> save(S entity);

    @Override
    Flux<Car> findAll();

    @Override
    Mono<Car> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CarRepositoryInternal {
    <S extends Car> Mono<S> save(S entity);

    Flux<Car> findAllBy(Pageable pageable);

    Flux<Car> findAll();

    Mono<Car> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Car> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Car> findOneWithEagerRelationships(Long id);

    Flux<Car> findAllWithEagerRelationships();

    Flux<Car> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
