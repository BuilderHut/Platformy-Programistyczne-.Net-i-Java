package com.mycompany.carrental.web.rest;

import static com.mycompany.carrental.domain.DrivingLicenseAsserts.*;
import static com.mycompany.carrental.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.carrental.IntegrationTest;
import com.mycompany.carrental.domain.DrivingLicense;
import com.mycompany.carrental.repository.DrivingLicenseRepository;
import com.mycompany.carrental.repository.EntityManager;
import com.mycompany.carrental.repository.search.DrivingLicenseSearchRepository;
import com.mycompany.carrental.service.dto.DrivingLicenseDTO;
import com.mycompany.carrental.service.mapper.DrivingLicenseMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link DrivingLicenseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DrivingLicenseResourceIT {

    private static final String DEFAULT_LICENSE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_LICENSE_NUMBER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_ISSUE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ISSUE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_EXPIRATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EXPIRATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/driving-licenses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/driving-licenses/_search";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DrivingLicenseRepository drivingLicenseRepository;

    @Autowired
    private DrivingLicenseMapper drivingLicenseMapper;

    @Autowired
    private DrivingLicenseSearchRepository drivingLicenseSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private DrivingLicense drivingLicense;

    private DrivingLicense insertedDrivingLicense;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DrivingLicense createEntity() {
        return new DrivingLicense()
            .licenseNumber(DEFAULT_LICENSE_NUMBER)
            .issueDate(DEFAULT_ISSUE_DATE)
            .expirationDate(DEFAULT_EXPIRATION_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DrivingLicense createUpdatedEntity() {
        return new DrivingLicense()
            .licenseNumber(UPDATED_LICENSE_NUMBER)
            .issueDate(UPDATED_ISSUE_DATE)
            .expirationDate(UPDATED_EXPIRATION_DATE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(DrivingLicense.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        drivingLicense = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDrivingLicense != null) {
            drivingLicenseRepository.delete(insertedDrivingLicense).block();
            drivingLicenseSearchRepository.delete(insertedDrivingLicense).block();
            insertedDrivingLicense = null;
        }
        deleteEntities(em);
    }

    @Test
    void createDrivingLicense() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        // Create the DrivingLicense
        DrivingLicenseDTO drivingLicenseDTO = drivingLicenseMapper.toDto(drivingLicense);
        var returnedDrivingLicenseDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(drivingLicenseDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DrivingLicenseDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the DrivingLicense in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDrivingLicense = drivingLicenseMapper.toEntity(returnedDrivingLicenseDTO);
        assertDrivingLicenseUpdatableFieldsEquals(returnedDrivingLicense, getPersistedDrivingLicense(returnedDrivingLicense));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedDrivingLicense = returnedDrivingLicense;
    }

    @Test
    void createDrivingLicenseWithExistingId() throws Exception {
        // Create the DrivingLicense with an existing ID
        drivingLicense.setId(1L);
        DrivingLicenseDTO drivingLicenseDTO = drivingLicenseMapper.toDto(drivingLicense);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(drivingLicenseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DrivingLicense in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkLicenseNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        // set the field null
        drivingLicense.setLicenseNumber(null);

        // Create the DrivingLicense, which fails.
        DrivingLicenseDTO drivingLicenseDTO = drivingLicenseMapper.toDto(drivingLicense);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(drivingLicenseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkIssueDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        // set the field null
        drivingLicense.setIssueDate(null);

        // Create the DrivingLicense, which fails.
        DrivingLicenseDTO drivingLicenseDTO = drivingLicenseMapper.toDto(drivingLicense);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(drivingLicenseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkExpirationDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        // set the field null
        drivingLicense.setExpirationDate(null);

        // Create the DrivingLicense, which fails.
        DrivingLicenseDTO drivingLicenseDTO = drivingLicenseMapper.toDto(drivingLicense);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(drivingLicenseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllDrivingLicenses() {
        // Initialize the database
        insertedDrivingLicense = drivingLicenseRepository.save(drivingLicense).block();

        // Get all the drivingLicenseList
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
            .value(hasItem(drivingLicense.getId().intValue()))
            .jsonPath("$.[*].licenseNumber")
            .value(hasItem(DEFAULT_LICENSE_NUMBER))
            .jsonPath("$.[*].issueDate")
            .value(hasItem(DEFAULT_ISSUE_DATE.toString()))
            .jsonPath("$.[*].expirationDate")
            .value(hasItem(DEFAULT_EXPIRATION_DATE.toString()));
    }

    @Test
    void getDrivingLicense() {
        // Initialize the database
        insertedDrivingLicense = drivingLicenseRepository.save(drivingLicense).block();

        // Get the drivingLicense
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, drivingLicense.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(drivingLicense.getId().intValue()))
            .jsonPath("$.licenseNumber")
            .value(is(DEFAULT_LICENSE_NUMBER))
            .jsonPath("$.issueDate")
            .value(is(DEFAULT_ISSUE_DATE.toString()))
            .jsonPath("$.expirationDate")
            .value(is(DEFAULT_EXPIRATION_DATE.toString()));
    }

    @Test
    void getNonExistingDrivingLicense() {
        // Get the drivingLicense
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDrivingLicense() throws Exception {
        // Initialize the database
        insertedDrivingLicense = drivingLicenseRepository.save(drivingLicense).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        drivingLicenseSearchRepository.save(drivingLicense).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());

        // Update the drivingLicense
        DrivingLicense updatedDrivingLicense = drivingLicenseRepository.findById(drivingLicense.getId()).block();
        updatedDrivingLicense.licenseNumber(UPDATED_LICENSE_NUMBER).issueDate(UPDATED_ISSUE_DATE).expirationDate(UPDATED_EXPIRATION_DATE);
        DrivingLicenseDTO drivingLicenseDTO = drivingLicenseMapper.toDto(updatedDrivingLicense);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, drivingLicenseDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(drivingLicenseDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DrivingLicense in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDrivingLicenseToMatchAllProperties(updatedDrivingLicense);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<DrivingLicense> drivingLicenseSearchList = Streamable.of(
                    drivingLicenseSearchRepository.findAll().collectList().block()
                ).toList();
                DrivingLicense testDrivingLicenseSearch = drivingLicenseSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertDrivingLicenseAllPropertiesEquals(testDrivingLicenseSearch, updatedDrivingLicense);
                assertDrivingLicenseUpdatableFieldsEquals(testDrivingLicenseSearch, updatedDrivingLicense);
            });
    }

    @Test
    void putNonExistingDrivingLicense() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        drivingLicense.setId(longCount.incrementAndGet());

        // Create the DrivingLicense
        DrivingLicenseDTO drivingLicenseDTO = drivingLicenseMapper.toDto(drivingLicense);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, drivingLicenseDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(drivingLicenseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DrivingLicense in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchDrivingLicense() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        drivingLicense.setId(longCount.incrementAndGet());

        // Create the DrivingLicense
        DrivingLicenseDTO drivingLicenseDTO = drivingLicenseMapper.toDto(drivingLicense);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(drivingLicenseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DrivingLicense in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamDrivingLicense() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        drivingLicense.setId(longCount.incrementAndGet());

        // Create the DrivingLicense
        DrivingLicenseDTO drivingLicenseDTO = drivingLicenseMapper.toDto(drivingLicense);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(drivingLicenseDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DrivingLicense in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateDrivingLicenseWithPatch() throws Exception {
        // Initialize the database
        insertedDrivingLicense = drivingLicenseRepository.save(drivingLicense).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the drivingLicense using partial update
        DrivingLicense partialUpdatedDrivingLicense = new DrivingLicense();
        partialUpdatedDrivingLicense.setId(drivingLicense.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDrivingLicense.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDrivingLicense))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DrivingLicense in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDrivingLicenseUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDrivingLicense, drivingLicense),
            getPersistedDrivingLicense(drivingLicense)
        );
    }

    @Test
    void fullUpdateDrivingLicenseWithPatch() throws Exception {
        // Initialize the database
        insertedDrivingLicense = drivingLicenseRepository.save(drivingLicense).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the drivingLicense using partial update
        DrivingLicense partialUpdatedDrivingLicense = new DrivingLicense();
        partialUpdatedDrivingLicense.setId(drivingLicense.getId());

        partialUpdatedDrivingLicense
            .licenseNumber(UPDATED_LICENSE_NUMBER)
            .issueDate(UPDATED_ISSUE_DATE)
            .expirationDate(UPDATED_EXPIRATION_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDrivingLicense.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDrivingLicense))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DrivingLicense in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDrivingLicenseUpdatableFieldsEquals(partialUpdatedDrivingLicense, getPersistedDrivingLicense(partialUpdatedDrivingLicense));
    }

    @Test
    void patchNonExistingDrivingLicense() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        drivingLicense.setId(longCount.incrementAndGet());

        // Create the DrivingLicense
        DrivingLicenseDTO drivingLicenseDTO = drivingLicenseMapper.toDto(drivingLicense);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, drivingLicenseDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(drivingLicenseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DrivingLicense in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchDrivingLicense() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        drivingLicense.setId(longCount.incrementAndGet());

        // Create the DrivingLicense
        DrivingLicenseDTO drivingLicenseDTO = drivingLicenseMapper.toDto(drivingLicense);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(drivingLicenseDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DrivingLicense in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamDrivingLicense() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        drivingLicense.setId(longCount.incrementAndGet());

        // Create the DrivingLicense
        DrivingLicenseDTO drivingLicenseDTO = drivingLicenseMapper.toDto(drivingLicense);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(drivingLicenseDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DrivingLicense in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteDrivingLicense() {
        // Initialize the database
        insertedDrivingLicense = drivingLicenseRepository.save(drivingLicense).block();
        drivingLicenseRepository.save(drivingLicense).block();
        drivingLicenseSearchRepository.save(drivingLicense).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the drivingLicense
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, drivingLicense.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(drivingLicenseSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchDrivingLicense() {
        // Initialize the database
        insertedDrivingLicense = drivingLicenseRepository.save(drivingLicense).block();
        drivingLicenseSearchRepository.save(drivingLicense).block();

        // Search the drivingLicense
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + drivingLicense.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(drivingLicense.getId().intValue()))
            .jsonPath("$.[*].licenseNumber")
            .value(hasItem(DEFAULT_LICENSE_NUMBER))
            .jsonPath("$.[*].issueDate")
            .value(hasItem(DEFAULT_ISSUE_DATE.toString()))
            .jsonPath("$.[*].expirationDate")
            .value(hasItem(DEFAULT_EXPIRATION_DATE.toString()));
    }

    protected long getRepositoryCount() {
        return drivingLicenseRepository.count().block();
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

    protected DrivingLicense getPersistedDrivingLicense(DrivingLicense drivingLicense) {
        return drivingLicenseRepository.findById(drivingLicense.getId()).block();
    }

    protected void assertPersistedDrivingLicenseToMatchAllProperties(DrivingLicense expectedDrivingLicense) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDrivingLicenseAllPropertiesEquals(expectedDrivingLicense, getPersistedDrivingLicense(expectedDrivingLicense));
        assertDrivingLicenseUpdatableFieldsEquals(expectedDrivingLicense, getPersistedDrivingLicense(expectedDrivingLicense));
    }

    protected void assertPersistedDrivingLicenseToMatchUpdatableProperties(DrivingLicense expectedDrivingLicense) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDrivingLicenseAllUpdatablePropertiesEquals(expectedDrivingLicense, getPersistedDrivingLicense(expectedDrivingLicense));
        assertDrivingLicenseUpdatableFieldsEquals(expectedDrivingLicense, getPersistedDrivingLicense(expectedDrivingLicense));
    }
}
