package it.unimi.di.islab.taitag.repository;

import it.unimi.di.islab.taitag.domain.Document;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface DocumentRepositoryWithBagRelationships {
    Optional<Document> fetchBagRelationships(Optional<Document> document);

    List<Document> fetchBagRelationships(List<Document> documents);

    Page<Document> fetchBagRelationships(Page<Document> documents);
}
