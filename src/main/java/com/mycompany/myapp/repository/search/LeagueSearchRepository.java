package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.League;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link League} entity.
 */
public interface LeagueSearchRepository extends ElasticsearchRepository<League, Long> {}
