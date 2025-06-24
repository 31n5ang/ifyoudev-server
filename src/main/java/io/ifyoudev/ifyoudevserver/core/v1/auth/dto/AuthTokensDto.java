package io.ifyoudev.ifyoudevserver.core.v1.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record AuthTokensDto(
        String accessToken,

        @JsonIgnore
        String refreshToken
) {}
