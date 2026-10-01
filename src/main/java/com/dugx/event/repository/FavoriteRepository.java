package com.dugx.event.repository;

import com.dugx.event.domain.Favorite;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Favorite entity.
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long>, JpaSpecificationExecutor<Favorite> {
    @Query("select favorite from Favorite favorite where favorite.user.login = ?#{authentication.name}")
    List<Favorite> findByUserIsCurrentUser();

    default Optional<Favorite> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Favorite> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Favorite> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select favorite from Favorite favorite left join fetch favorite.user left join fetch favorite.event",
        countQuery = "select count(favorite) from Favorite favorite"
    )
    Page<Favorite> findAllWithToOneRelationships(Pageable pageable);

    @Query("select favorite from Favorite favorite left join fetch favorite.user left join fetch favorite.event")
    List<Favorite> findAllWithToOneRelationships();

    @Query("select favorite from Favorite favorite left join fetch favorite.user left join fetch favorite.event where favorite.id =:id")
    Optional<Favorite> findOneWithToOneRelationships(@Param("id") Long id);

    /** Ban ghi yeu thich cua mot nguoi dung cho mot su kien (dung de bat/tat). */
    Optional<Favorite> findByUserLoginAndEventId(String login, Long eventId);

    boolean existsByUserLoginAndEventId(String login, Long eventId);

    /** Danh sach su kien nguoi dung da luu, moi nhat truoc. */
    @Query(
        """
        select f
        from Favorite f
        left join fetch f.event e
        left join fetch e.category
        left join fetch e.address
        where f.user.login = :login
        order by f.id desc
        """
    )
    List<Favorite> findMyFavorites(@Param("login") String login);
}
