package com.company.chat.dto;

import jakarta.validation.constraints.NotNull;

public class AddGroupMemberRequest {

    @NotNull
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
