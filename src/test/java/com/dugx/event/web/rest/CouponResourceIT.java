package com.dugx.event.web.rest;

import static com.dugx.event.domain.CouponAsserts.*;
import static com.dugx.event.web.rest.TestUtil.createUpdateProxyForBean;
import static com.dugx.event.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dugx.event.IntegrationTest;
import com.dugx.event.domain.Coupon;
import com.dugx.event.domain.Event;
import com.dugx.event.repository.CouponRepository;
import com.dugx.event.service.CouponService;
import com.dugx.event.service.dto.CouponDTO;
import com.dugx.event.service.mapper.CouponMapper;
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
 * Integration tests for the {@link CouponResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CouponResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_DISCOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_DISCOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_DISCOUNT = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.ofEpochMilli(1784084641017L);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.ofEpochMilli(1784084641017L);

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final String ENTITY_API_URL = "/api/coupons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CouponRepository couponRepository;

    @Mock
    private CouponRepository couponRepositoryMock;

    @Autowired
    private CouponMapper couponMapper;

    @Mock
    private CouponService couponServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCouponMockMvc;

    private Coupon coupon;

    private Coupon insertedCoupon;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coupon createEntity() {
        return new Coupon()
            .code(DEFAULT_CODE)
            .discount(DEFAULT_DISCOUNT)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .quantity(DEFAULT_QUANTITY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coupon createUpdatedEntity() {
        return new Coupon()
            .code(UPDATED_CODE)
            .discount(UPDATED_DISCOUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .quantity(UPDATED_QUANTITY);
    }

    @BeforeEach
    void initTest() {
        coupon = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCoupon != null) {
            couponRepository.delete(insertedCoupon);
            insertedCoupon = null;
        }
    }

    @Test
    @Transactional
    void createCoupon() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);
        var returnedCouponDTO = om.readValue(
            restCouponMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CouponDTO.class
        );

        // Validate the Coupon in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCoupon = couponMapper.toEntity(returnedCouponDTO);
        assertCouponUpdatableFieldsEquals(returnedCoupon, getPersistedCoupon(returnedCoupon));

        insertedCoupon = returnedCoupon;
    }

    @Test
    @Transactional
    void createCouponWithExistingId() throws Exception {
        // Create the Coupon with an existing ID
        coupon.setId(1L);
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCouponMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        coupon.setCode(null);

        // Create the Coupon, which fails.
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        restCouponMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCoupons() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList
        restCouponMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coupon.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].discount").value(hasItem(sameNumber(DEFAULT_DISCOUNT))))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCouponsWithEagerRelationshipsIsEnabled() throws Exception {
        when(couponServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCouponMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(couponServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCouponsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(couponServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCouponMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(couponRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCoupon() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get the coupon
        restCouponMockMvc
            .perform(get(ENTITY_API_URL_ID, coupon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(coupon.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.discount").value(sameNumber(DEFAULT_DISCOUNT)))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY));
    }

    @Test
    @Transactional
    void getCouponsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        Long id = coupon.getId();

        defaultCouponFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCouponFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCouponFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCouponsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where code equals to
        defaultCouponFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCouponsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where code in
        defaultCouponFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCouponsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where code is not null
        defaultCouponFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllCouponsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where code contains
        defaultCouponFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCouponsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where code does not contain
        defaultCouponFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllCouponsByDiscountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where discount equals to
        defaultCouponFiltering("discount.equals=" + DEFAULT_DISCOUNT, "discount.equals=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllCouponsByDiscountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where discount in
        defaultCouponFiltering("discount.in=" + DEFAULT_DISCOUNT + "," + UPDATED_DISCOUNT, "discount.in=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllCouponsByDiscountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where discount is not null
        defaultCouponFiltering("discount.specified=true", "discount.specified=false");
    }

    @Test
    @Transactional
    void getAllCouponsByDiscountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where discount is greater than or equal to
        defaultCouponFiltering("discount.greaterThanOrEqual=" + DEFAULT_DISCOUNT, "discount.greaterThanOrEqual=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllCouponsByDiscountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where discount is less than or equal to
        defaultCouponFiltering("discount.lessThanOrEqual=" + DEFAULT_DISCOUNT, "discount.lessThanOrEqual=" + SMALLER_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllCouponsByDiscountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where discount is less than
        defaultCouponFiltering("discount.lessThan=" + UPDATED_DISCOUNT, "discount.lessThan=" + DEFAULT_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllCouponsByDiscountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where discount is greater than
        defaultCouponFiltering("discount.greaterThan=" + SMALLER_DISCOUNT, "discount.greaterThan=" + DEFAULT_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllCouponsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where startDate equals to
        defaultCouponFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllCouponsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where startDate in
        defaultCouponFiltering("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE, "startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllCouponsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where startDate is not null
        defaultCouponFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllCouponsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where endDate equals to
        defaultCouponFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllCouponsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where endDate in
        defaultCouponFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllCouponsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where endDate is not null
        defaultCouponFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllCouponsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where quantity equals to
        defaultCouponFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllCouponsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where quantity in
        defaultCouponFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllCouponsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where quantity is not null
        defaultCouponFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllCouponsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where quantity is greater than or equal to
        defaultCouponFiltering("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY, "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllCouponsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where quantity is less than or equal to
        defaultCouponFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllCouponsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where quantity is less than
        defaultCouponFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllCouponsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList where quantity is greater than
        defaultCouponFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllCouponsByEventIsEqualToSomething() throws Exception {
        Event event;
        if (TestUtil.findAll(em, Event.class).isEmpty()) {
            couponRepository.saveAndFlush(coupon);
            event = EventResourceIT.createEntity();
        } else {
            event = TestUtil.findAll(em, Event.class).get(0);
        }
        em.persist(event);
        em.flush();
        coupon.setEvent(event);
        couponRepository.saveAndFlush(coupon);
        Long eventId = event.getId();
        // Get all the couponList where event equals to eventId
        defaultCouponShouldBeFound("eventId.equals=" + eventId);

        // Get all the couponList where event equals to (eventId + 1)
        defaultCouponShouldNotBeFound("eventId.equals=" + (eventId + 1));
    }

    private void defaultCouponFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCouponShouldBeFound(shouldBeFound);
        defaultCouponShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCouponShouldBeFound(String filter) throws Exception {
        restCouponMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coupon.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].discount").value(hasItem(sameNumber(DEFAULT_DISCOUNT))))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));

        // Check, that the count call also returns 1
        restCouponMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCouponShouldNotBeFound(String filter) throws Exception {
        restCouponMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCouponMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCoupon() throws Exception {
        // Get the coupon
        restCouponMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCoupon() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the coupon
        Coupon updatedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCoupon are not directly saved in db
        em.detach(updatedCoupon);
        updatedCoupon
            .code(UPDATED_CODE)
            .discount(UPDATED_DISCOUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .quantity(UPDATED_QUANTITY);
        CouponDTO couponDTO = couponMapper.toDto(updatedCoupon);

        restCouponMockMvc
            .perform(
                put(ENTITY_API_URL_ID, couponDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO))
            )
            .andExpect(status().isOk());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCouponToMatchAllProperties(updatedCoupon);
    }

    @Test
    @Transactional
    void putNonExistingCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(
                put(ENTITY_API_URL_ID, couponDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(couponDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCouponWithPatch() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the coupon using partial update
        Coupon partialUpdatedCoupon = new Coupon();
        partialUpdatedCoupon.setId(coupon.getId());

        partialUpdatedCoupon.code(UPDATED_CODE).discount(UPDATED_DISCOUNT).startDate(UPDATED_START_DATE);

        restCouponMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCoupon.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCoupon))
            )
            .andExpect(status().isOk());

        // Validate the Coupon in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCouponUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCoupon, coupon), getPersistedCoupon(coupon));
    }

    @Test
    @Transactional
    void fullUpdateCouponWithPatch() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the coupon using partial update
        Coupon partialUpdatedCoupon = new Coupon();
        partialUpdatedCoupon.setId(coupon.getId());

        partialUpdatedCoupon
            .code(UPDATED_CODE)
            .discount(UPDATED_DISCOUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .quantity(UPDATED_QUANTITY);

        restCouponMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCoupon.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCoupon))
            )
            .andExpect(status().isOk());

        // Validate the Coupon in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCouponUpdatableFieldsEquals(partialUpdatedCoupon, getPersistedCoupon(partialUpdatedCoupon));
    }

    @Test
    @Transactional
    void patchNonExistingCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, couponDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(couponDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(couponDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(couponDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCoupon() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the coupon
        restCouponMockMvc
            .perform(delete(ENTITY_API_URL_ID, coupon.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return couponRepository.count();
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

    protected Coupon getPersistedCoupon(Coupon coupon) {
        return couponRepository.findById(coupon.getId()).orElseThrow();
    }

    protected void assertPersistedCouponToMatchAllProperties(Coupon expectedCoupon) {
        assertCouponAllPropertiesEquals(expectedCoupon, getPersistedCoupon(expectedCoupon));
    }

    protected void assertPersistedCouponToMatchUpdatableProperties(Coupon expectedCoupon) {
        assertCouponAllUpdatablePropertiesEquals(expectedCoupon, getPersistedCoupon(expectedCoupon));
    }
}
