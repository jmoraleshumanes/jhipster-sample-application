package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Team;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Team} entity.
 */
public interface TeamSearchRepository extends ElasticsearchRepository<Team, Long> {}
