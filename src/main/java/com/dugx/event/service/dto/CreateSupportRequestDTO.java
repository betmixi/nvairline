package com.dugx.event.service.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateSupportRequestDTO {

    @NotBlank
    private String topic;

    @NotBlank
    private String content;

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
}
