package it.unimi.di.islab.taitag.repository;

import it.unimi.di.islab.taitag.domain.Document;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class DocumentRepositoryWithBagRelationshipsImpl implements DocumentRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Document> fetchBagRelationships(Optional<Document> document) {
        return document.map(this::fetchReceiveds).map(this::fetchQuotedIns);
    }

    @Override
    public Page<Document> fetchBagRelationships(Page<Document> documents) {
        return new PageImpl<>(fetchBagRelationships(documents.getContent()), documents.getPageable(), documents.getTotalElements());
    }

    @Override
    public List<Document> fetchBagRelationships(List<Document> documents) {
        return Optional.of(documents).map(this::fetchReceiveds).map(this::fetchQuotedIns).orElse(Collections.emptyList());
    }

    Document fetchReceiveds(Document result) {
        return entityManager
            .createQuery(
                "select document from Document document left join fetch document.receiveds where document is :document",
                Document.class
            )
            .setParameter("document", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Document> fetchReceiveds(List<Document> documents) {
        return entityManager
            .createQuery(
                "select distinct document from Document document left join fetch document.receiveds where document in :documents",
                Document.class
            )
            .setParameter("documents", documents)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }

    Document fetchQuotedIns(Document result) {
        return entityManager
            .createQuery(
                "select document from Document document left join fetch document.quotedIns where document is :document",
                Document.class
            )
            .setParameter("document", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Document> fetchQuotedIns(List<Document> documents) {
        return entityManager
            .createQuery(
                "select distinct document from Document document left join fetch document.quotedIns where document in :documents",
                Document.class
            )
            .setParameter("documents", documents)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
