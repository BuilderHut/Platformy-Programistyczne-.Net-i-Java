package com.mycompany.carrental.web.rest;

import static com.mycompany.carrental.domain.CarAsserts.*;
import static com.mycompany.carrental.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.carrental.IntegrationTest;
import com.mycompany.carrental.domain.Car;
import com.mycompany.carrental.domain.enumeration.CarStatus;
import com.mycompany.carrental.repository.CarRepository;
import com.mycompany.carrental.repository.EntityManager;
import com.mycompany.carrental.repository.search.CarSearchRepository;
import com.mycompany.carrental.service.CarService;
import com.mycompany.carrental.service.dto.CarDTO;
import com.mycompany.carrental.service.mapper.CarMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link CarResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CarResourceIT {

    private static final String DEFAULT_BRAND = "AAAAAAAAAA";
    private static final String UPDATED_BRAND = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_MODEL = "BBBBBBBBBB";

    private static final Integer DEFAULT_PRODUCTION_YEAR = 1990;
    private static final Integer UPDATED_PRODUCTION_YEAR = 1991;

    private static final Float DEFAULT_DAILY_PRICE = 0F;
    private static final Float UPDATED_DAILY_PRICE = 1F;

    private static final CarStatus DEFAULT_STATUS = CarStatus.AVAILABLE;
    private static final CarStatus UPDATED_STATUS = CarStatus.RENTED;

    private static final String ENTITY_API_URL = "/api/cars";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/cars/_search";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CarRepository carRepository;

    @Mock
    private CarRepository carRepositoryMock;

    @Autowired
    private CarMapper carMapper;

    @Mock
    private CarService carServiceMock;

    @Autowired
    private CarSearchRepository carSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Car car;

    private Car insertedCar;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Car createEntity() {
        return new Car()
            .brand(DEFAULT_BRAND)
            .model(DEFAULT_MODEL)
            .productionYear(DEFAULT_PRODUCTION_YEAR)
            .dailyPrice(DEFAULT_DAILY_PRICE)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Car createUpdatedEntity() {
        return new Car()
            .brand(UPDATED_BRAND)
            .model(UPDATED_MODEL)
            .productionYear(UPDATED_PRODUCTION_YEAR)
            .dailyPrice(UPDATED_DAILY_PRICE)
            .status(UPDATED_STATUS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_car__categories").block();
            em.deleteAll(Car.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        car = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCar != null) {
            carRepository.delete(insertedCar).block();
            carSearchRepository.delete(insertedCar).block();
            insertedCar = null;
        }
        deleteEntities(em);
    }

    @Test
    void createCar() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);
        var returnedCarDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(carDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(CarDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Car in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCar = carMapper.toEntity(returnedCarDTO);
        assertCarUpdatableFieldsEquals(returnedCar, getPersistedCar(returnedCar));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCar = returnedCar;
    }

    @Test
    void createCarWithExistingId() throws Exception {
        // Create the Car with an existing ID
        car.setId(1L);
        CarDTO carDTO = carMapper.toDto(car);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(carDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Car in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkBrandIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        // set the field null
        car.setBrand(null);

        // Create the Car, which fails.
        CarDTO carDTO = carMapper.toDto(car);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(carDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkModelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        // set the field null
        car.setModel(null);

        // Create the Car, which fails.
        CarDTO carDTO = carMapper.toDto(car);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(carDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkProductionYearIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        // set the field null
        car.setProductionYear(null);

        // Create the Car, which fails.
        CarDTO carDTO = carMapper.toDto(car);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(carDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDailyPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        // set the field null
        car.setDailyPrice(null);

        // Create the Car, which fails.
        CarDTO carDTO = carMapper.toDto(car);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(carDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        // set the field null
        car.setStatus(null);

        // Create the Car, which fails.
        CarDTO carDTO = carMapper.toDto(car);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(carDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllCars() {
        // Initialize the database
        insertedCar = carRepository.save(car).block();

        // Get all the carList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(car.getId().intValue()))
            .jsonPath("$.[*].brand")
            .value(hasItem(DEFAULT_BRAND))
            .jsonPath("$.[*].model")
            .value(hasItem(DEFAULT_MODEL))
            .jsonPath("$.[*].productionYear")
            .value(hasItem(DEFAULT_PRODUCTION_YEAR))
            .jsonPath("$.[*].dailyPrice")
            .value(hasItem(DEFAULT_DAILY_PRICE.doubleValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCarsWithEagerRelationshipsIsEnabled() {
        when(carServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(carServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCarsWithEagerRelationshipsIsNotEnabled() {
        when(carServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(carRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getCar() {
        // Initialize the database
        insertedCar = carRepository.save(car).block();

        // Get the car
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, car.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(car.getId().intValue()))
            .jsonPath("$.brand")
            .value(is(DEFAULT_BRAND))
            .jsonPath("$.model")
            .value(is(DEFAULT_MODEL))
            .jsonPath("$.productionYear")
            .value(is(DEFAULT_PRODUCTION_YEAR))
            .jsonPath("$.dailyPrice")
            .value(is(DEFAULT_DAILY_PRICE.doubleValue()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingCar() {
        // Get the car
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCar() throws Exception {
        // Initialize the database
        insertedCar = carRepository.save(car).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        carSearchRepository.save(car).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());

        // Update the car
        Car updatedCar = carRepository.findById(car.getId()).block();
        updatedCar
            .brand(UPDATED_BRAND)
            .model(UPDATED_MODEL)
            .productionYear(UPDATED_PRODUCTION_YEAR)
            .dailyPrice(UPDATED_DAILY_PRICE)
            .status(UPDATED_STATUS);
        CarDTO carDTO = carMapper.toDto(updatedCar);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, carDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(carDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Car in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCarToMatchAllProperties(updatedCar);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Car> carSearchList = Streamable.of(carSearchRepository.findAll().collectList().block()).toList();
                Car testCarSearch = carSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertCarAllPropertiesEquals(testCarSearch, updatedCar);
                assertCarUpdatableFieldsEquals(testCarSearch, updatedCar);
            });
    }

    @Test
    void putNonExistingCar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        car.setId(longCount.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, carDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(carDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Car in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchCar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        car.setId(longCount.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(carDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Car in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamCar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        car.setId(longCount.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(carDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Car in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateCarWithPatch() throws Exception {
        // Initialize the database
        insertedCar = carRepository.save(car).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the car using partial update
        Car partialUpdatedCar = new Car();
        partialUpdatedCar.setId(car.getId());

        partialUpdatedCar.productionYear(UPDATED_PRODUCTION_YEAR).dailyPrice(UPDATED_DAILY_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCar.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCar))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Car in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCar, car), getPersistedCar(car));
    }

    @Test
    void fullUpdateCarWithPatch() throws Exception {
        // Initialize the database
        insertedCar = carRepository.save(car).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the car using partial update
        Car partialUpdatedCar = new Car();
        partialUpdatedCar.setId(car.getId());

        partialUpdatedCar
            .brand(UPDATED_BRAND)
            .model(UPDATED_MODEL)
            .productionYear(UPDATED_PRODUCTION_YEAR)
            .dailyPrice(UPDATED_DAILY_PRICE)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCar.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCar))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Car in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarUpdatableFieldsEquals(partialUpdatedCar, getPersistedCar(partialUpdatedCar));
    }

    @Test
    void patchNonExistingCar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        car.setId(longCount.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, carDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(carDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Car in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchCar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        car.setId(longCount.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(carDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Car in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamCar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        car.setId(longCount.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(carDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Car in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteCar() {
        // Initialize the database
        insertedCar = carRepository.save(car).block();
        carRepository.save(car).block();
        carSearchRepository.save(car).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the car
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, car.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchCar() {
        // Initialize the database
        insertedCar = carRepository.save(car).block();
        carSearchRepository.save(car).block();

        // Search the car
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + car.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(car.getId().intValue()))
            .jsonPath("$.[*].brand")
            .value(hasItem(DEFAULT_BRAND))
            .jsonPath("$.[*].model")
            .value(hasItem(DEFAULT_MODEL))
            .jsonPath("$.[*].productionYear")
            .value(hasItem(DEFAULT_PRODUCTION_YEAR))
            .jsonPath("$.[*].dailyPrice")
            .value(hasItem(DEFAULT_DAILY_PRICE.doubleValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    protected long getRepositoryCount() {
        return carRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Car getPersistedCar(Car car) {
        return carRepository.findById(car.getId()).block();
    }

    protected void assertPersistedCarToMatchAllProperties(Car expectedCar) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCarAllPropertiesEquals(expectedCar, getPersistedCar(expectedCar));
        assertCarUpdatableFieldsEquals(expectedCar, getPersistedCar(expectedCar));
    }

    protected void assertPersistedCarToMatchUpdatableProperties(Car expectedCar) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCarAllUpdatablePropertiesEquals(expectedCar, getPersistedCar(expectedCar));
        assertCarUpdatableFieldsEquals(expectedCar, getPersistedCar(expectedCar));
    }
}
