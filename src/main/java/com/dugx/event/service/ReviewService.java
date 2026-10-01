package com.dugx.event.service;

import com.dugx.event.domain.Event;
import com.dugx.event.domain.Review;
import com.dugx.event.domain.User;
import com.dugx.event.repository.BookingDetailRepository;
import com.dugx.event.repository.EventRepository;
import com.dugx.event.repository.ReviewRepository;
import com.dugx.event.repository.UserRepository;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.CreateReviewRequest;
import com.dugx.event.service.dto.ReviewDTO;
import com.dugx.event.service.mapper.ReviewMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.Review}.
 */
@Service
@Transactional
public class ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;

    private final ReviewMapper reviewMapper;
    private final BookingDetailRepository bookingDetailRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    public ReviewService(
        ReviewRepository reviewRepository,
        ReviewMapper reviewMapper,
        BookingDetailRepository bookingDetailRepository,
        EventRepository eventRepository,
        UserRepository userRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.bookingDetailRepository = bookingDetailRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save a review.
     *
     * @param reviewDTO the entity to save.
     * @return the persisted entity.
     */
    public ReviewDTO save(ReviewDTO reviewDTO) {
        LOG.debug("Request to save Review : {}", reviewDTO);
        Review review = reviewMapper.toEntity(reviewDTO);
        review = reviewRepository.save(review);
        return reviewMapper.toDto(review);
    }

    /**
     * Update a review.
     *
     * @param reviewDTO the entity to save.
     * @return the persisted entity.
     */
    public ReviewDTO update(ReviewDTO reviewDTO) {
        LOG.debug("Request to update Review : {}", reviewDTO);
        Review review = reviewMapper.toEntity(reviewDTO);
        review = reviewRepository.save(review);
        return reviewMapper.toDto(review);
    }

    /**
     * Partially update a review.
     *
     * @param reviewDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ReviewDTO> partialUpdate(ReviewDTO reviewDTO) {
        LOG.debug("Request to partially update Review : {}", reviewDTO);

        return reviewRepository
            .findById(reviewDTO.getId())
            .map(existingReview -> {
                reviewMapper.partialUpdate(existingReview, reviewDTO);

                return existingReview;
            })
            .map(reviewRepository::save)
            .map(reviewMapper::toDto);
    }

    /**
     * Get all the reviews with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ReviewDTO> findAllWithEagerRelationships(Pageable pageable) {
        return reviewRepository.findAllWithEagerRelationships(pageable).map(reviewMapper::toDto);
    }

    /**
     * Get one review by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ReviewDTO> findOne(Long id) {
        LOG.debug("Request to get Review : {}", id);
        return reviewRepository.findOneWithEagerRelationships(id).map(reviewMapper::toDto);
    }

    /**
     * Delete the review by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Review : {}", id);
        reviewRepository.deleteById(id);
    }

    @Transactional
    public ReviewDTO createReview(Long eventId, CreateReviewRequest request) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("User not found"));

        boolean purchased = bookingDetailRepository.hasPurchasedEvent(login, eventId);

        if (!purchased) {
            throw new RuntimeException("You must purchase this event before reviewing.");
        }

        boolean reviewed = reviewRepository.existsByUserLoginAndEventId(login, eventId);

        if (reviewed) {
            throw new RuntimeException("You have already reviewed this event.");
        }

        User user = userRepository.findOneByLogin(login).orElseThrow(() -> new RuntimeException("User not found"));

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));

        Review review = new Review();
        review.setUser(user);
        review.setEvent(event);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedDate(Instant.now());
        review = reviewRepository.save(review);
        return reviewMapper.toDto(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByEvent(Long eventId) {
        return reviewRepository.findVisibleByEventId(eventId).stream().map(reviewMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(Long eventId) {
        Double avg = reviewRepository.getAverageRating(eventId);
        return avg == null ? 0.0 : avg;
    }

    /** Toan bo danh gia trong he thong - chi danh cho admin. */
    @Transactional(readOnly = true)
    public List<ReviewDTO> getAllForAdmin() {
        return reviewRepository.findAllForAdmin().stream().map(reviewMapper::toDto).toList();
    }

    /** An/hien mot danh gia - chi danh cho admin (UC Quan ly danh gia). */
    public ReviewDTO setHidden(Long id, boolean hidden) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
        review.setHidden(hidden);
        review = reviewRepository.save(review);
        return reviewMapper.toDto(review);
    }

    /** Phan hoi mot danh gia - chi danh cho admin (UC Quan ly danh gia). */
    public ReviewDTO reply(Long id, String replyText) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
        review.setReply(replyText);
        review.setRepliedDate(Instant.now());
        review = reviewRepository.save(review);
        return reviewMapper.toDto(review);
    }

    /** Sua danh gia cua chinh minh (UC Danh gia chuyen bay - Sua danh gia). */
    public ReviewDTO updateOwnReview(Long id, CreateReviewRequest request) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("User not found"));
        Review review = reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));

        if (review.getUser() == null || !login.equals(review.getUser().getLogin())) {
            throw new RuntimeException("You can only edit your own review.");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review = reviewRepository.save(review);
        return reviewMapper.toDto(review);
    }

    /** Xoa danh gia cua chinh minh (UC Danh gia chuyen bay - Xoa danh gia). */
    public void deleteOwnReview(Long id) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("User not found"));
        Review review = reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));

        if (review.getUser() == null || !login.equals(review.getUser().getLogin())) {
            throw new RuntimeException("You can only delete your own review.");
        }

        reviewRepository.deleteById(id);
    }
}
