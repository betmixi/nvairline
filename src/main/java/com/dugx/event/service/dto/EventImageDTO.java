package com.dugx.event.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.dugx.event.domain.EventImage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventImageDTO implements Serializable {

    private Long id;

    @NotNull
    private String imageUrl;

    private EventDTO event;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public EventDTO getEvent() {
        return event;
    }

    public void setEvent(EventDTO event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventImageDTO)) {
            return false;
        }

        EventImageDTO eventImageDTO = (EventImageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventImageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventImageDTO{" +
            "id=" + getId() +
            ", imageUrl='" + getImageUrl() + "'" +
            ", event=" + getEvent() +
            "}";
    }
}
