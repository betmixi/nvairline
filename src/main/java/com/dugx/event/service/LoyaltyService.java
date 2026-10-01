package com.dugx.event.service;

import com.dugx.event.domain.Booking;
import com.dugx.event.domain.LoyaltyAccount;
import com.dugx.event.domain.PointsHistory;
import com.dugx.event.domain.User;
import com.dugx.event.repository.LoyaltyAccountRepository;
import com.dugx.event.repository.PointsHistoryRepository;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.LoyaltyBalanceDTO;
import com.dugx.event.service.dto.LoyaltyOfferDTO;
import com.dugx.event.service.dto.PointsHistoryDTO;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tich diem Lotusmiles: cong diem sau khi thanh toan thanh cong, xem so du va lich su.
 */
@Service
@Transactional
public class LoyaltyService {

    private static final Logger LOG = LoggerFactory.getLogger(LoyaltyService.class);

    /** 1 diem cho moi 10.000d chi tieu (lam tron xuong). */
    private static final int VND_PER_POINT = 10_000;

    /** Danh sach uu dai co dinh co the doi bang diem. */
    private static final List<LoyaltyOfferDTO> OFFERS = List.of(
        new LoyaltyOfferDTO("DISCOUNT_20K", "Giảm 20.000đ cho vé tiếp theo", "Áp dụng khi thanh toán vé mới", 50),
        new LoyaltyOfferDTO("BAGGAGE_10KG", "Miễn phí 10kg hành lý", "Áp dụng cho vé tiếp theo của bạn", 80),
        new LoyaltyOfferDTO("DISCOUNT_50K", "Giảm 50.000đ cho vé tiếp theo", "Áp dụng khi thanh toán vé mới", 120),
        new LoyaltyOfferDTO("UPGRADE_BUSINESS", "Nâng hạng Thương gia", "Áp dụng cho 1 chặng bay tiếp theo", 300)
    );

    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final PointsHistoryRepository pointsHistoryRepository;

    public LoyaltyService(LoyaltyAccountRepository loyaltyAccountRepository, PointsHistoryRepository pointsHistoryRepository) {
        this.loyaltyAccountRepository = loyaltyAccountRepository;
        this.pointsHistoryRepository = pointsHistoryRepository;
    }

    /** So diem tuong ung voi so tien da chi (dung khi cong diem sau thanh toan). */
    public int pointsForAmount(java.math.BigDecimal amount) {
        if (amount == null) {
            return 0;
        }
        return amount.divide(java.math.BigDecimal.valueOf(VND_PER_POINT), java.math.RoundingMode.DOWN).intValue();
    }

    /**
     * Cong (hoac tru) diem cho nguoi dung, ghi lai lich su. Goi tu BookingService.confirmPaidBooking()
     * sau khi booking da duoc xac nhan PAID lan dau (guard idempotent da co san o do).
     */
    public void awardPoints(User user, int points, String reason, Booking booking) {
        if (user == null || points == 0) {
            return;
        }

        LoyaltyAccount account = loyaltyAccountRepository.findByUser_Id(user.getId()).orElseGet(() -> {
            LoyaltyAccount newAccount = new LoyaltyAccount();
            newAccount.setUser(user);
            newAccount.setPoints(0);
            return newAccount;
        });

        account.setPoints((account.getPoints() == null ? 0 : account.getPoints()) + points);
        loyaltyAccountRepository.save(account);

        PointsHistory history = new PointsHistory();
        history.setUser(user);
        history.setPoints(points);
        history.setReason(reason);
        history.setCreatedDate(Instant.now());
        history.setBooking(booking);
        pointsHistoryRepository.save(history);

        LOG.debug("Awarded {} points to user {} ({})", points, user.getLogin(), reason);
    }

    /** Danh sach uu dai co the doi bang diem. */
    public List<LoyaltyOfferDTO> getOffers() {
        return OFFERS;
    }

    /**
     * Doi mot uu dai bang diem: kiem tra so du, tru diem va ghi lich su (dung lai awardPoints
     * voi so diem am, booking = null vi khong gan voi mot lan dat ve cu the).
     */
    public LoyaltyBalanceDTO redeemOffer(String offerId) {
        LoyaltyOfferDTO offer = OFFERS.stream()
            .filter(o -> o.getId().equals(offerId))
            .findFirst()
            .orElseThrow(() -> new BadRequestAlertException("Ưu đãi không tồn tại", "loyalty", "offernotfound"));

        Long userId = currentUserId();
        LoyaltyAccount account = loyaltyAccountRepository
            .findByUser_Id(userId)
            .orElseThrow(() -> new BadRequestAlertException("Bạn chưa có đủ điểm để đổi ưu đãi này", "loyalty", "insufficientpoints"));

        int currentPoints = account.getPoints() == null ? 0 : account.getPoints();
        if (currentPoints < offer.getPointCost()) {
            throw new BadRequestAlertException("Bạn không đủ điểm để đổi ưu đãi này", "loyalty", "insufficientpoints");
        }

        awardPoints(account.getUser(), -offer.getPointCost(), "Đổi ưu đãi: " + offer.getLabel(), null);

        return getMyBalance();
    }

    @Transactional(readOnly = true)
    public LoyaltyBalanceDTO getMyBalance() {
        Long userId = currentUserId();
        int points = loyaltyAccountRepository.findByUser_Id(userId).map(LoyaltyAccount::getPoints).orElse(0);
        return new LoyaltyBalanceDTO(points);
    }

    @Transactional(readOnly = true)
    public Page<PointsHistoryDTO> getMyHistory(Pageable pageable) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("User not logged in", "loyalty", "usernotfound")
        );
        return pointsHistoryRepository
            .findByUserLogin(login, pageable)
            .map(h ->
                new PointsHistoryDTO(
                    h.getId(),
                    h.getPoints(),
                    h.getReason(),
                    h.getCreatedDate(),
                    h.getBooking() != null ? h.getBooking().getId() : null
                )
            );
    }

    private Long currentUserId() {
        return SecurityUtils.getCurrentUserId().orElseThrow(() ->
            new BadRequestAlertException("User not logged in", "loyalty", "usernotfound")
        );
    }
}
