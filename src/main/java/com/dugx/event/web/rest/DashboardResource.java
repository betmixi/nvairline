package com.dugx.event.web.rest;

import com.dugx.event.service.DashboardService;
import com.dugx.event.service.dto.DashboardDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardResource {

    private final DashboardService dashboardService;

    public DashboardResource(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<DashboardDTO> getDashboard() {
        DashboardDTO dto = dashboardService.getDashboard();
        return ResponseEntity.ok(dto);
    }
}
