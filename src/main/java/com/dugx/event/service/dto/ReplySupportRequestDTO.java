package com.dugx.event.service.dto;

import jakarta.validation.constraints.NotBlank;

public class ReplySupportRequestDTO {

    @NotBlank
    private String reply;

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
