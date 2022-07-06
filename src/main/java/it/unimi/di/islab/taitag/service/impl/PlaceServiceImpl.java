package it.unimi.di.islab.taitag.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import it.unimi.di.islab.taitag.domain.Place;
import it.unimi.di.islab.taitag.repository.PlaceRepository;
import it.unimi.di.islab.taitag.repository.search.PlaceSearchRepository;
import it.unimi.di.islab.taitag.service.PlaceService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Place}.
 */
@Service
@Transactional
public class PlaceServiceImpl implements PlaceService {

    private final Logger log = LoggerFactory.getLogger(PlaceServiceImpl.class);

    private final PlaceRepository placeRepository;

    private final PlaceSearchRepository placeSearchRepository;

    public PlaceServiceImpl(PlaceRepository placeRepository, PlaceSearchRepository placeSearchRepository) {
        this.placeRepository = placeRepository;
        this.placeSearchRepository = placeSearchRepository;
    }

    @Override
    public Place save(Place place) {
        log.debug("Request to save Place : {}", place);
        Place result = placeRepository.save(place);
        placeSearchRepository.save(result);
        return result;
    }

    @Override
    public Place update(Place place) {
        log.debug("Request to save Place : {}", place);
        Place result = placeRepository.save(place);
        placeSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Place> partialUpdate(Place place) {
        log.debug("Request to partially update Place : {}", place);

        return placeRepository
            .findById(place.getId())
            .map(existingPlace -> {
                if (place.getCity() != null) {
                    existingPlace.setCity(place.getCity());
                }
                if (place.getItalianName() != null) {
                    existingPlace.setItalianName(place.getItalianName());
                }

                return existingPlace;
            })
            .map(placeRepository::save)
            .map(savedPlace -> {
                placeSearchRepository.save(savedPlace);

                return savedPlace;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Place> findAll() {
        log.debug("Request to get all Places");
        return placeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Place> findOne(Long id) {
        log.debug("Request to get Place : {}", id);
        return placeRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Place : {}", id);
        placeRepository.deleteById(id);
        placeSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Place> search(String query) {
        log.debug("Request to search Places for query {}", query);
        return StreamSupport.stream(placeSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
