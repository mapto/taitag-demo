package it.unimi.di.islab.taitag.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link PlaceSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PlaceSearchRepositoryMockConfiguration {

    @MockBean
    private PlaceSearchRepository mockPlaceSearchRepository;
}
