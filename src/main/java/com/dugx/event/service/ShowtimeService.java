package com.dugx.event.service;

import com.dugx.event.domain.Event;
import com.dugx.event.domain.Seat;
import com.dugx.event.domain.SeatStatus;
import com.dugx.event.domain.SeatType;
import com.dugx.event.domain.Showtime;
import com.dugx.event.domain.ShowtimeSeat;
import com.dugx.event.repository.EventRepository;
import com.dugx.event.repository.SeatRepository;
import com.dugx.event.repository.ShowtimeRepository;
import com.dugx.event.repository.ShowtimeSeatRepository;
import com.dugx.event.service.dto.ShowtimeDTO;
import com.dugx.event.service.dto.ShowtimeSeatDTO;
import com.dugx.event.service.mapper.ShowtimeMapper;
import com.dugx.event.service.mapper.ShowtimeSeatMapper;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.Showtime}.
 */
@Service
@Transactional
public class ShowtimeService {

    private static final Logger LOG = LoggerFactory.getLogger(ShowtimeService.class);

    private final ShowtimeRepository showtimeRepository;

    private final ShowtimeMapper showtimeMapper;

    private final ShowtimeSeatRepository showtimeSeatRepository;

    private final ShowtimeSeatMapper showtimeSeatMapper;

    private final SeatRepository seatRepository;

    private final EventRepository eventRepository;

    public ShowtimeService(
        ShowtimeRepository showtimeRepository,
        ShowtimeMapper showtimeMapper,
        ShowtimeSeatRepository showtimeSeatRepository,
        ShowtimeSeatMapper showtimeSeatMapper,
        SeatRepository seatRepository,
        EventRepository eventRepository
    ) {
        this.showtimeRepository = showtimeRepository;
        this.showtimeMapper = showtimeMapper;
        this.showtimeSeatRepository = showtimeSeatRepository;
        this.showtimeSeatMapper = showtimeSeatMapper;
        this.seatRepository = seatRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Tao mot suat chieu moi cho phim (Event), va tu dong sinh ShowtimeSeat cho tung ghe
     * cua phong chieu duoc chon.
     *
     * @param showtimeDTO the entity to save.
     * @return the persisted entity.
     */
    public ShowtimeDTO save(ShowtimeDTO showtimeDTO) {
        LOG.debug("Request to save Showtime : {}", showtimeDTO);

        if (showtimeDTO.getEvent() == null || showtimeDTO.getEvent().getId() == null) {
            throw new BadRequestAlertException("Event is required", "showtime", "eventrequired");
        }
        if (showtimeDTO.getAircraft() == null || showtimeDTO.getAircraft().getId() == null) {
            throw new BadRequestAlertException("Aircraft is required", "showtime", "roomrequired");
        }

        eventRepository
            .findById(showtimeDTO.getEvent().getId())
            .orElseThrow(() -> new BadRequestAlertException("Event not found", "showtime", "eventnotfound"));

        Showtime showtime = showtimeMapper.toEntity(showtimeDTO);
        showtime = showtimeRepository.save(showtime);

        generateShowtimeSeats(showtime);

        return showtimeMapper.toDto(showtime);
    }

    private void generateShowtimeSeats(Showtime showtime) {
        List<Seat> seats = seatRepository.findByAircraft_Id(showtime.getAircraft().getId());
        List<ShowtimeSeat> showtimeSeats = new ArrayList<>();

        for (Seat seat : seats) {
            ShowtimeSeat showtimeSeat = new ShowtimeSeat();
            showtimeSeat.setShowtime(showtime);
            showtimeSeat.setSeat(seat);
            showtimeSeat.setStatus(SeatStatus.AVAILABLE);
            showtimeSeat.setPrice(priceForSeatType(showtime, seat.getSeatType()));
            showtimeSeats.add(showtimeSeat);
        }

        showtimeSeatRepository.saveAll(showtimeSeats);
    }

    private BigDecimal priceForSeatType(Showtime showtime, SeatType seatType) {
        if (seatType == SeatType.VIP && showtime.getVipPrice() != null) {
            return showtime.getVipPrice();
        }
        if (seatType == SeatType.COUPLE && showtime.getCouplePrice() != null) {
            return showtime.getCouplePrice();
        }
        return showtime.getBasePrice();
    }

    @Transactional(readOnly = true)
    public List<ShowtimeDTO> findByEvent(Long eventId) {
        return showtimeRepository.findByEvent_Id(eventId).stream().map(showtimeMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Optional<ShowtimeDTO> findOne(Long id) {
        LOG.debug("Request to get Showtime : {}", id);
        return showtimeRepository.findById(id).map(showtimeMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ShowtimeSeatDTO> getSeats(Long showtimeId) {
        return showtimeSeatRepository.findByShowtime_Id(showtimeId).stream().map(showtimeSeatMapper::toDto).toList();
    }

    public void delete(Long id) {
        LOG.debug("Request to delete Showtime : {}", id);
        showtimeSeatRepository.deleteByShowtime_Id(id);
        showtimeRepository.deleteById(id);
    }
}
