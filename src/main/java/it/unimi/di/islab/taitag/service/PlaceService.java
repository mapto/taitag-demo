package it.unimi.di.islab.taitag.service;

import it.unimi.di.islab.taitag.domain.Place;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Place}.
 */
public interface PlaceService {
    /**
     * Save a place.
     *
     * @param place the entity to save.
     * @return the persisted entity.
     */
    Place save(Place place);

    /**
     * Updates a place.
     *
     * @param place the entity to update.
     * @return the persisted entity.
     */
    Place update(Place place);

    /**
     * Partially updates a place.
     *
     * @param place the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Place> partialUpdate(Place place);

    /**
     * Get all the places.
     *
     * @return the list of entities.
     */
    List<Place> findAll();

    /**
     * Get the "id" place.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Place> findOne(Long id);

    /**
     * Delete the "id" place.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the place corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<Place> search(String query);
}
