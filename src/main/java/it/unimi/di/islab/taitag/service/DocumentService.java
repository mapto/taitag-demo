package it.unimi.di.islab.taitag.service;

import it.unimi.di.islab.taitag.domain.Document;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Document}.
 */
public interface DocumentService {
    /**
     * Save a document.
     *
     * @param document the entity to save.
     * @return the persisted entity.
     */
    Document save(Document document);

    /**
     * Updates a document.
     *
     * @param document the entity to update.
     * @return the persisted entity.
     */
    Document update(Document document);

    /**
     * Partially updates a document.
     *
     * @param document the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Document> partialUpdate(Document document);

    /**
     * Get all the documents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Document> findAll(Pageable pageable);

    /**
     * Get all the documents with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Document> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" document.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Document> findOne(String id);

    /**
     * Delete the "id" document.
     *
     * @param id the id of the entity.
     */
    void delete(String id);

    /**
     * Search for the document corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Document> search(String query, Pageable pageable);
}
