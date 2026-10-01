package com.dugx.event.web.rest;

import com.dugx.event.service.LoyaltyService;
import com.dugx.event.service.dto.LoyaltyBalanceDTO;
import com.dugx.event.service.dto.LoyaltyCouponDTO;
import com.dugx.event.service.dto.LoyaltyOfferDTO;
import com.dugx.event.service.dto.PointsHistoryDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

/**
 * Tich diem Lotusmiles cua nguoi dung dang dang nhap (tu phuc vu, giong /my-bookings).
 */
@RestController
@RequestMapping("/api/loyalty")
public class LoyaltyResource {

    private final LoyaltyService loyaltyService;

    public LoyaltyResource(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @GetMapping("/me")
    public ResponseEntity<LoyaltyBalanceDTO> getMyBalance() {
        return ResponseEntity.ok(loyaltyService.getMyBalance());
    }

    @GetMapping("/me/history")
    public ResponseEntity<List<PointsHistoryDTO>> getMyHistory(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        Page<PointsHistoryDTO> page = loyaltyService.getMyHistory(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/offers")
    public ResponseEntity<List<LoyaltyOfferDTO>> getOffers() {
        return ResponseEntity.ok(loyaltyService.getOffers());
    }

    @PostMapping("/redeem/{offerId}")
    public ResponseEntity<LoyaltyBalanceDTO> redeem(@PathVariable String offerId) {
        return ResponseEntity.ok(loyaltyService.redeemOffer(offerId));
    }

    @GetMapping("/me/coupons")
    public ResponseEntity<List<LoyaltyCouponDTO>> getMyCoupons() {
        return ResponseEntity.ok(loyaltyService.getMyCoupons());
    }
}
