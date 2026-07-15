package com.dugx.event.web.rest;

import static com.dugx.event.domain.EventImageAsserts.*;
import static com.dugx.event.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dugx.event.IntegrationTest;
import com.dugx.event.domain.Event;
import com.dugx.event.domain.EventImage;
import com.dugx.event.repository.EventImageRepository;
import com.dugx.event.service.EventImageService;
import com.dugx.event.service.dto.EventImageDTO;
import com.dugx.event.service.mapper.EventImageMapper;
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
 * Integration tests for the {@link EventImageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EventImageResourceIT {

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/event-images";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EventImageRepository eventImageRepository;

    @Mock
    private EventImageRepository eventImageRepositoryMock;

    @Autowired
    private EventImageMapper eventImageMapper;

    @Mock
    private EventImageService eventImageServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventImageMockMvc;

    private EventImage eventImage;

    private EventImage insertedEventImage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventImage createEntity() {
        return new EventImage().imageUrl(DEFAULT_IMAGE_URL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EventImage createUpdatedEntity() {
        return new EventImage().imageUrl(UPDATED_IMAGE_URL);
    }

    @BeforeEach
    void initTest() {
        eventImage = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEventImage != null) {
            eventImageRepository.delete(insertedEventImage);
            insertedEventImage = null;
        }
    }

    @Test
    @Transactional
    void createEventImage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EventImage
        EventImageDTO eventImageDTO = eventImageMapper.toDto(eventImage);
        var returnedEventImageDTO = om.readValue(
            restEventImageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventImageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EventImageDTO.class
        );

        // Validate the EventImage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEventImage = eventImageMapper.toEntity(returnedEventImageDTO);
        assertEventImageUpdatableFieldsEquals(returnedEventImage, getPersistedEventImage(returnedEventImage));

        insertedEventImage = returnedEventImage;
    }

    @Test
    @Transactional
    void createEventImageWithExistingId() throws Exception {
        // Create the EventImage with an existing ID
        eventImage.setId(1L);
        EventImageDTO eventImageDTO = eventImageMapper.toDto(eventImage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventImageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EventImage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkImageUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        eventImage.setImageUrl(null);

        // Create the EventImage, which fails.
        EventImageDTO eventImageDTO = eventImageMapper.toDto(eventImage);

        restEventImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventImageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEventImages() throws Exception {
        // Initialize the database
        insertedEventImage = eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList
        restEventImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventImage.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEventImagesWithEagerRelationshipsIsEnabled() throws Exception {
        when(eventImageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEventImageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(eventImageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEventImagesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(eventImageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEventImageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(eventImageRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEventImage() throws Exception {
        // Initialize the database
        insertedEventImage = eventImageRepository.saveAndFlush(eventImage);

        // Get the eventImage
        restEventImageMockMvc
            .perform(get(ENTITY_API_URL_ID, eventImage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(eventImage.getId().intValue()))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL));
    }

    @Test
    @Transactional
    void getEventImagesByIdFiltering() throws Exception {
        // Initialize the database
        insertedEventImage = eventImageRepository.saveAndFlush(eventImage);

        Long id = eventImage.getId();

        defaultEventImageFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEventImageFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEventImageFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEventImagesByImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEventImage = eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where imageUrl equals to
        defaultEventImageFiltering("imageUrl.equals=" + DEFAULT_IMAGE_URL, "imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllEventImagesByImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEventImage = eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where imageUrl in
        defaultEventImageFiltering("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL, "imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllEventImagesByImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEventImage = eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where imageUrl is not null
        defaultEventImageFiltering("imageUrl.specified=true", "imageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllEventImagesByImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedEventImage = eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where imageUrl contains
        defaultEventImageFiltering("imageUrl.contains=" + DEFAULT_IMAGE_URL, "imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllEventImagesByImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEventImage = eventImageRepository.saveAndFlush(eventImage);

        // Get all the eventImageList where imageUrl does not contain
        defaultEventImageFiltering("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL, "imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllEventImagesByEventIsEqualToSomething() throws Exception {
        Event event;
        if (TestUtil.findAll(em, Event.class).isEmpty()) {
            eventImageRepository.saveAndFlush(eventImage);
            event = EventResourceIT.createEntity();
        } else {
            event = TestUtil.findAll(em, Event.class).get(0);
        }
        em.persist(event);
        em.flush();
        eventImage.setEvent(event);
        eventImageRepository.saveAndFlush(eventImage);
        Long eventId = event.getId();
        // Get all the eventImageList where event equals to eventId
        defaultEventImageShouldBeFound("eventId.equals=" + eventId);

        // Get all the eventImageList where event equals to (eventId + 1)
        defaultEventImageShouldNotBeFound("eventId.equals=" + (eventId + 1));
    }

    private void defaultEventImageFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultEventImageShouldBeFound(shouldBeFound);
        defaultEventImageShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEventImageShouldBeFound(String filter) throws Exception {
        restEventImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventImage.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)));

        // Check, that the count call also returns 1
        restEventImageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEventImageShouldNotBeFound(String filter) throws Exception {
        restEventImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEventImageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEventImage() throws Exception {
        // Get the eventImage
        restEventImageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEventImage() throws Exception {
        // Initialize the database
        insertedEventImage = eventImageRepository.saveAndFlush(eventImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventImage
        EventImage updatedEventImage = eventImageRepository.findById(eventImage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEventImage are not directly saved in db
        em.detach(updatedEventImage);
        updatedEventImage.imageUrl(UPDATED_IMAGE_URL);
        EventImageDTO eventImageDTO = eventImageMapper.toDto(updatedEventImage);

        restEventImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventImageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventImageDTO))
            )
            .andExpect(status().isOk());

        // Validate the EventImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEventImageToMatchAllProperties(updatedEventImage);
    }

    @Test
    @Transactional
    void putNonExistingEventImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventImage.setId(longCount.incrementAndGet());

        // Create the EventImage
        EventImageDTO eventImageDTO = eventImageMapper.toDto(eventImage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventImageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEventImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventImage.setId(longCount.incrementAndGet());

        // Create the EventImage
        EventImageDTO eventImageDTO = eventImageMapper.toDto(eventImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEventImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventImage.setId(longCount.incrementAndGet());

        // Create the EventImage
        EventImageDTO eventImageDTO = eventImageMapper.toDto(eventImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventImageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventImageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEventImageWithPatch() throws Exception {
        // Initialize the database
        insertedEventImage = eventImageRepository.saveAndFlush(eventImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventImage using partial update
        EventImage partialUpdatedEventImage = new EventImage();
        partialUpdatedEventImage.setId(eventImage.getId());

        restEventImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEventImage))
            )
            .andExpect(status().isOk());

        // Validate the EventImage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventImageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEventImage, eventImage),
            getPersistedEventImage(eventImage)
        );
    }

    @Test
    @Transactional
    void fullUpdateEventImageWithPatch() throws Exception {
        // Initialize the database
        insertedEventImage = eventImageRepository.saveAndFlush(eventImage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventImage using partial update
        EventImage partialUpdatedEventImage = new EventImage();
        partialUpdatedEventImage.setId(eventImage.getId());

        partialUpdatedEventImage.imageUrl(UPDATED_IMAGE_URL);

        restEventImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEventImage))
            )
            .andExpect(status().isOk());

        // Validate the EventImage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventImageUpdatableFieldsEquals(partialUpdatedEventImage, getPersistedEventImage(partialUpdatedEventImage));
    }

    @Test
    @Transactional
    void patchNonExistingEventImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventImage.setId(longCount.incrementAndGet());

        // Create the EventImage
        EventImageDTO eventImageDTO = eventImageMapper.toDto(eventImage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eventImageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEventImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventImage.setId(longCount.incrementAndGet());

        // Create the EventImage
        EventImageDTO eventImageDTO = eventImageMapper.toDto(eventImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventImageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EventImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEventImage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventImage.setId(longCount.incrementAndGet());

        // Create the EventImage
        EventImageDTO eventImageDTO = eventImageMapper.toDto(eventImage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventImageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(eventImageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EventImage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEventImage() throws Exception {
        // Initialize the database
        insertedEventImage = eventImageRepository.saveAndFlush(eventImage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the eventImage
        restEventImageMockMvc
            .perform(delete(ENTITY_API_URL_ID, eventImage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return eventImageRepository.count();
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

    protected EventImage getPersistedEventImage(EventImage eventImage) {
        return eventImageRepository.findById(eventImage.getId()).orElseThrow();
    }

    protected void assertPersistedEventImageToMatchAllProperties(EventImage expectedEventImage) {
        assertEventImageAllPropertiesEquals(expectedEventImage, getPersistedEventImage(expectedEventImage));
    }

    protected void assertPersistedEventImageToMatchUpdatableProperties(EventImage expectedEventImage) {
        assertEventImageAllUpdatablePropertiesEquals(expectedEventImage, getPersistedEventImage(expectedEventImage));
    }
}
