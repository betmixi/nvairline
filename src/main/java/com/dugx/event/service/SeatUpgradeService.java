package com.dugx.event.service;

import com.dugx.event.domain.*;
import com.dugx.event.repository.*;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.TicketUpgradeInfoDTO;
import com.dugx.event.service.dto.UpgradeOptionDTO;
import com.dugx.event.service.dto.UpgradeResponseDTO;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Nang hang ghe cho mot ve da mua: doi sang ghe hang cao hon cung suat bay va
 * thu them phan chenh lech gia qua VNPay (tai su dung ha tang Booking/Payment
 * san co, xem PaymentService.createVnPayUrl voi bookingId).
 */
@Service
@Transactional
public class SeatUpgradeService {

    private static final Logger LOG = LoggerFactory.getLogger(SeatUpgradeService.class);
    private static final String ENTITY_NAME = "seatUpgrade";

    private final TicketRepository ticketRepository;
    private final ShowtimeSeatRepository showtimeSeatRepository;
    private final SeatUpgradeRepository seatUpgradeRepository;
    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final UserRepository userRepository;

    public SeatUpgradeService(
        TicketRepository ticketRepository,
        ShowtimeSeatRepository showtimeSeatRepository,
        SeatUpgradeRepository seatUpgradeRepository,
        BookingRepository bookingRepository,
        BookingDetailRepository bookingDetailRepository,
        UserRepository userRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.showtimeSeatRepository = showtimeSeatRepository;
        this.seatUpgradeRepository = seatUpgradeRepository;
        this.bookingRepository = bookingRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.userRepository = userRepository;
    }

    /** Cac hang ghe hop le, xep theo thu tu hang thap -> cao (STANDARD luon thap nhat). */
    private static final List<SeatType> TIER_ORDER = List.of(SeatType.STANDARD, SeatType.COUPLE, SeatType.VIP);

    private String label(SeatType type) {
        return switch (type) {
            case STANDARD -> "Phổ thông";
            case COUPLE -> "Hạng nhất";
            case VIP -> "Thương gia";
        };
    }

    private BigDecimal priceOf(Showtime showtime, SeatType type) {
        return switch (type) {
            case STANDARD -> showtime.getBasePrice();
            case COUPLE -> showtime.getCouplePrice();
            case VIP -> showtime.getVipPrice();
        };
    }

    @Transactional(readOnly = true)
    public TicketUpgradeInfoDTO getUpgradeInfo(Long ticketId) {
        Ticket ticket = findOwnedTicket(ticketId);
        ShowtimeSeat currentSeat = ticket.getBookingDetail().getShowtimeSeat();
        Showtime showtime = currentSeat.getShowtime();
        SeatType currentType = currentSeat.getSeat().getSeatType();

        TicketUpgradeInfoDTO dto = new TicketUpgradeInfoDTO();
        dto.setTicketId(ticketId);
        dto.setCurrentSeatType(currentType.name());
        dto.setCurrentSeatTypeLabel(label(currentType));
        dto.setCurrentPrice(currentSeat.getPrice());

        String blockedReason = blockedReason(ticket, showtime);

        List<SeatType> higherTiers = higherTiers(showtime, currentType);

        dto.setMaxTier(higherTiers.isEmpty());

        List<UpgradeOptionDTO> options = new ArrayList<>();
        for (SeatType type : higherTiers) {
            BigDecimal price = priceOf(showtime, type);
            long availableCount = showtimeSeatRepository.countAvailableByShowtimeAndSeatType(showtime.getId(), type);
            BigDecimal diff = price.subtract(currentSeat.getPrice());
            options.add(new UpgradeOptionDTO(type.name(), label(type), price, diff, availableCount > 0));
        }
        dto.setOptions(options);

        if (blockedReason != null) {
            dto.setUpgradable(false);
            dto.setReason(blockedReason);
        } else if (higherTiers.isEmpty()) {
            dto.setUpgradable(false);
            dto.setReason("Vé đang ở hạng cao nhất, không thể nâng hạng thêm.");
        } else if (options.stream().noneMatch(UpgradeOptionDTO::isAvailable)) {
            dto.setUpgradable(false);
            dto.setReason("Hiện không còn ghế trống ở hạng cao hơn cho chuyến bay này.");
        } else {
            dto.setUpgradable(true);
        }

        return dto;
    }

    @Transactional
    public UpgradeResponseDTO requestUpgrade(Long ticketId, String targetSeatTypeRaw) {
        Ticket ticket = findOwnedTicket(ticketId);
        ShowtimeSeat oldSeat = ticket.getBookingDetail().getShowtimeSeat();
        Showtime showtime = oldSeat.getShowtime();
        SeatType currentType = oldSeat.getSeat().getSeatType();

        String blockedReason = blockedReason(ticket, showtime);
        if (blockedReason != null) {
            throw new BadRequestAlertException(blockedReason, ENTITY_NAME, "notupgradable");
        }

        SeatType targetType = parseSeatType(targetSeatTypeRaw);

        if (!higherTiers(showtime, currentType).contains(targetType)) {
            throw new BadRequestAlertException("Hạng ghế được chọn không cao hơn hạng hiện tại", ENTITY_NAME, "invalidtarget");
        }

        ShowtimeSeat newSeat = showtimeSeatRepository
            .findFirstAvailableByShowtimeAndSeatTypeForUpdate(showtime.getId(), targetType)
            .orElseThrow(() -> new BadRequestAlertException("Không còn ghế trống ở hạng này", ENTITY_NAME, "seatunavailable"));

        BigDecimal diff = newSeat.getPrice().subtract(oldSeat.getPrice());
        if (diff.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestAlertException("Hạng ghế được chọn không cao hơn hạng hiện tại", ENTITY_NAME, "invalidtarget");
        }

        // Giu cho ghe moi ngay lap tuc de tranh nguoi khac dat trung trong luc cho thanh toan.
        newSeat.setStatus(SeatStatus.HELD);
        showtimeSeatRepository.save(newSeat);

        User user = userRepository
            .findOneByLogin(currentLogin())
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "usernotfound"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBookingDate(Instant.now());
        booking.setStatus("PENDING");
        booking.setTotalAmount(diff);
        booking = bookingRepository.save(booking);

        SeatUpgrade upgrade = new SeatUpgrade();
        upgrade.setTicket(ticket);
        upgrade.setOldShowtimeSeat(oldSeat);
        upgrade.setNewShowtimeSeat(newSeat);
        upgrade.setPriceDifference(diff);
        upgrade.setStatus("PENDING");
        upgrade.setCreatedDate(Instant.now());
        upgrade.setBooking(booking);
        upgrade = seatUpgradeRepository.save(upgrade);

        LOG.debug("Created seat upgrade {} for ticket {} -> booking {}", upgrade.getId(), ticketId, booking.getId());

        return new UpgradeResponseDTO(upgrade.getId(), booking.getId(), diff);
    }

    /**
     * Goi tu BookingService khi mot booking duoc xac nhan da thanh toan. Neu
     * booking nay la mot yeu cau nang hang (khong phai mua ve moi) thi hoan
     * tat viec doi ghe va tra ve true de BookingService bo qua luong tao ve
     * thong thuong (vi booking nang hang khong co booking_detail).
     */
    @Transactional
    public boolean completeUpgradeIfApplicable(Long bookingId) {
        Optional<SeatUpgrade> maybeUpgrade = seatUpgradeRepository.findByBooking_Id(bookingId);
        if (maybeUpgrade.isEmpty()) {
            return false;
        }

        SeatUpgrade upgrade = maybeUpgrade.get();
        if ("PAID".equals(upgrade.getStatus())) {
            return true;
        }

        ShowtimeSeat oldSeat = upgrade.getOldShowtimeSeat();
        ShowtimeSeat newSeat = upgrade.getNewShowtimeSeat();

        oldSeat.setStatus(SeatStatus.AVAILABLE);
        showtimeSeatRepository.save(oldSeat);

        newSeat.setStatus(SeatStatus.BOOKED);
        showtimeSeatRepository.save(newSeat);

        BookingDetail detail = upgrade.getTicket().getBookingDetail();
        detail.setShowtimeSeat(newSeat);
        detail.setPrice(newSeat.getPrice());
        bookingDetailRepository.save(detail);

        upgrade.setStatus("PAID");
        seatUpgradeRepository.save(upgrade);

        LOG.debug(
            "Completed seat upgrade {} for ticket {}: seat {} -> {}",
            upgrade.getId(),
            upgrade.getTicket().getId(),
            oldSeat.getId(),
            newSeat.getId()
        );

        return true;
    }

    /** Goi tu BookingService khi booking nang hang bi huy/thanh toan that bai: tra lai ghe moi da giu. */
    @Transactional
    public boolean cancelUpgradeIfApplicable(Long bookingId) {
        Optional<SeatUpgrade> maybeUpgrade = seatUpgradeRepository.findByBooking_Id(bookingId);
        if (maybeUpgrade.isEmpty()) {
            return false;
        }

        SeatUpgrade upgrade = maybeUpgrade.get();
        if (!"PENDING".equals(upgrade.getStatus())) {
            return true;
        }

        ShowtimeSeat newSeat = upgrade.getNewShowtimeSeat();
        newSeat.setStatus(SeatStatus.AVAILABLE);
        showtimeSeatRepository.save(newSeat);

        upgrade.setStatus("CANCELLED");
        seatUpgradeRepository.save(upgrade);

        return true;
    }

    private List<SeatType> higherTiers(Showtime showtime, SeatType currentType) {
        List<SeatType> priced = new ArrayList<>();
        for (SeatType type : TIER_ORDER) {
            if (priceOf(showtime, type) != null) {
                priced.add(type);
            }
        }
        priced.sort(Comparator.comparing(type -> priceOf(showtime, type)));

        BigDecimal currentPrice = priceOf(showtime, currentType);
        if (currentPrice == null) {
            return List.of();
        }

        List<SeatType> higher = new ArrayList<>();
        for (SeatType type : priced) {
            if (priceOf(showtime, type).compareTo(currentPrice) > 0) {
                higher.add(type);
            }
        }
        return higher;
    }

    private String blockedReason(Ticket ticket, Showtime showtime) {
        if (Boolean.TRUE.equals(ticket.getCheckedIn())) {
            return "Vé đã check-in, không thể nâng hạng.";
        }
        if ("CANCELLED".equals(ticket.getStatus())) {
            return "Vé đã bị huỷ, không thể nâng hạng.";
        }
        if (showtime.getStartTime() != null && Instant.now().isAfter(showtime.getStartTime())) {
            return "Chuyến bay đã khởi hành, không thể nâng hạng.";
        }
        return null;
    }

    private SeatType parseSeatType(String raw) {
        try {
            return SeatType.valueOf(raw);
        } catch (Exception e) {
            throw new BadRequestAlertException("Hạng ghế không hợp lệ", ENTITY_NAME, "invalidseattype");
        }
    }

    private Ticket findOwnedTicket(Long ticketId) {
        Ticket ticket = ticketRepository
            .findByIdWithFullDetails(ticketId)
            .orElseThrow(() -> new BadRequestAlertException("Ticket not found", ENTITY_NAME, "ticketnotfound"));

        String login = currentLogin();
        if (!ticket.getBookingDetail().getBooking().getUser().getLogin().equals(login)) {
            throw new BadRequestAlertException("You do not own this ticket", ENTITY_NAME, "accessdenied");
        }

        return ticket;
    }

    private String currentLogin() {
        return SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("User not logged in", ENTITY_NAME, "usernotfound")
        );
    }
}
