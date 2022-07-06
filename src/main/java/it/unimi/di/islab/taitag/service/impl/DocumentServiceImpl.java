package it.unimi.di.islab.taitag.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import it.unimi.di.islab.taitag.domain.Document;
import it.unimi.di.islab.taitag.repository.DocumentRepository;
import it.unimi.di.islab.taitag.repository.search.DocumentSearchRepository;
import it.unimi.di.islab.taitag.service.DocumentService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Document}.
 */
@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private final DocumentRepository documentRepository;

    private final DocumentSearchRepository documentSearchRepository;

    public DocumentServiceImpl(DocumentRepository documentRepository, DocumentSearchRepository documentSearchRepository) {
        this.documentRepository = documentRepository;
        this.documentSearchRepository = documentSearchRepository;
    }

    @Override
    public Document save(Document document) {
        log.debug("Request to save Document : {}", document);
        Document result = documentRepository.save(document);
        documentSearchRepository.save(result);
        return result;
    }

    @Override
    public Document update(Document document) {
        log.debug("Request to save Document : {}", document);
        document.setIsPersisted();
        Document result = documentRepository.save(document);
        documentSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Document> partialUpdate(Document document) {
        log.debug("Request to partially update Document : {}", document);

        return documentRepository
            .findById(document.getId())
            .map(existingDocument -> {
                if (document.getGeorgianDate() != null) {
                    existingDocument.setGeorgianDate(document.getGeorgianDate());
                }
                if (document.getType() != null) {
                    existingDocument.setType(document.getType());
                }
                if (document.getIncipit() != null) {
                    existingDocument.setIncipit(document.getIncipit());
                }
                if (document.getTranscription() != null) {
                    existingDocument.setTranscription(document.getTranscription());
                }
                if (document.getCollection() != null) {
                    existingDocument.setCollection(document.getCollection());
                }
                if (document.getArchive() != null) {
                    existingDocument.setArchive(document.getArchive());
                }
                if (document.getFolder() != null) {
                    existingDocument.setFolder(document.getFolder());
                }
                if (document.getFolderN() != null) {
                    existingDocument.setFolderN(document.getFolderN());
                }
                if (document.getShelfMark() != null) {
                    existingDocument.setShelfMark(document.getShelfMark());
                }
                if (document.getPublication() != null) {
                    existingDocument.setPublication(document.getPublication());
                }
                if (document.getField() != null) {
                    existingDocument.setField(document.getField());
                }

                return existingDocument;
            })
            .map(documentRepository::save)
            .map(savedDocument -> {
                documentSearchRepository.save(savedDocument);

                return savedDocument;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Document> findAll(Pageable pageable) {
        log.debug("Request to get all Documents");
        return documentRepository.findAll(pageable);
    }

    public Page<Document> findAllWithEagerRelationships(Pageable pageable) {
        return documentRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Document> findOne(String id) {
        log.debug("Request to get Document : {}", id);
        return documentRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete Document : {}", id);
        documentRepository.deleteById(id);
        documentSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Document> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Documents for query {}", query);
        return documentSearchRepository.search(query, pageable);
    }
}
