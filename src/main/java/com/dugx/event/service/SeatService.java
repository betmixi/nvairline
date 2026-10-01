package com.dugx.event.service;

import com.dugx.event.domain.Seat;
import com.dugx.event.repository.SeatRepository;
import com.dugx.event.service.dto.SeatDTO;
import com.dugx.event.service.mapper.SeatMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.Seat}.
 */
@Service
@Transactional
public class SeatService {

    private static final Logger LOG = LoggerFactory.getLogger(SeatService.class);

    private final SeatRepository seatRepository;

    private final SeatMapper seatMapper;

    public SeatService(SeatRepository seatRepository, SeatMapper seatMapper) {
        this.seatRepository = seatRepository;
        this.seatMapper = seatMapper;
    }

    public SeatDTO save(SeatDTO seatDTO) {
        LOG.debug("Request to save Seat : {}", seatDTO);
        Seat seat = seatMapper.toEntity(seatDTO);
        seat = seatRepository.save(seat);
        return seatMapper.toDto(seat);
    }

    public SeatDTO update(SeatDTO seatDTO) {
        LOG.debug("Request to update Seat : {}", seatDTO);
        Seat seat = seatMapper.toEntity(seatDTO);
        seat = seatRepository.save(seat);
        return seatMapper.toDto(seat);
    }

    @Transactional(readOnly = true)
    public Page<SeatDTO> findAll(Pageable pageable) {
        return seatRepository.findAll(pageable).map(seatMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<SeatDTO> findByAircraft(Long aircraftId) {
        return seatRepository.findByAircraft_Id(aircraftId).stream().map(seatMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Optional<SeatDTO> findOne(Long id) {
        LOG.debug("Request to get Seat : {}", id);
        return seatRepository.findById(id).map(seatMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete Seat : {}", id);
        seatRepository.deleteById(id);
    }
}
