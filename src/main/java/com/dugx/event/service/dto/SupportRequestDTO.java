package com.dugx.event.service.dto;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for the {@link com.dugx.event.domain.SupportRequest} entity.
 */
public class SupportRequestDTO implements Serializable {

    private Long id;

    @NotBlank
    private String topic;

    @NotBlank
    private String content;

    private String status;

    private String reply;

    private Instant createdDate;

    private Instant repliedDate;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getRepliedDate() {
        return repliedDate;
    }

    public void setRepliedDate(Instant repliedDate) {
        this.repliedDate = repliedDate;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
