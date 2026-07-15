package com.dugx.event.config;

import java.time.Duration;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.boot.cache.autoconfigure.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        var ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Object.class,
                Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries())
            )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build()
        );
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.dugx.event.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.dugx.event.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.dugx.event.domain.Authority.class.getName());
            createCache(cm, com.dugx.event.domain.Event.class.getName());
            createCache(cm, com.dugx.event.domain.Category.class.getName());
            createCache(cm, com.dugx.event.domain.Organizer.class.getName());
            createCache(cm, com.dugx.event.domain.Venue.class.getName());
            createCache(cm, com.dugx.event.domain.TicketType.class.getName());
            createCache(cm, com.dugx.event.domain.Booking.class.getName());
            createCache(cm, com.dugx.event.domain.BookingDetail.class.getName());
            createCache(cm, com.dugx.event.domain.Payment.class.getName());
            createCache(cm, com.dugx.event.domain.Review.class.getName());
            createCache(cm, com.dugx.event.domain.Favorite.class.getName());
            createCache(cm, com.dugx.event.domain.Notification.class.getName());
            createCache(cm, com.dugx.event.domain.EventImage.class.getName());
            createCache(cm, com.dugx.event.domain.Coupon.class.getName());
            createCache(cm, com.dugx.event.domain.Report.class.getName());
            createCache(cm, com.dugx.event.domain.CheckIn.class.getName());
            createCache(cm, com.dugx.event.domain.AuditLog.class.getName());
            createCache(cm, com.dugx.event.domain.Address.class.getName());
            createCache(cm, com.dugx.event.domain.Ticket.class.getName());
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }
}
