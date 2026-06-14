package com.mycompany.carrental.service;

import com.mycompany.carrental.repository.CarRepository;
import com.mycompany.carrental.repository.search.CarSearchRepository;
import com.mycompany.carrental.service.dto.CarDTO;
import com.mycompany.carrental.service.mapper.CarMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.carrental.domain.Car}.
 */
@Service
@Transactional
public class CarService {

    private static final Logger LOG = LoggerFactory.getLogger(CarService.class);

    private final CarRepository carRepository;

    private final CarMapper carMapper;

    private final CarSearchRepository carSearchRepository;

    public CarService(CarRepository carRepository, CarMapper carMapper, CarSearchRepository carSearchRepository) {
        this.carRepository = carRepository;
        this.carMapper = carMapper;
        this.carSearchRepository = carSearchRepository;
    }

    /**
     * Save a car.
     *
     * @param carDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CarDTO> save(CarDTO carDTO) {
        LOG.debug("Request to save Car : {}", carDTO);
        return carRepository
            .save(carMapper.toEntity(carDTO))

            .flatMap(carSearchRepository::save)
            .map(carMapper::toDto);
    }

    /**
     * Update a car.
     *
     * @param carDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CarDTO> update(CarDTO carDTO) {
        LOG.debug("Request to update Car : {}", carDTO);
        return carRepository
            .save(carMapper.toEntity(carDTO))

            .flatMap(carSearchRepository::save)
            .map(carMapper::toDto);
    }

    /**
     * Partially update a car.
     *
     * @param carDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CarDTO> partialUpdate(CarDTO carDTO) {
        LOG.debug("Request to partially update Car : {}", carDTO);

        return carRepository
            .findById(carDTO.getId())
            .map(existingCar -> {
                carMapper.partialUpdate(existingCar, carDTO);

                return existingCar;
            })
            .flatMap(carRepository::save)
            .flatMap(savedCar -> {
                carSearchRepository.save(savedCar);
                return Mono.just(savedCar);
            })
            .map(carMapper::toDto);
    }

    /**
     * Get all the cars.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CarDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Cars");
        return carRepository.findAllBy(pageable).map(carMapper::toDto);
    }

    /**
     * Get all the cars with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<CarDTO> findAllWithEagerRelationships(Pageable pageable) {
        return carRepository.findAllWithEagerRelationships(pageable).map(carMapper::toDto);
    }

    /**
     * Returns the number of cars available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return carRepository.count();
    }

    /**
     * Returns the number of cars available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return carSearchRepository.count();
    }

    /**
     * Get one car by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CarDTO> findOne(Long id) {
        LOG.debug("Request to get Car : {}", id);
        return carRepository.findOneWithEagerRelationships(id).map(carMapper::toDto);
    }

    /**
     * Delete the car by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Car : {}", id);
        return carRepository
            .deleteById(id)

            .then(carSearchRepository.deleteById(id));
    }

    /**
     * Search for the car corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CarDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Cars for query {}", query);
        return carSearchRepository.search(query, pageable).map(carMapper::toDto);
    }
}
