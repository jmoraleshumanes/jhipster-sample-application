package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.League;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link League}.
 */
public interface LeagueService {
    /**
     * Save a league.
     *
     * @param league the entity to save.
     * @return the persisted entity.
     */
    League save(League league);

    /**
     * Partially updates a league.
     *
     * @param league the entity to update partially.
     * @return the persisted entity.
     */
    Optional<League> partialUpdate(League league);

    /**
     * Get all the leagues.
     *
     * @return the list of entities.
     */
    List<League> findAll();

    /**
     * Get the "id" league.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<League> findOne(Long id);

    /**
     * Delete the "id" league.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the league corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<League> search(String query);
}
