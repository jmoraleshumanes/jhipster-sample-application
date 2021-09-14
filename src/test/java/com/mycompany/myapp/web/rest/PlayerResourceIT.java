package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Player;
import com.mycompany.myapp.domain.enumeration.Foot;
import com.mycompany.myapp.domain.enumeration.Position;
import com.mycompany.myapp.repository.PlayerRepository;
import com.mycompany.myapp.repository.search.PlayerSearchRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link PlayerResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PlayerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final Long DEFAULT_AGE = 1L;
    private static final Long UPDATED_AGE = 2L;

    private static final Position DEFAULT_POSITION = Position.POR;
    private static final Position UPDATED_POSITION = Position.LI;

    private static final Foot DEFAULT_FOOT = Foot.LEFT;
    private static final Foot UPDATED_FOOT = Foot.RIGHT;

    private static final Instant DEFAULT_SIGNED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SIGNED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CONTRACT_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CONTRACT_UNTIL = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_VALUE = 1L;
    private static final Long UPDATED_VALUE = 2L;

    private static final String ENTITY_API_URL = "/api/players";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/players";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PlayerRepository playerRepository;

    /**
     * This repository is mocked in the com.mycompany.myapp.repository.search test package.
     *
     * @see com.mycompany.myapp.repository.search.PlayerSearchRepositoryMockConfiguration
     */
    @Autowired
    private PlayerSearchRepository mockPlayerSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlayerMockMvc;

    private Player player;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Player createEntity(EntityManager em) {
        Player player = new Player()
            .name(DEFAULT_NAME)
            .country(DEFAULT_COUNTRY)
            .age(DEFAULT_AGE)
            .position(DEFAULT_POSITION)
            .foot(DEFAULT_FOOT)
            .signed(DEFAULT_SIGNED)
            .contractUntil(DEFAULT_CONTRACT_UNTIL)
            .value(DEFAULT_VALUE);
        return player;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Player createUpdatedEntity(EntityManager em) {
        Player player = new Player()
            .name(UPDATED_NAME)
            .country(UPDATED_COUNTRY)
            .age(UPDATED_AGE)
            .position(UPDATED_POSITION)
            .foot(UPDATED_FOOT)
            .signed(UPDATED_SIGNED)
            .contractUntil(UPDATED_CONTRACT_UNTIL)
            .value(UPDATED_VALUE);
        return player;
    }

    @BeforeEach
    public void initTest() {
        player = createEntity(em);
    }

    @Test
    @Transactional
    void createPlayer() throws Exception {
        int databaseSizeBeforeCreate = playerRepository.findAll().size();
        // Create the Player
        restPlayerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(player)))
            .andExpect(status().isCreated());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeCreate + 1);
        Player testPlayer = playerList.get(playerList.size() - 1);
        assertThat(testPlayer.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPlayer.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testPlayer.getAge()).isEqualTo(DEFAULT_AGE);
        assertThat(testPlayer.getPosition()).isEqualTo(DEFAULT_POSITION);
        assertThat(testPlayer.getFoot()).isEqualTo(DEFAULT_FOOT);
        assertThat(testPlayer.getSigned()).isEqualTo(DEFAULT_SIGNED);
        assertThat(testPlayer.getContractUntil()).isEqualTo(DEFAULT_CONTRACT_UNTIL);
        assertThat(testPlayer.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the Player in Elasticsearch
        verify(mockPlayerSearchRepository, times(1)).save(testPlayer);
    }

    @Test
    @Transactional
    void createPlayerWithExistingId() throws Exception {
        // Create the Player with an existing ID
        player.setId(1L);

        int databaseSizeBeforeCreate = playerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlayerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(player)))
            .andExpect(status().isBadRequest());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeCreate);

        // Validate the Player in Elasticsearch
        verify(mockPlayerSearchRepository, times(0)).save(player);
    }

    @Test
    @Transactional
    void getAllPlayers() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get all the playerList
        restPlayerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(player.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE.intValue())))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION.toString())))
            .andExpect(jsonPath("$.[*].foot").value(hasItem(DEFAULT_FOOT.toString())))
            .andExpect(jsonPath("$.[*].signed").value(hasItem(DEFAULT_SIGNED.toString())))
            .andExpect(jsonPath("$.[*].contractUntil").value(hasItem(DEFAULT_CONTRACT_UNTIL.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.intValue())));
    }

    @Test
    @Transactional
    void getPlayer() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        // Get the player
        restPlayerMockMvc
            .perform(get(ENTITY_API_URL_ID, player.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(player.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY))
            .andExpect(jsonPath("$.age").value(DEFAULT_AGE.intValue()))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION.toString()))
            .andExpect(jsonPath("$.foot").value(DEFAULT_FOOT.toString()))
            .andExpect(jsonPath("$.signed").value(DEFAULT_SIGNED.toString()))
            .andExpect(jsonPath("$.contractUntil").value(DEFAULT_CONTRACT_UNTIL.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingPlayer() throws Exception {
        // Get the player
        restPlayerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPlayer() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        int databaseSizeBeforeUpdate = playerRepository.findAll().size();

        // Update the player
        Player updatedPlayer = playerRepository.findById(player.getId()).get();
        // Disconnect from session so that the updates on updatedPlayer are not directly saved in db
        em.detach(updatedPlayer);
        updatedPlayer
            .name(UPDATED_NAME)
            .country(UPDATED_COUNTRY)
            .age(UPDATED_AGE)
            .position(UPDATED_POSITION)
            .foot(UPDATED_FOOT)
            .signed(UPDATED_SIGNED)
            .contractUntil(UPDATED_CONTRACT_UNTIL)
            .value(UPDATED_VALUE);

        restPlayerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPlayer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPlayer))
            )
            .andExpect(status().isOk());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);
        Player testPlayer = playerList.get(playerList.size() - 1);
        assertThat(testPlayer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPlayer.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testPlayer.getAge()).isEqualTo(UPDATED_AGE);
        assertThat(testPlayer.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testPlayer.getFoot()).isEqualTo(UPDATED_FOOT);
        assertThat(testPlayer.getSigned()).isEqualTo(UPDATED_SIGNED);
        assertThat(testPlayer.getContractUntil()).isEqualTo(UPDATED_CONTRACT_UNTIL);
        assertThat(testPlayer.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the Player in Elasticsearch
        verify(mockPlayerSearchRepository).save(testPlayer);
    }

    @Test
    @Transactional
    void putNonExistingPlayer() throws Exception {
        int databaseSizeBeforeUpdate = playerRepository.findAll().size();
        player.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, player.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(player))
            )
            .andExpect(status().isBadRequest());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Player in Elasticsearch
        verify(mockPlayerSearchRepository, times(0)).save(player);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlayer() throws Exception {
        int databaseSizeBeforeUpdate = playerRepository.findAll().size();
        player.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(player))
            )
            .andExpect(status().isBadRequest());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Player in Elasticsearch
        verify(mockPlayerSearchRepository, times(0)).save(player);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlayer() throws Exception {
        int databaseSizeBeforeUpdate = playerRepository.findAll().size();
        player.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(player)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Player in Elasticsearch
        verify(mockPlayerSearchRepository, times(0)).save(player);
    }

    @Test
    @Transactional
    void partialUpdatePlayerWithPatch() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        int databaseSizeBeforeUpdate = playerRepository.findAll().size();

        // Update the player using partial update
        Player partialUpdatedPlayer = new Player();
        partialUpdatedPlayer.setId(player.getId());

        partialUpdatedPlayer
            .name(UPDATED_NAME)
            .country(UPDATED_COUNTRY)
            .age(UPDATED_AGE)
            .foot(UPDATED_FOOT)
            .signed(UPDATED_SIGNED)
            .contractUntil(UPDATED_CONTRACT_UNTIL)
            .value(UPDATED_VALUE);

        restPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlayer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlayer))
            )
            .andExpect(status().isOk());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);
        Player testPlayer = playerList.get(playerList.size() - 1);
        assertThat(testPlayer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPlayer.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testPlayer.getAge()).isEqualTo(UPDATED_AGE);
        assertThat(testPlayer.getPosition()).isEqualTo(DEFAULT_POSITION);
        assertThat(testPlayer.getFoot()).isEqualTo(UPDATED_FOOT);
        assertThat(testPlayer.getSigned()).isEqualTo(UPDATED_SIGNED);
        assertThat(testPlayer.getContractUntil()).isEqualTo(UPDATED_CONTRACT_UNTIL);
        assertThat(testPlayer.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void fullUpdatePlayerWithPatch() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        int databaseSizeBeforeUpdate = playerRepository.findAll().size();

        // Update the player using partial update
        Player partialUpdatedPlayer = new Player();
        partialUpdatedPlayer.setId(player.getId());

        partialUpdatedPlayer
            .name(UPDATED_NAME)
            .country(UPDATED_COUNTRY)
            .age(UPDATED_AGE)
            .position(UPDATED_POSITION)
            .foot(UPDATED_FOOT)
            .signed(UPDATED_SIGNED)
            .contractUntil(UPDATED_CONTRACT_UNTIL)
            .value(UPDATED_VALUE);

        restPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlayer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlayer))
            )
            .andExpect(status().isOk());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);
        Player testPlayer = playerList.get(playerList.size() - 1);
        assertThat(testPlayer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPlayer.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testPlayer.getAge()).isEqualTo(UPDATED_AGE);
        assertThat(testPlayer.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testPlayer.getFoot()).isEqualTo(UPDATED_FOOT);
        assertThat(testPlayer.getSigned()).isEqualTo(UPDATED_SIGNED);
        assertThat(testPlayer.getContractUntil()).isEqualTo(UPDATED_CONTRACT_UNTIL);
        assertThat(testPlayer.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingPlayer() throws Exception {
        int databaseSizeBeforeUpdate = playerRepository.findAll().size();
        player.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, player.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(player))
            )
            .andExpect(status().isBadRequest());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Player in Elasticsearch
        verify(mockPlayerSearchRepository, times(0)).save(player);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlayer() throws Exception {
        int databaseSizeBeforeUpdate = playerRepository.findAll().size();
        player.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(player))
            )
            .andExpect(status().isBadRequest());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Player in Elasticsearch
        verify(mockPlayerSearchRepository, times(0)).save(player);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlayer() throws Exception {
        int databaseSizeBeforeUpdate = playerRepository.findAll().size();
        player.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(player)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Player in the database
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Player in Elasticsearch
        verify(mockPlayerSearchRepository, times(0)).save(player);
    }

    @Test
    @Transactional
    void deletePlayer() throws Exception {
        // Initialize the database
        playerRepository.saveAndFlush(player);

        int databaseSizeBeforeDelete = playerRepository.findAll().size();

        // Delete the player
        restPlayerMockMvc
            .perform(delete(ENTITY_API_URL_ID, player.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Player> playerList = playerRepository.findAll();
        assertThat(playerList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Player in Elasticsearch
        verify(mockPlayerSearchRepository, times(1)).deleteById(player.getId());
    }

    @Test
    @Transactional
    void searchPlayer() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        playerRepository.saveAndFlush(player);
        when(mockPlayerSearchRepository.search(queryStringQuery("id:" + player.getId()))).thenReturn(Collections.singletonList(player));

        // Search the player
        restPlayerMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + player.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(player.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE.intValue())))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION.toString())))
            .andExpect(jsonPath("$.[*].foot").value(hasItem(DEFAULT_FOOT.toString())))
            .andExpect(jsonPath("$.[*].signed").value(hasItem(DEFAULT_SIGNED.toString())))
            .andExpect(jsonPath("$.[*].contractUntil").value(hasItem(DEFAULT_CONTRACT_UNTIL.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.intValue())));
    }
}
