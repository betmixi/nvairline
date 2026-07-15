package com.dugx.event.web.rest;

import static com.dugx.event.domain.TicketTypeAsserts.*;
import static com.dugx.event.web.rest.TestUtil.createUpdateProxyForBean;
import static com.dugx.event.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dugx.event.IntegrationTest;
import com.dugx.event.domain.Event;
import com.dugx.event.domain.TicketType;
import com.dugx.event.repository.TicketTypeRepository;
import com.dugx.event.service.TicketTypeService;
import com.dugx.event.service.dto.TicketTypeDTO;
import com.dugx.event.service.mapper.TicketTypeMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link TicketTypeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TicketTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(1 - 1);

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final Integer DEFAULT_REMAINING = 1;
    private static final Integer UPDATED_REMAINING = 2;
    private static final Integer SMALLER_REMAINING = 1 - 1;

    private static final Instant DEFAULT_SALE_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SALE_START = Instant.ofEpochMilli(1784084641017L);

    private static final Instant DEFAULT_SALE_END = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SALE_END = Instant.ofEpochMilli(1784084641017L);

    private static final String ENTITY_API_URL = "/api/ticket-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Mock
    private TicketTypeRepository ticketTypeRepositoryMock;

    @Autowired
    private TicketTypeMapper ticketTypeMapper;

    @Mock
    private TicketTypeService ticketTypeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTicketTypeMockMvc;

    private TicketType ticketType;

    private TicketType insertedTicketType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketType createEntity() {
        return new TicketType()
            .name(DEFAULT_NAME)
            .price(DEFAULT_PRICE)
            .quantity(DEFAULT_QUANTITY)
            .remaining(DEFAULT_REMAINING)
            .saleStart(DEFAULT_SALE_START)
            .saleEnd(DEFAULT_SALE_END);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketType createUpdatedEntity() {
        return new TicketType()
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .quantity(UPDATED_QUANTITY)
            .remaining(UPDATED_REMAINING)
            .saleStart(UPDATED_SALE_START)
            .saleEnd(UPDATED_SALE_END);
    }

    @BeforeEach
    void initTest() {
        ticketType = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTicketType != null) {
            ticketTypeRepository.delete(insertedTicketType);
            insertedTicketType = null;
        }
    }

    @Test
    @Transactional
    void createTicketType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TicketType
        TicketTypeDTO ticketTypeDTO = ticketTypeMapper.toDto(ticketType);
        var returnedTicketTypeDTO = om.readValue(
            restTicketTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketTypeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TicketTypeDTO.class
        );

        // Validate the TicketType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTicketType = ticketTypeMapper.toEntity(returnedTicketTypeDTO);
        assertTicketTypeUpdatableFieldsEquals(returnedTicketType, getPersistedTicketType(returnedTicketType));

        insertedTicketType = returnedTicketType;
    }

    @Test
    @Transactional
    void createTicketTypeWithExistingId() throws Exception {
        // Create the TicketType with an existing ID
        ticketType.setId(1L);
        TicketTypeDTO ticketTypeDTO = ticketTypeMapper.toDto(ticketType);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TicketType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ticketType.setName(null);

        // Create the TicketType, which fails.
        TicketTypeDTO ticketTypeDTO = ticketTypeMapper.toDto(ticketType);

        restTicketTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ticketType.setPrice(null);

        // Create the TicketType, which fails.
        TicketTypeDTO ticketTypeDTO = ticketTypeMapper.toDto(ticketType);

        restTicketTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ticketType.setQuantity(null);

        // Create the TicketType, which fails.
        TicketTypeDTO ticketTypeDTO = ticketTypeMapper.toDto(ticketType);

        restTicketTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRemainingIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ticketType.setRemaining(null);

        // Create the TicketType, which fails.
        TicketTypeDTO ticketTypeDTO = ticketTypeMapper.toDto(ticketType);

        restTicketTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTicketTypes() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList
        restTicketTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].remaining").value(hasItem(DEFAULT_REMAINING)))
            .andExpect(jsonPath("$.[*].saleStart").value(hasItem(DEFAULT_SALE_START.toString())))
            .andExpect(jsonPath("$.[*].saleEnd").value(hasItem(DEFAULT_SALE_END.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTicketTypesWithEagerRelationshipsIsEnabled() throws Exception {
        when(ticketTypeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTicketTypeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ticketTypeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTicketTypesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ticketTypeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTicketTypeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(ticketTypeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTicketType() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get the ticketType
        restTicketTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, ticketType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ticketType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.remaining").value(DEFAULT_REMAINING))
            .andExpect(jsonPath("$.saleStart").value(DEFAULT_SALE_START.toString()))
            .andExpect(jsonPath("$.saleEnd").value(DEFAULT_SALE_END.toString()));
    }

    @Test
    @Transactional
    void getTicketTypesByIdFiltering() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        Long id = ticketType.getId();

        defaultTicketTypeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTicketTypeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTicketTypeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTicketTypesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where name equals to
        defaultTicketTypeFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTicketTypesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where name in
        defaultTicketTypeFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTicketTypesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where name is not null
        defaultTicketTypeFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketTypesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where name contains
        defaultTicketTypeFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTicketTypesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where name does not contain
        defaultTicketTypeFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllTicketTypesByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where price equals to
        defaultTicketTypeFiltering("price.equals=" + DEFAULT_PRICE, "price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllTicketTypesByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where price in
        defaultTicketTypeFiltering("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE, "price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllTicketTypesByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where price is not null
        defaultTicketTypeFiltering("price.specified=true", "price.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketTypesByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where price is greater than or equal to
        defaultTicketTypeFiltering("price.greaterThanOrEqual=" + DEFAULT_PRICE, "price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllTicketTypesByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where price is less than or equal to
        defaultTicketTypeFiltering("price.lessThanOrEqual=" + DEFAULT_PRICE, "price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllTicketTypesByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where price is less than
        defaultTicketTypeFiltering("price.lessThan=" + UPDATED_PRICE, "price.lessThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllTicketTypesByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where price is greater than
        defaultTicketTypeFiltering("price.greaterThan=" + SMALLER_PRICE, "price.greaterThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllTicketTypesByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where quantity equals to
        defaultTicketTypeFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllTicketTypesByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where quantity in
        defaultTicketTypeFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllTicketTypesByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where quantity is not null
        defaultTicketTypeFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketTypesByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where quantity is greater than or equal to
        defaultTicketTypeFiltering("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY, "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllTicketTypesByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where quantity is less than or equal to
        defaultTicketTypeFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllTicketTypesByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where quantity is less than
        defaultTicketTypeFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllTicketTypesByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where quantity is greater than
        defaultTicketTypeFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllTicketTypesByRemainingIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where remaining equals to
        defaultTicketTypeFiltering("remaining.equals=" + DEFAULT_REMAINING, "remaining.equals=" + UPDATED_REMAINING);
    }

    @Test
    @Transactional
    void getAllTicketTypesByRemainingIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where remaining in
        defaultTicketTypeFiltering("remaining.in=" + DEFAULT_REMAINING + "," + UPDATED_REMAINING, "remaining.in=" + UPDATED_REMAINING);
    }

    @Test
    @Transactional
    void getAllTicketTypesByRemainingIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where remaining is not null
        defaultTicketTypeFiltering("remaining.specified=true", "remaining.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketTypesByRemainingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where remaining is greater than or equal to
        defaultTicketTypeFiltering(
            "remaining.greaterThanOrEqual=" + DEFAULT_REMAINING,
            "remaining.greaterThanOrEqual=" + UPDATED_REMAINING
        );
    }

    @Test
    @Transactional
    void getAllTicketTypesByRemainingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where remaining is less than or equal to
        defaultTicketTypeFiltering("remaining.lessThanOrEqual=" + DEFAULT_REMAINING, "remaining.lessThanOrEqual=" + SMALLER_REMAINING);
    }

    @Test
    @Transactional
    void getAllTicketTypesByRemainingIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where remaining is less than
        defaultTicketTypeFiltering("remaining.lessThan=" + UPDATED_REMAINING, "remaining.lessThan=" + DEFAULT_REMAINING);
    }

    @Test
    @Transactional
    void getAllTicketTypesByRemainingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where remaining is greater than
        defaultTicketTypeFiltering("remaining.greaterThan=" + SMALLER_REMAINING, "remaining.greaterThan=" + DEFAULT_REMAINING);
    }

    @Test
    @Transactional
    void getAllTicketTypesBySaleStartIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where saleStart equals to
        defaultTicketTypeFiltering("saleStart.equals=" + DEFAULT_SALE_START, "saleStart.equals=" + UPDATED_SALE_START);
    }

    @Test
    @Transactional
    void getAllTicketTypesBySaleStartIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where saleStart in
        defaultTicketTypeFiltering("saleStart.in=" + DEFAULT_SALE_START + "," + UPDATED_SALE_START, "saleStart.in=" + UPDATED_SALE_START);
    }

    @Test
    @Transactional
    void getAllTicketTypesBySaleStartIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where saleStart is not null
        defaultTicketTypeFiltering("saleStart.specified=true", "saleStart.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketTypesBySaleEndIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where saleEnd equals to
        defaultTicketTypeFiltering("saleEnd.equals=" + DEFAULT_SALE_END, "saleEnd.equals=" + UPDATED_SALE_END);
    }

    @Test
    @Transactional
    void getAllTicketTypesBySaleEndIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where saleEnd in
        defaultTicketTypeFiltering("saleEnd.in=" + DEFAULT_SALE_END + "," + UPDATED_SALE_END, "saleEnd.in=" + UPDATED_SALE_END);
    }

    @Test
    @Transactional
    void getAllTicketTypesBySaleEndIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        // Get all the ticketTypeList where saleEnd is not null
        defaultTicketTypeFiltering("saleEnd.specified=true", "saleEnd.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketTypesByEventIsEqualToSomething() throws Exception {
        Event event;
        if (TestUtil.findAll(em, Event.class).isEmpty()) {
            ticketTypeRepository.saveAndFlush(ticketType);
            event = EventResourceIT.createEntity();
        } else {
            event = TestUtil.findAll(em, Event.class).get(0);
        }
        em.persist(event);
        em.flush();
        ticketType.setEvent(event);
        ticketTypeRepository.saveAndFlush(ticketType);
        Long eventId = event.getId();
        // Get all the ticketTypeList where event equals to eventId
        defaultTicketTypeShouldBeFound("eventId.equals=" + eventId);

        // Get all the ticketTypeList where event equals to (eventId + 1)
        defaultTicketTypeShouldNotBeFound("eventId.equals=" + (eventId + 1));
    }

    private void defaultTicketTypeFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTicketTypeShouldBeFound(shouldBeFound);
        defaultTicketTypeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTicketTypeShouldBeFound(String filter) throws Exception {
        restTicketTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].remaining").value(hasItem(DEFAULT_REMAINING)))
            .andExpect(jsonPath("$.[*].saleStart").value(hasItem(DEFAULT_SALE_START.toString())))
            .andExpect(jsonPath("$.[*].saleEnd").value(hasItem(DEFAULT_SALE_END.toString())));

        // Check, that the count call also returns 1
        restTicketTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTicketTypeShouldNotBeFound(String filter) throws Exception {
        restTicketTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTicketTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTicketType() throws Exception {
        // Get the ticketType
        restTicketTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTicketType() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketType
        TicketType updatedTicketType = ticketTypeRepository.findById(ticketType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTicketType are not directly saved in db
        em.detach(updatedTicketType);
        updatedTicketType
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .quantity(UPDATED_QUANTITY)
            .remaining(UPDATED_REMAINING)
            .saleStart(UPDATED_SALE_START)
            .saleEnd(UPDATED_SALE_END);
        TicketTypeDTO ticketTypeDTO = ticketTypeMapper.toDto(updatedTicketType);

        restTicketTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the TicketType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTicketTypeToMatchAllProperties(updatedTicketType);
    }

    @Test
    @Transactional
    void putNonExistingTicketType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketType.setId(longCount.incrementAndGet());

        // Create the TicketType
        TicketTypeDTO ticketTypeDTO = ticketTypeMapper.toDto(ticketType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTicketType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketType.setId(longCount.incrementAndGet());

        // Create the TicketType
        TicketTypeDTO ticketTypeDTO = ticketTypeMapper.toDto(ticketType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTicketType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketType.setId(longCount.incrementAndGet());

        // Create the TicketType
        TicketTypeDTO ticketTypeDTO = ticketTypeMapper.toDto(ticketType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTicketTypeWithPatch() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketType using partial update
        TicketType partialUpdatedTicketType = new TicketType();
        partialUpdatedTicketType.setId(ticketType.getId());

        partialUpdatedTicketType.name(UPDATED_NAME).quantity(UPDATED_QUANTITY).remaining(UPDATED_REMAINING).saleEnd(UPDATED_SALE_END);

        restTicketTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicketType))
            )
            .andExpect(status().isOk());

        // Validate the TicketType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTicketType, ticketType),
            getPersistedTicketType(ticketType)
        );
    }

    @Test
    @Transactional
    void fullUpdateTicketTypeWithPatch() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketType using partial update
        TicketType partialUpdatedTicketType = new TicketType();
        partialUpdatedTicketType.setId(ticketType.getId());

        partialUpdatedTicketType
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .quantity(UPDATED_QUANTITY)
            .remaining(UPDATED_REMAINING)
            .saleStart(UPDATED_SALE_START)
            .saleEnd(UPDATED_SALE_END);

        restTicketTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicketType))
            )
            .andExpect(status().isOk());

        // Validate the TicketType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketTypeUpdatableFieldsEquals(partialUpdatedTicketType, getPersistedTicketType(partialUpdatedTicketType));
    }

    @Test
    @Transactional
    void patchNonExistingTicketType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketType.setId(longCount.incrementAndGet());

        // Create the TicketType
        TicketTypeDTO ticketTypeDTO = ticketTypeMapper.toDto(ticketType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ticketTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTicketType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketType.setId(longCount.incrementAndGet());

        // Create the TicketType
        TicketTypeDTO ticketTypeDTO = ticketTypeMapper.toDto(ticketType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTicketType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketType.setId(longCount.incrementAndGet());

        // Create the TicketType
        TicketTypeDTO ticketTypeDTO = ticketTypeMapper.toDto(ticketType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ticketTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTicketType() throws Exception {
        // Initialize the database
        insertedTicketType = ticketTypeRepository.saveAndFlush(ticketType);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ticketType
        restTicketTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, ticketType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ticketTypeRepository.count();
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

    protected TicketType getPersistedTicketType(TicketType ticketType) {
        return ticketTypeRepository.findById(ticketType.getId()).orElseThrow();
    }

    protected void assertPersistedTicketTypeToMatchAllProperties(TicketType expectedTicketType) {
        assertTicketTypeAllPropertiesEquals(expectedTicketType, getPersistedTicketType(expectedTicketType));
    }

    protected void assertPersistedTicketTypeToMatchUpdatableProperties(TicketType expectedTicketType) {
        assertTicketTypeAllUpdatablePropertiesEquals(expectedTicketType, getPersistedTicketType(expectedTicketType));
    }
}
