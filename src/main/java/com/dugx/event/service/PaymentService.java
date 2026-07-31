package com.dugx.event.service;

import com.dugx.event.domain.Booking;
import com.dugx.event.domain.Payment;
import com.dugx.event.domain.User;
import com.dugx.event.repository.BookingRepository;
import com.dugx.event.repository.PaymentRepository;
import com.dugx.event.repository.UserRepository;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.BookingDTO;
import com.dugx.event.service.dto.BookingRequest;
import com.dugx.event.service.dto.PaymentDTO;
import com.dugx.event.service.dto.PaymentRequest;
import com.dugx.event.service.dto.VNPayRequestDTO;
import com.dugx.event.service.dto.VNPayResultDTO;
import com.dugx.event.service.mapper.PaymentMapper;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.Payment}.
 */
@Service
@Transactional
public class PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);
    private static final String ENTITY_NAME = "payment";
    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final VNPayService vnPayService;

    /** Phuong thuc thanh toan qua cong VNPay. */
    public static final String METHOD_VNPAY = "VNPAY";

    /** Ve mien phi: khong can di qua cong thanh toan. */
    public static final String METHOD_FREE = "FREE";

    public static final String STATUS_SUCCESS = "SUCCESS";

    public static final String STATUS_FAILED = "FAILED";

    /** Ma "giao dich thanh cong" cua VNPay. */
    private static final String VNPAY_SUCCESS_CODE = "00";

    public PaymentService(
        PaymentRepository paymentRepository,
        PaymentMapper paymentMapper,
        BookingRepository bookingRepository,
        UserRepository userRepository,
        BookingService bookingService,
        VNPayService vnPayService
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.bookingService = bookingService;
        this.vnPayService = vnPayService;
    }

    /**
     * Tao link thanh toan VNPay.
     *
     * Neu request co bookingId thi dung lai booking do, nguoc lai se tao mot
     * booking PENDING moi tu ticketTypeId + quantity.
     *
     * @param request thong tin don hang.
     * @param ipAddress dia chi IP cua nguoi mua, VNPay bat buoc phai co.
     * @return URL de redirect nguoi dung sang VNPay (hoac thang toi trang ket
     *         qua neu don hang tri gia 0 dong).
     */
    @Transactional
    public String createVnPayUrl(VNPayRequestDTO request, String ipAddress) {
        Booking booking = resolveBooking(request);

        BigDecimal amount = booking.getTotalAmount();

        // Ve mien phi hoac coupon giam ve 0 dong: xac nhan luon, khong qua VNPay.
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return confirmFreeBooking(booking);
        }

        return vnPayService.createPaymentUrl(booking, ipAddress);
    }

    /**
     * Xu ly ket qua VNPay tra ve (dung chung cho return-url va IPN).
     *
     * Ham nay idempotent: neu da ghi nhan giao dich nay roi thi tra ve ket qua
     * cu chu khong xu ly lai.
     *
     * @param params toan bo tham so vnp_* nhan duoc.
     * @return ket qua da chuan hoa.
     */
    @Transactional
    public VNPayResultDTO handleVnPayResult(Map<String, String> params) {
        String txnRef = params.get("vnp_TxnRef");
        Long bookingId = vnPayService.parseBookingId(txnRef);

        // Da xu ly truoc do (VNPay goi ca return-url lan IPN) -> tra ve ket qua cu.
        Optional<Payment> existing = paymentRepository.findByTransactionCode(txnRef);

        if (existing.isPresent()) {
            Payment payment = existing.get();
            boolean success = STATUS_SUCCESS.equals(payment.getStatus());

            return new VNPayResultDTO(
                bookingId,
                success,
                success ? VNPAY_SUCCESS_CODE : params.get("vnp_ResponseCode"),
                success ? "Thanh toan thanh cong" : "Thanh toan khong thanh cong",
                txnRef,
                payment.getAmount()
            );
        }

        Booking booking = bookingRepository
            .findById(bookingId)
            .orElseThrow(() -> new BadRequestAlertException("Booking not found", ENTITY_NAME, "bookingnotfound"));

        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");

        boolean success =
            VNPAY_SUCCESS_CODE.equals(responseCode) && (transactionStatus == null || VNPAY_SUCCESS_CODE.equals(transactionStatus));

        // Chong gian lan: so tien VNPay bao phai khop voi so tien cua booking.
        if (success && !isAmountMatching(booking, params.get("vnp_Amount"))) {
            LOG.warn("VNPay amount mismatch for booking {}: received {}", bookingId, params.get("vnp_Amount"));
            success = false;
            responseCode = "04";
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());
        payment.setMethod(METHOD_VNPAY);
        payment.setStatus(success ? STATUS_SUCCESS : STATUS_FAILED);
        payment.setPaymentDate(Instant.now());
        payment.setTransactionCode(txnRef);
        paymentRepository.save(payment);

        if (success) {
            bookingService.confirmPaidBooking(bookingId);
        } else {
            bookingService.cancelBooking(bookingId);
        }

        LOG.debug("Handled VNPay result for booking {}: success={}, code={}", bookingId, success, responseCode);

        return new VNPayResultDTO(bookingId, success, responseCode, describeResponseCode(responseCode), txnRef, booking.getTotalAmount());
    }

    /** Dung lai booking co san hoac tao booking PENDING moi. */
    private Booking resolveBooking(VNPayRequestDTO request) {
        if (request.getBookingId() != null) {
            Booking booking = bookingService.findOwnedBooking(request.getBookingId());

            if (!BookingService.BookingStatus.PENDING.name().equals(booking.getStatus())) {
                throw new BadRequestAlertException("Booking is not awaiting payment", ENTITY_NAME, "invalidstatus");
            }
            return booking;
        }

        if (request.getTicketTypeId() == null || request.getQuantity() == null) {
            throw new BadRequestAlertException("Missing ticket information", ENTITY_NAME, "missingticket");
        }

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setTicketTypeId(request.getTicketTypeId());
        bookingRequest.setQuantity(request.getQuantity());
        bookingRequest.setCouponCode(request.getCouponCode());

        BookingDTO created = bookingService.book(bookingRequest);

        return bookingRepository
            .findById(created.getId())
            .orElseThrow(() -> new BadRequestAlertException("Booking not found", ENTITY_NAME, "bookingnotfound"));
    }

    /** Xac nhan don 0 dong va tra ve thang trang ket qua. */
    private String confirmFreeBooking(Booking booking) {
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(BigDecimal.ZERO);
        payment.setMethod(METHOD_FREE);
        payment.setStatus(STATUS_SUCCESS);
        payment.setPaymentDate(Instant.now());
        payment.setTransactionCode(UUID.randomUUID().toString());
        paymentRepository.save(payment);

        bookingService.confirmPaidBooking(booking.getId());

        return (
            vnPayService.getFrontendReturnUrl() +
            "?bookingId=" +
            booking.getId() +
            "&success=true&message=" +
            URLEncoder.encode("Dat ve thanh cong", StandardCharsets.UTF_8)
        );
    }

    /** VNPay gui so tien da nhan 100, doi chieu voi tong tien cua booking. */
    private boolean isAmountMatching(Booking booking, String vnpAmount) {
        if (vnpAmount == null || booking.getTotalAmount() == null) {
            return false;
        }
        try {
            long expected = booking.getTotalAmount().setScale(0, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).longValueExact();

            return expected == Long.parseLong(vnpAmount);
        } catch (ArithmeticException | NumberFormatException e) {
            return false;
        }
    }

    /** Dich mot so ma loi pho bien cua VNPay sang thong diep cho nguoi dung. */
    private String describeResponseCode(String code) {
        if (code == null) {
            return "Khong nhan duoc phan hoi tu VNPay";
        }
        return switch (code) {
            case "00" -> "Thanh toan thanh cong";
            case "04" -> "So tien giao dich khong hop le";
            case "07" -> "Giao dich bi nghi ngo gian lan";
            case "09" -> "The chua dang ky dich vu Internet Banking";
            case "10" -> "Xac thuc thong tin the khong dung qua 3 lan";
            case "11" -> "Da het han cho thanh toan";
            case "12" -> "The hoac tai khoan bi khoa";
            case "24" -> "Nguoi dung huy giao dich";
            case "51" -> "Tai khoan khong du so du";
            case "65" -> "Tai khoan vuot han muc giao dich trong ngay";
            case "75" -> "Ngan hang dang bao tri";
            case "79" -> "Nhap sai mat khau thanh toan qua so lan quy dinh";
            default -> "Giao dich khong thanh cong (ma " + code + ")";
        };
    }

    /**
     * Save a payment.
     *
     * @param paymentDTO the entity to save.
     * @return the persisted entity.
     */
    public PaymentDTO save(PaymentDTO paymentDTO) {
        LOG.debug("Request to save Payment : {}", paymentDTO);
        Payment payment = paymentMapper.toEntity(paymentDTO);
        payment = paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    /**
     * Update a payment.
     *
     * @param paymentDTO the entity to save.
     * @return the persisted entity.
     */
    public PaymentDTO update(PaymentDTO paymentDTO) {
        LOG.debug("Request to update Payment : {}", paymentDTO);
        Payment payment = paymentMapper.toEntity(paymentDTO);
        payment = paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    /**
     * Partially update a payment.
     *
     * @param paymentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PaymentDTO> partialUpdate(PaymentDTO paymentDTO) {
        LOG.debug("Request to partially update Payment : {}", paymentDTO);

        return paymentRepository
            .findById(paymentDTO.getId())
            .map(existingPayment -> {
                paymentMapper.partialUpdate(existingPayment, paymentDTO);

                return existingPayment;
            })
            .map(paymentRepository::save)
            .map(paymentMapper::toDto);
    }

    /**
     * Get one payment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PaymentDTO> findOne(Long id) {
        LOG.debug("Request to get Payment : {}", id);
        return paymentRepository.findById(id).map(paymentMapper::toDto);
    }

    /**
     * Delete the payment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Payment : {}", id);
        paymentRepository.deleteById(id);
    }

    @Transactional
    public PaymentDTO pay(PaymentRequest request) {
        Booking booking = validateBooking(request);

        User user = getCurrentUser();

        validateOwner(booking, user);

        Payment payment = createPayment(booking, request);

        updateBookingStatus(booking);

        return paymentMapper.toDto(payment);
    }

    private Booking validateBooking(PaymentRequest request) {
        Booking booking = bookingRepository
            .findById(request.getBookingId())
            .orElseThrow(() -> new BadRequestAlertException("Booking not found", ENTITY_NAME, "bookingnotfound"));

        if (!"PENDING".equals(booking.getStatus())) {
            throw new BadRequestAlertException("Booking has already been paid", ENTITY_NAME, "invalidstatus");
        }

        return booking;
    }

    private User getCurrentUser() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("User not logged in", ENTITY_NAME, "usernotfound")
        );

        return userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("User not found", ENTITY_NAME, "usernotfound"));
    }

    private void validateOwner(Booking booking, User user) {
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new BadRequestAlertException("Access denied", ENTITY_NAME, "accessdenied");
        }
    }

    private Payment createPayment(Booking booking, PaymentRequest request) {
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());

        payment.setMethod(request.getMethod());
        payment.setStatus(STATUS_SUCCESS);
        payment.setPaymentDate(Instant.now());
        payment.setTransactionCode(UUID.randomUUID().toString());
        return paymentRepository.save(payment);
    }

    private void updateBookingStatus(Booking booking) {
        // confirmPaidBooking vua doi trang thai sang PAID vua sinh ve kem QR.
        bookingService.confirmPaidBooking(booking.getId());
    }
}
