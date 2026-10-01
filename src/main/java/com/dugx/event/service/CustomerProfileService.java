package com.dugx.event.service;

import com.dugx.event.domain.CustomerProfile;
import com.dugx.event.domain.User;
import com.dugx.event.repository.CustomerProfileRepository;
import com.dugx.event.repository.UserRepository;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.CustomerProfileDTO;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Thong tin khach hang (sdt, ngay sinh, gioi tinh, CCCD/ho chieu, dia chi) cua nguoi dung dang dang nhap,
 * tu phuc vu giong LoyaltyService (/my-bookings, /loyalty/me).
 */
@Service
@Transactional
public class CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final UserRepository userRepository;

    public CustomerProfileService(CustomerProfileRepository customerProfileRepository, UserRepository userRepository) {
        this.customerProfileRepository = customerProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public CustomerProfileDTO getMyProfile() {
        Long userId = currentUserId();
        return customerProfileRepository.findByUser_Id(userId).map(this::toDto).orElseGet(CustomerProfileDTO::new);
    }

    public CustomerProfileDTO updateMyProfile(CustomerProfileDTO dto) {
        Long userId = currentUserId();
        CustomerProfile profile = customerProfileRepository.findByUser_Id(userId).orElseGet(() -> {
            CustomerProfile newProfile = new CustomerProfile();
            User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new BadRequestAlertException("User not found", "customerProfile", "usernotfound"));
            newProfile.setUser(user);
            return newProfile;
        });

        profile.setPhone(dto.getPhone());
        profile.setDateOfBirth(dto.getDateOfBirth());
        profile.setGender(dto.getGender());
        profile.setIdNumber(dto.getIdNumber());
        profile.setAddress(dto.getAddress());

        return toDto(customerProfileRepository.save(profile));
    }

    private CustomerProfileDTO toDto(CustomerProfile profile) {
        return new CustomerProfileDTO(
            profile.getPhone(),
            profile.getDateOfBirth(),
            profile.getGender(),
            profile.getIdNumber(),
            profile.getAddress()
        );
    }

    private Long currentUserId() {
        return SecurityUtils.getCurrentUserId().orElseThrow(() ->
            new BadRequestAlertException("User not logged in", "customerProfile", "usernotfound")
        );
    }
}
