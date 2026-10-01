package com.dugx.event.service;

import com.dugx.event.domain.SupportRequest;
import com.dugx.event.domain.User;
import com.dugx.event.repository.SupportRequestRepository;
import com.dugx.event.repository.UserRepository;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.CreateSupportRequestDTO;
import com.dugx.event.service.dto.SupportRequestDTO;
import com.dugx.event.service.dto.UserDTO;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.SupportRequest} - UC Lien he ho tro.
 */
@Service
@Transactional
public class SupportRequestService {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_REPLIED = "REPLIED";

    /** Cac chu de ho tro co dinh de khach hang chon, theo UC Lien he ho tro. */
    public static final List<String> TOPICS = List.of("Đặt vé & thanh toán", "Hành lý", "Hoàn/huỷ vé", "Thông tin chuyến bay", "Khác");

    private final SupportRequestRepository supportRequestRepository;
    private final UserRepository userRepository;

    public SupportRequestService(SupportRequestRepository supportRequestRepository, UserRepository userRepository) {
        this.supportRequestRepository = supportRequestRepository;
        this.userRepository = userRepository;
    }

    public SupportRequestDTO createRequest(CreateSupportRequestDTO request) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("User not found"));
        User user = userRepository.findOneByLogin(login).orElseThrow(() -> new RuntimeException("User not found"));

        SupportRequest entity = new SupportRequest();
        entity.setUser(user);
        entity.setTopic(request.getTopic());
        entity.setContent(request.getContent());
        entity.setStatus(STATUS_PENDING);
        entity.setCreatedDate(Instant.now());
        entity = supportRequestRepository.save(entity);
        return toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<SupportRequestDTO> getMyRequests() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("User not found"));
        return supportRequestRepository.findByUserLoginOrderByCreatedDateDesc(login).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<SupportRequestDTO> getAllForAdmin() {
        return supportRequestRepository.findAllForAdmin().stream().map(this::toDto).toList();
    }

    public SupportRequestDTO reply(Long id, String replyText) {
        SupportRequest entity = supportRequestRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy yêu cầu hỗ trợ", "supportRequest", "idnotfound"));

        entity.setReply(replyText);
        entity.setStatus(STATUS_REPLIED);
        entity.setRepliedDate(Instant.now());
        entity = supportRequestRepository.save(entity);
        return toDto(entity);
    }

    private SupportRequestDTO toDto(SupportRequest entity) {
        SupportRequestDTO dto = new SupportRequestDTO();
        dto.setId(entity.getId());
        dto.setTopic(entity.getTopic());
        dto.setContent(entity.getContent());
        dto.setStatus(entity.getStatus());
        dto.setReply(entity.getReply());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setRepliedDate(entity.getRepliedDate());
        dto.setUser(new UserDTO(entity.getUser()));
        return dto;
    }
}
