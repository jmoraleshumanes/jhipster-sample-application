package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Player;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Player}.
 */
public interface PlayerService {
    /**
     * Save a player.
     *
     * @param player the entity to save.
     * @return the persisted entity.
     */
    Player save(Player player);

    /**
     * Partially updates a player.
     *
     * @param player the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Player> partialUpdate(Player player);

    /**
     * Get all the players.
     *
     * @return the list of entities.
     */
    List<Player> findAll();

    /**
     * Get the "id" player.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Player> findOne(Long id);

    /**
     * Delete the "id" player.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the player corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<Player> search(String query);
}
