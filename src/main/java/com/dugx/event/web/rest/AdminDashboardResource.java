package com.dugx.event.web.rest;

import com.dugx.event.service.AdminDashboardService;
import com.dugx.event.service.OrganizerService;
import com.dugx.event.service.dto.AdminDashboardDTO;
import com.dugx.event.service.dto.OrganizerDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardResource {

    private final AdminDashboardService adminDashboardService;
    private final OrganizerService organizerService;

    public AdminDashboardResource(AdminDashboardService adminDashboardService, OrganizerService organizerService) {
        this.adminDashboardService = adminDashboardService;
        this.organizerService = organizerService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public AdminDashboardDTO getDashboard() {
        return adminDashboardService.getDashboard();
    }

    @GetMapping("/organizers/pending")
    public ResponseEntity<List<OrganizerDTO>> getPendingOrganizers() {
        return ResponseEntity.ok(organizerService.getPendingOrganizers());
    }

    @PatchMapping("/organizers/{id}/approve")
    public ResponseEntity<OrganizerDTO> approveOrganizer(@PathVariable Long id) {
        return ResponseEntity.ok(organizerService.approve(id));
    }

    @PatchMapping("/organizers/{id}/reject")
    public ResponseEntity<OrganizerDTO> rejectOrganizer(@PathVariable Long id) {
        return ResponseEntity.ok(organizerService.reject(id));
    }
}
