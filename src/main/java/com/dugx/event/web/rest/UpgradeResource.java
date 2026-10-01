package com.dugx.event.web.rest;

import com.dugx.event.service.SeatUpgradeService;
import com.dugx.event.service.dto.TicketUpgradeInfoDTO;
import com.dugx.event.service.dto.UpgradeRequestDTO;
import com.dugx.event.service.dto.UpgradeResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller cho tinh nang nang hang ghe cua mot ve da mua.
 */
@RestController
@RequestMapping("/api/upgrades")
public class UpgradeResource {

    private static final Logger LOG = LoggerFactory.getLogger(UpgradeResource.class);

    private final SeatUpgradeService seatUpgradeService;

    public UpgradeResource(SeatUpgradeService seatUpgradeService) {
        this.seatUpgradeService = seatUpgradeService;
    }

    /**
     * {@code GET /api/upgrades/tickets/:ticketId} : thong tin cac hang ghe co
     * the nang len cho mot ve.
     */
    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<TicketUpgradeInfoDTO> getUpgradeInfo(@PathVariable Long ticketId) {
        LOG.debug("REST request to get upgrade info for ticket : {}", ticketId);
        return ResponseEntity.ok(seatUpgradeService.getUpgradeInfo(ticketId));
    }

    /**
     * {@code POST /api/upgrades} : tao yeu cau nang hang, tra ve bookingId de
     * client goi tiep {@code POST /api/payments/vnpay/create-url} voi bookingId
     * do thu phan chenh lech gia.
     */
    @PostMapping("")
    public ResponseEntity<UpgradeResponseDTO> requestUpgrade(@RequestBody UpgradeRequestDTO request) {
        LOG.debug("REST request to upgrade ticket : {}", request.getTicketId());
        return ResponseEntity.ok(seatUpgradeService.requestUpgrade(request.getTicketId(), request.getTargetSeatType()));
    }
}
