package it.unimi.di.islab.taitag.repository;

import it.unimi.di.islab.taitag.domain.Document;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Document entity.
 */
@Repository
public interface DocumentRepository extends DocumentRepositoryWithBagRelationships, JpaRepository<Document, String> {
    default Optional<Document> findOneWithEagerRelationships(String id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Document> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Document> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
