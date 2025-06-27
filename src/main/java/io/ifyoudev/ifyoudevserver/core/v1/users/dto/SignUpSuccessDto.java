package io.ifyoudev.ifyoudevserver.core.v1.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Controller <-> Service DTO입니다.
 */
public record SignUpSuccessDto(
        @JsonIgnore
        Long userId,
        String userUuid
) {
    public SignUpSuccessDto() {
        this((String) null, null);
    }

    public SignUpSuccessDto(String userUuid) {
        this(null, userUuid);
    }

    public SignUpSuccessDto(Long userId) {
        this(userId, null);
    }

    public SignUpSuccessDto(String userUuid, Long userId) {
        this(userId, userUuid);
    }
}
