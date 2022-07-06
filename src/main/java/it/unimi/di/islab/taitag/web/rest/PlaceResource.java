package it.unimi.di.islab.taitag.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import it.unimi.di.islab.taitag.domain.Place;
import it.unimi.di.islab.taitag.repository.PlaceRepository;
import it.unimi.di.islab.taitag.service.PlaceService;
import it.unimi.di.islab.taitag.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link it.unimi.di.islab.taitag.domain.Place}.
 */
@RestController
@RequestMapping("/api")
public class PlaceResource {

    private final Logger log = LoggerFactory.getLogger(PlaceResource.class);

    private static final String ENTITY_NAME = "place";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlaceService placeService;

    private final PlaceRepository placeRepository;

    public PlaceResource(PlaceService placeService, PlaceRepository placeRepository) {
        this.placeService = placeService;
        this.placeRepository = placeRepository;
    }

    /**
     * {@code POST  /places} : Create a new place.
     *
     * @param place the place to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new place, or with status {@code 400 (Bad Request)} if the place has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/places")
    public ResponseEntity<Place> createPlace(@Valid @RequestBody Place place) throws URISyntaxException {
        log.debug("REST request to save Place : {}", place);
        if (place.getId() != null) {
            throw new BadRequestAlertException("A new place cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Place result = placeService.save(place);
        return ResponseEntity
            .created(new URI("/api/places/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /places/:id} : Updates an existing place.
     *
     * @param id the id of the place to save.
     * @param place the place to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated place,
     * or with status {@code 400 (Bad Request)} if the place is not valid,
     * or with status {@code 500 (Internal Server Error)} if the place couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/places/{id}")
    public ResponseEntity<Place> updatePlace(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Place place)
        throws URISyntaxException {
        log.debug("REST request to update Place : {}, {}", id, place);
        if (place.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, place.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!placeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Place result = placeService.update(place);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, place.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /places/:id} : Partial updates given fields of an existing place, field will ignore if it is null
     *
     * @param id the id of the place to save.
     * @param place the place to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated place,
     * or with status {@code 400 (Bad Request)} if the place is not valid,
     * or with status {@code 404 (Not Found)} if the place is not found,
     * or with status {@code 500 (Internal Server Error)} if the place couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/places/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Place> partialUpdatePlace(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Place place
    ) throws URISyntaxException {
        log.debug("REST request to partial update Place partially : {}, {}", id, place);
        if (place.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, place.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!placeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Place> result = placeService.partialUpdate(place);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, place.getId().toString())
        );
    }

    /**
     * {@code GET  /places} : get all the places.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of places in body.
     */
    @GetMapping("/places")
    public List<Place> getAllPlaces() {
        log.debug("REST request to get all Places");
        return placeService.findAll();
    }

    /**
     * {@code GET  /places/:id} : get the "id" place.
     *
     * @param id the id of the place to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the place, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/places/{id}")
    public ResponseEntity<Place> getPlace(@PathVariable Long id) {
        log.debug("REST request to get Place : {}", id);
        Optional<Place> place = placeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(place);
    }

    /**
     * {@code DELETE  /places/:id} : delete the "id" place.
     *
     * @param id the id of the place to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/places/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        log.debug("REST request to delete Place : {}", id);
        placeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/places?query=:query} : search for the place corresponding
     * to the query.
     *
     * @param query the query of the place search.
     * @return the result of the search.
     */
    @GetMapping("/_search/places")
    public List<Place> searchPlaces(@RequestParam String query) {
        log.debug("REST request to search Places for query {}", query);
        return placeService.search(query);
    }
}
