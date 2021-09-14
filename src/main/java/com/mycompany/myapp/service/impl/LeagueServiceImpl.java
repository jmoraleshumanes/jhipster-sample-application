package com.mycompany.myapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.mycompany.myapp.domain.League;
import com.mycompany.myapp.repository.LeagueRepository;
import com.mycompany.myapp.repository.search.LeagueSearchRepository;
import com.mycompany.myapp.service.LeagueService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link League}.
 */
@Service
@Transactional
public class LeagueServiceImpl implements LeagueService {

    private final Logger log = LoggerFactory.getLogger(LeagueServiceImpl.class);

    private final LeagueRepository leagueRepository;

    private final LeagueSearchRepository leagueSearchRepository;

    public LeagueServiceImpl(LeagueRepository leagueRepository, LeagueSearchRepository leagueSearchRepository) {
        this.leagueRepository = leagueRepository;
        this.leagueSearchRepository = leagueSearchRepository;
    }

    @Override
    public League save(League league) {
        log.debug("Request to save League : {}", league);
        League result = leagueRepository.save(league);
        leagueSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<League> partialUpdate(League league) {
        log.debug("Request to partially update League : {}", league);

        return leagueRepository
            .findById(league.getId())
            .map(
                existingLeague -> {
                    if (league.getName() != null) {
                        existingLeague.setName(league.getName());
                    }

                    return existingLeague;
                }
            )
            .map(leagueRepository::save)
            .map(
                savedLeague -> {
                    leagueSearchRepository.save(savedLeague);

                    return savedLeague;
                }
            );
    }

    @Override
    @Transactional(readOnly = true)
    public List<League> findAll() {
        log.debug("Request to get all Leagues");
        return leagueRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<League> findOne(Long id) {
        log.debug("Request to get League : {}", id);
        return leagueRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete League : {}", id);
        leagueRepository.deleteById(id);
        leagueSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<League> search(String query) {
        log.debug("Request to search Leagues for query {}", query);
        return StreamSupport
            .stream(leagueSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
