package com.dugx.event.web.rest;

import static com.dugx.event.domain.FavoriteAsserts.*;
import static com.dugx.event.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dugx.event.IntegrationTest;
import com.dugx.event.domain.Event;
import com.dugx.event.domain.Favorite;
import com.dugx.event.domain.User;
import com.dugx.event.repository.FavoriteRepository;
import com.dugx.event.repository.UserRepository;
import com.dugx.event.service.FavoriteService;
import com.dugx.event.service.dto.FavoriteDTO;
import com.dugx.event.service.mapper.FavoriteMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FavoriteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FavoriteResourceIT {

    private static final String ENTITY_API_URL = "/api/favorites";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private FavoriteRepository favoriteRepositoryMock;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Mock
    private FavoriteService favoriteServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFavoriteMockMvc;

    private Favorite favorite;

    private Favorite insertedFavorite;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Favorite createEntity() {
        return new Favorite();
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Favorite createUpdatedEntity() {
        return new Favorite();
    }

    @BeforeEach
    void initTest() {
        favorite = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFavorite != null) {
            favoriteRepository.delete(insertedFavorite);
            insertedFavorite = null;
        }
    }

    @Test
    @Transactional
    void createFavorite() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);
        var returnedFavoriteDTO = om.readValue(
            restFavoriteMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(favoriteDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FavoriteDTO.class
        );

        // Validate the Favorite in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFavorite = favoriteMapper.toEntity(returnedFavoriteDTO);
        assertFavoriteUpdatableFieldsEquals(returnedFavorite, getPersistedFavorite(returnedFavorite));

        insertedFavorite = returnedFavorite;
    }

    @Test
    @Transactional
    void createFavoriteWithExistingId() throws Exception {
        // Create the Favorite with an existing ID
        favorite.setId(1L);
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFavoriteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(favoriteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFavorites() throws Exception {
        // Initialize the database
        insertedFavorite = favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(favorite.getId().intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFavoritesWithEagerRelationshipsIsEnabled() throws Exception {
        when(favoriteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFavoriteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(favoriteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFavoritesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(favoriteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFavoriteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(favoriteRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFavorite() throws Exception {
        // Initialize the database
        insertedFavorite = favoriteRepository.saveAndFlush(favorite);

        // Get the favorite
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL_ID, favorite.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(favorite.getId().intValue()));
    }

    @Test
    @Transactional
    void getFavoritesByIdFiltering() throws Exception {
        // Initialize the database
        insertedFavorite = favoriteRepository.saveAndFlush(favorite);

        Long id = favorite.getId();

        defaultFavoriteFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultFavoriteFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultFavoriteFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFavoritesByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            favoriteRepository.saveAndFlush(favorite);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        favorite.setUser(user);
        favoriteRepository.saveAndFlush(favorite);
        Long userId = user.getId();
        // Get all the favoriteList where user equals to userId
        defaultFavoriteShouldBeFound("userId.equals=" + userId);

        // Get all the favoriteList where user equals to (userId + 1)
        defaultFavoriteShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllFavoritesByEventIsEqualToSomething() throws Exception {
        Event event;
        if (TestUtil.findAll(em, Event.class).isEmpty()) {
            favoriteRepository.saveAndFlush(favorite);
            event = EventResourceIT.createEntity();
        } else {
            event = TestUtil.findAll(em, Event.class).get(0);
        }
        em.persist(event);
        em.flush();
        favorite.setEvent(event);
        favoriteRepository.saveAndFlush(favorite);
        Long eventId = event.getId();
        // Get all the favoriteList where event equals to eventId
        defaultFavoriteShouldBeFound("eventId.equals=" + eventId);

        // Get all the favoriteList where event equals to (eventId + 1)
        defaultFavoriteShouldNotBeFound("eventId.equals=" + (eventId + 1));
    }

    private void defaultFavoriteFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultFavoriteShouldBeFound(shouldBeFound);
        defaultFavoriteShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFavoriteShouldBeFound(String filter) throws Exception {
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(favorite.getId().intValue())));

        // Check, that the count call also returns 1
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFavoriteShouldNotBeFound(String filter) throws Exception {
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFavorite() throws Exception {
        // Get the favorite
        restFavoriteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFavorite() throws Exception {
        // Initialize the database
        insertedFavorite = favoriteRepository.saveAndFlush(favorite);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the favorite
        Favorite updatedFavorite = favoriteRepository.findById(favorite.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFavorite are not directly saved in db
        em.detach(updatedFavorite);
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(updatedFavorite);

        restFavoriteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, favoriteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(favoriteDTO))
            )
            .andExpect(status().isOk());

        // Validate the Favorite in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFavoriteToMatchAllProperties(updatedFavorite);
    }

    @Test
    @Transactional
    void putNonExistingFavorite() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        favorite.setId(longCount.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, favoriteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFavorite() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        favorite.setId(longCount.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFavorite() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        favorite.setId(longCount.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(favoriteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Favorite in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFavoriteWithPatch() throws Exception {
        // Initialize the database
        insertedFavorite = favoriteRepository.saveAndFlush(favorite);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the favorite using partial update
        Favorite partialUpdatedFavorite = new Favorite();
        partialUpdatedFavorite.setId(favorite.getId());

        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFavorite.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFavorite))
            )
            .andExpect(status().isOk());

        // Validate the Favorite in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFavoriteUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFavorite, favorite), getPersistedFavorite(favorite));
    }

    @Test
    @Transactional
    void fullUpdateFavoriteWithPatch() throws Exception {
        // Initialize the database
        insertedFavorite = favoriteRepository.saveAndFlush(favorite);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the favorite using partial update
        Favorite partialUpdatedFavorite = new Favorite();
        partialUpdatedFavorite.setId(favorite.getId());

        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFavorite.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFavorite))
            )
            .andExpect(status().isOk());

        // Validate the Favorite in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFavoriteUpdatableFieldsEquals(partialUpdatedFavorite, getPersistedFavorite(partialUpdatedFavorite));
    }

    @Test
    @Transactional
    void patchNonExistingFavorite() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        favorite.setId(longCount.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, favoriteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFavorite() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        favorite.setId(longCount.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFavorite() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        favorite.setId(longCount.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(favoriteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Favorite in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFavorite() throws Exception {
        // Initialize the database
        insertedFavorite = favoriteRepository.saveAndFlush(favorite);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the favorite
        restFavoriteMockMvc
            .perform(delete(ENTITY_API_URL_ID, favorite.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return favoriteRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Favorite getPersistedFavorite(Favorite favorite) {
        return favoriteRepository.findById(favorite.getId()).orElseThrow();
    }

    protected void assertPersistedFavoriteToMatchAllProperties(Favorite expectedFavorite) {
        assertFavoriteAllPropertiesEquals(expectedFavorite, getPersistedFavorite(expectedFavorite));
    }

    protected void assertPersistedFavoriteToMatchUpdatableProperties(Favorite expectedFavorite) {
        assertFavoriteAllUpdatablePropertiesEquals(expectedFavorite, getPersistedFavorite(expectedFavorite));
    }
}
