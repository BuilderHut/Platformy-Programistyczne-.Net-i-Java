package com.mycompany.carrental.repository;

import com.mycompany.carrental.domain.Rental;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Rental entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RentalRepository extends ReactiveCrudRepository<Rental, Long>, RentalRepositoryInternal {
    Flux<Rental> findAllBy(Pageable pageable);

    @Query("SELECT * FROM rental entity WHERE entity.car_id = :id")
    Flux<Rental> findByCar(Long id);

    @Query("SELECT * FROM rental entity WHERE entity.car_id IS NULL")
    Flux<Rental> findAllWhereCarIsNull();

    @Query("SELECT * FROM rental entity WHERE entity.customer_id = :id")
    Flux<Rental> findByCustomer(Long id);

    @Query("SELECT * FROM rental entity WHERE entity.customer_id IS NULL")
    Flux<Rental> findAllWhereCustomerIsNull();

    @Override
    <S extends Rental> Mono<S> save(S entity);

    @Override
    Flux<Rental> findAll();

    @Override
    Mono<Rental> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RentalRepositoryInternal {
    <S extends Rental> Mono<S> save(S entity);

    Flux<Rental> findAllBy(Pageable pageable);

    Flux<Rental> findAll();

    Mono<Rental> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Rental> findAllBy(Pageable pageable, Criteria criteria);
}
