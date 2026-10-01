package com.dugx.event.service;

import com.dugx.event.domain.*;
import com.dugx.event.repository.*;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.BaggageOptionDTO;
import com.dugx.event.service.dto.BaggageResponseDTO;
import com.dugx.event.service.dto.PurchasedBaggageDTO;
import com.dugx.event.service.dto.TicketBaggageInfoDTO;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mua them hanh ly ky gui tra truoc cho mot ve da mua, thanh toan phan tien
 * qua VNPay (tai su dung ha tang Booking/Payment san co, xem
 * PaymentService.createVnPayUrl voi bookingId).
 */
@Service
@Transactional
public class BaggageService {

    private static final Logger LOG = LoggerFactory.getLogger(BaggageService.class);
    private static final String ENTITY_NAME = "baggagePurchase";

    /** Bang gia co dinh cac goi hanh ly ky gui trong nuoc. */
    private static final Map<Integer, BigDecimal> CATALOG = new LinkedHashMap<>();

    static {
        CATALOG.put(5, BigDecimal.valueOf(50_000));
        CATALOG.put(10, BigDecimal.valueOf(90_000));
        CATALOG.put(20, BigDecimal.valueOf(160_000));
        CATALOG.put(30, BigDecimal.valueOf(220_000));
    }

    /**
     * Moi hanh khach chi duoc mua toi da tung nay kg hanh ly ky gui tra truoc
     * cho mot ve (gioi han theo quy dinh khai thac, khong the mua tiep tuc
     * khong gioi han).
     */
    public static final int MAX_TOTAL_KG = 30;

    /** Gia cua mot goi hanh ly hop le, hoac null neu khong co trong bang gia. */
    public BigDecimal priceFor(Integer weightKg) {
        if (weightKg == null || weightKg == 0) {
            return BigDecimal.ZERO;
        }
        return CATALOG.get(weightKg);
    }

    private final TicketRepository ticketRepository;
    private final BaggagePurchaseRepository baggagePurchaseRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public BaggageService(
        TicketRepository ticketRepository,
        BaggagePurchaseRepository baggagePurchaseRepository,
        BookingRepository bookingRepository,
        UserRepository userRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.baggagePurchaseRepository = baggagePurchaseRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public TicketBaggageInfoDTO getBaggageInfo(Long ticketId) {
        Ticket ticket = findOwnedTicket(ticketId);
        Showtime showtime = ticket.getBookingDetail().getShowtimeSeat().getShowtime();

        TicketBaggageInfoDTO dto = new TicketBaggageInfoDTO();
        dto.setTicketId(ticketId);

        List<BaggagePurchase> paid = baggagePurchaseRepository.findPaidByTicket_Id(ticketId);
        int totalKg = paid.stream().mapToInt(BaggagePurchase::getWeightKg).sum();
        dto.setPurchased(paid.stream().map(this::toPurchasedDto).toList());
        dto.setTotalPurchasedKg(totalKg);

        int remainingKg = MAX_TOTAL_KG - totalKg;
        dto.setOptions(
            CATALOG.entrySet()
                .stream()
                .filter(e -> e.getKey() <= remainingKg)
                .map(e -> new BaggageOptionDTO(e.getKey(), e.getValue()))
                .toList()
        );

        String blockedReason = blockedReason(ticket, showtime);
        if (blockedReason != null) {
            dto.setPurchasable(false);
            dto.setReason(blockedReason);
        } else if (dto.getOptions().isEmpty()) {
            dto.setPurchasable(false);
            dto.setReason("Vé đã đạt giới hạn hành lý ký gửi tối đa (" + MAX_TOTAL_KG + "kg), không thể mua thêm.");
        } else {
            dto.setPurchasable(true);
        }

        return dto;
    }

    @Transactional
    public BaggageResponseDTO requestPurchase(Long ticketId, Integer weightKg) {
        Ticket ticket = findOwnedTicket(ticketId);
        Showtime showtime = ticket.getBookingDetail().getShowtimeSeat().getShowtime();

        String blockedReason = blockedReason(ticket, showtime);
        if (blockedReason != null) {
            throw new BadRequestAlertException(blockedReason, ENTITY_NAME, "notpurchasable");
        }

        BigDecimal price = CATALOG.get(weightKg);
        if (price == null) {
            throw new BadRequestAlertException("Gói hành lý không hợp lệ", ENTITY_NAME, "invalidoption");
        }

        int alreadyPurchasedKg = baggagePurchaseRepository
            .findPaidByTicket_Id(ticketId)
            .stream()
            .mapToInt(BaggagePurchase::getWeightKg)
            .sum();

        if (alreadyPurchasedKg + weightKg > MAX_TOTAL_KG) {
            throw new BadRequestAlertException(
                "Vượt quá giới hạn hành lý ký gửi tối đa (" + MAX_TOTAL_KG + "kg) cho vé này",
                ENTITY_NAME,
                "baggagelimitexceeded"
            );
        }

        User user = userRepository
            .findOneByLogin(currentLogin())
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "usernotfound"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBookingDate(Instant.now());
        booking.setStatus("PENDING");
        booking.setTotalAmount(price);
        booking = bookingRepository.save(booking);

        BaggagePurchase purchase = new BaggagePurchase();
        purchase.setTicket(ticket);
        purchase.setWeightKg(weightKg);
        purchase.setPrice(price);
        purchase.setStatus("PENDING");
        purchase.setCreatedDate(Instant.now());
        purchase.setBooking(booking);
        purchase = baggagePurchaseRepository.save(purchase);

        LOG.debug("Created baggage purchase {} for ticket {} -> booking {}", purchase.getId(), ticketId, booking.getId());

        return new BaggageResponseDTO(purchase.getId(), booking.getId(), price);
    }

    /**
     * Ghi nhan hanh ly da duoc tra tien ngay trong don dat ve chinh (khong
     * phai mua them sau nay) - goi tu BookingService.confirmPaidBooking khi
     * ve moi vua duoc sinh ra va co kem yeu cau hanh ly.
     */
    @Transactional
    public void recordPaidBaggage(Ticket ticket, Integer weightKg, Booking booking) {
        if (weightKg == null || weightKg <= 0) {
            return;
        }

        BaggagePurchase purchase = new BaggagePurchase();
        purchase.setTicket(ticket);
        purchase.setWeightKg(weightKg);
        purchase.setPrice(priceFor(weightKg));
        purchase.setStatus("PAID");
        purchase.setCreatedDate(Instant.now());
        purchase.setBooking(booking);
        baggagePurchaseRepository.save(purchase);
    }

    /**
     * Goi tu BookingService khi mot booking duoc xac nhan da thanh toan. Neu
     * booking nay la mot don mua hanh ly (khong phai mua ve moi) thi danh dau
     * da thanh toan va tra ve true de BookingService bo qua luong tao ve
     * thong thuong.
     */
    @Transactional
    public boolean completePurchaseIfApplicable(Long bookingId) {
        Optional<BaggagePurchase> maybePurchase = baggagePurchaseRepository.findByBooking_Id(bookingId);
        if (maybePurchase.isEmpty()) {
            return false;
        }

        BaggagePurchase purchase = maybePurchase.get();
        if (!"PAID".equals(purchase.getStatus())) {
            purchase.setStatus("PAID");
            baggagePurchaseRepository.save(purchase);
        }

        return true;
    }

    /** Goi tu BookingService khi don mua hanh ly bi huy/thanh toan that bai. */
    @Transactional
    public boolean cancelPurchaseIfApplicable(Long bookingId) {
        Optional<BaggagePurchase> maybePurchase = baggagePurchaseRepository.findByBooking_Id(bookingId);
        if (maybePurchase.isEmpty()) {
            return false;
        }

        BaggagePurchase purchase = maybePurchase.get();
        if ("PENDING".equals(purchase.getStatus())) {
            purchase.setStatus("CANCELLED");
            baggagePurchaseRepository.save(purchase);
        }

        return true;
    }

    private String blockedReason(Ticket ticket, Showtime showtime) {
        if (Boolean.TRUE.equals(ticket.getCheckedIn())) {
            return "Vé đã check-in, không thể mua thêm hành lý.";
        }
        if ("CANCELLED".equals(ticket.getStatus())) {
            return "Vé đã bị huỷ, không thể mua thêm hành lý.";
        }
        if (showtime.getStartTime() != null && Instant.now().isAfter(showtime.getStartTime())) {
            return "Chuyến bay đã khởi hành, không thể mua thêm hành lý.";
        }
        return null;
    }

    private PurchasedBaggageDTO toPurchasedDto(BaggagePurchase purchase) {
        return new PurchasedBaggageDTO(purchase.getId(), purchase.getWeightKg(), purchase.getPrice(), purchase.getStatus());
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
