package it.unimi.di.islab.taitag.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import it.unimi.di.islab.taitag.domain.Document;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Document} entity.
 */
public interface DocumentSearchRepository extends ElasticsearchRepository<Document, String>, DocumentSearchRepositoryInternal {}

interface DocumentSearchRepositoryInternal {
    Page<Document> search(String query, Pageable pageable);
}

class DocumentSearchRepositoryInternalImpl implements DocumentSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    DocumentSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Document> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<Document> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Document.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}
