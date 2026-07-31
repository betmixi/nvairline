package com.dugx.event.web.rest;

import com.dugx.event.service.OrganizerRevenueService;
import com.dugx.event.service.dto.OrganizerRevenueDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizer")
public class OrganizerRevenueResource {

    private final OrganizerRevenueService revenueService;

    public OrganizerRevenueResource(OrganizerRevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @GetMapping("/revenue")
    public OrganizerRevenueDTO revenue() {
        return revenueService.getRevenue();
    }
}
