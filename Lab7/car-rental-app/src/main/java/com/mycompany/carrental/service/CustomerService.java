package com.mycompany.carrental.service;

import com.mycompany.carrental.repository.CustomerRepository;
import com.mycompany.carrental.repository.search.CustomerSearchRepository;
import com.mycompany.carrental.service.dto.CustomerDTO;
import com.mycompany.carrental.service.mapper.CustomerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.carrental.domain.Customer}.
 */
@Service
@Transactional
public class CustomerService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    private final CustomerSearchRepository customerSearchRepository;

    public CustomerService(
        CustomerRepository customerRepository,
        CustomerMapper customerMapper,
        CustomerSearchRepository customerSearchRepository
    ) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.customerSearchRepository = customerSearchRepository;
    }

    /**
     * Save a customer.
     *
     * @param customerDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CustomerDTO> save(CustomerDTO customerDTO) {
        LOG.debug("Request to save Customer : {}", customerDTO);
        return customerRepository
            .save(customerMapper.toEntity(customerDTO))

            .flatMap(customerSearchRepository::save)
            .map(customerMapper::toDto);
    }

    /**
     * Update a customer.
     *
     * @param customerDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CustomerDTO> update(CustomerDTO customerDTO) {
        LOG.debug("Request to update Customer : {}", customerDTO);
        return customerRepository
            .save(customerMapper.toEntity(customerDTO))

            .flatMap(customerSearchRepository::save)
            .map(customerMapper::toDto);
    }

    /**
     * Partially update a customer.
     *
     * @param customerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CustomerDTO> partialUpdate(CustomerDTO customerDTO) {
        LOG.debug("Request to partially update Customer : {}", customerDTO);

        return customerRepository
            .findById(customerDTO.getId())
            .map(existingCustomer -> {
                customerMapper.partialUpdate(existingCustomer, customerDTO);

                return existingCustomer;
            })
            .flatMap(customerRepository::save)
            .flatMap(savedCustomer -> {
                customerSearchRepository.save(savedCustomer);
                return Mono.just(savedCustomer);
            })
            .map(customerMapper::toDto);
    }

    /**
     * Get all the customers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CustomerDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Customers");
        return customerRepository.findAllBy(pageable).map(customerMapper::toDto);
    }

    /**
     * Get all the customers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<CustomerDTO> findAllWithEagerRelationships(Pageable pageable) {
        return customerRepository.findAllWithEagerRelationships(pageable).map(customerMapper::toDto);
    }

    /**
     * Returns the number of customers available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return customerRepository.count();
    }

    /**
     * Returns the number of customers available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return customerSearchRepository.count();
    }

    /**
     * Get one customer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CustomerDTO> findOne(Long id) {
        LOG.debug("Request to get Customer : {}", id);
        return customerRepository.findOneWithEagerRelationships(id).map(customerMapper::toDto);
    }

    /**
     * Delete the customer by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Customer : {}", id);
        return customerRepository
            .deleteById(id)

            .then(customerSearchRepository.deleteById(id));
    }

    /**
     * Search for the customer corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CustomerDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Customers for query {}", query);
        return customerSearchRepository.search(query, pageable).map(customerMapper::toDto);
    }
}
