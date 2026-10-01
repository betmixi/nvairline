package com.dugx.event.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * UC Lien he ho tro: khach hang gui yeu cau ho tro theo chu de, quan tri vien phan hoi lai.
 */
@Entity
@Table(name = "support_request")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SupportRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "topic", nullable = false)
    private String topic;

    @NotBlank
    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "reply", columnDefinition = "text")
    private String reply;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "replied_date")
    private Instant repliedDate;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "authorities" }, allowSetters = true)
    private User user;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReply() {
        return this.reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getRepliedDate() {
        return this.repliedDate;
    }

    public void setRepliedDate(Instant repliedDate) {
        this.repliedDate = repliedDate;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SupportRequest)) {
            return false;
        }
        return getId() != null && getId().equals(((SupportRequest) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "SupportRequest{" + "id=" + getId() + ", topic='" + getTopic() + "'" + ", status='" + getStatus() + "'" + "}";
    }
}
