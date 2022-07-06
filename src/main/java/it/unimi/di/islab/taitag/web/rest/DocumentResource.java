package it.unimi.di.islab.taitag.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import it.unimi.di.islab.taitag.domain.Document;
import it.unimi.di.islab.taitag.repository.DocumentRepository;
import it.unimi.di.islab.taitag.service.DocumentService;
import it.unimi.di.islab.taitag.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link it.unimi.di.islab.taitag.domain.Document}.
 */
@RestController
@RequestMapping("/api")
public class DocumentResource {

    private final Logger log = LoggerFactory.getLogger(DocumentResource.class);

    private static final String ENTITY_NAME = "document";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DocumentService documentService;

    private final DocumentRepository documentRepository;

    public DocumentResource(DocumentService documentService, DocumentRepository documentRepository) {
        this.documentService = documentService;
        this.documentRepository = documentRepository;
    }

    /**
     * {@code POST  /documents} : Create a new document.
     *
     * @param document the document to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new document, or with status {@code 400 (Bad Request)} if the document has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/documents")
    public ResponseEntity<Document> createDocument(@Valid @RequestBody Document document) throws URISyntaxException {
        log.debug("REST request to save Document : {}", document);
        if (document.getId() != null) {
            throw new BadRequestAlertException("A new document cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Document result = documentService.save(document);
        return ResponseEntity
            .created(new URI("/api/documents/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /documents/:id} : Updates an existing document.
     *
     * @param id the id of the document to save.
     * @param document the document to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated document,
     * or with status {@code 400 (Bad Request)} if the document is not valid,
     * or with status {@code 500 (Internal Server Error)} if the document couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/documents/{id}")
    public ResponseEntity<Document> updateDocument(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody Document document
    ) throws URISyntaxException {
        log.debug("REST request to update Document : {}, {}", id, document);
        if (document.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, document.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!documentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Document result = documentService.update(document);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, document.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /documents/:id} : Partial updates given fields of an existing document, field will ignore if it is null
     *
     * @param id the id of the document to save.
     * @param document the document to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated document,
     * or with status {@code 400 (Bad Request)} if the document is not valid,
     * or with status {@code 404 (Not Found)} if the document is not found,
     * or with status {@code 500 (Internal Server Error)} if the document couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/documents/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Document> partialUpdateDocument(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Document document
    ) throws URISyntaxException {
        log.debug("REST request to partial update Document partially : {}, {}", id, document);
        if (document.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, document.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!documentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Document> result = documentService.partialUpdate(document);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, document.getId())
        );
    }

    /**
     * {@code GET  /documents} : get all the documents.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of documents in body.
     */
    @GetMapping("/documents")
    public ResponseEntity<List<Document>> getAllDocuments(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Documents");
        Page<Document> page;
        if (eagerload) {
            page = documentService.findAllWithEagerRelationships(pageable);
        } else {
            page = documentService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /documents/:id} : get the "id" document.
     *
     * @param id the id of the document to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the document, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/documents/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable String id) {
        log.debug("REST request to get Document : {}", id);
        Optional<Document> document = documentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(document);
    }

    /**
     * {@code DELETE  /documents/:id} : delete the "id" document.
     *
     * @param id the id of the document to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        log.debug("REST request to delete Document : {}", id);
        documentService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/documents?query=:query} : search for the document corresponding
     * to the query.
     *
     * @param query the query of the document search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/documents")
    public ResponseEntity<List<Document>> searchDocuments(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Documents for query {}", query);
        Page<Document> page = documentService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
