package com.dugx.event.service;

import com.dugx.event.domain.Airport;
import com.dugx.event.repository.AirportRepository;
import com.dugx.event.service.dto.AirportDTO;
import com.dugx.event.service.mapper.AirportMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.Airport}.
 */
@Service
@Transactional
public class AirportService {

    private static final Logger LOG = LoggerFactory.getLogger(AirportService.class);

    private final AirportRepository airportRepository;

    private final AirportMapper airportMapper;

    public AirportService(AirportRepository airportRepository, AirportMapper airportMapper) {
        this.airportRepository = airportRepository;
        this.airportMapper = airportMapper;
    }

    public AirportDTO save(AirportDTO airportDTO) {
        LOG.debug("Request to save Airport : {}", airportDTO);
        Airport airport = airportMapper.toEntity(airportDTO);
        airport = airportRepository.save(airport);
        return airportMapper.toDto(airport);
    }

    public AirportDTO update(AirportDTO airportDTO) {
        LOG.debug("Request to update Airport : {}", airportDTO);
        Airport airport = airportMapper.toEntity(airportDTO);
        airport = airportRepository.save(airport);
        return airportMapper.toDto(airport);
    }

    @Transactional(readOnly = true)
    public List<AirportDTO> findAll() {
        return airportRepository.findAll().stream().map(airportMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Optional<AirportDTO> findOne(Long id) {
        LOG.debug("Request to get Airport : {}", id);
        return airportRepository.findById(id).map(airportMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete Airport : {}", id);
        airportRepository.deleteById(id);
    }
}
