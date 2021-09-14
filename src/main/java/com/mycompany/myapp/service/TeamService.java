package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Team;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Team}.
 */
public interface TeamService {
    /**
     * Save a team.
     *
     * @param team the entity to save.
     * @return the persisted entity.
     */
    Team save(Team team);

    /**
     * Partially updates a team.
     *
     * @param team the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Team> partialUpdate(Team team);

    /**
     * Get all the teams.
     *
     * @return the list of entities.
     */
    List<Team> findAll();

    /**
     * Get the "id" team.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Team> findOne(Long id);

    /**
     * Delete the "id" team.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the team corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<Team> search(String query);
}
