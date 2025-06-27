package io.ifyoudev.ifyoudevserver.core.v1.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Controller <-> Service DTO입니다.
 */
public record SignUpSuccessDto(
        String userUuid
) {}
