package com.dugx.event.service;

import com.dugx.event.domain.BookingDetail;
import com.dugx.event.repository.BookingDetailRepository;
import com.dugx.event.service.dto.BookingDetailDTO;
import com.dugx.event.service.mapper.BookingDetailMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.BookingDetail}.
 */
@Service
@Transactional
public class BookingDetailService {

    private static final Logger LOG = LoggerFactory.getLogger(BookingDetailService.class);

    private final BookingDetailRepository bookingDetailRepository;

    private final BookingDetailMapper bookingDetailMapper;

    public BookingDetailService(BookingDetailRepository bookingDetailRepository, BookingDetailMapper bookingDetailMapper) {
        this.bookingDetailRepository = bookingDetailRepository;
        this.bookingDetailMapper = bookingDetailMapper;
    }

    /**
     * Save a bookingDetail.
     *
     * @param bookingDetailDTO the entity to save.
     * @return the persisted entity.
     */
    public BookingDetailDTO save(BookingDetailDTO bookingDetailDTO) {
        LOG.debug("Request to save BookingDetail : {}", bookingDetailDTO);
        BookingDetail bookingDetail = bookingDetailMapper.toEntity(bookingDetailDTO);
        bookingDetail = bookingDetailRepository.save(bookingDetail);
        return bookingDetailMapper.toDto(bookingDetail);
    }

    /**
     * Update a bookingDetail.
     *
     * @param bookingDetailDTO the entity to save.
     * @return the persisted entity.
     */
    public BookingDetailDTO update(BookingDetailDTO bookingDetailDTO) {
        LOG.debug("Request to update BookingDetail : {}", bookingDetailDTO);
        BookingDetail bookingDetail = bookingDetailMapper.toEntity(bookingDetailDTO);
        bookingDetail = bookingDetailRepository.save(bookingDetail);
        return bookingDetailMapper.toDto(bookingDetail);
    }

    /**
     * Partially update a bookingDetail.
     *
     * @param bookingDetailDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BookingDetailDTO> partialUpdate(BookingDetailDTO bookingDetailDTO) {
        LOG.debug("Request to partially update BookingDetail : {}", bookingDetailDTO);

        return bookingDetailRepository
            .findById(bookingDetailDTO.getId())
            .map(existingBookingDetail -> {
                bookingDetailMapper.partialUpdate(existingBookingDetail, bookingDetailDTO);

                return existingBookingDetail;
            })
            .map(bookingDetailRepository::save)
            .map(bookingDetailMapper::toDto);
    }

    /**
     * Get all the bookingDetails with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<BookingDetailDTO> findAllWithEagerRelationships(Pageable pageable) {
        return bookingDetailRepository.findAllWithEagerRelationships(pageable).map(bookingDetailMapper::toDto);
    }

    /**
     * Get one bookingDetail by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BookingDetailDTO> findOne(Long id) {
        LOG.debug("Request to get BookingDetail : {}", id);
        return bookingDetailRepository.findOneWithEagerRelationships(id).map(bookingDetailMapper::toDto);
    }

    /**
     * Delete the bookingDetail by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete BookingDetail : {}", id);
        bookingDetailRepository.deleteById(id);
    }
}
