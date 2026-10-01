package com.dugx.event.service;

import com.dugx.event.domain.Aircraft;
import com.dugx.event.domain.Seat;
import com.dugx.event.domain.SeatType;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import com.dugx.event.repository.AircraftRepository;
import com.dugx.event.repository.SeatRepository;
import com.dugx.event.service.dto.AircraftDTO;
import com.dugx.event.service.mapper.AircraftMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.Aircraft}.
 */
@Service
@Transactional
public class AircraftService {

    private static final Logger LOG = LoggerFactory.getLogger(AircraftService.class);

    private final AircraftRepository aircraftRepository;

    private final AircraftMapper aircraftMapper;

    private final SeatRepository seatRepository;

    public AircraftService(AircraftRepository aircraftRepository, AircraftMapper aircraftMapper, SeatRepository seatRepository) {
        this.aircraftRepository = aircraftRepository;
        this.aircraftMapper = aircraftMapper;
        this.seatRepository = seatRepository;
    }

    /**
     * Save an aircraft. On first creation (no seats yet for this aircraft), auto-generate a full
     * grid of STANDARD seats from totalRows/totalColumns so admins don't have to hand-enter every seat.
     *
     * @param aircraftDTO the entity to save.
     * @return the persisted entity.
     */
    public AircraftDTO save(AircraftDTO aircraftDTO) {
        LOG.debug("Request to save Aircraft : {}", aircraftDTO);
        validateAircraft(aircraftDTO, null);
        boolean isNew = aircraftDTO.getId() == null;
        Aircraft aircraft = aircraftMapper.toEntity(aircraftDTO);
        aircraft = aircraftRepository.save(aircraft);

        if (isNew && aircraft.getTotalRows() != null && aircraft.getTotalColumns() != null) {
            generateSeats(aircraft);
        }

        return aircraftMapper.toDto(aircraft);
    }

    private void generateSeats(Aircraft aircraft) {
        List<Seat> seats = new ArrayList<>();

        for (int row = 0; row < aircraft.getTotalRows(); row++) {
            String rowLabel = String.valueOf((char) ('A' + row));

            for (int col = 1; col <= aircraft.getTotalColumns(); col++) {
                Seat seat = new Seat();
                seat.setAircraft(aircraft);
                seat.setRowLabel(rowLabel);
                seat.setSeatNumber(col);
                seat.setSeatType(SeatType.STANDARD);
                seats.add(seat);
            }
        }

        seatRepository.saveAll(seats);
    }

    /**
     * Update an aircraft.
     *
     * @param aircraftDTO the entity to save.
     * @return the persisted entity.
     */
    public AircraftDTO update(AircraftDTO aircraftDTO) {
        LOG.debug("Request to update Aircraft : {}", aircraftDTO);
        validateAircraft(aircraftDTO, aircraftDTO.getId());
        Aircraft aircraft = aircraftMapper.toEntity(aircraftDTO);
        aircraft = aircraftRepository.save(aircraft);
        return aircraftMapper.toDto(aircraft);
    }

    /**
     * Get all the aircraft.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AircraftDTO> findAll(Pageable pageable) {
        return aircraftRepository.findAll(pageable).map(aircraftMapper::toDto);
    }

    /**
     * Get one aircraft by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AircraftDTO> findOne(Long id) {
        LOG.debug("Request to get Aircraft : {}", id);
        return aircraftRepository.findById(id).map(aircraftMapper::toDto);
    }

    /**
     * Delete the aircraft by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Aircraft : {}", id);
        aircraftRepository.deleteById(id);
    }

    /** Kiem tra Aircraft name: khong de trong va khong trung (khong phan biet hoa thuong). */
    private void validateAircraft(AircraftDTO dto, Long excludeId) {
        String value = dto.getName() == null ? "" : dto.getName().trim();
        if (value.isEmpty()) {
            throw new BadRequestAlertException("Aircraft name must not be blank", "aircraft", "namerequired");
        }
        dto.setName(value);
        boolean duplicated = excludeId == null ? aircraftRepository.existsByNameIgnoreCase(value) : aircraftRepository.existsByNameIgnoreCaseAndIdNot(value, excludeId);
        if (duplicated) {
            throw new BadRequestAlertException("Aircraft name already exists", "aircraft", "nameexists");
        }
    }
}
