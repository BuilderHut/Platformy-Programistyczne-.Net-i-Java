package com.mycompany.carrental.service;

import com.mycompany.carrental.repository.DrivingLicenseRepository;
import com.mycompany.carrental.repository.search.DrivingLicenseSearchRepository;
import com.mycompany.carrental.service.dto.DrivingLicenseDTO;
import com.mycompany.carrental.service.mapper.DrivingLicenseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.carrental.domain.DrivingLicense}.
 */
@Service
@Transactional
public class DrivingLicenseService {

    private static final Logger LOG = LoggerFactory.getLogger(DrivingLicenseService.class);

    private final DrivingLicenseRepository drivingLicenseRepository;

    private final DrivingLicenseMapper drivingLicenseMapper;

    private final DrivingLicenseSearchRepository drivingLicenseSearchRepository;

    public DrivingLicenseService(
        DrivingLicenseRepository drivingLicenseRepository,
        DrivingLicenseMapper drivingLicenseMapper,
        DrivingLicenseSearchRepository drivingLicenseSearchRepository
    ) {
        this.drivingLicenseRepository = drivingLicenseRepository;
        this.drivingLicenseMapper = drivingLicenseMapper;
        this.drivingLicenseSearchRepository = drivingLicenseSearchRepository;
    }

    /**
     * Save a drivingLicense.
     *
     * @param drivingLicenseDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<DrivingLicenseDTO> save(DrivingLicenseDTO drivingLicenseDTO) {
        LOG.debug("Request to save DrivingLicense : {}", drivingLicenseDTO);
        return drivingLicenseRepository
            .save(drivingLicenseMapper.toEntity(drivingLicenseDTO))

            .flatMap(drivingLicenseSearchRepository::save)
            .map(drivingLicenseMapper::toDto);
    }

    /**
     * Update a drivingLicense.
     *
     * @param drivingLicenseDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<DrivingLicenseDTO> update(DrivingLicenseDTO drivingLicenseDTO) {
        LOG.debug("Request to update DrivingLicense : {}", drivingLicenseDTO);
        return drivingLicenseRepository
            .save(drivingLicenseMapper.toEntity(drivingLicenseDTO))

            .flatMap(drivingLicenseSearchRepository::save)
            .map(drivingLicenseMapper::toDto);
    }

    /**
     * Partially update a drivingLicense.
     *
     * @param drivingLicenseDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<DrivingLicenseDTO> partialUpdate(DrivingLicenseDTO drivingLicenseDTO) {
        LOG.debug("Request to partially update DrivingLicense : {}", drivingLicenseDTO);

        return drivingLicenseRepository
            .findById(drivingLicenseDTO.getId())
            .map(existingDrivingLicense -> {
                drivingLicenseMapper.partialUpdate(existingDrivingLicense, drivingLicenseDTO);

                return existingDrivingLicense;
            })
            .flatMap(drivingLicenseRepository::save)
            .flatMap(savedDrivingLicense -> {
                drivingLicenseSearchRepository.save(savedDrivingLicense);
                return Mono.just(savedDrivingLicense);
            })
            .map(drivingLicenseMapper::toDto);
    }

    /**
     * Get all the drivingLicenses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<DrivingLicenseDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all DrivingLicenses");
        return drivingLicenseRepository.findAllBy(pageable).map(drivingLicenseMapper::toDto);
    }

    /**
     *  Get all the drivingLicenses where Customer is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<DrivingLicenseDTO> findAllWhereCustomerIsNull() {
        LOG.debug("Request to get all drivingLicenses where Customer is null");
        return drivingLicenseRepository.findAllWhereCustomerIsNull().map(drivingLicenseMapper::toDto);
    }

    /**
     * Returns the number of drivingLicenses available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return drivingLicenseRepository.count();
    }

    /**
     * Returns the number of drivingLicenses available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return drivingLicenseSearchRepository.count();
    }

    /**
     * Get one drivingLicense by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<DrivingLicenseDTO> findOne(Long id) {
        LOG.debug("Request to get DrivingLicense : {}", id);
        return drivingLicenseRepository.findById(id).map(drivingLicenseMapper::toDto);
    }

    /**
     * Delete the drivingLicense by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete DrivingLicense : {}", id);
        return drivingLicenseRepository
            .deleteById(id)

            .then(drivingLicenseSearchRepository.deleteById(id));
    }

    /**
     * Search for the drivingLicense corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<DrivingLicenseDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of DrivingLicenses for query {}", query);
        return drivingLicenseSearchRepository.search(query, pageable).map(drivingLicenseMapper::toDto);
    }
}
