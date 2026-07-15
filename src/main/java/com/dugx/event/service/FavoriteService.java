package com.dugx.event.service;

import com.dugx.event.domain.Favorite;
import com.dugx.event.repository.FavoriteRepository;
import com.dugx.event.service.dto.FavoriteDTO;
import com.dugx.event.service.mapper.FavoriteMapper;
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

    public FavoriteService(FavoriteRepository favoriteRepository, FavoriteMapper favoriteMapper) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteMapper = favoriteMapper;
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
}
