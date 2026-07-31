package com.dugx.event.web.rest;

import com.dugx.event.service.PaymentService;
import com.dugx.event.service.VNPayService;
import com.dugx.event.service.dto.VNPayRequestDTO;
import com.dugx.event.service.dto.VNPayResultDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Cac endpoint phuc vu thanh toan qua VNPay.
 *
 * Luong day du:
 * 1. Client goi POST /api/payments/vnpay/create-url -> nhan URL cong thanh toan.
 * 2. Nguoi dung thanh toan tren VNPay.
 * 3. VNPay redirect trinh duyet ve GET /api/payments/vnpay-return -> server xu ly
 *    roi redirect tiep ve trang ket qua cua Angular.
 * 4. Song song do VNPay goi GET /api/payments/vnpay-ipn (server-to-server) de
 *    chot ket qua ke ca khi nguoi dung dong trinh duyet.
 */
@RestController
@RequestMapping("/api/payments")
public class VNPayResource {

    private static final Logger LOG = LoggerFactory.getLogger(VNPayResource.class);

    private final VNPayService vnPayService;

    private final PaymentService paymentService;

    public VNPayResource(VNPayService vnPayService, PaymentService paymentService) {
        this.vnPayService = vnPayService;
        this.paymentService = paymentService;
    }

    /**
     * POST /api/payments/vnpay/create-url : tao link thanh toan.
     *
     * @param request bookingId, hoac ticketTypeId + quantity (+ couponCode).
     * @param httpRequest dung de lay dia chi IP cua nguoi mua.
     * @return JSON dang { "paymentUrl": "https://sandbox.vnpayment.vn/..." }
     */
    @PostMapping("/vnpay/create-url")
    public ResponseEntity<Map<String, String>> createPaymentUrl(@RequestBody VNPayRequestDTO request, HttpServletRequest httpRequest) {
        LOG.debug("REST request to create VNPay payment url : {}", request.getBookingId());

        String ipAddress = vnPayService.resolveIpAddress(httpRequest);
        String paymentUrl = paymentService.createVnPayUrl(request, ipAddress);

        Map<String, String> body = new HashMap<>();
        body.put("paymentUrl", paymentUrl);

        return ResponseEntity.ok(body);
    }

    /**
     * GET /api/payments/vnpay-return : noi VNPay dieu huong trinh duyet ve.
     *
     * Endpoint nay khong tra JSON ma redirect ve trang ket qua cua Angular kem
     * theo trang thai, de nguoi dung thay ngay ket qua.
     */
    @GetMapping("/vnpay-return")
    public void vnPayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> params = vnPayService.extractParams(request);

        String redirectUrl;

        if (!vnPayService.verifySignature(params)) {
            LOG.warn("Invalid VNPay signature on return url");
            redirectUrl = buildRedirectUrl(null, false, "Chu ky khong hop le");
        } else {
            try {
                VNPayResultDTO result = paymentService.handleVnPayResult(params);
                redirectUrl = buildRedirectUrl(result.getBookingId(), result.isSuccess(), result.getMessage());
            } catch (RuntimeException e) {
                LOG.error("Cannot handle VNPay return", e);
                redirectUrl = buildRedirectUrl(null, false, "Khong xu ly duoc giao dich");
            }
        }

        response.sendRedirect(redirectUrl);
    }

    /**
     * GET /api/payments/vnpay-ipn : VNPay goi truc tiep tu server cua ho.
     *
     * Day moi la nguon su that cuoi cung ve trang thai giao dich. Phai tra ve
     * dung dinh dang { "RspCode": "...", "Message": "..." }.
     */
    @GetMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, String>> vnPayIpn(HttpServletRequest request) {
        Map<String, String> params = vnPayService.extractParams(request);

        if (!vnPayService.verifySignature(params)) {
            LOG.warn("Invalid VNPay signature on IPN");
            return ResponseEntity.ok(ipnResponse("97", "Invalid signature"));
        }

        try {
            VNPayResultDTO result = paymentService.handleVnPayResult(params);
            LOG.debug("VNPay IPN processed : {}", result);

            return ResponseEntity.ok(ipnResponse("00", "Confirm success"));
        } catch (RuntimeException e) {
            LOG.error("Cannot handle VNPay IPN", e);
            return ResponseEntity.ok(ipnResponse("01", "Order not found"));
        }
    }

    /** Ghep query param vao URL trang ket qua cua Angular. */
    private String buildRedirectUrl(Long bookingId, boolean success, String message) {
        StringBuilder url = new StringBuilder(vnPayService.getFrontendReturnUrl());

        url.append(url.indexOf("?") >= 0 ? '&' : '?');
        url.append("success=").append(success);

        if (bookingId != null) {
            url.append("&bookingId=").append(bookingId);
        }
        if (message != null) {
            url.append("&message=").append(URLEncoder.encode(message, StandardCharsets.UTF_8));
        }
        return url.toString();
    }

    private Map<String, String> ipnResponse(String code, String message) {
        Map<String, String> body = new HashMap<>();
        body.put("RspCode", code);
        body.put("Message", message);
        return body;
    }
}
