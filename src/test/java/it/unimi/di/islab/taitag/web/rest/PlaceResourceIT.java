package it.unimi.di.islab.taitag.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.unimi.di.islab.taitag.IntegrationTest;
import it.unimi.di.islab.taitag.domain.Place;
import it.unimi.di.islab.taitag.repository.PlaceRepository;
import it.unimi.di.islab.taitag.repository.search.PlaceSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PlaceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PlaceResourceIT {

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_ITALIAN_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ITALIAN_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/places";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/places";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PlaceRepository placeRepository;

    /**
     * This repository is mocked in the it.unimi.di.islab.taitag.repository.search test package.
     *
     * @see it.unimi.di.islab.taitag.repository.search.PlaceSearchRepositoryMockConfiguration
     */
    @Autowired
    private PlaceSearchRepository mockPlaceSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlaceMockMvc;

    private Place place;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Place createEntity(EntityManager em) {
        Place place = new Place().city(DEFAULT_CITY).italianName(DEFAULT_ITALIAN_NAME);
        return place;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Place createUpdatedEntity(EntityManager em) {
        Place place = new Place().city(UPDATED_CITY).italianName(UPDATED_ITALIAN_NAME);
        return place;
    }

    @BeforeEach
    public void initTest() {
        place = createEntity(em);
    }

    @Test
    @Transactional
    void createPlace() throws Exception {
        int databaseSizeBeforeCreate = placeRepository.findAll().size();
        // Create the Place
        restPlaceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(place)))
            .andExpect(status().isCreated());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeCreate + 1);
        Place testPlace = placeList.get(placeList.size() - 1);
        assertThat(testPlace.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testPlace.getItalianName()).isEqualTo(DEFAULT_ITALIAN_NAME);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(1)).save(testPlace);
    }

    @Test
    @Transactional
    void createPlaceWithExistingId() throws Exception {
        // Create the Place with an existing ID
        place.setId(1L);

        int databaseSizeBeforeCreate = placeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlaceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(place)))
            .andExpect(status().isBadRequest());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeCreate);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(0)).save(place);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = placeRepository.findAll().size();
        // set the field null
        place.setCity(null);

        // Create the Place, which fails.

        restPlaceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(place)))
            .andExpect(status().isBadRequest());

        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPlaces() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList
        restPlaceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(place.getId().intValue())))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].italianName").value(hasItem(DEFAULT_ITALIAN_NAME)));
    }

    @Test
    @Transactional
    void getPlace() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get the place
        restPlaceMockMvc
            .perform(get(ENTITY_API_URL_ID, place.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(place.getId().intValue()))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.italianName").value(DEFAULT_ITALIAN_NAME));
    }

    @Test
    @Transactional
    void getNonExistingPlace() throws Exception {
        // Get the place
        restPlaceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPlace() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        int databaseSizeBeforeUpdate = placeRepository.findAll().size();

        // Update the place
        Place updatedPlace = placeRepository.findById(place.getId()).get();
        // Disconnect from session so that the updates on updatedPlace are not directly saved in db
        em.detach(updatedPlace);
        updatedPlace.city(UPDATED_CITY).italianName(UPDATED_ITALIAN_NAME);

        restPlaceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPlace.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPlace))
            )
            .andExpect(status().isOk());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate);
        Place testPlace = placeList.get(placeList.size() - 1);
        assertThat(testPlace.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testPlace.getItalianName()).isEqualTo(UPDATED_ITALIAN_NAME);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository).save(testPlace);
    }

    @Test
    @Transactional
    void putNonExistingPlace() throws Exception {
        int databaseSizeBeforeUpdate = placeRepository.findAll().size();
        place.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlaceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, place.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(place))
            )
            .andExpect(status().isBadRequest());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(0)).save(place);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlace() throws Exception {
        int databaseSizeBeforeUpdate = placeRepository.findAll().size();
        place.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlaceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(place))
            )
            .andExpect(status().isBadRequest());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(0)).save(place);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlace() throws Exception {
        int databaseSizeBeforeUpdate = placeRepository.findAll().size();
        place.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlaceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(place)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(0)).save(place);
    }

    @Test
    @Transactional
    void partialUpdatePlaceWithPatch() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        int databaseSizeBeforeUpdate = placeRepository.findAll().size();

        // Update the place using partial update
        Place partialUpdatedPlace = new Place();
        partialUpdatedPlace.setId(place.getId());

        restPlaceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlace.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlace))
            )
            .andExpect(status().isOk());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate);
        Place testPlace = placeList.get(placeList.size() - 1);
        assertThat(testPlace.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testPlace.getItalianName()).isEqualTo(DEFAULT_ITALIAN_NAME);
    }

    @Test
    @Transactional
    void fullUpdatePlaceWithPatch() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        int databaseSizeBeforeUpdate = placeRepository.findAll().size();

        // Update the place using partial update
        Place partialUpdatedPlace = new Place();
        partialUpdatedPlace.setId(place.getId());

        partialUpdatedPlace.city(UPDATED_CITY).italianName(UPDATED_ITALIAN_NAME);

        restPlaceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlace.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlace))
            )
            .andExpect(status().isOk());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate);
        Place testPlace = placeList.get(placeList.size() - 1);
        assertThat(testPlace.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testPlace.getItalianName()).isEqualTo(UPDATED_ITALIAN_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingPlace() throws Exception {
        int databaseSizeBeforeUpdate = placeRepository.findAll().size();
        place.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlaceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, place.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(place))
            )
            .andExpect(status().isBadRequest());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(0)).save(place);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlace() throws Exception {
        int databaseSizeBeforeUpdate = placeRepository.findAll().size();
        place.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlaceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(place))
            )
            .andExpect(status().isBadRequest());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(0)).save(place);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlace() throws Exception {
        int databaseSizeBeforeUpdate = placeRepository.findAll().size();
        place.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlaceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(place)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(0)).save(place);
    }

    @Test
    @Transactional
    void deletePlace() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        int databaseSizeBeforeDelete = placeRepository.findAll().size();

        // Delete the place
        restPlaceMockMvc
            .perform(delete(ENTITY_API_URL_ID, place.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(1)).deleteById(place.getId());
    }

    @Test
    @Transactional
    void searchPlace() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        placeRepository.saveAndFlush(place);
        when(mockPlaceSearchRepository.search("id:" + place.getId())).thenReturn(Stream.of(place));

        // Search the place
        restPlaceMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + place.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(place.getId().intValue())))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].italianName").value(hasItem(DEFAULT_ITALIAN_NAME)));
    }
}
