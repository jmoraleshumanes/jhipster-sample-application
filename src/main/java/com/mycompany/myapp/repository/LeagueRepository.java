package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.League;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the League entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {}
