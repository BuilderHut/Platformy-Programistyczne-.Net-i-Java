package com.mycompany.carrental.repository;

import com.mycompany.carrental.domain.DrivingLicense;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the DrivingLicense entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DrivingLicenseRepository extends ReactiveCrudRepository<DrivingLicense, Long>, DrivingLicenseRepositoryInternal {
    Flux<DrivingLicense> findAllBy(Pageable pageable);

    @Query("SELECT * FROM driving_license entity WHERE entity.id not in (select customer_id from customer)")
    Flux<DrivingLicense> findAllWhereCustomerIsNull();

    @Override
    <S extends DrivingLicense> Mono<S> save(S entity);

    @Override
    Flux<DrivingLicense> findAll();

    @Override
    Mono<DrivingLicense> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DrivingLicenseRepositoryInternal {
    <S extends DrivingLicense> Mono<S> save(S entity);

    Flux<DrivingLicense> findAllBy(Pageable pageable);

    Flux<DrivingLicense> findAll();

    Mono<DrivingLicense> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<DrivingLicense> findAllBy(Pageable pageable, Criteria criteria);
}
