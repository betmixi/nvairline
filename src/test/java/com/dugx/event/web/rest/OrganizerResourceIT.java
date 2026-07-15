package com.dugx.event.web.rest;

import static com.dugx.event.domain.OrganizerAsserts.*;
import static com.dugx.event.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dugx.event.IntegrationTest;
import com.dugx.event.domain.Organizer;
import com.dugx.event.domain.User;
import com.dugx.event.repository.OrganizerRepository;
import com.dugx.event.repository.UserRepository;
import com.dugx.event.service.OrganizerService;
import com.dugx.event.service.dto.OrganizerDTO;
import com.dugx.event.service.mapper.OrganizerMapper;
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
 * Integration tests for the {@link OrganizerResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OrganizerResourceIT {

    private static final String DEFAULT_COMPANY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TAX_CODE = "AAAAAAAAAA";
    private static final String UPDATED_TAX_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_VERIFIED = false;
    private static final Boolean UPDATED_VERIFIED = true;

    private static final String ENTITY_API_URL = "/api/organizers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private OrganizerRepository organizerRepositoryMock;

    @Autowired
    private OrganizerMapper organizerMapper;

    @Mock
    private OrganizerService organizerServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrganizerMockMvc;

    private Organizer organizer;

    private Organizer insertedOrganizer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organizer createEntity() {
        return new Organizer()
            .companyName(DEFAULT_COMPANY_NAME)
            .taxCode(DEFAULT_TAX_CODE)
            .description(DEFAULT_DESCRIPTION)
            .verified(DEFAULT_VERIFIED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organizer createUpdatedEntity() {
        return new Organizer()
            .companyName(UPDATED_COMPANY_NAME)
            .taxCode(UPDATED_TAX_CODE)
            .description(UPDATED_DESCRIPTION)
            .verified(UPDATED_VERIFIED);
    }

    @BeforeEach
    void initTest() {
        organizer = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOrganizer != null) {
            organizerRepository.delete(insertedOrganizer);
            insertedOrganizer = null;
        }
    }

    @Test
    @Transactional
    void createOrganizer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Organizer
        OrganizerDTO organizerDTO = organizerMapper.toDto(organizer);
        var returnedOrganizerDTO = om.readValue(
            restOrganizerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(organizerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OrganizerDTO.class
        );

        // Validate the Organizer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOrganizer = organizerMapper.toEntity(returnedOrganizerDTO);
        assertOrganizerUpdatableFieldsEquals(returnedOrganizer, getPersistedOrganizer(returnedOrganizer));

        insertedOrganizer = returnedOrganizer;
    }

    @Test
    @Transactional
    void createOrganizerWithExistingId() throws Exception {
        // Create the Organizer with an existing ID
        organizer.setId(1L);
        OrganizerDTO organizerDTO = organizerMapper.toDto(organizer);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrganizerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(organizerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Organizer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCompanyNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        organizer.setCompanyName(null);

        // Create the Organizer, which fails.
        OrganizerDTO organizerDTO = organizerMapper.toDto(organizer);

        restOrganizerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(organizerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTaxCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        organizer.setTaxCode(null);

        // Create the Organizer, which fails.
        OrganizerDTO organizerDTO = organizerMapper.toDto(organizer);

        restOrganizerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(organizerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrganizers() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        // Get all the organizerList
        restOrganizerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organizer.getId().intValue())))
            .andExpect(jsonPath("$.[*].companyName").value(hasItem(DEFAULT_COMPANY_NAME)))
            .andExpect(jsonPath("$.[*].taxCode").value(hasItem(DEFAULT_TAX_CODE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].verified").value(hasItem(DEFAULT_VERIFIED)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrganizersWithEagerRelationshipsIsEnabled() throws Exception {
        when(organizerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrganizerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(organizerServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrganizersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(organizerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrganizerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(organizerRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getOrganizer() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        // Get the organizer
        restOrganizerMockMvc
            .perform(get(ENTITY_API_URL_ID, organizer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(organizer.getId().intValue()))
            .andExpect(jsonPath("$.companyName").value(DEFAULT_COMPANY_NAME))
            .andExpect(jsonPath("$.taxCode").value(DEFAULT_TAX_CODE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.verified").value(DEFAULT_VERIFIED));
    }

    @Test
    @Transactional
    void getOrganizersByIdFiltering() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        Long id = organizer.getId();

        defaultOrganizerFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultOrganizerFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultOrganizerFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrganizersByCompanyNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        // Get all the organizerList where companyName equals to
        defaultOrganizerFiltering("companyName.equals=" + DEFAULT_COMPANY_NAME, "companyName.equals=" + UPDATED_COMPANY_NAME);
    }

    @Test
    @Transactional
    void getAllOrganizersByCompanyNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        // Get all the organizerList where companyName in
        defaultOrganizerFiltering(
            "companyName.in=" + DEFAULT_COMPANY_NAME + "," + UPDATED_COMPANY_NAME,
            "companyName.in=" + UPDATED_COMPANY_NAME
        );
    }

    @Test
    @Transactional
    void getAllOrganizersByCompanyNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        // Get all the organizerList where companyName is not null
        defaultOrganizerFiltering("companyName.specified=true", "companyName.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganizersByCompanyNameContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        // Get all the organizerList where companyName contains
        defaultOrganizerFiltering("companyName.contains=" + DEFAULT_COMPANY_NAME, "companyName.contains=" + UPDATED_COMPANY_NAME);
    }

    @Test
    @Transactional
    void getAllOrganizersByCompanyNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        // Get all the organizerList where companyName does not contain
        defaultOrganizerFiltering(
            "companyName.doesNotContain=" + UPDATED_COMPANY_NAME,
            "companyName.doesNotContain=" + DEFAULT_COMPANY_NAME
        );
    }

    @Test
    @Transactional
    void getAllOrganizersByTaxCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        // Get all the organizerList where taxCode equals to
        defaultOrganizerFiltering("taxCode.equals=" + DEFAULT_TAX_CODE, "taxCode.equals=" + UPDATED_TAX_CODE);
    }

    @Test
    @Transactional
    void getAllOrganizersByTaxCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        // Get all the organizerList where taxCode in
        defaultOrganizerFiltering("taxCode.in=" + DEFAULT_TAX_CODE + "," + UPDATED_TAX_CODE, "taxCode.in=" + UPDATED_TAX_CODE);
    }

    @Test
    @Transactional
    void getAllOrganizersByTaxCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        // Get all the organizerList where taxCode is not null
        defaultOrganizerFiltering("taxCode.specified=true", "taxCode.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganizersByTaxCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        // Get all the organizerList where taxCode contains
        defaultOrganizerFiltering("taxCode.contains=" + DEFAULT_TAX_CODE, "taxCode.contains=" + UPDATED_TAX_CODE);
    }

    @Test
    @Transactional
    void getAllOrganizersByTaxCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        // Get all the organizerList where taxCode does not contain
        defaultOrganizerFiltering("taxCode.doesNotContain=" + UPDATED_TAX_CODE, "taxCode.doesNotContain=" + DEFAULT_TAX_CODE);
    }

    @Test
    @Transactional
    void getAllOrganizersByVerifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        // Get all the organizerList where verified equals to
        defaultOrganizerFiltering("verified.equals=" + DEFAULT_VERIFIED, "verified.equals=" + UPDATED_VERIFIED);
    }

    @Test
    @Transactional
    void getAllOrganizersByVerifiedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        // Get all the organizerList where verified in
        defaultOrganizerFiltering("verified.in=" + DEFAULT_VERIFIED + "," + UPDATED_VERIFIED, "verified.in=" + UPDATED_VERIFIED);
    }

    @Test
    @Transactional
    void getAllOrganizersByVerifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        // Get all the organizerList where verified is not null
        defaultOrganizerFiltering("verified.specified=true", "verified.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganizersByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            organizerRepository.saveAndFlush(organizer);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        organizer.setUser(user);
        organizerRepository.saveAndFlush(organizer);
        Long userId = user.getId();
        // Get all the organizerList where user equals to userId
        defaultOrganizerShouldBeFound("userId.equals=" + userId);

        // Get all the organizerList where user equals to (userId + 1)
        defaultOrganizerShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultOrganizerFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultOrganizerShouldBeFound(shouldBeFound);
        defaultOrganizerShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrganizerShouldBeFound(String filter) throws Exception {
        restOrganizerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organizer.getId().intValue())))
            .andExpect(jsonPath("$.[*].companyName").value(hasItem(DEFAULT_COMPANY_NAME)))
            .andExpect(jsonPath("$.[*].taxCode").value(hasItem(DEFAULT_TAX_CODE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].verified").value(hasItem(DEFAULT_VERIFIED)));

        // Check, that the count call also returns 1
        restOrganizerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrganizerShouldNotBeFound(String filter) throws Exception {
        restOrganizerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrganizerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrganizer() throws Exception {
        // Get the organizer
        restOrganizerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrganizer() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the organizer
        Organizer updatedOrganizer = organizerRepository.findById(organizer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOrganizer are not directly saved in db
        em.detach(updatedOrganizer);
        updatedOrganizer
            .companyName(UPDATED_COMPANY_NAME)
            .taxCode(UPDATED_TAX_CODE)
            .description(UPDATED_DESCRIPTION)
            .verified(UPDATED_VERIFIED);
        OrganizerDTO organizerDTO = organizerMapper.toDto(updatedOrganizer);

        restOrganizerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, organizerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(organizerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Organizer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrganizerToMatchAllProperties(updatedOrganizer);
    }

    @Test
    @Transactional
    void putNonExistingOrganizer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organizer.setId(longCount.incrementAndGet());

        // Create the Organizer
        OrganizerDTO organizerDTO = organizerMapper.toDto(organizer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrganizerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, organizerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(organizerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organizer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrganizer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organizer.setId(longCount.incrementAndGet());

        // Create the Organizer
        OrganizerDTO organizerDTO = organizerMapper.toDto(organizer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganizerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(organizerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organizer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrganizer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organizer.setId(longCount.incrementAndGet());

        // Create the Organizer
        OrganizerDTO organizerDTO = organizerMapper.toDto(organizer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganizerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(organizerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Organizer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrganizerWithPatch() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the organizer using partial update
        Organizer partialUpdatedOrganizer = new Organizer();
        partialUpdatedOrganizer.setId(organizer.getId());

        partialUpdatedOrganizer.description(UPDATED_DESCRIPTION);

        restOrganizerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrganizer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrganizer))
            )
            .andExpect(status().isOk());

        // Validate the Organizer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrganizerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrganizer, organizer),
            getPersistedOrganizer(organizer)
        );
    }

    @Test
    @Transactional
    void fullUpdateOrganizerWithPatch() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the organizer using partial update
        Organizer partialUpdatedOrganizer = new Organizer();
        partialUpdatedOrganizer.setId(organizer.getId());

        partialUpdatedOrganizer
            .companyName(UPDATED_COMPANY_NAME)
            .taxCode(UPDATED_TAX_CODE)
            .description(UPDATED_DESCRIPTION)
            .verified(UPDATED_VERIFIED);

        restOrganizerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrganizer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrganizer))
            )
            .andExpect(status().isOk());

        // Validate the Organizer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrganizerUpdatableFieldsEquals(partialUpdatedOrganizer, getPersistedOrganizer(partialUpdatedOrganizer));
    }

    @Test
    @Transactional
    void patchNonExistingOrganizer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organizer.setId(longCount.incrementAndGet());

        // Create the Organizer
        OrganizerDTO organizerDTO = organizerMapper.toDto(organizer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrganizerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, organizerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(organizerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organizer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrganizer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organizer.setId(longCount.incrementAndGet());

        // Create the Organizer
        OrganizerDTO organizerDTO = organizerMapper.toDto(organizer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganizerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(organizerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organizer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrganizer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organizer.setId(longCount.incrementAndGet());

        // Create the Organizer
        OrganizerDTO organizerDTO = organizerMapper.toDto(organizer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganizerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(organizerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Organizer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrganizer() throws Exception {
        // Initialize the database
        insertedOrganizer = organizerRepository.saveAndFlush(organizer);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the organizer
        restOrganizerMockMvc
            .perform(delete(ENTITY_API_URL_ID, organizer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return organizerRepository.count();
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

    protected Organizer getPersistedOrganizer(Organizer organizer) {
        return organizerRepository.findById(organizer.getId()).orElseThrow();
    }

    protected void assertPersistedOrganizerToMatchAllProperties(Organizer expectedOrganizer) {
        assertOrganizerAllPropertiesEquals(expectedOrganizer, getPersistedOrganizer(expectedOrganizer));
    }

    protected void assertPersistedOrganizerToMatchUpdatableProperties(Organizer expectedOrganizer) {
        assertOrganizerAllUpdatablePropertiesEquals(expectedOrganizer, getPersistedOrganizer(expectedOrganizer));
    }
}
