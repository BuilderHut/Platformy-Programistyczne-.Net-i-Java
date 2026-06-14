package com.mycompany.carrental.service;

import com.mycompany.carrental.repository.RentalRepository;
import com.mycompany.carrental.repository.search.RentalSearchRepository;
import com.mycompany.carrental.service.dto.RentalDTO;
import com.mycompany.carrental.service.mapper.RentalMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.carrental.domain.Rental}.
 */
@Service
@Transactional
public class RentalService {

    private static final Logger LOG = LoggerFactory.getLogger(RentalService.class);

    private final RentalRepository rentalRepository;

    private final RentalMapper rentalMapper;

    private final RentalSearchRepository rentalSearchRepository;

    public RentalService(RentalRepository rentalRepository, RentalMapper rentalMapper, RentalSearchRepository rentalSearchRepository) {
        this.rentalRepository = rentalRepository;
        this.rentalMapper = rentalMapper;
        this.rentalSearchRepository = rentalSearchRepository;
    }

    /**
     * Save a rental.
     *
     * @param rentalDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RentalDTO> save(RentalDTO rentalDTO) {
        LOG.debug("Request to save Rental : {}", rentalDTO);
        return rentalRepository
            .save(rentalMapper.toEntity(rentalDTO))

            .flatMap(rentalSearchRepository::save)
            .map(rentalMapper::toDto);
    }

    /**
     * Update a rental.
     *
     * @param rentalDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RentalDTO> update(RentalDTO rentalDTO) {
        LOG.debug("Request to update Rental : {}", rentalDTO);
        return rentalRepository
            .save(rentalMapper.toEntity(rentalDTO))

            .flatMap(rentalSearchRepository::save)
            .map(rentalMapper::toDto);
    }

    /**
     * Partially update a rental.
     *
     * @param rentalDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<RentalDTO> partialUpdate(RentalDTO rentalDTO) {
        LOG.debug("Request to partially update Rental : {}", rentalDTO);

        return rentalRepository
            .findById(rentalDTO.getId())
            .map(existingRental -> {
                rentalMapper.partialUpdate(existingRental, rentalDTO);

                return existingRental;
            })
            .flatMap(rentalRepository::save)
            .flatMap(savedRental -> {
                rentalSearchRepository.save(savedRental);
                return Mono.just(savedRental);
            })
            .map(rentalMapper::toDto);
    }

    /**
     * Get all the rentals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RentalDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Rentals");
        return rentalRepository.findAllBy(pageable).map(rentalMapper::toDto);
    }

    /**
     * Returns the number of rentals available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return rentalRepository.count();
    }

    /**
     * Returns the number of rentals available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return rentalSearchRepository.count();
    }

    /**
     * Get one rental by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<RentalDTO> findOne(Long id) {
        LOG.debug("Request to get Rental : {}", id);
        return rentalRepository.findById(id).map(rentalMapper::toDto);
    }

    /**
     * Delete the rental by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Rental : {}", id);
        return rentalRepository
            .deleteById(id)

            .then(rentalSearchRepository.deleteById(id));
    }

    /**
     * Search for the rental corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RentalDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Rentals for query {}", query);
        return rentalSearchRepository.search(query, pageable).map(rentalMapper::toDto);
    }
}
