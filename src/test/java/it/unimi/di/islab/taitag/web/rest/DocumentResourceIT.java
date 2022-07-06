package it.unimi.di.islab.taitag.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.unimi.di.islab.taitag.IntegrationTest;
import it.unimi.di.islab.taitag.domain.Document;
import it.unimi.di.islab.taitag.domain.enumeration.Archive;
import it.unimi.di.islab.taitag.domain.enumeration.DocumentType;
import it.unimi.di.islab.taitag.domain.enumeration.Field;
import it.unimi.di.islab.taitag.repository.DocumentRepository;
import it.unimi.di.islab.taitag.repository.search.DocumentSearchRepository;
import it.unimi.di.islab.taitag.service.DocumentService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DocumentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DocumentResourceIT {

    private static final LocalDate DEFAULT_GEORGIAN_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_GEORGIAN_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final DocumentType DEFAULT_TYPE = DocumentType.ResearchNotes;
    private static final DocumentType UPDATED_TYPE = DocumentType.Letter;

    private static final String DEFAULT_INCIPIT = "AAAAAAAAAA";
    private static final String UPDATED_INCIPIT = "BBBBBBBBBB";

    private static final String DEFAULT_TRANSCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_TRANSCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_COLLECTION = "AAAAAAAAAA";
    private static final String UPDATED_COLLECTION = "BBBBBBBBBB";

    private static final Archive DEFAULT_ARCHIVE = Archive.BNF;
    private static final Archive UPDATED_ARCHIVE = Archive.BNCF;

    private static final String DEFAULT_FOLDER = "AAAAAAAAAA";
    private static final String UPDATED_FOLDER = "BBBBBBBBBB";

    private static final String DEFAULT_FOLDER_N = "AAAAAAAAAA";
    private static final String UPDATED_FOLDER_N = "BBBBBBBBBB";

    private static final String DEFAULT_SHELF_MARK = "AAAAAAAAAA";
    private static final String UPDATED_SHELF_MARK = "BBBBBBBBBB";

    private static final String DEFAULT_PUBLICATION = "AAAAAAAAAA";
    private static final String UPDATED_PUBLICATION = "BBBBBBBBBB";

    private static final Field DEFAULT_FIELD = Field.Pneumatics;
    private static final Field UPDATED_FIELD = Field.Travel;

    private static final String ENTITY_API_URL = "/api/documents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/documents";

    @Autowired
    private DocumentRepository documentRepository;

    @Mock
    private DocumentRepository documentRepositoryMock;

    @Mock
    private DocumentService documentServiceMock;

    /**
     * This repository is mocked in the it.unimi.di.islab.taitag.repository.search test package.
     *
     * @see it.unimi.di.islab.taitag.repository.search.DocumentSearchRepositoryMockConfiguration
     */
    @Autowired
    private DocumentSearchRepository mockDocumentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDocumentMockMvc;

    private Document document;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Document createEntity(EntityManager em) {
        Document document = new Document()
            .georgianDate(DEFAULT_GEORGIAN_DATE)
            .type(DEFAULT_TYPE)
            .incipit(DEFAULT_INCIPIT)
            .transcription(DEFAULT_TRANSCRIPTION)
            .collection(DEFAULT_COLLECTION)
            .archive(DEFAULT_ARCHIVE)
            .folder(DEFAULT_FOLDER)
            .folderN(DEFAULT_FOLDER_N)
            .shelfMark(DEFAULT_SHELF_MARK)
            .publication(DEFAULT_PUBLICATION)
            .field(DEFAULT_FIELD);
        return document;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Document createUpdatedEntity(EntityManager em) {
        Document document = new Document()
            .georgianDate(UPDATED_GEORGIAN_DATE)
            .type(UPDATED_TYPE)
            .incipit(UPDATED_INCIPIT)
            .transcription(UPDATED_TRANSCRIPTION)
            .collection(UPDATED_COLLECTION)
            .archive(UPDATED_ARCHIVE)
            .folder(UPDATED_FOLDER)
            .folderN(UPDATED_FOLDER_N)
            .shelfMark(UPDATED_SHELF_MARK)
            .publication(UPDATED_PUBLICATION)
            .field(UPDATED_FIELD);
        return document;
    }

    @BeforeEach
    public void initTest() {
        document = createEntity(em);
    }

    @Test
    @Transactional
    void createDocument() throws Exception {
        int databaseSizeBeforeCreate = documentRepository.findAll().size();
        // Create the Document
        restDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(document)))
            .andExpect(status().isCreated());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeCreate + 1);
        Document testDocument = documentList.get(documentList.size() - 1);
        assertThat(testDocument.getGeorgianDate()).isEqualTo(DEFAULT_GEORGIAN_DATE);
        assertThat(testDocument.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testDocument.getIncipit()).isEqualTo(DEFAULT_INCIPIT);
        assertThat(testDocument.getTranscription()).isEqualTo(DEFAULT_TRANSCRIPTION);
        assertThat(testDocument.getCollection()).isEqualTo(DEFAULT_COLLECTION);
        assertThat(testDocument.getArchive()).isEqualTo(DEFAULT_ARCHIVE);
        assertThat(testDocument.getFolder()).isEqualTo(DEFAULT_FOLDER);
        assertThat(testDocument.getFolderN()).isEqualTo(DEFAULT_FOLDER_N);
        assertThat(testDocument.getShelfMark()).isEqualTo(DEFAULT_SHELF_MARK);
        assertThat(testDocument.getPublication()).isEqualTo(DEFAULT_PUBLICATION);
        assertThat(testDocument.getField()).isEqualTo(DEFAULT_FIELD);

        // Validate the Document in Elasticsearch
        verify(mockDocumentSearchRepository, times(1)).save(testDocument);
    }

    @Test
    @Transactional
    void createDocumentWithExistingId() throws Exception {
        // Create the Document with an existing ID
        document.setId("existing_id");

        int databaseSizeBeforeCreate = documentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(document)))
            .andExpect(status().isBadRequest());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeCreate);

        // Validate the Document in Elasticsearch
        verify(mockDocumentSearchRepository, times(0)).save(document);
    }

    @Test
    @Transactional
    void getAllDocuments() throws Exception {
        // Initialize the database
        document.setId(UUID.randomUUID().toString());
        documentRepository.saveAndFlush(document);

        // Get all the documentList
        restDocumentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(document.getId())))
            .andExpect(jsonPath("$.[*].georgianDate").value(hasItem(DEFAULT_GEORGIAN_DATE.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].incipit").value(hasItem(DEFAULT_INCIPIT)))
            .andExpect(jsonPath("$.[*].transcription").value(hasItem(DEFAULT_TRANSCRIPTION)))
            .andExpect(jsonPath("$.[*].collection").value(hasItem(DEFAULT_COLLECTION)))
            .andExpect(jsonPath("$.[*].archive").value(hasItem(DEFAULT_ARCHIVE.toString())))
            .andExpect(jsonPath("$.[*].folder").value(hasItem(DEFAULT_FOLDER)))
            .andExpect(jsonPath("$.[*].folderN").value(hasItem(DEFAULT_FOLDER_N)))
            .andExpect(jsonPath("$.[*].shelfMark").value(hasItem(DEFAULT_SHELF_MARK)))
            .andExpect(jsonPath("$.[*].publication").value(hasItem(DEFAULT_PUBLICATION)))
            .andExpect(jsonPath("$.[*].field").value(hasItem(DEFAULT_FIELD.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDocumentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(documentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDocumentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(documentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDocumentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(documentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDocumentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(documentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getDocument() throws Exception {
        // Initialize the database
        document.setId(UUID.randomUUID().toString());
        documentRepository.saveAndFlush(document);

        // Get the document
        restDocumentMockMvc
            .perform(get(ENTITY_API_URL_ID, document.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(document.getId()))
            .andExpect(jsonPath("$.georgianDate").value(DEFAULT_GEORGIAN_DATE.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.incipit").value(DEFAULT_INCIPIT))
            .andExpect(jsonPath("$.transcription").value(DEFAULT_TRANSCRIPTION))
            .andExpect(jsonPath("$.collection").value(DEFAULT_COLLECTION))
            .andExpect(jsonPath("$.archive").value(DEFAULT_ARCHIVE.toString()))
            .andExpect(jsonPath("$.folder").value(DEFAULT_FOLDER))
            .andExpect(jsonPath("$.folderN").value(DEFAULT_FOLDER_N))
            .andExpect(jsonPath("$.shelfMark").value(DEFAULT_SHELF_MARK))
            .andExpect(jsonPath("$.publication").value(DEFAULT_PUBLICATION))
            .andExpect(jsonPath("$.field").value(DEFAULT_FIELD.toString()));
    }

    @Test
    @Transactional
    void getNonExistingDocument() throws Exception {
        // Get the document
        restDocumentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDocument() throws Exception {
        // Initialize the database
        document.setId(UUID.randomUUID().toString());
        documentRepository.saveAndFlush(document);

        int databaseSizeBeforeUpdate = documentRepository.findAll().size();

        // Update the document
        Document updatedDocument = documentRepository.findById(document.getId()).get();
        // Disconnect from session so that the updates on updatedDocument are not directly saved in db
        em.detach(updatedDocument);
        updatedDocument
            .georgianDate(UPDATED_GEORGIAN_DATE)
            .type(UPDATED_TYPE)
            .incipit(UPDATED_INCIPIT)
            .transcription(UPDATED_TRANSCRIPTION)
            .collection(UPDATED_COLLECTION)
            .archive(UPDATED_ARCHIVE)
            .folder(UPDATED_FOLDER)
            .folderN(UPDATED_FOLDER_N)
            .shelfMark(UPDATED_SHELF_MARK)
            .publication(UPDATED_PUBLICATION)
            .field(UPDATED_FIELD);

        restDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDocument.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDocument))
            )
            .andExpect(status().isOk());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);
        Document testDocument = documentList.get(documentList.size() - 1);
        assertThat(testDocument.getGeorgianDate()).isEqualTo(UPDATED_GEORGIAN_DATE);
        assertThat(testDocument.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testDocument.getIncipit()).isEqualTo(UPDATED_INCIPIT);
        assertThat(testDocument.getTranscription()).isEqualTo(UPDATED_TRANSCRIPTION);
        assertThat(testDocument.getCollection()).isEqualTo(UPDATED_COLLECTION);
        assertThat(testDocument.getArchive()).isEqualTo(UPDATED_ARCHIVE);
        assertThat(testDocument.getFolder()).isEqualTo(UPDATED_FOLDER);
        assertThat(testDocument.getFolderN()).isEqualTo(UPDATED_FOLDER_N);
        assertThat(testDocument.getShelfMark()).isEqualTo(UPDATED_SHELF_MARK);
        assertThat(testDocument.getPublication()).isEqualTo(UPDATED_PUBLICATION);
        assertThat(testDocument.getField()).isEqualTo(UPDATED_FIELD);

        // Validate the Document in Elasticsearch
        verify(mockDocumentSearchRepository).save(testDocument);
    }

    @Test
    @Transactional
    void putNonExistingDocument() throws Exception {
        int databaseSizeBeforeUpdate = documentRepository.findAll().size();
        document.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, document.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(document))
            )
            .andExpect(status().isBadRequest());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Document in Elasticsearch
        verify(mockDocumentSearchRepository, times(0)).save(document);
    }

    @Test
    @Transactional
    void putWithIdMismatchDocument() throws Exception {
        int databaseSizeBeforeUpdate = documentRepository.findAll().size();
        document.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(document))
            )
            .andExpect(status().isBadRequest());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Document in Elasticsearch
        verify(mockDocumentSearchRepository, times(0)).save(document);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDocument() throws Exception {
        int databaseSizeBeforeUpdate = documentRepository.findAll().size();
        document.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(document)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Document in Elasticsearch
        verify(mockDocumentSearchRepository, times(0)).save(document);
    }

    @Test
    @Transactional
    void partialUpdateDocumentWithPatch() throws Exception {
        // Initialize the database
        document.setId(UUID.randomUUID().toString());
        documentRepository.saveAndFlush(document);

        int databaseSizeBeforeUpdate = documentRepository.findAll().size();

        // Update the document using partial update
        Document partialUpdatedDocument = new Document();
        partialUpdatedDocument.setId(document.getId());

        partialUpdatedDocument
            .georgianDate(UPDATED_GEORGIAN_DATE)
            .type(UPDATED_TYPE)
            .folderN(UPDATED_FOLDER_N)
            .publication(UPDATED_PUBLICATION);

        restDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDocument.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDocument))
            )
            .andExpect(status().isOk());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);
        Document testDocument = documentList.get(documentList.size() - 1);
        assertThat(testDocument.getGeorgianDate()).isEqualTo(UPDATED_GEORGIAN_DATE);
        assertThat(testDocument.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testDocument.getIncipit()).isEqualTo(DEFAULT_INCIPIT);
        assertThat(testDocument.getTranscription()).isEqualTo(DEFAULT_TRANSCRIPTION);
        assertThat(testDocument.getCollection()).isEqualTo(DEFAULT_COLLECTION);
        assertThat(testDocument.getArchive()).isEqualTo(DEFAULT_ARCHIVE);
        assertThat(testDocument.getFolder()).isEqualTo(DEFAULT_FOLDER);
        assertThat(testDocument.getFolderN()).isEqualTo(UPDATED_FOLDER_N);
        assertThat(testDocument.getShelfMark()).isEqualTo(DEFAULT_SHELF_MARK);
        assertThat(testDocument.getPublication()).isEqualTo(UPDATED_PUBLICATION);
        assertThat(testDocument.getField()).isEqualTo(DEFAULT_FIELD);
    }

    @Test
    @Transactional
    void fullUpdateDocumentWithPatch() throws Exception {
        // Initialize the database
        document.setId(UUID.randomUUID().toString());
        documentRepository.saveAndFlush(document);

        int databaseSizeBeforeUpdate = documentRepository.findAll().size();

        // Update the document using partial update
        Document partialUpdatedDocument = new Document();
        partialUpdatedDocument.setId(document.getId());

        partialUpdatedDocument
            .georgianDate(UPDATED_GEORGIAN_DATE)
            .type(UPDATED_TYPE)
            .incipit(UPDATED_INCIPIT)
            .transcription(UPDATED_TRANSCRIPTION)
            .collection(UPDATED_COLLECTION)
            .archive(UPDATED_ARCHIVE)
            .folder(UPDATED_FOLDER)
            .folderN(UPDATED_FOLDER_N)
            .shelfMark(UPDATED_SHELF_MARK)
            .publication(UPDATED_PUBLICATION)
            .field(UPDATED_FIELD);

        restDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDocument.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDocument))
            )
            .andExpect(status().isOk());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);
        Document testDocument = documentList.get(documentList.size() - 1);
        assertThat(testDocument.getGeorgianDate()).isEqualTo(UPDATED_GEORGIAN_DATE);
        assertThat(testDocument.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testDocument.getIncipit()).isEqualTo(UPDATED_INCIPIT);
        assertThat(testDocument.getTranscription()).isEqualTo(UPDATED_TRANSCRIPTION);
        assertThat(testDocument.getCollection()).isEqualTo(UPDATED_COLLECTION);
        assertThat(testDocument.getArchive()).isEqualTo(UPDATED_ARCHIVE);
        assertThat(testDocument.getFolder()).isEqualTo(UPDATED_FOLDER);
        assertThat(testDocument.getFolderN()).isEqualTo(UPDATED_FOLDER_N);
        assertThat(testDocument.getShelfMark()).isEqualTo(UPDATED_SHELF_MARK);
        assertThat(testDocument.getPublication()).isEqualTo(UPDATED_PUBLICATION);
        assertThat(testDocument.getField()).isEqualTo(UPDATED_FIELD);
    }

    @Test
    @Transactional
    void patchNonExistingDocument() throws Exception {
        int databaseSizeBeforeUpdate = documentRepository.findAll().size();
        document.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, document.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(document))
            )
            .andExpect(status().isBadRequest());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Document in Elasticsearch
        verify(mockDocumentSearchRepository, times(0)).save(document);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDocument() throws Exception {
        int databaseSizeBeforeUpdate = documentRepository.findAll().size();
        document.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(document))
            )
            .andExpect(status().isBadRequest());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Document in Elasticsearch
        verify(mockDocumentSearchRepository, times(0)).save(document);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDocument() throws Exception {
        int databaseSizeBeforeUpdate = documentRepository.findAll().size();
        document.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(document)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Document in the database
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Document in Elasticsearch
        verify(mockDocumentSearchRepository, times(0)).save(document);
    }

    @Test
    @Transactional
    void deleteDocument() throws Exception {
        // Initialize the database
        document.setId(UUID.randomUUID().toString());
        documentRepository.saveAndFlush(document);

        int databaseSizeBeforeDelete = documentRepository.findAll().size();

        // Delete the document
        restDocumentMockMvc
            .perform(delete(ENTITY_API_URL_ID, document.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Document> documentList = documentRepository.findAll();
        assertThat(documentList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Document in Elasticsearch
        verify(mockDocumentSearchRepository, times(1)).deleteById(document.getId());
    }

    @Test
    @Transactional
    void searchDocument() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        document.setId(UUID.randomUUID().toString());
        documentRepository.saveAndFlush(document);
        when(mockDocumentSearchRepository.search("id:" + document.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(document), PageRequest.of(0, 1), 1));

        // Search the document
        restDocumentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + document.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(document.getId())))
            .andExpect(jsonPath("$.[*].georgianDate").value(hasItem(DEFAULT_GEORGIAN_DATE.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].incipit").value(hasItem(DEFAULT_INCIPIT)))
            .andExpect(jsonPath("$.[*].transcription").value(hasItem(DEFAULT_TRANSCRIPTION)))
            .andExpect(jsonPath("$.[*].collection").value(hasItem(DEFAULT_COLLECTION)))
            .andExpect(jsonPath("$.[*].archive").value(hasItem(DEFAULT_ARCHIVE.toString())))
            .andExpect(jsonPath("$.[*].folder").value(hasItem(DEFAULT_FOLDER)))
            .andExpect(jsonPath("$.[*].folderN").value(hasItem(DEFAULT_FOLDER_N)))
            .andExpect(jsonPath("$.[*].shelfMark").value(hasItem(DEFAULT_SHELF_MARK)))
            .andExpect(jsonPath("$.[*].publication").value(hasItem(DEFAULT_PUBLICATION)))
            .andExpect(jsonPath("$.[*].field").value(hasItem(DEFAULT_FIELD.toString())));
    }
}
