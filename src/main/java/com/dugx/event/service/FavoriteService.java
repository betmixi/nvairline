package com.dugx.event.service;

import com.dugx.event.domain.Event;
import com.dugx.event.domain.Favorite;
import com.dugx.event.domain.User;
import com.dugx.event.repository.EventRepository;
import com.dugx.event.repository.FavoriteRepository;
import com.dugx.event.repository.UserRepository;
import com.dugx.event.security.SecurityUtils;
import com.dugx.event.service.dto.EventDTO;
import com.dugx.event.service.dto.FavoriteDTO;
import com.dugx.event.service.mapper.EventMapper;
import com.dugx.event.service.mapper.FavoriteMapper;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dugx.event.domain.Favorite}.
 */
@Service
@Transactional
public class FavoriteService {

    private static final Logger LOG = LoggerFactory.getLogger(FavoriteService.class);

    private final FavoriteRepository favoriteRepository;

    private final FavoriteMapper favoriteMapper;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    public FavoriteService(
        FavoriteRepository favoriteRepository,
        FavoriteMapper favoriteMapper,
        UserRepository userRepository,
        EventRepository eventRepository,
        EventMapper eventMapper
    ) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteMapper = favoriteMapper;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    /**
     * Save a favorite.
     *
     * @param favoriteDTO the entity to save.
     * @return the persisted entity.
     */
    public FavoriteDTO save(FavoriteDTO favoriteDTO) {
        LOG.debug("Request to save Favorite : {}", favoriteDTO);
        Favorite favorite = favoriteMapper.toEntity(favoriteDTO);
        favorite = favoriteRepository.save(favorite);
        return favoriteMapper.toDto(favorite);
    }

    /**
     * Update a favorite.
     *
     * @param favoriteDTO the entity to save.
     * @return the persisted entity.
     */
    public FavoriteDTO update(FavoriteDTO favoriteDTO) {
        LOG.debug("Request to update Favorite : {}", favoriteDTO);
        Favorite favorite = favoriteMapper.toEntity(favoriteDTO);
        favorite = favoriteRepository.save(favorite);
        return favoriteMapper.toDto(favorite);
    }

    /**
     * Partially update a favorite.
     *
     * @param favoriteDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FavoriteDTO> partialUpdate(FavoriteDTO favoriteDTO) {
        LOG.debug("Request to partially update Favorite : {}", favoriteDTO);

        return favoriteRepository
            .findById(favoriteDTO.getId())
            .map(existingFavorite -> {
                favoriteMapper.partialUpdate(existingFavorite, favoriteDTO);

                return existingFavorite;
            })
            .map(favoriteRepository::save)
            .map(favoriteMapper::toDto);
    }

    /**
     * Get all the favorites with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<FavoriteDTO> findAllWithEagerRelationships(Pageable pageable) {
        return favoriteRepository.findAllWithEagerRelationships(pageable).map(favoriteMapper::toDto);
    }

    /**
     * Get one favorite by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FavoriteDTO> findOne(Long id) {
        LOG.debug("Request to get Favorite : {}", id);
        return favoriteRepository.findOneWithEagerRelationships(id).map(favoriteMapper::toDto);
    }

    /**
     * Delete the favorite by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Favorite : {}", id);
        favoriteRepository.deleteById(id);
    }

    /**
     * Bat/tat yeu thich mot su kien cho nguoi dung dang dang nhap.
     *
     * @return true neu sau thao tac su kien dang duoc yeu thich.
     */
    public boolean toggleFavorite(Long eventId) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("Chua dang nhap", "favorite", "unauthorized")
        );

        Optional<Favorite> existing = favoriteRepository.findByUserLoginAndEventId(login, eventId);

        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return false;
        }

        User user = userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("Khong tim thay nguoi dung", "favorite", "usernotfound"));

        Event event = eventRepository
            .findById(eventId)
            .orElseThrow(() -> new BadRequestAlertException("Khong tim thay su kien", "favorite", "eventnotfound"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setEvent(event);
        favoriteRepository.save(favorite);

        return true;
    }

    /** Su kien nay co dang duoc nguoi dung hien tai yeu thich khong. */
    @Transactional(readOnly = true)
    public boolean isFavorited(Long eventId) {
        return SecurityUtils.getCurrentUserLogin()
            .map(login -> favoriteRepository.existsByUserLoginAndEventId(login, eventId))
            .orElse(false);
    }

    /** Danh sach su kien nguoi dung dang dang nhap da luu. */
    @Transactional(readOnly = true)
    public List<EventDTO> getMyFavoriteEvents() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
            new BadRequestAlertException("Chua dang nhap", "favorite", "unauthorized")
        );

        return favoriteRepository
            .findMyFavorites(login)
            .stream()
            .map(Favorite::getEvent)
            .filter(Objects::nonNull)
            .map(eventMapper::toDto)
            .toList();
    }
}
