package it.unimi.di.islab.taitag.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.unimi.di.islab.taitag.IntegrationTest;
import it.unimi.di.islab.taitag.domain.Person;
import it.unimi.di.islab.taitag.repository.PersonRepository;
import it.unimi.di.islab.taitag.repository.search.PersonSearchRepository;
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
 * Integration tests for the {@link PersonResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PersonResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LATIN_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LATIN_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_AKA = "AAAAAAAAAA";
    private static final String UPDATED_AKA = "BBBBBBBBBB";

    private static final Integer DEFAULT_BIRTH = 1;
    private static final Integer UPDATED_BIRTH = 2;

    private static final Integer DEFAULT_DEATH = 1;
    private static final Integer UPDATED_DEATH = 2;

    private static final String DEFAULT_AFFILIATION = "AAAAAAAAAA";
    private static final String UPDATED_AFFILIATION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/people";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/people";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PersonRepository personRepository;

    /**
     * This repository is mocked in the it.unimi.di.islab.taitag.repository.search test package.
     *
     * @see it.unimi.di.islab.taitag.repository.search.PersonSearchRepositoryMockConfiguration
     */
    @Autowired
    private PersonSearchRepository mockPersonSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPersonMockMvc;

    private Person person;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Person createEntity(EntityManager em) {
        Person person = new Person()
            .name(DEFAULT_NAME)
            .latinName(DEFAULT_LATIN_NAME)
            .aka(DEFAULT_AKA)
            .birth(DEFAULT_BIRTH)
            .death(DEFAULT_DEATH)
            .affiliation(DEFAULT_AFFILIATION);
        return person;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Person createUpdatedEntity(EntityManager em) {
        Person person = new Person()
            .name(UPDATED_NAME)
            .latinName(UPDATED_LATIN_NAME)
            .aka(UPDATED_AKA)
            .birth(UPDATED_BIRTH)
            .death(UPDATED_DEATH)
            .affiliation(UPDATED_AFFILIATION);
        return person;
    }

    @BeforeEach
    public void initTest() {
        person = createEntity(em);
    }

    @Test
    @Transactional
    void createPerson() throws Exception {
        int databaseSizeBeforeCreate = personRepository.findAll().size();
        // Create the Person
        restPersonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(person)))
            .andExpect(status().isCreated());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeCreate + 1);
        Person testPerson = personList.get(personList.size() - 1);
        assertThat(testPerson.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPerson.getLatinName()).isEqualTo(DEFAULT_LATIN_NAME);
        assertThat(testPerson.getAka()).isEqualTo(DEFAULT_AKA);
        assertThat(testPerson.getBirth()).isEqualTo(DEFAULT_BIRTH);
        assertThat(testPerson.getDeath()).isEqualTo(DEFAULT_DEATH);
        assertThat(testPerson.getAffiliation()).isEqualTo(DEFAULT_AFFILIATION);

        // Validate the Person in Elasticsearch
        verify(mockPersonSearchRepository, times(1)).save(testPerson);
    }

    @Test
    @Transactional
    void createPersonWithExistingId() throws Exception {
        // Create the Person with an existing ID
        person.setId(1L);

        int databaseSizeBeforeCreate = personRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPersonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(person)))
            .andExpect(status().isBadRequest());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeCreate);

        // Validate the Person in Elasticsearch
        verify(mockPersonSearchRepository, times(0)).save(person);
    }

    @Test
    @Transactional
    void getAllPeople() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList
        restPersonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(person.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].latinName").value(hasItem(DEFAULT_LATIN_NAME)))
            .andExpect(jsonPath("$.[*].aka").value(hasItem(DEFAULT_AKA)))
            .andExpect(jsonPath("$.[*].birth").value(hasItem(DEFAULT_BIRTH)))
            .andExpect(jsonPath("$.[*].death").value(hasItem(DEFAULT_DEATH)))
            .andExpect(jsonPath("$.[*].affiliation").value(hasItem(DEFAULT_AFFILIATION)));
    }

    @Test
    @Transactional
    void getPerson() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get the person
        restPersonMockMvc
            .perform(get(ENTITY_API_URL_ID, person.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(person.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.latinName").value(DEFAULT_LATIN_NAME))
            .andExpect(jsonPath("$.aka").value(DEFAULT_AKA))
            .andExpect(jsonPath("$.birth").value(DEFAULT_BIRTH))
            .andExpect(jsonPath("$.death").value(DEFAULT_DEATH))
            .andExpect(jsonPath("$.affiliation").value(DEFAULT_AFFILIATION));
    }

    @Test
    @Transactional
    void getNonExistingPerson() throws Exception {
        // Get the person
        restPersonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPerson() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        int databaseSizeBeforeUpdate = personRepository.findAll().size();

        // Update the person
        Person updatedPerson = personRepository.findById(person.getId()).get();
        // Disconnect from session so that the updates on updatedPerson are not directly saved in db
        em.detach(updatedPerson);
        updatedPerson
            .name(UPDATED_NAME)
            .latinName(UPDATED_LATIN_NAME)
            .aka(UPDATED_AKA)
            .birth(UPDATED_BIRTH)
            .death(UPDATED_DEATH)
            .affiliation(UPDATED_AFFILIATION);

        restPersonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPerson.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPerson))
            )
            .andExpect(status().isOk());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
        Person testPerson = personList.get(personList.size() - 1);
        assertThat(testPerson.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPerson.getLatinName()).isEqualTo(UPDATED_LATIN_NAME);
        assertThat(testPerson.getAka()).isEqualTo(UPDATED_AKA);
        assertThat(testPerson.getBirth()).isEqualTo(UPDATED_BIRTH);
        assertThat(testPerson.getDeath()).isEqualTo(UPDATED_DEATH);
        assertThat(testPerson.getAffiliation()).isEqualTo(UPDATED_AFFILIATION);

        // Validate the Person in Elasticsearch
        verify(mockPersonSearchRepository).save(testPerson);
    }

    @Test
    @Transactional
    void putNonExistingPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().size();
        person.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, person.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(person))
            )
            .andExpect(status().isBadRequest());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Person in Elasticsearch
        verify(mockPersonSearchRepository, times(0)).save(person);
    }

    @Test
    @Transactional
    void putWithIdMismatchPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().size();
        person.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(person))
            )
            .andExpect(status().isBadRequest());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Person in Elasticsearch
        verify(mockPersonSearchRepository, times(0)).save(person);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().size();
        person.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(person)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Person in Elasticsearch
        verify(mockPersonSearchRepository, times(0)).save(person);
    }

    @Test
    @Transactional
    void partialUpdatePersonWithPatch() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        int databaseSizeBeforeUpdate = personRepository.findAll().size();

        // Update the person using partial update
        Person partialUpdatedPerson = new Person();
        partialUpdatedPerson.setId(person.getId());

        partialUpdatedPerson.aka(UPDATED_AKA);

        restPersonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPerson.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPerson))
            )
            .andExpect(status().isOk());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
        Person testPerson = personList.get(personList.size() - 1);
        assertThat(testPerson.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPerson.getLatinName()).isEqualTo(DEFAULT_LATIN_NAME);
        assertThat(testPerson.getAka()).isEqualTo(UPDATED_AKA);
        assertThat(testPerson.getBirth()).isEqualTo(DEFAULT_BIRTH);
        assertThat(testPerson.getDeath()).isEqualTo(DEFAULT_DEATH);
        assertThat(testPerson.getAffiliation()).isEqualTo(DEFAULT_AFFILIATION);
    }

    @Test
    @Transactional
    void fullUpdatePersonWithPatch() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        int databaseSizeBeforeUpdate = personRepository.findAll().size();

        // Update the person using partial update
        Person partialUpdatedPerson = new Person();
        partialUpdatedPerson.setId(person.getId());

        partialUpdatedPerson
            .name(UPDATED_NAME)
            .latinName(UPDATED_LATIN_NAME)
            .aka(UPDATED_AKA)
            .birth(UPDATED_BIRTH)
            .death(UPDATED_DEATH)
            .affiliation(UPDATED_AFFILIATION);

        restPersonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPerson.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPerson))
            )
            .andExpect(status().isOk());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
        Person testPerson = personList.get(personList.size() - 1);
        assertThat(testPerson.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPerson.getLatinName()).isEqualTo(UPDATED_LATIN_NAME);
        assertThat(testPerson.getAka()).isEqualTo(UPDATED_AKA);
        assertThat(testPerson.getBirth()).isEqualTo(UPDATED_BIRTH);
        assertThat(testPerson.getDeath()).isEqualTo(UPDATED_DEATH);
        assertThat(testPerson.getAffiliation()).isEqualTo(UPDATED_AFFILIATION);
    }

    @Test
    @Transactional
    void patchNonExistingPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().size();
        person.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, person.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(person))
            )
            .andExpect(status().isBadRequest());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Person in Elasticsearch
        verify(mockPersonSearchRepository, times(0)).save(person);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().size();
        person.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(person))
            )
            .andExpect(status().isBadRequest());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Person in Elasticsearch
        verify(mockPersonSearchRepository, times(0)).save(person);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().size();
        person.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(person)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Person in Elasticsearch
        verify(mockPersonSearchRepository, times(0)).save(person);
    }

    @Test
    @Transactional
    void deletePerson() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        int databaseSizeBeforeDelete = personRepository.findAll().size();

        // Delete the person
        restPersonMockMvc
            .perform(delete(ENTITY_API_URL_ID, person.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Person in Elasticsearch
        verify(mockPersonSearchRepository, times(1)).deleteById(person.getId());
    }

    @Test
    @Transactional
    void searchPerson() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        personRepository.saveAndFlush(person);
        when(mockPersonSearchRepository.search("id:" + person.getId())).thenReturn(Stream.of(person));

        // Search the person
        restPersonMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + person.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(person.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].latinName").value(hasItem(DEFAULT_LATIN_NAME)))
            .andExpect(jsonPath("$.[*].aka").value(hasItem(DEFAULT_AKA)))
            .andExpect(jsonPath("$.[*].birth").value(hasItem(DEFAULT_BIRTH)))
            .andExpect(jsonPath("$.[*].death").value(hasItem(DEFAULT_DEATH)))
            .andExpect(jsonPath("$.[*].affiliation").value(hasItem(DEFAULT_AFFILIATION)));
    }
}
