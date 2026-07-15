package com.dugx.event.web.rest;

import static com.dugx.event.domain.CheckInAsserts.*;
import static com.dugx.event.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dugx.event.IntegrationTest;
import com.dugx.event.domain.CheckIn;
import com.dugx.event.domain.Ticket;
import com.dugx.event.domain.User;
import com.dugx.event.repository.CheckInRepository;
import com.dugx.event.repository.UserRepository;
import com.dugx.event.service.CheckInService;
import com.dugx.event.service.dto.CheckInDTO;
import com.dugx.event.service.mapper.CheckInMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
 * Integration tests for the {@link CheckInResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CheckInResourceIT {

    private static final Instant DEFAULT_CHECK_IN_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CHECK_IN_TIME = Instant.ofEpochMilli(1784084641017L);

    private static final String ENTITY_API_URL = "/api/check-ins";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CheckInRepository checkInRepositoryMock;

    @Autowired
    private CheckInMapper checkInMapper;

    @Mock
    private CheckInService checkInServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCheckInMockMvc;

    private CheckIn checkIn;

    private CheckIn insertedCheckIn;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CheckIn createEntity() {
        return new CheckIn().checkInTime(DEFAULT_CHECK_IN_TIME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CheckIn createUpdatedEntity() {
        return new CheckIn().checkInTime(UPDATED_CHECK_IN_TIME);
    }

    @BeforeEach
    void initTest() {
        checkIn = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCheckIn != null) {
            checkInRepository.delete(insertedCheckIn);
            insertedCheckIn = null;
        }
    }

    @Test
    @Transactional
    void createCheckIn() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CheckIn
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);
        var returnedCheckInDTO = om.readValue(
            restCheckInMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkInDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CheckInDTO.class
        );

        // Validate the CheckIn in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCheckIn = checkInMapper.toEntity(returnedCheckInDTO);
        assertCheckInUpdatableFieldsEquals(returnedCheckIn, getPersistedCheckIn(returnedCheckIn));

        insertedCheckIn = returnedCheckIn;
    }

    @Test
    @Transactional
    void createCheckInWithExistingId() throws Exception {
        // Create the CheckIn with an existing ID
        checkIn.setId(1L);
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCheckInMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkInDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CheckIn in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCheckIns() throws Exception {
        // Initialize the database
        insertedCheckIn = checkInRepository.saveAndFlush(checkIn);

        // Get all the checkInList
        restCheckInMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(checkIn.getId().intValue())))
            .andExpect(jsonPath("$.[*].checkInTime").value(hasItem(DEFAULT_CHECK_IN_TIME.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCheckInsWithEagerRelationshipsIsEnabled() throws Exception {
        when(checkInServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCheckInMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(checkInServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCheckInsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(checkInServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCheckInMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(checkInRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCheckIn() throws Exception {
        // Initialize the database
        insertedCheckIn = checkInRepository.saveAndFlush(checkIn);

        // Get the checkIn
        restCheckInMockMvc
            .perform(get(ENTITY_API_URL_ID, checkIn.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(checkIn.getId().intValue()))
            .andExpect(jsonPath("$.checkInTime").value(DEFAULT_CHECK_IN_TIME.toString()));
    }

    @Test
    @Transactional
    void getCheckInsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCheckIn = checkInRepository.saveAndFlush(checkIn);

        Long id = checkIn.getId();

        defaultCheckInFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCheckInFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCheckInFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCheckInsByCheckInTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCheckIn = checkInRepository.saveAndFlush(checkIn);

        // Get all the checkInList where checkInTime equals to
        defaultCheckInFiltering("checkInTime.equals=" + DEFAULT_CHECK_IN_TIME, "checkInTime.equals=" + UPDATED_CHECK_IN_TIME);
    }

    @Test
    @Transactional
    void getAllCheckInsByCheckInTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCheckIn = checkInRepository.saveAndFlush(checkIn);

        // Get all the checkInList where checkInTime in
        defaultCheckInFiltering(
            "checkInTime.in=" + DEFAULT_CHECK_IN_TIME + "," + UPDATED_CHECK_IN_TIME,
            "checkInTime.in=" + UPDATED_CHECK_IN_TIME
        );
    }

    @Test
    @Transactional
    void getAllCheckInsByCheckInTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCheckIn = checkInRepository.saveAndFlush(checkIn);

        // Get all the checkInList where checkInTime is not null
        defaultCheckInFiltering("checkInTime.specified=true", "checkInTime.specified=false");
    }

    @Test
    @Transactional
    void getAllCheckInsByTicketIsEqualToSomething() throws Exception {
        Ticket ticket;
        if (TestUtil.findAll(em, Ticket.class).isEmpty()) {
            checkInRepository.saveAndFlush(checkIn);
            ticket = TicketResourceIT.createEntity();
        } else {
            ticket = TestUtil.findAll(em, Ticket.class).get(0);
        }
        em.persist(ticket);
        em.flush();
        checkIn.setTicket(ticket);
        checkInRepository.saveAndFlush(checkIn);
        Long ticketId = ticket.getId();
        // Get all the checkInList where ticket equals to ticketId
        defaultCheckInShouldBeFound("ticketId.equals=" + ticketId);

        // Get all the checkInList where ticket equals to (ticketId + 1)
        defaultCheckInShouldNotBeFound("ticketId.equals=" + (ticketId + 1));
    }

    @Test
    @Transactional
    void getAllCheckInsByCheckedByIsEqualToSomething() throws Exception {
        User checkedBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            checkInRepository.saveAndFlush(checkIn);
            checkedBy = UserResourceIT.createEntity();
        } else {
            checkedBy = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(checkedBy);
        em.flush();
        checkIn.setCheckedBy(checkedBy);
        checkInRepository.saveAndFlush(checkIn);
        Long checkedById = checkedBy.getId();
        // Get all the checkInList where checkedBy equals to checkedById
        defaultCheckInShouldBeFound("checkedById.equals=" + checkedById);

        // Get all the checkInList where checkedBy equals to (checkedById + 1)
        defaultCheckInShouldNotBeFound("checkedById.equals=" + (checkedById + 1));
    }

    private void defaultCheckInFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCheckInShouldBeFound(shouldBeFound);
        defaultCheckInShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCheckInShouldBeFound(String filter) throws Exception {
        restCheckInMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(checkIn.getId().intValue())))
            .andExpect(jsonPath("$.[*].checkInTime").value(hasItem(DEFAULT_CHECK_IN_TIME.toString())));

        // Check, that the count call also returns 1
        restCheckInMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCheckInShouldNotBeFound(String filter) throws Exception {
        restCheckInMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCheckInMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCheckIn() throws Exception {
        // Get the checkIn
        restCheckInMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCheckIn() throws Exception {
        // Initialize the database
        insertedCheckIn = checkInRepository.saveAndFlush(checkIn);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the checkIn
        CheckIn updatedCheckIn = checkInRepository.findById(checkIn.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCheckIn are not directly saved in db
        em.detach(updatedCheckIn);
        updatedCheckIn.checkInTime(UPDATED_CHECK_IN_TIME);
        CheckInDTO checkInDTO = checkInMapper.toDto(updatedCheckIn);

        restCheckInMockMvc
            .perform(
                put(ENTITY_API_URL_ID, checkInDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkInDTO))
            )
            .andExpect(status().isOk());

        // Validate the CheckIn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCheckInToMatchAllProperties(updatedCheckIn);
    }

    @Test
    @Transactional
    void putNonExistingCheckIn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkIn.setId(longCount.incrementAndGet());

        // Create the CheckIn
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCheckInMockMvc
            .perform(
                put(ENTITY_API_URL_ID, checkInDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkInDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CheckIn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCheckIn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkIn.setId(longCount.incrementAndGet());

        // Create the CheckIn
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckInMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(checkInDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CheckIn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCheckIn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkIn.setId(longCount.incrementAndGet());

        // Create the CheckIn
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckInMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkInDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CheckIn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCheckInWithPatch() throws Exception {
        // Initialize the database
        insertedCheckIn = checkInRepository.saveAndFlush(checkIn);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the checkIn using partial update
        CheckIn partialUpdatedCheckIn = new CheckIn();
        partialUpdatedCheckIn.setId(checkIn.getId());

        partialUpdatedCheckIn.checkInTime(UPDATED_CHECK_IN_TIME);

        restCheckInMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCheckIn.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCheckIn))
            )
            .andExpect(status().isOk());

        // Validate the CheckIn in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCheckInUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCheckIn, checkIn), getPersistedCheckIn(checkIn));
    }

    @Test
    @Transactional
    void fullUpdateCheckInWithPatch() throws Exception {
        // Initialize the database
        insertedCheckIn = checkInRepository.saveAndFlush(checkIn);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the checkIn using partial update
        CheckIn partialUpdatedCheckIn = new CheckIn();
        partialUpdatedCheckIn.setId(checkIn.getId());

        partialUpdatedCheckIn.checkInTime(UPDATED_CHECK_IN_TIME);

        restCheckInMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCheckIn.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCheckIn))
            )
            .andExpect(status().isOk());

        // Validate the CheckIn in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCheckInUpdatableFieldsEquals(partialUpdatedCheckIn, getPersistedCheckIn(partialUpdatedCheckIn));
    }

    @Test
    @Transactional
    void patchNonExistingCheckIn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkIn.setId(longCount.incrementAndGet());

        // Create the CheckIn
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCheckInMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, checkInDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(checkInDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CheckIn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCheckIn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkIn.setId(longCount.incrementAndGet());

        // Create the CheckIn
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckInMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(checkInDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CheckIn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCheckIn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkIn.setId(longCount.incrementAndGet());

        // Create the CheckIn
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckInMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(checkInDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CheckIn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCheckIn() throws Exception {
        // Initialize the database
        insertedCheckIn = checkInRepository.saveAndFlush(checkIn);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the checkIn
        restCheckInMockMvc
            .perform(delete(ENTITY_API_URL_ID, checkIn.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return checkInRepository.count();
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

    protected CheckIn getPersistedCheckIn(CheckIn checkIn) {
        return checkInRepository.findById(checkIn.getId()).orElseThrow();
    }

    protected void assertPersistedCheckInToMatchAllProperties(CheckIn expectedCheckIn) {
        assertCheckInAllPropertiesEquals(expectedCheckIn, getPersistedCheckIn(expectedCheckIn));
    }

    protected void assertPersistedCheckInToMatchUpdatableProperties(CheckIn expectedCheckIn) {
        assertCheckInAllUpdatablePropertiesEquals(expectedCheckIn, getPersistedCheckIn(expectedCheckIn));
    }
}
