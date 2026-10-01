package com.dugx.event.web.rest;

import com.dugx.event.service.BaggageService;
import com.dugx.event.service.dto.BaggageRequestDTO;
import com.dugx.event.service.dto.BaggageResponseDTO;
import com.dugx.event.service.dto.TicketBaggageInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller cho tinh nang mua hanh ly ky gui tra truoc cho mot ve da mua.
 */
@RestController
@RequestMapping("/api/baggage")
public class BaggageResource {

    private static final Logger LOG = LoggerFactory.getLogger(BaggageResource.class);

    private final BaggageService baggageService;

    public BaggageResource(BaggageService baggageService) {
        this.baggageService = baggageService;
    }

    /**
     * {@code GET /api/baggage/tickets/:ticketId} : thong tin hanh ly da mua va
     * cac goi co the mua them cho mot ve.
     */
    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<TicketBaggageInfoDTO> getBaggageInfo(@PathVariable Long ticketId) {
        LOG.debug("REST request to get baggage info for ticket : {}", ticketId);
        return ResponseEntity.ok(baggageService.getBaggageInfo(ticketId));
    }

    /**
     * {@code POST /api/baggage} : tao yeu cau mua hanh ly, tra ve bookingId de
     * client goi tiep {@code POST /api/payments/vnpay/create-url} voi bookingId
     * do de thanh toan.
     */
    @PostMapping("")
    public ResponseEntity<BaggageResponseDTO> requestPurchase(@RequestBody BaggageRequestDTO request) {
        LOG.debug("REST request to purchase baggage for ticket : {}", request.getTicketId());
        return ResponseEntity.ok(baggageService.requestPurchase(request.getTicketId(), request.getWeightKg()));
    }
}
