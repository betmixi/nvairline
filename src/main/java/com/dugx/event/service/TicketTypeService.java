package com.dugx.event.service;

import com.dugx.event.domain.TicketType;
import com.dugx.event.repository.TicketTypeRepository;
import com.dugx.event.service.dto.TicketTypeDTO;
import com.dugx.event.service.mapper.TicketTypeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.TicketType}.
 */
@Service
@Transactional
public class TicketTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketTypeService.class);

    private final TicketTypeRepository ticketTypeRepository;

    private final TicketTypeMapper ticketTypeMapper;

    public TicketTypeService(TicketTypeRepository ticketTypeRepository, TicketTypeMapper ticketTypeMapper) {
        this.ticketTypeRepository = ticketTypeRepository;
        this.ticketTypeMapper = ticketTypeMapper;
    }

    /**
     * Save a ticketType.
     *
     * @param ticketTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public TicketTypeDTO save(TicketTypeDTO ticketTypeDTO) {
        LOG.debug("Request to save TicketType : {}", ticketTypeDTO);
        TicketType ticketType = ticketTypeMapper.toEntity(ticketTypeDTO);
        ticketType = ticketTypeRepository.save(ticketType);
        return ticketTypeMapper.toDto(ticketType);
    }

    /**
     * Update a ticketType.
     *
     * @param ticketTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public TicketTypeDTO update(TicketTypeDTO ticketTypeDTO) {
        LOG.debug("Request to update TicketType : {}", ticketTypeDTO);
        TicketType ticketType = ticketTypeMapper.toEntity(ticketTypeDTO);
        ticketType = ticketTypeRepository.save(ticketType);
        return ticketTypeMapper.toDto(ticketType);
    }

    /**
     * Partially update a ticketType.
     *
     * @param ticketTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TicketTypeDTO> partialUpdate(TicketTypeDTO ticketTypeDTO) {
        LOG.debug("Request to partially update TicketType : {}", ticketTypeDTO);

        return ticketTypeRepository
            .findById(ticketTypeDTO.getId())
            .map(existingTicketType -> {
                ticketTypeMapper.partialUpdate(existingTicketType, ticketTypeDTO);

                return existingTicketType;
            })
            .map(ticketTypeRepository::save)
            .map(ticketTypeMapper::toDto);
    }

    /**
     * Get all the ticketTypes with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TicketTypeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ticketTypeRepository.findAllWithEagerRelationships(pageable).map(ticketTypeMapper::toDto);
    }

    /**
     * Get one ticketType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TicketTypeDTO> findOne(Long id) {
        LOG.debug("Request to get TicketType : {}", id);
        return ticketTypeRepository.findOneWithEagerRelationships(id).map(ticketTypeMapper::toDto);
    }

    /**
     * Delete the ticketType by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TicketType : {}", id);
        ticketTypeRepository.deleteById(id);
    }
}
