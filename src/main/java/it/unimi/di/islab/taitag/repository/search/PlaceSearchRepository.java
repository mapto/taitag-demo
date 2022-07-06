package it.unimi.di.islab.taitag.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import it.unimi.di.islab.taitag.domain.Place;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Place} entity.
 */
public interface PlaceSearchRepository extends ElasticsearchRepository<Place, Long>, PlaceSearchRepositoryInternal {}

interface PlaceSearchRepositoryInternal {
    Stream<Place> search(String query);
}

class PlaceSearchRepositoryInternalImpl implements PlaceSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    PlaceSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Place> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Place.class).map(SearchHit::getContent).stream();
    }
}
