package com.dugx.event.service;

import com.dugx.event.config.VNPayConfig;
import com.dugx.event.domain.Booking;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Sinh URL thanh toan VNPay va xac thuc chu ky khi VNPay goi nguoc ve.
 *
 * Tai lieu: https://sandbox.vnpayment.vn/apis/docs/thanh-toan-pay/pay.html
 */
@Service
public class VNPayService {

    private static final Logger LOG = LoggerFactory.getLogger(VNPayService.class);

    private static final String VERSION = "2.1.0";
    private static final String COMMAND = "pay";
    private static final String CURRENCY = "VND";
    private static final String ORDER_TYPE = "other";
    private static final String LOCALE = "vn";

    /** VNPay lam viec theo gio Viet Nam (GMT+7). */
    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** So phut giu cho don hang truoc khi VNPay tu huy. */
    private static final int EXPIRE_MINUTES = 15;

    private final VNPayConfig vnPayConfig;

    public VNPayService(VNPayConfig vnPayConfig) {
        this.vnPayConfig = vnPayConfig;
    }

    /**
     * Sinh URL de redirect nguoi dung sang cong thanh toan VNPay.
     *
     * @param booking don hang dang o trang thai PENDING.
     * @param ipAddress dia chi IP cua nguoi dat ve.
     * @return URL day du kem chu ky vnp_SecureHash.
     */
    public String createPaymentUrl(Booking booking, String ipAddress) {
        BigDecimal amount = booking.getTotalAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestAlertException("Invalid payment amount", "payment", "invalidamount");
        }

        // VNPay nhan so tien da nhan 100 va khong co phan thap phan.
        long vnpAmount = amount.setScale(0, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).longValueExact();

        ZonedDateTime now = ZonedDateTime.now(VN_ZONE);

        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", VERSION);
        params.put("vnp_Command", COMMAND);
        params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        params.put("vnp_Amount", String.valueOf(vnpAmount));
        params.put("vnp_CurrCode", CURRENCY);
        params.put("vnp_TxnRef", buildTxnRef(booking));
        params.put("vnp_OrderInfo", "Thanh toan don hang " + booking.getId());
        params.put("vnp_OrderType", ORDER_TYPE);
        params.put("vnp_Locale", LOCALE);
        params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        params.put("vnp_IpAddr", ipAddress == null || ipAddress.isBlank() ? "127.0.0.1" : ipAddress);
        params.put("vnp_CreateDate", DATE_FORMAT.format(now));
        params.put("vnp_ExpireDate", DATE_FORMAT.format(now.plusMinutes(EXPIRE_MINUTES)));

        String query = buildQuery(params);
        String secureHash = hmacSHA512(vnPayConfig.getHashSecret(), query);

        LOG.debug("Created VNPay payment url for booking {}", booking.getId());

        return vnPayConfig.getPayUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;
    }

    /**
     * Kiem tra chu ky VNPay gui kem trong return-url hoac IPN.
     *
     * @param params toan bo tham so vnp_* nhan duoc tu VNPay.
     * @return true neu chu ky hop le.
     */
    public boolean verifySignature(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");

        if (receivedHash == null || receivedHash.isBlank()) {
            return false;
        }

        Map<String, String> signed = new HashMap<>(params);
        signed.remove("vnp_SecureHash");
        signed.remove("vnp_SecureHashType");

        String expectedHash = hmacSHA512(vnPayConfig.getHashSecret(), buildQuery(signed));

        return constantTimeEquals(expectedHash, receivedHash);
    }

    /** Doc toan bo tham so vnp_* tu request cua VNPay. */
    public Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> names = request.getParameterNames();

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = request.getParameter(name);

            if (name.startsWith("vnp_") && value != null && !value.isEmpty()) {
                params.put(name, value);
            }
        }
        return params;
    }

    /** Lay dia chi IP that cua client, co xet den reverse proxy / ngrok. */
    public String resolveIpAddress(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * vnp_TxnRef phai duy nhat cho moi lan thanh toan, nhung van phai suy nguoc
     * ra duoc booking. Dinh dang: {bookingId}T{epochSecond}.
     */
    public String buildTxnRef(Booking booking) {
        return booking.getId() + "T" + ZonedDateTime.now(VN_ZONE).toEpochSecond();
    }

    /** Tach bookingId tu vnp_TxnRef. */
    public Long parseBookingId(String txnRef) {
        if (txnRef == null || txnRef.isBlank()) {
            throw new BadRequestAlertException("Missing transaction reference", "payment", "missingtxnref");
        }
        try {
            int separator = txnRef.indexOf('T');
            return Long.valueOf(separator > 0 ? txnRef.substring(0, separator) : txnRef);
        } catch (NumberFormatException e) {
            throw new BadRequestAlertException("Invalid transaction reference", "payment", "invalidtxnref");
        }
    }

    /** URL trang ket qua thanh toan phia Angular. */
    public String getFrontendReturnUrl() {
        String url = vnPayConfig.getFrontendReturnUrl();
        return url == null || url.isBlank() ? "/payment/result" : url;
    }

    /**
     * Sap xep tham so theo alphabet va noi lai thanh chuoi query da url-encode.
     * Day chinh la chuoi duoc dung de tao chu ky.
     */
    private String buildQuery(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        keys.sort(String::compareTo);

        StringBuilder builder = new StringBuilder();

        for (String key : keys) {
            String value = params.get(key);

            if (value == null || value.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append('&');
            }
            builder
                .append(URLEncoder.encode(key, StandardCharsets.US_ASCII))
                .append('=')
                .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
        }
        return builder.toString();
    }

    /** Ky chuoi du lieu bang HMAC-SHA512 theo yeu cau cua VNPay. */
    private String hmacSHA512(String secretKey, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));

            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(bytes.length * 2);

            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot sign VNPay request", e);
        }
    }

    /** So sanh chu ky khong phu thuoc thoi gian de tranh timing attack. */
    private boolean constantTimeEquals(String expected, String actual) {
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual.toLowerCase().getBytes(StandardCharsets.UTF_8));
    }

    public String getTmnCode() {
        return vnPayConfig.getTmnCode();
    }
}
