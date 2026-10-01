package com.dugx.event.web.rest;

import com.dugx.event.service.TicketAddonService;
import com.dugx.event.service.dto.AddonPurchaseRequestDTO;
import com.dugx.event.service.dto.AddonPurchaseResponseDTO;
import com.dugx.event.service.dto.TicketAddonInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller cho cac dich vu bo tro mua them cho mot ve da mua (mua sam
 * mien thue, khach san &amp; tour, bao hiem du lich, dich vu khac).
 */
@RestController
@RequestMapping("/api/ticket-addons")
public class TicketAddonResource {

    private static final Logger LOG = LoggerFactory.getLogger(TicketAddonResource.class);

    private final TicketAddonService ticketAddonService;

    public TicketAddonResource(TicketAddonService ticketAddonService) {
        this.ticketAddonService = ticketAddonService;
    }

    /**
     * {@code GET /api/ticket-addons/tickets/:ticketId?type=SHOPPING} : danh
     * muc va cac dich vu bo tro da mua (theo loai) cho mot ve.
     */
    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<TicketAddonInfoDTO> getInfo(@PathVariable Long ticketId, @RequestParam("type") String addonType) {
        LOG.debug("REST request to get addon info for ticket {} type {}", ticketId, addonType);
        return ResponseEntity.ok(ticketAddonService.getInfo(ticketId, addonType));
    }

    /**
     * {@code POST /api/ticket-addons} : tao yeu cau mua dich vu bo tro, tra
     * ve bookingId de client goi tiep {@code POST /api/payments/vnpay/create-url}
     * voi bookingId do de thanh toan.
     */
    @PostMapping("")
    public ResponseEntity<AddonPurchaseResponseDTO> requestPurchase(@RequestBody AddonPurchaseRequestDTO request) {
        LOG.debug("REST request to purchase addon for ticket : {}", request.getTicketId());
        return ResponseEntity.ok(ticketAddonService.requestPurchase(request.getTicketId(), request.getAddonType(), request.getItems()));
    }
}
