package com.dugx.event.service;

import com.dugx.event.domain.*;
import com.dugx.event.repository.*;
import com.dugx.event.repository.CouponRepository;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.BookingDTO;
import com.dugx.event.service.dto.BookingRequest;
import com.dugx.event.service.mapper.BookingMapper;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
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

    private final TicketTypeRepository ticketTypeRepository;

    private final BookingDetailRepository bookingDetailRepository;

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final CouponRepository couponRepository;

    public BookingService(
        BookingRepository bookingRepository,
        BookingMapper bookingMapper,
        TicketTypeRepository ticketTypeRepository,
        BookingDetailRepository bookingDetailRepository,
        UserRepository userRepository,
        TicketRepository ticketRepository,
        CouponRepository couponRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.ticketTypeRepository = ticketTypeRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.couponRepository = couponRepository;
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
        /** Thanh toan that bai hoac nguoi dung huy, kho ve da duoc hoan lai. */
        CANCELLED,
    }

    @Transactional
    public BookingDTO book(BookingRequest request) {
        TicketType ticket = validateTicket(request);
        User user = getCurrentUser();
        BigDecimal finalPrice = calculatePrice(ticket, request);
        finalPrice = applyCoupon(request, ticket, finalPrice);
        Booking booking = createBooking(user, finalPrice);
        BookingDetail bookingDetail = createBookingDetail(booking, ticket, request, finalPrice);
        // Chi giu cho o buoc nay. Ve (Ticket + QR) chi duoc sinh ra
        // sau khi thanh toan thanh cong - xem confirmPaidBooking().
        updateRemaining(ticket, request);

        LOG.debug("Created pending booking {} with detail {}", booking.getId(), bookingDetail.getId());

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

        for (BookingDetail detail : bookingDetailRepository.findByBooking_Id(bookingId)) {
            int quantity = detail.getQuantity() == null ? 0 : detail.getQuantity();

            for (int i = 0; i < quantity; i++) {
                createTicket(detail);
            }
        }

        booking.setStatus(BookingStatus.PAID.name());

        return bookingRepository.save(booking);
    }

    /**
     * Huy booking chua thanh toan va tra lai so ve da giu cho vao kho.
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

        // Hoan lai kho ve da giu cho.
        for (BookingDetail detail : bookingDetailRepository.findByBooking_Id(bookingId)) {
            TicketType ticketType = detail.getTicketType();

            if (ticketType == null || detail.getQuantity() == null) {
                continue;
            }

            ticketType.setRemaining(ticketType.getRemaining() + detail.getQuantity());
            ticketTypeRepository.save(ticketType);
        }

        booking.setStatus(BookingStatus.CANCELLED.name());

        LOG.debug("Cancelled booking {} and restored stock", bookingId);

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

        return bookingRepository.findByUser_Login(login, pageable).map(bookingMapper::toDto);
    }

    private TicketType validateTicket(BookingRequest request) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BadRequestAlertException("Quantity must be greater than 0", "booking", "invalidquantity");
        }
        TicketType ticket = ticketTypeRepository
            .findById(request.getTicketTypeId())
            .orElseThrow(() -> new BadRequestAlertException("Ticket not found", "booking", "ticketnotfound"));
        Instant now = Instant.now();

        if (ticket.getSaleStart() != null && now.isBefore(ticket.getSaleStart())) {
            throw new BadRequestAlertException("Ticket sale has not started", "booking", "sale_not_started");
        }
        if (ticket.getSaleEnd() != null && now.isAfter(ticket.getSaleEnd())) {
            throw new BadRequestAlertException("Ticket sale has ended", "booking", "sale_ended");
        }
        if (ticket.getRemaining() < request.getQuantity()) {
            throw new BadRequestAlertException("Not enough tickets", "booking", "notenoughtickets");
        }
        return ticket;
    }

    private User getCurrentUser() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("User not logged in", "booking", "usernotfound")
        );
        return userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("User not found", "booking", "usernotfound"));
    }

    private BigDecimal calculatePrice(TicketType ticket, BookingRequest request) {
        return ticket.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
    }

    private Booking createBooking(User user, BigDecimal totalAmount) {
        Booking booking = new Booking();

        booking.setUser(user);

        booking.setBookingDate(Instant.now());

        booking.setStatus(BookingStatus.PENDING.name());

        booking.setTotalAmount(totalAmount);

        return bookingRepository.save(booking);
    }

    private BookingDetail createBookingDetail(Booking booking, TicketType ticket, BookingRequest request, BigDecimal totalPrice) {
        BookingDetail detail = new BookingDetail();

        detail.setBooking(booking);
        detail.setTicketType(ticket);
        detail.setQuantity(request.getQuantity());
        detail.setPrice(totalPrice);

        return bookingDetailRepository.save(detail);
    }

    private void createTicket(BookingDetail bookingDetail) {
        Ticket ticket = new Ticket();

        ticket.setBookingDetail(bookingDetail); // BẮT BUỘC
        ticket.setQrCode(UUID.randomUUID().toString());
        ticket.setStatus("ACTIVE");
        ticket.setCheckedIn(false);

        ticketRepository.save(ticket);
    }

    private void updateRemaining(TicketType ticket, BookingRequest request) {
        ticket.setRemaining(ticket.getRemaining() - request.getQuantity());

        ticketTypeRepository.save(ticket);
    }

    private BigDecimal applyCoupon(BookingRequest request, TicketType ticket, BigDecimal total) {
        if (request.getCouponCode() == null || request.getCouponCode().isBlank()) {
            return total;
        }

        Coupon coupon = couponRepository
            .findByCode(request.getCouponCode())
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

        if (!coupon.getEvent().getId().equals(ticket.getEvent().getId())) {
            throw new BadRequestAlertException("Coupon does not belong to this event", "booking", "coupon_invalid");
        }

        BigDecimal finalPrice = total.subtract(coupon.getDiscount());

        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        coupon.setQuantity(coupon.getQuantity() - 1);

        couponRepository.save(coupon);

        return finalPrice;
    }
}
