package com.mycompany.carrental.web.rest;

import com.mycompany.carrental.repository.DrivingLicenseRepository;
import com.mycompany.carrental.service.DrivingLicenseService;
import com.mycompany.carrental.service.dto.DrivingLicenseDTO;
import com.mycompany.carrental.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.carrental.domain.DrivingLicense}.
 */
@RestController
@RequestMapping("/api/driving-licenses")
public class DrivingLicenseResource {

    private static final Logger LOG = LoggerFactory.getLogger(DrivingLicenseResource.class);

    private static final String ENTITY_NAME = "drivingLicense";

    @Value("${jhipster.clientApp.name:carrental}")
    private String applicationName;

    private final DrivingLicenseService drivingLicenseService;

    private final DrivingLicenseRepository drivingLicenseRepository;

    public DrivingLicenseResource(DrivingLicenseService drivingLicenseService, DrivingLicenseRepository drivingLicenseRepository) {
        this.drivingLicenseService = drivingLicenseService;
        this.drivingLicenseRepository = drivingLicenseRepository;
    }

    /**
     * {@code POST  /driving-licenses} : Create a new drivingLicense.
     *
     * @param drivingLicenseDTO the drivingLicenseDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new drivingLicenseDTO, or with status {@code 400 (Bad Request)} if the drivingLicense has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<DrivingLicenseDTO>> createDrivingLicense(@Valid @RequestBody DrivingLicenseDTO drivingLicenseDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DrivingLicense : {}", drivingLicenseDTO);
        if (drivingLicenseDTO.getId() != null) {
            throw new BadRequestAlertException("A new drivingLicense cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return drivingLicenseService.save(drivingLicenseDTO).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/driving-licenses/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /driving-licenses/:id} : Updates an existing drivingLicense.
     *
     * @param id the id of the drivingLicenseDTO to save.
     * @param drivingLicenseDTO the drivingLicenseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated drivingLicenseDTO,
     * or with status {@code 400 (Bad Request)} if the drivingLicenseDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the drivingLicenseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<DrivingLicenseDTO>> updateDrivingLicense(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DrivingLicenseDTO drivingLicenseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DrivingLicense : {}, {}", id, drivingLicenseDTO);
        if (drivingLicenseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, drivingLicenseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return drivingLicenseRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return drivingLicenseService
                .update(drivingLicenseDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /driving-licenses/:id} : Partial updates given fields of an existing drivingLicense, field will ignore if it is null
     *
     * @param id the id of the drivingLicenseDTO to save.
     * @param drivingLicenseDTO the drivingLicenseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated drivingLicenseDTO,
     * or with status {@code 400 (Bad Request)} if the drivingLicenseDTO is not valid,
     * or with status {@code 404 (Not Found)} if the drivingLicenseDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the drivingLicenseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<DrivingLicenseDTO>> partialUpdateDrivingLicense(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DrivingLicenseDTO drivingLicenseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DrivingLicense partially : {}, {}", id, drivingLicenseDTO);
        if (drivingLicenseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, drivingLicenseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return drivingLicenseRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<DrivingLicenseDTO> result = drivingLicenseService.partialUpdate(drivingLicenseDTO);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /driving-licenses} : get all the Driving Licenses.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Driving Licenses in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<DrivingLicenseDTO>>> getAllDrivingLicenses(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "filter", required = false) String filter
    ) {
        if ("customer-is-null".equals(filter)) {
            LOG.debug("REST request to get all DrivingLicenses where customer is null");
            return drivingLicenseService.findAllWhereCustomerIsNull().collectList().map(ResponseEntity::ok);
        }
        LOG.debug("REST request to get a page of DrivingLicenses");
        return drivingLicenseService
            .countAll()
            .zipWith(drivingLicenseService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /driving-licenses/:id} : get the "id" drivingLicense.
     *
     * @param id the id of the drivingLicenseDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the drivingLicenseDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<DrivingLicenseDTO>> getDrivingLicense(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DrivingLicense : {}", id);
        Mono<DrivingLicenseDTO> drivingLicenseDTO = drivingLicenseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(drivingLicenseDTO);
    }

    /**
     * {@code DELETE  /driving-licenses/:id} : delete the "id" drivingLicense.
     *
     * @param id the id of the drivingLicenseDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDrivingLicense(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DrivingLicense : {}", id);
        return drivingLicenseService
            .delete(id)

            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code SEARCH  /driving-licenses/_search?query=:query} : search for the drivingLicense corresponding
     * to the query.
     *
     * @param query the query of the drivingLicense search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<DrivingLicenseDTO>>> searchDrivingLicenses(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to search for a page of DrivingLicenses for query {}", query);
        return drivingLicenseService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page ->
                PaginationUtil.generatePaginationHttpHeaders(
                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                    page
                )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(drivingLicenseService.search(query, pageable)));
    }
}
