package com.dugx.event.service;

import com.dugx.event.domain.*;
import com.dugx.event.repository.*;
import com.dugx.event.repository.CouponRepository;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.BookingDTO;
import com.dugx.event.service.dto.BookingRequest;
import com.dugx.event.service.dto.CustomerBookingDTO;
import com.dugx.event.service.dto.LegRequest;
import com.dugx.event.service.mapper.BookingMapper;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.Booking}.
 */
@Service
@Transactional
public class BookingService {

    private static final Logger LOG = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    private final ShowtimeRepository showtimeRepository;

    private final ShowtimeSeatRepository showtimeSeatRepository;

    private final BookingDetailRepository bookingDetailRepository;

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final CouponRepository couponRepository;
    private final SeatUpgradeService seatUpgradeService;
    private final BaggageService baggageService;
    private final TicketAddonService ticketAddonService;
    private final LoyaltyService loyaltyService;
    private final BaggagePurchaseRepository baggagePurchaseRepository;
    private final SeatUpgradeRepository seatUpgradeRepository;
    private final TicketAddonRepository ticketAddonRepository;
    private final PaymentRepository paymentRepository;

    public BookingService(
        BookingRepository bookingRepository,
        BookingMapper bookingMapper,
        ShowtimeRepository showtimeRepository,
        ShowtimeSeatRepository showtimeSeatRepository,
        BookingDetailRepository bookingDetailRepository,
        UserRepository userRepository,
        TicketRepository ticketRepository,
        CouponRepository couponRepository,
        SeatUpgradeService seatUpgradeService,
        BaggageService baggageService,
        TicketAddonService ticketAddonService,
        LoyaltyService loyaltyService,
        BaggagePurchaseRepository baggagePurchaseRepository,
        SeatUpgradeRepository seatUpgradeRepository,
        TicketAddonRepository ticketAddonRepository,
        PaymentRepository paymentRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.showtimeRepository = showtimeRepository;
        this.showtimeSeatRepository = showtimeSeatRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.couponRepository = couponRepository;
        this.baggagePurchaseRepository = baggagePurchaseRepository;
        this.seatUpgradeRepository = seatUpgradeRepository;
        this.ticketAddonRepository = ticketAddonRepository;
        this.paymentRepository = paymentRepository;
        this.seatUpgradeService = seatUpgradeService;
        this.baggageService = baggageService;
        this.ticketAddonService = ticketAddonService;
        this.loyaltyService = loyaltyService;
    }

    /**
     * Save a booking.
     *
     * @param bookingDTO the entity to save.
     * @return the persisted entity.
     */
    public BookingDTO save(BookingDTO bookingDTO) {
        LOG.debug("Request to save Booking : {}", bookingDTO);
        Booking booking = bookingMapper.toEntity(bookingDTO);
        booking = bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    /**
     * Update a booking.
     *
     * @param bookingDTO the entity to save.
     * @return the persisted entity.
     */
    public BookingDTO update(BookingDTO bookingDTO) {
        LOG.debug("Request to update Booking : {}", bookingDTO);
        Booking booking = bookingMapper.toEntity(bookingDTO);
        booking = bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    /**
     * Partially update a booking.
     *
     * @param bookingDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BookingDTO> partialUpdate(BookingDTO bookingDTO) {
        LOG.debug("Request to partially update Booking : {}", bookingDTO);

        return bookingRepository
            .findById(bookingDTO.getId())
            .map(existingBooking -> {
                bookingMapper.partialUpdate(existingBooking, bookingDTO);

                return existingBooking;
            })
            .map(bookingRepository::save)
            .map(bookingMapper::toDto);
    }

    /**
     * Get all the bookings with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<BookingDTO> findAllWithEagerRelationships(Pageable pageable) {
        return bookingRepository.findAllWithEagerRelationships(pageable).map(bookingMapper::toDto);
    }

    /**
     * Get one booking by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BookingDTO> findOne(Long id) {
        LOG.debug("Request to get Booking : {}", id);
        return bookingRepository.findOneWithEagerRelationships(id).map(bookingMapper::toDto);
    }

    /**
     * Delete the booking by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Booking : {}", id);
        bookingRepository.deleteById(id);
    }

    public enum BookingStatus {
        /** Da giu cho, dang cho thanh toan. */
        PENDING,
        /** Da thanh toan thanh cong, ve da duoc sinh ra. */
        PAID,
        /** Thanh toan that bai hoac nguoi dung huy, ghe da duoc tra lai. */
        CANCELLED,
    }

    /**
     * Dat ve. Mot chieu chi co 1 leg trong request, khu hoi/nhieu chang co nhieu leg. Khoa
     * ghe cua TAT CA cac leg trong MOT lan goi de tranh deadlock giua cac chang, sau do
     * validate/tinh gia tung leg rieng. Ma coupon (neu co) chi ap dung cho leg dau tien co
     * Event khop voi coupon, cac leg con lai tinh gia goc.
     */
    @Transactional
    public BookingDTO book(BookingRequest request) {
        List<LegRequest> legs = request.getLegs();
        if (legs == null || legs.isEmpty()) {
            throw new BadRequestAlertException("At least one leg is required", "booking", "invalidlegs");
        }

        List<Showtime> showtimes = new ArrayList<>();
        List<Long> allSeatIds = new ArrayList<>();
        for (LegRequest leg : legs) {
            showtimes.add(validateShowtimeForLeg(leg));
            allSeatIds.addAll(leg.getSeatIds());
        }

        List<ShowtimeSeat> lockedSeats = showtimeSeatRepository.findAllByIdInForUpdate(allSeatIds);
        if (lockedSeats.size() != allSeatIds.size()) {
            throw new BadRequestAlertException("Some seats were not found", "booking", "seatnotfound");
        }
        Map<Long, ShowtimeSeat> seatsById = new HashMap<>();
        for (ShowtimeSeat seat : lockedSeats) {
            seatsById.put(seat.getId(), seat);
        }

        List<List<ShowtimeSeat>> seatsPerLeg = new ArrayList<>();
        List<Map<Long, Integer>> baggagePerLeg = new ArrayList<>();
        for (int i = 0; i < legs.size(); i++) {
            seatsPerLeg.add(resolveLegSeats(legs.get(i), showtimes.get(i), seatsById));
            baggagePerLeg.add(validateBaggageSelection(legs.get(i)));
        }

        Coupon coupon = null;
        Integer couponLegIndex = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            coupon = validateCoupon(request.getCouponCode());
            if (coupon.getEvent() == null) {
                // Ma khong gan voi Event cu the (vd doi tu diem Lotusmiles) - ap dung cho chang dau tien.
                couponLegIndex = 0;
            } else {
                for (int i = 0; i < showtimes.size(); i++) {
                    if (coupon.getEvent().getId().equals(showtimes.get(i).getEvent().getId())) {
                        couponLegIndex = i;
                        break;
                    }
                }
                if (couponLegIndex == null) {
                    throw new BadRequestAlertException("Coupon does not belong to this event", "booking", "coupon_invalid");
                }
            }
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (int i = 0; i < legs.size(); i++) {
            BigDecimal legTicketTotal = calculatePrice(seatsPerLeg.get(i));
            if (couponLegIndex != null && couponLegIndex == i) {
                legTicketTotal = applyCouponDiscount(coupon, legTicketTotal);
            }
            totalAmount = totalAmount.add(legTicketTotal).add(calculateBaggageTotal(seatsPerLeg.get(i), baggagePerLeg.get(i)));
        }

        User user = getCurrentUser();
        Booking booking = createBooking(user, totalAmount);

        for (int i = 0; i < legs.size(); i++) {
            Map<Long, Integer> baggageBySeat = baggagePerLeg.get(i);
            for (ShowtimeSeat seat : seatsPerLeg.get(i)) {
                seat.setStatus(SeatStatus.HELD);
                showtimeSeatRepository.save(seat);
                createBookingDetail(booking, seat, baggageBySeat.get(seat.getId()), i);
            }
        }

        if (coupon != null) {
            coupon.setQuantity(coupon.getQuantity() - 1);
            couponRepository.save(coupon);
        }

        // Chi giu cho o buoc nay. Ve (Ticket + QR) chi duoc sinh ra
        // sau khi thanh toan thanh cong - xem confirmPaidBooking().
        LOG.debug("Created pending booking {} with {} leg(s)", booking.getId(), legs.size());

        return bookingMapper.toDto(booking);
    }

    /**
     * Xac nhan mot booking da thanh toan thanh cong: chuyen trang thai sang PAID
     * va sinh ra tung ve kem ma QR.
     *
     * Ham nay idempotent - VNPay co the goi ca return-url lan IPN cho cung mot
     * giao dich, nen goi lai lan hai se khong sinh trung ve.
     *
     * @param bookingId id cua booking.
     * @return booking sau khi cap nhat.
     */
    @Transactional
    public Booking confirmPaidBooking(Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);

        if (BookingStatus.PAID.name().equals(booking.getStatus())) {
            LOG.debug("Booking {} already confirmed, skipping", bookingId);
            return booking;
        }

        if (BookingStatus.CANCELLED.name().equals(booking.getStatus())) {
            throw new BadRequestAlertException("Booking has been cancelled", "booking", "bookingcancelled");
        }

        // Booking nang hang / mua hanh ly khong co booking_detail rieng (khong
        // mua ve moi ma doi/bo sung cho ve da co) - xu ly rieng, vong lap ben
        // duoi se khong chay cho cac booking nay.
        seatUpgradeService.completeUpgradeIfApplicable(bookingId);
        baggageService.completePurchaseIfApplicable(bookingId);
        ticketAddonService.completePurchaseIfApplicable(bookingId);

        for (BookingDetail detail : bookingDetailRepository.findByBooking_Id(bookingId)) {
            Ticket ticket = createTicket(detail);

            if (detail.getExtraBaggageKg() != null && detail.getExtraBaggageKg() > 0) {
                baggageService.recordPaidBaggage(ticket, detail.getExtraBaggageKg(), booking);
            }

            ShowtimeSeat seat = detail.getShowtimeSeat();

            if (seat != null) {
                seat.setStatus(SeatStatus.BOOKED);
                showtimeSeatRepository.save(seat);
            }
        }

        booking.setStatus(BookingStatus.PAID.name());
        Booking savedBooking = bookingRepository.save(booking);

        // Tich diem Lotusmiles: 1 diem / 10.000d, chi cong 1 lan nho guard idempotent o tren.
        int earnedPoints = loyaltyService.pointsForAmount(savedBooking.getTotalAmount());
        loyaltyService.awardPoints(savedBooking.getUser(), earnedPoints, "Tích điểm chuyến bay #" + savedBooking.getId(), savedBooking);

        return savedBooking;
    }

    /**
     * Huy booking chua thanh toan va tra lai ghe da giu cho.
     *
     * @param bookingId id cua booking.
     * @return booking sau khi cap nhat.
     */
    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);

        if (BookingStatus.PAID.name().equals(booking.getStatus())) {
            throw new BadRequestAlertException("Cannot cancel a paid booking", "booking", "bookingalreadypaid");
        }

        if (BookingStatus.CANCELLED.name().equals(booking.getStatus())) {
            return booking;
        }

        seatUpgradeService.cancelUpgradeIfApplicable(bookingId);
        baggageService.cancelPurchaseIfApplicable(bookingId);
        ticketAddonService.cancelPurchaseIfApplicable(bookingId);

        // Tra lai ghe da giu cho.
        for (BookingDetail detail : bookingDetailRepository.findByBooking_Id(bookingId)) {
            ShowtimeSeat seat = detail.getShowtimeSeat();

            if (seat == null) {
                continue;
            }

            seat.setStatus(SeatStatus.AVAILABLE);
            showtimeSeatRepository.save(seat);
        }

        booking.setStatus(BookingStatus.CANCELLED.name());

        LOG.debug("Cancelled booking {} and released seats", bookingId);

        return bookingRepository.save(booking);
    }

    /** Lay booking kem quyen so huu cua nguoi dung hien tai. */
    @Transactional(readOnly = true)
    public Booking findOwnedBooking(Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);
        User user = getCurrentUser();

        if (booking.getUser() == null || !booking.getUser().getId().equals(user.getId())) {
            throw new BadRequestAlertException("Access denied", "booking", "accessdenied");
        }
        return booking;
    }

    /** Nhu findOwnedBooking, nhung tra ve DTO cho trang ket qua thanh toan cua khach hang. */
    @Transactional(readOnly = true)
    public BookingDTO findOwnedBookingDto(Long bookingId) {
        return bookingMapper.toDto(findOwnedBooking(bookingId));
    }

    private Booking findBookingOrThrow(Long bookingId) {
        return bookingRepository
            .findById(bookingId)
            .orElseThrow(() -> new BadRequestAlertException("Booking not found", "booking", "bookingnotfound"));
    }

    @Transactional(readOnly = true)
    public Page<BookingDTO> getMyBookings(Pageable pageable) {
        LOG.debug("Request to get current user bookings");

        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("User not logged in", "booking", "usernotfound")
        );

        return bookingRepository.findByUserLoginWithToOneRelationships(login, pageable).map(bookingMapper::toDto);
    }

    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter LEG_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM HH:mm").withZone(VN_ZONE);

    /**
     * Danh sach khach hang da dat ve (danh cho man hinh quan ly cua admin).
     *
     * Truy van goc tra ve mot dong cho moi (booking, showtime) - mot booking khu hoi/nhieu
     * chang se co nhieu dong. Gop lai o day thanh mot dong duy nhat cho moi booking, kem
     * mo ta tung chang trong legSummary, de trang admin khong bi hien thi trung booking.
     *
     * Ngoai ra gop them cac booking mua them (hanh ly / nang hang ghe / dich vu bo tro) -
     * cac booking nay khong co BookingDetail nen khong xuat hien trong truy van tren, phai
     * lay rieng tu 3 bang tuong ung roi gop chung vao mot danh sach duy nhat.
     */
    @Transactional(readOnly = true)
    public List<CustomerBookingDTO> getCustomerBookings() {
        LOG.debug("Request to get customer booking list");

        List<CustomerBookingDTO> legRows = bookingDetailRepository.findAllCustomerBookings();
        Map<Long, CustomerBookingDTO> merged = new LinkedHashMap<>();

        for (CustomerBookingDTO row : legRows) {
            row.setLegSummary(formatLeg(row));
            merged.merge(row.getBookingId(), row, CustomerBookingDTO::mergeLeg);
        }

        List<CustomerBookingDTO> allRows = new ArrayList<>(merged.values());
        allRows.addAll(buildBaggageRows());
        allRows.addAll(buildSeatUpgradeRows());
        allRows.addAll(buildAddonRows());

        attachPaymentDates(allRows);

        allRows.sort(Comparator.comparing(CustomerBookingDTO::getBookingDate, Comparator.nullsLast(Comparator.reverseOrder())));

        return allRows;
    }

    private String formatLeg(CustomerBookingDTO row) {
        String from = row.getDepartureAirportCode() != null ? row.getDepartureAirportCode() : "?";
        String to = row.getArrivalAirportCode() != null ? row.getArrivalAirportCode() : "?";
        String time = row.getShowtimeStart() != null ? LEG_TIME_FORMAT.format(row.getShowtimeStart()) : null;
        return time != null ? from + " → " + to + " (" + time + ")" : from + " → " + to;
    }

    /** Dung chung cho 3 loai booking mua them: dung cung mot khung CustomerBookingDTO nhu booking dat ve moi. */
    private CustomerBookingDTO buildAddonBaseDto(Booking booking, Event event, Instant showtimeStart) {
        User user = booking.getUser();
        return new CustomerBookingDTO(
            booking.getId(),
            booking.getBookingDate(),
            booking.getTotalAmount(),
            booking.getStatus(),
            user.getId(),
            user.getLogin(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            event.getTitle(),
            event.getDepartureAirport() != null ? event.getDepartureAirport().getCode() : null,
            event.getArrivalAirport() != null ? event.getArrivalAirport().getCode() : null,
            showtimeStart,
            1L
        );
    }

    private List<CustomerBookingDTO> buildBaggageRows() {
        List<CustomerBookingDTO> rows = new ArrayList<>();

        for (BaggagePurchase bp : baggagePurchaseRepository.findAllWithDetailsForAdmin()) {
            Ticket ticket = bp.getTicket();
            Showtime showtime = ticket.getBookingDetail().getShowtimeSeat().getShowtime();
            CustomerBookingDTO dto = buildAddonBaseDto(bp.getBooking(), showtime.getEvent(), showtime.getStartTime());
            dto.setBookingType("BAGGAGE");
            dto.setLegSummary("🧳 Hành lý +" + bp.getWeightKg() + "kg cho vé " + ticket.getQrCode());
            rows.add(dto);
        }

        return rows;
    }

    private List<CustomerBookingDTO> buildSeatUpgradeRows() {
        List<CustomerBookingDTO> rows = new ArrayList<>();

        for (SeatUpgrade su : seatUpgradeRepository.findAllWithDetailsForAdmin()) {
            Ticket ticket = su.getTicket();
            Showtime showtime = ticket.getBookingDetail().getShowtimeSeat().getShowtime();
            CustomerBookingDTO dto = buildAddonBaseDto(su.getBooking(), showtime.getEvent(), showtime.getStartTime());
            dto.setBookingType("SEAT_UPGRADE");
            String newTypeLabel = seatTypeLabel(su.getNewShowtimeSeat().getSeat());
            dto.setLegSummary("💺 Nâng hạng " + newTypeLabel + " cho vé " + ticket.getQrCode());
            rows.add(dto);
        }

        return rows;
    }

    private List<CustomerBookingDTO> buildAddonRows() {
        Map<Long, List<TicketAddon>> byBooking = new LinkedHashMap<>();

        for (TicketAddon ta : ticketAddonRepository.findAllWithDetailsForAdmin()) {
            byBooking.computeIfAbsent(ta.getBooking().getId(), key -> new ArrayList<>()).add(ta);
        }

        List<CustomerBookingDTO> rows = new ArrayList<>();

        for (List<TicketAddon> lines : byBooking.values()) {
            TicketAddon first = lines.get(0);
            Ticket ticket = first.getTicket();
            Showtime showtime = ticket.getBookingDetail().getShowtimeSeat().getShowtime();
            CustomerBookingDTO dto = buildAddonBaseDto(first.getBooking(), showtime.getEvent(), showtime.getStartTime());
            dto.setBookingType("ADDON");
            String items = lines
                .stream()
                .map(line -> line.getItemLabel() + " x" + line.getQuantity())
                .collect(Collectors.joining(", "));
            dto.setLegSummary("🛍️ " + items + " cho vé " + ticket.getQrCode());
            rows.add(dto);
        }

        return rows;
    }

    private String seatTypeLabel(Seat seat) {
        if (seat == null || seat.getSeatType() == null) {
            return "ghế mới";
        }
        return switch (seat.getSeatType()) {
            case VIP -> "Thương gia";
            case COUPLE -> "Hạng nhất";
            default -> "Phổ thông";
        };
    }

    private void attachPaymentDates(List<CustomerBookingDTO> rows) {
        List<Long> bookingIds = rows.stream().map(CustomerBookingDTO::getBookingId).distinct().toList();

        if (bookingIds.isEmpty()) {
            return;
        }

        Map<Long, Instant> paymentDates = paymentRepository
            .findSuccessPaymentDatesByBookingIds(bookingIds)
            .stream()
            .collect(
                Collectors.toMap(PaymentRepository.BookingPaymentDate::getBookingId, PaymentRepository.BookingPaymentDate::getPaymentDate)
            );

        for (CustomerBookingDTO row : rows) {
            row.setPaymentDate(paymentDates.get(row.getBookingId()));
        }
    }

    private Showtime validateShowtimeForLeg(LegRequest leg) {
        if (leg.getSeatIds() == null || leg.getSeatIds().isEmpty()) {
            throw new BadRequestAlertException("Seat selection is required", "booking", "invalidseats");
        }
        if (leg.getShowtimeId() == null) {
            throw new BadRequestAlertException("Showtime is required", "booking", "invalidshowtime");
        }

        Showtime showtime = showtimeRepository
            .findById(leg.getShowtimeId())
            .orElseThrow(() -> new BadRequestAlertException("Showtime not found", "booking", "showtimenotfound"));

        if (showtime.getStartTime() != null && Instant.now().isAfter(showtime.getStartTime())) {
            throw new BadRequestAlertException("Showtime has already started", "booking", "showtime_started");
        }

        return showtime;
    }

    /** Lay ghe da khoa cho tung leg tu map ghe da khoa chung, kiem tra ghe dung khop showtime cua chinh leg do. */
    private List<ShowtimeSeat> resolveLegSeats(LegRequest leg, Showtime showtime, Map<Long, ShowtimeSeat> lockedSeatsById) {
        List<ShowtimeSeat> legSeats = new ArrayList<>();

        for (Long seatId : leg.getSeatIds()) {
            ShowtimeSeat seat = lockedSeatsById.get(seatId);
            if (seat == null) {
                throw new BadRequestAlertException("Some seats were not found", "booking", "seatnotfound");
            }
            if (seat.getShowtime() == null || !seat.getShowtime().getId().equals(showtime.getId())) {
                throw new BadRequestAlertException("Seat does not belong to this showtime", "booking", "seatmismatch");
            }
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new BadRequestAlertException("Seat is no longer available", "booking", "seatunavailable");
            }
            legSeats.add(seat);
        }

        return legSeats;
    }

    private User getCurrentUser() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("User not logged in", "booking", "usernotfound")
        );
        return userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("User not found", "booking", "usernotfound"));
    }

    private BigDecimal calculatePrice(List<ShowtimeSeat> seats) {
        return seats
            .stream()
            .map(ShowtimeSeat::getPrice)
            .filter(p -> p != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Booking createBooking(User user, BigDecimal totalAmount) {
        Booking booking = new Booking();

        booking.setUser(user);

        booking.setBookingDate(Instant.now());

        booking.setStatus(BookingStatus.PENDING.name());

        booking.setTotalAmount(totalAmount);

        return bookingRepository.save(booking);
    }

    private BookingDetail createBookingDetail(Booking booking, ShowtimeSeat seat, Integer extraBaggageKg, int legIndex) {
        BookingDetail detail = new BookingDetail();

        detail.setBooking(booking);
        detail.setShowtimeSeat(seat);
        detail.setPrice(seat.getPrice());
        detail.setExtraBaggageKg(extraBaggageKg);
        detail.setLegIndex(legIndex);

        return bookingDetailRepository.save(detail);
    }

    /** Kiem tra tung lua chon hanh ly (neu co) cua mot leg la mot goi hop le trong bang gia. */
    private Map<Long, Integer> validateBaggageSelection(LegRequest leg) {
        Map<Long, Integer> baggageBySeat = leg.getBaggageBySeat();
        if (baggageBySeat == null) {
            return new HashMap<>();
        }

        for (Map.Entry<Long, Integer> entry : baggageBySeat.entrySet()) {
            if (entry.getValue() != null && entry.getValue() > 0 && baggageService.priceFor(entry.getValue()) == null) {
                throw new BadRequestAlertException("Gói hành lý không hợp lệ", "booking", "invalidbaggage");
            }
        }

        return baggageBySeat;
    }

    private BigDecimal calculateBaggageTotal(List<ShowtimeSeat> seats, Map<Long, Integer> baggageBySeat) {
        BigDecimal total = BigDecimal.ZERO;
        for (ShowtimeSeat seat : seats) {
            BigDecimal price = baggageService.priceFor(baggageBySeat.get(seat.getId()));
            if (price != null) {
                total = total.add(price);
            }
        }
        return total;
    }

    private static final String TICKET_NUMBER_ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"; // bo 0/1/O/I de tranh nham lan
    private static final SecureRandom TICKET_NUMBER_RANDOM = new SecureRandom();

    /**
     * Sinh so ve de nguoi hien thi/nhap tay (thay the QR code truoc day). Dung bookingDetail.id
     * (da la khoa chinh duy nhat toan he thong) de dam bao khong trung, cong them hau to ngau
     * nhien 4 ky tu de tranh bi do/liet ke ve cua nguoi khac qua endpoint tra cuu theo ma ve.
     */
    private String generateTicketNumber(BookingDetail bookingDetail) {
        StringBuilder suffix = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            suffix.append(TICKET_NUMBER_ALPHABET.charAt(TICKET_NUMBER_RANDOM.nextInt(TICKET_NUMBER_ALPHABET.length())));
        }
        return "VN-" + bookingDetail.getId() + "-" + suffix;
    }

    private Ticket createTicket(BookingDetail bookingDetail) {
        Ticket ticket = new Ticket();

        ticket.setBookingDetail(bookingDetail); // BẮT BUỘC
        ticket.setQrCode(generateTicketNumber(bookingDetail));
        ticket.setStatus("ACTIVE");
        ticket.setCheckedIn(false);

        return ticketRepository.save(ticket);
    }

    /** Kiem tra ma coupon con hop le (con han, con so luong). Khong kiem tra khop Event - viec do
     * lam o book() vi coupon co the khop bat ky leg nao trong nhieu leg. Khong tru so luong o day -
     * chi tru dung 1 lan sau khi da xac dinh chac chan leg nao dung coupon. */
    private Coupon validateCoupon(String couponCode) {
        Coupon coupon = couponRepository
            .findByCode(couponCode)
            .orElseThrow(() -> new BadRequestAlertException("Coupon not found", "booking", "couponnotfound"));

        Instant now = Instant.now();

        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            throw new BadRequestAlertException("Coupon not started", "booking", "coupon_not_started");
        }

        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            throw new BadRequestAlertException("Coupon expired", "booking", "coupon_expired");
        }

        if (coupon.getQuantity() == null || coupon.getQuantity() <= 0) {
            throw new BadRequestAlertException("Coupon out of stock", "booking", "coupon_empty");
        }

        return coupon;
    }

    private BigDecimal applyCouponDiscount(Coupon coupon, BigDecimal total) {
        BigDecimal finalPrice = total.subtract(coupon.getDiscount());

        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        return finalPrice;
    }
}
