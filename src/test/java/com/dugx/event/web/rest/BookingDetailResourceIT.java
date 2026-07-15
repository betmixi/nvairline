package com.dugx.event.web.rest;

import static com.dugx.event.domain.BookingDetailAsserts.*;
import static com.dugx.event.web.rest.TestUtil.createUpdateProxyForBean;
import static com.dugx.event.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dugx.event.IntegrationTest;
import com.dugx.event.domain.Booking;
import com.dugx.event.domain.BookingDetail;
import com.dugx.event.domain.TicketType;
import com.dugx.event.repository.BookingDetailRepository;
import com.dugx.event.service.BookingDetailService;
import com.dugx.event.service.dto.BookingDetailDTO;
import com.dugx.event.service.mapper.BookingDetailMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link BookingDetailResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BookingDetailResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/booking-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @Mock
    private BookingDetailRepository bookingDetailRepositoryMock;

    @Autowired
    private BookingDetailMapper bookingDetailMapper;

    @Mock
    private BookingDetailService bookingDetailServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookingDetailMockMvc;

    private BookingDetail bookingDetail;

    private BookingDetail insertedBookingDetail;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookingDetail createEntity() {
        return new BookingDetail().quantity(DEFAULT_QUANTITY).price(DEFAULT_PRICE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookingDetail createUpdatedEntity() {
        return new BookingDetail().quantity(UPDATED_QUANTITY).price(UPDATED_PRICE);
    }

    @BeforeEach
    void initTest() {
        bookingDetail = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBookingDetail != null) {
            bookingDetailRepository.delete(insertedBookingDetail);
            insertedBookingDetail = null;
        }
    }

    @Test
    @Transactional
    void createBookingDetail() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BookingDetail
        BookingDetailDTO bookingDetailDTO = bookingDetailMapper.toDto(bookingDetail);
        var returnedBookingDetailDTO = om.readValue(
            restBookingDetailMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookingDetailDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BookingDetailDTO.class
        );

        // Validate the BookingDetail in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBookingDetail = bookingDetailMapper.toEntity(returnedBookingDetailDTO);
        assertBookingDetailUpdatableFieldsEquals(returnedBookingDetail, getPersistedBookingDetail(returnedBookingDetail));

        insertedBookingDetail = returnedBookingDetail;
    }

    @Test
    @Transactional
    void createBookingDetailWithExistingId() throws Exception {
        // Create the BookingDetail with an existing ID
        bookingDetail.setId(1L);
        BookingDetailDTO bookingDetailDTO = bookingDetailMapper.toDto(bookingDetail);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookingDetailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookingDetailDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BookingDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBookingDetails() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        // Get all the bookingDetailList
        restBookingDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookingDetail.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBookingDetailsWithEagerRelationshipsIsEnabled() throws Exception {
        when(bookingDetailServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookingDetailMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bookingDetailServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBookingDetailsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bookingDetailServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookingDetailMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(bookingDetailRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBookingDetail() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        // Get the bookingDetail
        restBookingDetailMockMvc
            .perform(get(ENTITY_API_URL_ID, bookingDetail.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookingDetail.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)));
    }

    @Test
    @Transactional
    void getBookingDetailsByIdFiltering() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        Long id = bookingDetail.getId();

        defaultBookingDetailFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBookingDetailFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBookingDetailFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBookingDetailsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        // Get all the bookingDetailList where quantity equals to
        defaultBookingDetailFiltering("quantity.equals=" + DEFAULT_QUANTITY, "quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllBookingDetailsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        // Get all the bookingDetailList where quantity in
        defaultBookingDetailFiltering("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY, "quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllBookingDetailsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        // Get all the bookingDetailList where quantity is not null
        defaultBookingDetailFiltering("quantity.specified=true", "quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllBookingDetailsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        // Get all the bookingDetailList where quantity is greater than or equal to
        defaultBookingDetailFiltering("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY, "quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllBookingDetailsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        // Get all the bookingDetailList where quantity is less than or equal to
        defaultBookingDetailFiltering("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY, "quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllBookingDetailsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        // Get all the bookingDetailList where quantity is less than
        defaultBookingDetailFiltering("quantity.lessThan=" + UPDATED_QUANTITY, "quantity.lessThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllBookingDetailsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        // Get all the bookingDetailList where quantity is greater than
        defaultBookingDetailFiltering("quantity.greaterThan=" + SMALLER_QUANTITY, "quantity.greaterThan=" + DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void getAllBookingDetailsByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        // Get all the bookingDetailList where price equals to
        defaultBookingDetailFiltering("price.equals=" + DEFAULT_PRICE, "price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllBookingDetailsByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        // Get all the bookingDetailList where price in
        defaultBookingDetailFiltering("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE, "price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllBookingDetailsByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        // Get all the bookingDetailList where price is not null
        defaultBookingDetailFiltering("price.specified=true", "price.specified=false");
    }

    @Test
    @Transactional
    void getAllBookingDetailsByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        // Get all the bookingDetailList where price is greater than or equal to
        defaultBookingDetailFiltering("price.greaterThanOrEqual=" + DEFAULT_PRICE, "price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllBookingDetailsByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        // Get all the bookingDetailList where price is less than or equal to
        defaultBookingDetailFiltering("price.lessThanOrEqual=" + DEFAULT_PRICE, "price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllBookingDetailsByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        // Get all the bookingDetailList where price is less than
        defaultBookingDetailFiltering("price.lessThan=" + UPDATED_PRICE, "price.lessThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllBookingDetailsByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        // Get all the bookingDetailList where price is greater than
        defaultBookingDetailFiltering("price.greaterThan=" + SMALLER_PRICE, "price.greaterThan=" + DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void getAllBookingDetailsByBookingIsEqualToSomething() throws Exception {
        Booking booking;
        if (TestUtil.findAll(em, Booking.class).isEmpty()) {
            bookingDetailRepository.saveAndFlush(bookingDetail);
            booking = BookingResourceIT.createEntity();
        } else {
            booking = TestUtil.findAll(em, Booking.class).get(0);
        }
        em.persist(booking);
        em.flush();
        bookingDetail.setBooking(booking);
        bookingDetailRepository.saveAndFlush(bookingDetail);
        Long bookingId = booking.getId();
        // Get all the bookingDetailList where booking equals to bookingId
        defaultBookingDetailShouldBeFound("bookingId.equals=" + bookingId);

        // Get all the bookingDetailList where booking equals to (bookingId + 1)
        defaultBookingDetailShouldNotBeFound("bookingId.equals=" + (bookingId + 1));
    }

    @Test
    @Transactional
    void getAllBookingDetailsByTicketTypeIsEqualToSomething() throws Exception {
        TicketType ticketType;
        if (TestUtil.findAll(em, TicketType.class).isEmpty()) {
            bookingDetailRepository.saveAndFlush(bookingDetail);
            ticketType = TicketTypeResourceIT.createEntity();
        } else {
            ticketType = TestUtil.findAll(em, TicketType.class).get(0);
        }
        em.persist(ticketType);
        em.flush();
        bookingDetail.setTicketType(ticketType);
        bookingDetailRepository.saveAndFlush(bookingDetail);
        Long ticketTypeId = ticketType.getId();
        // Get all the bookingDetailList where ticketType equals to ticketTypeId
        defaultBookingDetailShouldBeFound("ticketTypeId.equals=" + ticketTypeId);

        // Get all the bookingDetailList where ticketType equals to (ticketTypeId + 1)
        defaultBookingDetailShouldNotBeFound("ticketTypeId.equals=" + (ticketTypeId + 1));
    }

    private void defaultBookingDetailFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBookingDetailShouldBeFound(shouldBeFound);
        defaultBookingDetailShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBookingDetailShouldBeFound(String filter) throws Exception {
        restBookingDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookingDetail.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))));

        // Check, that the count call also returns 1
        restBookingDetailMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBookingDetailShouldNotBeFound(String filter) throws Exception {
        restBookingDetailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBookingDetailMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBookingDetail() throws Exception {
        // Get the bookingDetail
        restBookingDetailMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBookingDetail() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookingDetail
        BookingDetail updatedBookingDetail = bookingDetailRepository.findById(bookingDetail.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBookingDetail are not directly saved in db
        em.detach(updatedBookingDetail);
        updatedBookingDetail.quantity(UPDATED_QUANTITY).price(UPDATED_PRICE);
        BookingDetailDTO bookingDetailDTO = bookingDetailMapper.toDto(updatedBookingDetail);

        restBookingDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookingDetailDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookingDetailDTO))
            )
            .andExpect(status().isOk());

        // Validate the BookingDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBookingDetailToMatchAllProperties(updatedBookingDetail);
    }

    @Test
    @Transactional
    void putNonExistingBookingDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingDetail.setId(longCount.incrementAndGet());

        // Create the BookingDetail
        BookingDetailDTO bookingDetailDTO = bookingDetailMapper.toDto(bookingDetail);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookingDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookingDetailDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookingDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookingDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBookingDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingDetail.setId(longCount.incrementAndGet());

        // Create the BookingDetail
        BookingDetailDTO bookingDetailDTO = bookingDetailMapper.toDto(bookingDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingDetailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookingDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookingDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBookingDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingDetail.setId(longCount.incrementAndGet());

        // Create the BookingDetail
        BookingDetailDTO bookingDetailDTO = bookingDetailMapper.toDto(bookingDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingDetailMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookingDetailDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookingDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookingDetailWithPatch() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookingDetail using partial update
        BookingDetail partialUpdatedBookingDetail = new BookingDetail();
        partialUpdatedBookingDetail.setId(bookingDetail.getId());

        partialUpdatedBookingDetail.quantity(UPDATED_QUANTITY);

        restBookingDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookingDetail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookingDetail))
            )
            .andExpect(status().isOk());

        // Validate the BookingDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookingDetailUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBookingDetail, bookingDetail),
            getPersistedBookingDetail(bookingDetail)
        );
    }

    @Test
    @Transactional
    void fullUpdateBookingDetailWithPatch() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookingDetail using partial update
        BookingDetail partialUpdatedBookingDetail = new BookingDetail();
        partialUpdatedBookingDetail.setId(bookingDetail.getId());

        partialUpdatedBookingDetail.quantity(UPDATED_QUANTITY).price(UPDATED_PRICE);

        restBookingDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookingDetail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookingDetail))
            )
            .andExpect(status().isOk());

        // Validate the BookingDetail in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookingDetailUpdatableFieldsEquals(partialUpdatedBookingDetail, getPersistedBookingDetail(partialUpdatedBookingDetail));
    }

    @Test
    @Transactional
    void patchNonExistingBookingDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingDetail.setId(longCount.incrementAndGet());

        // Create the BookingDetail
        BookingDetailDTO bookingDetailDTO = bookingDetailMapper.toDto(bookingDetail);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookingDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookingDetailDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookingDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookingDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBookingDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingDetail.setId(longCount.incrementAndGet());

        // Create the BookingDetail
        BookingDetailDTO bookingDetailDTO = bookingDetailMapper.toDto(bookingDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingDetailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookingDetailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookingDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBookingDetail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookingDetail.setId(longCount.incrementAndGet());

        // Create the BookingDetail
        BookingDetailDTO bookingDetailDTO = bookingDetailMapper.toDto(bookingDetail);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookingDetailMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(bookingDetailDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookingDetail in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBookingDetail() throws Exception {
        // Initialize the database
        insertedBookingDetail = bookingDetailRepository.saveAndFlush(bookingDetail);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the bookingDetail
        restBookingDetailMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookingDetail.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return bookingDetailRepository.count();
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

    protected BookingDetail getPersistedBookingDetail(BookingDetail bookingDetail) {
        return bookingDetailRepository.findById(bookingDetail.getId()).orElseThrow();
    }

    protected void assertPersistedBookingDetailToMatchAllProperties(BookingDetail expectedBookingDetail) {
        assertBookingDetailAllPropertiesEquals(expectedBookingDetail, getPersistedBookingDetail(expectedBookingDetail));
    }

    protected void assertPersistedBookingDetailToMatchUpdatableProperties(BookingDetail expectedBookingDetail) {
        assertBookingDetailAllUpdatablePropertiesEquals(expectedBookingDetail, getPersistedBookingDetail(expectedBookingDetail));
    }
}
