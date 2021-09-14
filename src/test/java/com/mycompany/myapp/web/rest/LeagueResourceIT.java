package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.League;
import com.mycompany.myapp.repository.LeagueRepository;
import com.mycompany.myapp.repository.search.LeagueSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LeagueResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LeagueResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/leagues";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/leagues";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LeagueRepository leagueRepository;

    /**
     * This repository is mocked in the com.mycompany.myapp.repository.search test package.
     *
     * @see com.mycompany.myapp.repository.search.LeagueSearchRepositoryMockConfiguration
     */
    @Autowired
    private LeagueSearchRepository mockLeagueSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLeagueMockMvc;

    private League league;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static League createEntity(EntityManager em) {
        League league = new League().name(DEFAULT_NAME);
        return league;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static League createUpdatedEntity(EntityManager em) {
        League league = new League().name(UPDATED_NAME);
        return league;
    }

    @BeforeEach
    public void initTest() {
        league = createEntity(em);
    }

    @Test
    @Transactional
    void createLeague() throws Exception {
        int databaseSizeBeforeCreate = leagueRepository.findAll().size();
        // Create the League
        restLeagueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(league)))
            .andExpect(status().isCreated());

        // Validate the League in the database
        List<League> leagueList = leagueRepository.findAll();
        assertThat(leagueList).hasSize(databaseSizeBeforeCreate + 1);
        League testLeague = leagueList.get(leagueList.size() - 1);
        assertThat(testLeague.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the League in Elasticsearch
        verify(mockLeagueSearchRepository, times(1)).save(testLeague);
    }

    @Test
    @Transactional
    void createLeagueWithExistingId() throws Exception {
        // Create the League with an existing ID
        league.setId(1L);

        int databaseSizeBeforeCreate = leagueRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLeagueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(league)))
            .andExpect(status().isBadRequest());

        // Validate the League in the database
        List<League> leagueList = leagueRepository.findAll();
        assertThat(leagueList).hasSize(databaseSizeBeforeCreate);

        // Validate the League in Elasticsearch
        verify(mockLeagueSearchRepository, times(0)).save(league);
    }

    @Test
    @Transactional
    void getAllLeagues() throws Exception {
        // Initialize the database
        leagueRepository.saveAndFlush(league);

        // Get all the leagueList
        restLeagueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(league.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getLeague() throws Exception {
        // Initialize the database
        leagueRepository.saveAndFlush(league);

        // Get the league
        restLeagueMockMvc
            .perform(get(ENTITY_API_URL_ID, league.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(league.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingLeague() throws Exception {
        // Get the league
        restLeagueMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLeague() throws Exception {
        // Initialize the database
        leagueRepository.saveAndFlush(league);

        int databaseSizeBeforeUpdate = leagueRepository.findAll().size();

        // Update the league
        League updatedLeague = leagueRepository.findById(league.getId()).get();
        // Disconnect from session so that the updates on updatedLeague are not directly saved in db
        em.detach(updatedLeague);
        updatedLeague.name(UPDATED_NAME);

        restLeagueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLeague.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLeague))
            )
            .andExpect(status().isOk());

        // Validate the League in the database
        List<League> leagueList = leagueRepository.findAll();
        assertThat(leagueList).hasSize(databaseSizeBeforeUpdate);
        League testLeague = leagueList.get(leagueList.size() - 1);
        assertThat(testLeague.getName()).isEqualTo(UPDATED_NAME);

        // Validate the League in Elasticsearch
        verify(mockLeagueSearchRepository).save(testLeague);
    }

    @Test
    @Transactional
    void putNonExistingLeague() throws Exception {
        int databaseSizeBeforeUpdate = leagueRepository.findAll().size();
        league.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLeagueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, league.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(league))
            )
            .andExpect(status().isBadRequest());

        // Validate the League in the database
        List<League> leagueList = leagueRepository.findAll();
        assertThat(leagueList).hasSize(databaseSizeBeforeUpdate);

        // Validate the League in Elasticsearch
        verify(mockLeagueSearchRepository, times(0)).save(league);
    }

    @Test
    @Transactional
    void putWithIdMismatchLeague() throws Exception {
        int databaseSizeBeforeUpdate = leagueRepository.findAll().size();
        league.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeagueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(league))
            )
            .andExpect(status().isBadRequest());

        // Validate the League in the database
        List<League> leagueList = leagueRepository.findAll();
        assertThat(leagueList).hasSize(databaseSizeBeforeUpdate);

        // Validate the League in Elasticsearch
        verify(mockLeagueSearchRepository, times(0)).save(league);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLeague() throws Exception {
        int databaseSizeBeforeUpdate = leagueRepository.findAll().size();
        league.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeagueMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(league)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the League in the database
        List<League> leagueList = leagueRepository.findAll();
        assertThat(leagueList).hasSize(databaseSizeBeforeUpdate);

        // Validate the League in Elasticsearch
        verify(mockLeagueSearchRepository, times(0)).save(league);
    }

    @Test
    @Transactional
    void partialUpdateLeagueWithPatch() throws Exception {
        // Initialize the database
        leagueRepository.saveAndFlush(league);

        int databaseSizeBeforeUpdate = leagueRepository.findAll().size();

        // Update the league using partial update
        League partialUpdatedLeague = new League();
        partialUpdatedLeague.setId(league.getId());

        restLeagueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLeague.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLeague))
            )
            .andExpect(status().isOk());

        // Validate the League in the database
        List<League> leagueList = leagueRepository.findAll();
        assertThat(leagueList).hasSize(databaseSizeBeforeUpdate);
        League testLeague = leagueList.get(leagueList.size() - 1);
        assertThat(testLeague.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateLeagueWithPatch() throws Exception {
        // Initialize the database
        leagueRepository.saveAndFlush(league);

        int databaseSizeBeforeUpdate = leagueRepository.findAll().size();

        // Update the league using partial update
        League partialUpdatedLeague = new League();
        partialUpdatedLeague.setId(league.getId());

        partialUpdatedLeague.name(UPDATED_NAME);

        restLeagueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLeague.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLeague))
            )
            .andExpect(status().isOk());

        // Validate the League in the database
        List<League> leagueList = leagueRepository.findAll();
        assertThat(leagueList).hasSize(databaseSizeBeforeUpdate);
        League testLeague = leagueList.get(leagueList.size() - 1);
        assertThat(testLeague.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingLeague() throws Exception {
        int databaseSizeBeforeUpdate = leagueRepository.findAll().size();
        league.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLeagueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, league.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(league))
            )
            .andExpect(status().isBadRequest());

        // Validate the League in the database
        List<League> leagueList = leagueRepository.findAll();
        assertThat(leagueList).hasSize(databaseSizeBeforeUpdate);

        // Validate the League in Elasticsearch
        verify(mockLeagueSearchRepository, times(0)).save(league);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLeague() throws Exception {
        int databaseSizeBeforeUpdate = leagueRepository.findAll().size();
        league.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeagueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(league))
            )
            .andExpect(status().isBadRequest());

        // Validate the League in the database
        List<League> leagueList = leagueRepository.findAll();
        assertThat(leagueList).hasSize(databaseSizeBeforeUpdate);

        // Validate the League in Elasticsearch
        verify(mockLeagueSearchRepository, times(0)).save(league);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLeague() throws Exception {
        int databaseSizeBeforeUpdate = leagueRepository.findAll().size();
        league.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeagueMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(league)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the League in the database
        List<League> leagueList = leagueRepository.findAll();
        assertThat(leagueList).hasSize(databaseSizeBeforeUpdate);

        // Validate the League in Elasticsearch
        verify(mockLeagueSearchRepository, times(0)).save(league);
    }

    @Test
    @Transactional
    void deleteLeague() throws Exception {
        // Initialize the database
        leagueRepository.saveAndFlush(league);

        int databaseSizeBeforeDelete = leagueRepository.findAll().size();

        // Delete the league
        restLeagueMockMvc
            .perform(delete(ENTITY_API_URL_ID, league.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<League> leagueList = leagueRepository.findAll();
        assertThat(leagueList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the League in Elasticsearch
        verify(mockLeagueSearchRepository, times(1)).deleteById(league.getId());
    }

    @Test
    @Transactional
    void searchLeague() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        leagueRepository.saveAndFlush(league);
        when(mockLeagueSearchRepository.search(queryStringQuery("id:" + league.getId()))).thenReturn(Collections.singletonList(league));

        // Search the league
        restLeagueMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + league.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(league.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
