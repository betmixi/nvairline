package com.dugx.event.service;

import com.dugx.event.domain.Organizer;
import com.dugx.event.repository.OrganizerRepository;
import com.dugx.event.service.dto.OrganizerDTO;
import com.dugx.event.service.mapper.OrganizerMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.Organizer}.
 */
@Service
@Transactional
public class OrganizerService {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizerService.class);

    private final OrganizerRepository organizerRepository;

    private final OrganizerMapper organizerMapper;

    public OrganizerService(OrganizerRepository organizerRepository, OrganizerMapper organizerMapper) {
        this.organizerRepository = organizerRepository;
        this.organizerMapper = organizerMapper;
    }

    /**
     * Save a organizer.
     *
     * @param organizerDTO the entity to save.
     * @return the persisted entity.
     */
    public OrganizerDTO save(OrganizerDTO organizerDTO) {
        LOG.debug("Request to save Organizer : {}", organizerDTO);
        Organizer organizer = organizerMapper.toEntity(organizerDTO);
        organizer = organizerRepository.save(organizer);
        return organizerMapper.toDto(organizer);
    }

    /**
     * Update a organizer.
     *
     * @param organizerDTO the entity to save.
     * @return the persisted entity.
     */
    public OrganizerDTO update(OrganizerDTO organizerDTO) {
        LOG.debug("Request to update Organizer : {}", organizerDTO);
        Organizer organizer = organizerMapper.toEntity(organizerDTO);
        organizer = organizerRepository.save(organizer);
        return organizerMapper.toDto(organizer);
    }

    /**
     * Partially update a organizer.
     *
     * @param organizerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrganizerDTO> partialUpdate(OrganizerDTO organizerDTO) {
        LOG.debug("Request to partially update Organizer : {}", organizerDTO);

        return organizerRepository
            .findById(organizerDTO.getId())
            .map(existingOrganizer -> {
                organizerMapper.partialUpdate(existingOrganizer, organizerDTO);

                return existingOrganizer;
            })
            .map(organizerRepository::save)
            .map(organizerMapper::toDto);
    }

    /**
     * Get all the organizers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<OrganizerDTO> findAllWithEagerRelationships(Pageable pageable) {
        return organizerRepository.findAllWithEagerRelationships(pageable).map(organizerMapper::toDto);
    }

    /**
     * Get one organizer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrganizerDTO> findOne(Long id) {
        LOG.debug("Request to get Organizer : {}", id);
        return organizerRepository.findOneWithEagerRelationships(id).map(organizerMapper::toDto);
    }

    /**
     * Delete the organizer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Organizer : {}", id);
        organizerRepository.deleteById(id);
    }
}
