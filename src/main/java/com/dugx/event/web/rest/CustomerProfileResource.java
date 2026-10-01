package com.dugx.event.web.rest;

import com.dugx.event.service.CustomerProfileService;
import com.dugx.event.service.dto.CustomerProfileDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Thong tin khach hang bo sung cua nguoi dung dang dang nhap (tu phuc vu, giong /api/loyalty/me).
 */
@RestController
@RequestMapping("/api/customer-profile")
public class CustomerProfileResource {

    private final CustomerProfileService customerProfileService;

    public CustomerProfileResource(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerProfileDTO> getMyProfile() {
        return ResponseEntity.ok(customerProfileService.getMyProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<CustomerProfileDTO> updateMyProfile(@RequestBody CustomerProfileDTO dto) {
        return ResponseEntity.ok(customerProfileService.updateMyProfile(dto));
    }
}
