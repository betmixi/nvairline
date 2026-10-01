package com.dugx.event.web.rest;

import com.dugx.event.service.SupportRequestService;
import com.dugx.event.service.dto.CreateSupportRequestDTO;
import com.dugx.event.service.dto.ReplySupportRequestDTO;
import com.dugx.event.service.dto.SupportRequestDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for UC Lien he ho tro (customer support requests).
 */
@RestController
@RequestMapping("/api/support-requests")
public class SupportRequestResource {

    private final SupportRequestService supportRequestService;

    public SupportRequestResource(SupportRequestService supportRequestService) {
        this.supportRequestService = supportRequestService;
    }

    @GetMapping("/topics")
    public List<String> getTopics() {
        return SupportRequestService.TOPICS;
    }

    @PostMapping("")
    public SupportRequestDTO createRequest(@Valid @RequestBody CreateSupportRequestDTO request) {
        return supportRequestService.createRequest(request);
    }

    @GetMapping("/mine")
    public List<SupportRequestDTO> getMyRequests() {
        return supportRequestService.getMyRequests();
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<SupportRequestDTO> getAllForAdmin() {
        return supportRequestService.getAllForAdmin();
    }

    @PatchMapping("/{id}/reply")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public SupportRequestDTO reply(@PathVariable Long id, @Valid @RequestBody ReplySupportRequestDTO request) {
        return supportRequestService.reply(id, request.getReply());
    }
}
