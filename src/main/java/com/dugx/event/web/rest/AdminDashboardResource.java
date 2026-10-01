package com.dugx.event.web.rest;

import com.dugx.event.service.AdminDashboardService;
import com.dugx.event.service.dto.AdminDashboardDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardResource {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardResource(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public AdminDashboardDTO getDashboard() {
        return adminDashboardService.getDashboard();
    }
}
