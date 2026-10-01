package com.dugx.event.service;

import com.dugx.event.domain.*;
import com.dugx.event.repository.*;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.*;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mua them dich vu bo tro cho mot ve da mua: mua sam mien thue, khach san &amp;
 * tour, bao hiem du lich, dich vu khac - thanh toan qua VNPay (tai su dung ha
 * tang Booking/Payment san co, giong SeatUpgradeService/BaggageService).
 */
@Service
@Transactional
public class TicketAddonService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketAddonService.class);
    private static final String ENTITY_NAME = "ticketAddon";

    public static final String SHOPPING = "SHOPPING";
    public static final String HOTEL_TOUR = "HOTEL_TOUR";
    public static final String INSURANCE = "INSURANCE";
    public static final String OTHER_SERVICE = "OTHER_SERVICE";

    private static final Map<String, List<AddonCatalogItemDTO>> CATALOGS = new LinkedHashMap<>();

    static {
        CATALOGS.put(
            SHOPPING,
            List.of(
                new AddonCatalogItemDTO("PERFUME", "Nước hoa Chanel No.5 100ml", BigDecimal.valueOf(2_500_000), 5),
                new AddonCatalogItemDTO("WINE", "Rượu vang Pháp", BigDecimal.valueOf(890_000), 5),
                new AddonCatalogItemDTO("CHOCOLATE", "Socola Bỉ hộp quà", BigDecimal.valueOf(350_000), 5),
                new AddonCatalogItemDTO("WATCH", "Đồng hồ miễn thuế", BigDecimal.valueOf(4_200_000), 5)
            )
        );
        CATALOGS.put(
            HOTEL_TOUR,
            List.of(
                new AddonCatalogItemDTO("HOTEL_3SAO", "Khách sạn 3 sao - 1 đêm", BigDecimal.valueOf(600_000), 10),
                new AddonCatalogItemDTO("HOTEL_4SAO", "Khách sạn 4 sao - 1 đêm", BigDecimal.valueOf(1_200_000), 10),
                new AddonCatalogItemDTO("HOTEL_5SAO", "Khách sạn 5 sao - 1 đêm", BigDecimal.valueOf(2_500_000), 10),
                new AddonCatalogItemDTO("TOUR_HALFDAY", "Tour tham quan nửa ngày", BigDecimal.valueOf(450_000), 10)
            )
        );
        CATALOGS.put(
            INSURANCE,
            List.of(
                new AddonCatalogItemDTO("INS_BASIC", "Bảo hiểm cơ bản", BigDecimal.valueOf(50_000), 1),
                new AddonCatalogItemDTO("INS_ADVANCED", "Bảo hiểm nâng cao", BigDecimal.valueOf(120_000), 1),
                new AddonCatalogItemDTO("INS_FULL", "Bảo hiểm toàn diện", BigDecimal.valueOf(250_000), 1)
            )
        );
        CATALOGS.put(
            OTHER_SERVICE,
            List.of(
                new AddonCatalogItemDTO("PRIORITY_CHECKIN", "Ưu tiên làm thủ tục", BigDecimal.valueOf(80_000), 1),
                new AddonCatalogItemDTO("LOUNGE", "Phòng chờ hạng thương gia", BigDecimal.valueOf(350_000), 1),
                new AddonCatalogItemDTO("MEAL", "Chọn bữa ăn đặc biệt", BigDecimal.valueOf(150_000), 1),
                new AddonCatalogItemDTO("WIFI", "Wifi trên chuyến bay", BigDecimal.valueOf(100_000), 1)
            )
        );
    }

    private final TicketRepository ticketRepository;
    private final TicketAddonRepository ticketAddonRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public TicketAddonService(
        TicketRepository ticketRepository,
        TicketAddonRepository ticketAddonRepository,
        BookingRepository bookingRepository,
        UserRepository userRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.ticketAddonRepository = ticketAddonRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    private List<AddonCatalogItemDTO> catalogOf(String addonType) {
        List<AddonCatalogItemDTO> catalog = CATALOGS.get(addonType);
        if (catalog == null) {
            throw new BadRequestAlertException("Loại dịch vụ không hợp lệ", ENTITY_NAME, "invalidaddontype");
        }
        return catalog;
    }

    private AddonCatalogItemDTO findCatalogItem(String addonType, String itemCode) {
        return catalogOf(addonType)
            .stream()
            .filter(item -> item.getItemCode().equals(itemCode))
            .findFirst()
            .orElseThrow(() -> new BadRequestAlertException("Mặt hàng không hợp lệ", ENTITY_NAME, "invaliditem"));
    }

    @Transactional(readOnly = true)
    public TicketAddonInfoDTO getInfo(Long ticketId, String addonType) {
        Ticket ticket = findOwnedTicket(ticketId);
        Showtime showtime = ticket.getBookingDetail().getShowtimeSeat().getShowtime();

        TicketAddonInfoDTO dto = new TicketAddonInfoDTO();
        dto.setTicketId(ticketId);
        dto.setAddonType(addonType);
        dto.setCatalog(catalogOf(addonType));

        List<TicketAddon> paid = ticketAddonRepository.findPaidByTicket_IdAndAddonType(ticketId, addonType);
        dto.setPurchased(paid.stream().map(this::toPurchasedDto).toList());

        String blockedReason = blockedReason(ticket, showtime);
        if (blockedReason != null) {
            dto.setPurchasable(false);
            dto.setReason(blockedReason);
        } else {
            dto.setPurchasable(true);
        }

        return dto;
    }

    @Transactional
    public AddonPurchaseResponseDTO requestPurchase(Long ticketId, String addonType, List<AddonCartItemDTO> items) {
        Ticket ticket = findOwnedTicket(ticketId);
        Showtime showtime = ticket.getBookingDetail().getShowtimeSeat().getShowtime();

        String blockedReason = blockedReason(ticket, showtime);
        if (blockedReason != null) {
            throw new BadRequestAlertException(blockedReason, ENTITY_NAME, "notpurchasable");
        }

        if (items == null || items.isEmpty()) {
            throw new BadRequestAlertException("Giỏ hàng trống", ENTITY_NAME, "emptycart");
        }

        List<TicketAddon> lines = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (AddonCartItemDTO item : items) {
            AddonCatalogItemDTO catalogItem = findCatalogItem(addonType, item.getItemCode());
            int quantity = item.getQuantity() == null ? 1 : item.getQuantity();

            if (quantity <= 0 || quantity > catalogItem.getMaxQuantity()) {
                throw new BadRequestAlertException(
                    "Số lượng không hợp lệ cho " + catalogItem.getItemLabel(),
                    ENTITY_NAME,
                    "invalidquantity"
                );
            }

            BigDecimal lineTotal = catalogItem.getUnitPrice().multiply(BigDecimal.valueOf(quantity));
            total = total.add(lineTotal);

            TicketAddon line = new TicketAddon();
            line.setTicket(ticket);
            line.setAddonType(addonType);
            line.setItemCode(catalogItem.getItemCode());
            line.setItemLabel(catalogItem.getItemLabel());
            line.setUnitPrice(catalogItem.getUnitPrice());
            line.setQuantity(quantity);
            line.setTotalPrice(lineTotal);
            line.setStatus("PENDING");
            line.setCreatedDate(Instant.now());
            lines.add(line);
        }

        User user = userRepository
            .findOneByLogin(currentLogin())
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "usernotfound"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBookingDate(Instant.now());
        booking.setStatus("PENDING");
        booking.setTotalAmount(total);
        booking = bookingRepository.save(booking);

        for (TicketAddon line : lines) {
            line.setBooking(booking);
            ticketAddonRepository.save(line);
        }

        LOG.debug("Created {} addon line(s) [{}] for ticket {} -> booking {}", lines.size(), addonType, ticketId, booking.getId());

        return new AddonPurchaseResponseDTO(booking.getId(), total);
    }

    /**
     * Goi tu BookingService khi mot booking duoc xac nhan da thanh toan.
     */
    @Transactional
    public boolean completePurchaseIfApplicable(Long bookingId) {
        List<TicketAddon> lines = ticketAddonRepository.findByBooking_Id(bookingId);
        if (lines.isEmpty()) {
            return false;
        }

        for (TicketAddon line : lines) {
            if (!"PAID".equals(line.getStatus())) {
                line.setStatus("PAID");
                ticketAddonRepository.save(line);
            }
        }

        return true;
    }

    /** Goi tu BookingService khi don mua dich vu bo tro bi huy/thanh toan that bai. */
    @Transactional
    public boolean cancelPurchaseIfApplicable(Long bookingId) {
        List<TicketAddon> lines = ticketAddonRepository.findByBooking_Id(bookingId);
        if (lines.isEmpty()) {
            return false;
        }

        for (TicketAddon line : lines) {
            if ("PENDING".equals(line.getStatus())) {
                line.setStatus("CANCELLED");
                ticketAddonRepository.save(line);
            }
        }

        return true;
    }

    private String blockedReason(Ticket ticket, Showtime showtime) {
        if (Boolean.TRUE.equals(ticket.getCheckedIn())) {
            return "Vé đã check-in, không thể mua thêm dịch vụ.";
        }
        if ("CANCELLED".equals(ticket.getStatus())) {
            return "Vé đã bị huỷ, không thể mua thêm dịch vụ.";
        }
        if (showtime.getStartTime() != null && Instant.now().isAfter(showtime.getStartTime())) {
            return "Chuyến bay đã khởi hành, không thể mua thêm dịch vụ.";
        }
        return null;
    }

    private PurchasedAddonDTO toPurchasedDto(TicketAddon line) {
        return new PurchasedAddonDTO(
            line.getId(),
            line.getItemCode(),
            line.getItemLabel(),
            line.getQuantity(),
            line.getTotalPrice(),
            line.getStatus()
        );
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
