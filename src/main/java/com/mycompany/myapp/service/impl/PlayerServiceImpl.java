package com.mycompany.myapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.Player;
import com.mycompany.myapp.repository.PlayerRepository;
import com.mycompany.myapp.repository.search.PlayerSearchRepository;
import com.mycompany.myapp.service.PlayerService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Player}.
 */
@Service
@Transactional
public class PlayerServiceImpl implements PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);

    private final PlayerRepository playerRepository;

    private final PlayerSearchRepository playerSearchRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository, PlayerSearchRepository playerSearchRepository) {
        this.playerRepository = playerRepository;
        this.playerSearchRepository = playerSearchRepository;
    }

    @Override
    public Player save(Player player) {
        log.debug("Request to save Player : {}", player);
        Player result = playerRepository.save(player);
        playerSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Player> partialUpdate(Player player) {
        log.debug("Request to partially update Player : {}", player);

        return playerRepository
            .findById(player.getId())
            .map(
                existingPlayer -> {
                    if (player.getName() != null) {
                        existingPlayer.setName(player.getName());
                    }
                    if (player.getCountry() != null) {
                        existingPlayer.setCountry(player.getCountry());
                    }
                    if (player.getAge() != null) {
                        existingPlayer.setAge(player.getAge());
                    }
                    if (player.getPosition() != null) {
                        existingPlayer.setPosition(player.getPosition());
                    }
                    if (player.getFoot() != null) {
                        existingPlayer.setFoot(player.getFoot());
                    }
                    if (player.getSigned() != null) {
                        existingPlayer.setSigned(player.getSigned());
                    }
                    if (player.getContractUntil() != null) {
                        existingPlayer.setContractUntil(player.getContractUntil());
                    }
                    if (player.getValue() != null) {
                        existingPlayer.setValue(player.getValue());
                    }

                    return existingPlayer;
                }
            )
            .map(playerRepository::save)
            .map(
                savedPlayer -> {
                    playerSearchRepository.save(savedPlayer);

                    return savedPlayer;
                }
            );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Player> findAll() {
        log.debug("Request to get all Players");
        return playerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Player> findOne(Long id) {
        log.debug("Request to get Player : {}", id);
        return playerRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Player : {}", id);
        playerRepository.deleteById(id);
        playerSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Player> search(String query) {
        log.debug("Request to search Players for query {}", query);
        return StreamSupport
            .stream(playerSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
