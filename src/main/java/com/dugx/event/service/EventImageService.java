package com.dugx.event.service;

import com.dugx.event.domain.EventImage;
import com.dugx.event.repository.EventImageRepository;
import com.dugx.event.service.dto.EventImageDTO;
import com.dugx.event.service.mapper.EventImageMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.EventImage}.
 */
@Service
@Transactional
public class EventImageService {

    private static final Logger LOG = LoggerFactory.getLogger(EventImageService.class);

    private final EventImageRepository eventImageRepository;

    private final EventImageMapper eventImageMapper;

    public EventImageService(EventImageRepository eventImageRepository, EventImageMapper eventImageMapper) {
        this.eventImageRepository = eventImageRepository;
        this.eventImageMapper = eventImageMapper;
    }

    /**
     * Save a eventImage.
     *
     * @param eventImageDTO the entity to save.
     * @return the persisted entity.
     */
    public EventImageDTO save(EventImageDTO eventImageDTO) {
        LOG.debug("Request to save EventImage : {}", eventImageDTO);
        EventImage eventImage = eventImageMapper.toEntity(eventImageDTO);
        eventImage = eventImageRepository.save(eventImage);
        return eventImageMapper.toDto(eventImage);
    }

    /**
     * Update a eventImage.
     *
     * @param eventImageDTO the entity to save.
     * @return the persisted entity.
     */
    public EventImageDTO update(EventImageDTO eventImageDTO) {
        LOG.debug("Request to update EventImage : {}", eventImageDTO);
        EventImage eventImage = eventImageMapper.toEntity(eventImageDTO);
        eventImage = eventImageRepository.save(eventImage);
        return eventImageMapper.toDto(eventImage);
    }

    /**
     * Partially update a eventImage.
     *
     * @param eventImageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EventImageDTO> partialUpdate(EventImageDTO eventImageDTO) {
        LOG.debug("Request to partially update EventImage : {}", eventImageDTO);

        return eventImageRepository
            .findById(eventImageDTO.getId())
            .map(existingEventImage -> {
                eventImageMapper.partialUpdate(existingEventImage, eventImageDTO);

                return existingEventImage;
            })
            .map(eventImageRepository::save)
            .map(eventImageMapper::toDto);
    }

    /**
     * Get all the eventImages with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<EventImageDTO> findAllWithEagerRelationships(Pageable pageable) {
        return eventImageRepository.findAllWithEagerRelationships(pageable).map(eventImageMapper::toDto);
    }

    /**
     * Get one eventImage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EventImageDTO> findOne(Long id) {
        LOG.debug("Request to get EventImage : {}", id);
        return eventImageRepository.findOneWithEagerRelationships(id).map(eventImageMapper::toDto);
    }

    /**
     * Delete the eventImage by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete EventImage : {}", id);
        eventImageRepository.deleteById(id);
    }
}
