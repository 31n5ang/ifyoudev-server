package io.ifyoudev.ifyoudevserver.core.v1.posts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller <-> Service DTO입니다.
 * User의 UUID는 인증 정보(토큰)에서 추출해야합니다.
 */
public record PostCreateDto(

        @NotBlank(message = "제목은 필수 항목입니다.")
        @Size(min = 2, max = 255, message = "제목은 {min}자 이상 {max}자 이하여야 합니다.")
        String title,

        @NotBlank(message = "내용은 필수 항목입니다.")
        @Size(min = 10, max = 10000, message = "내용은 {min}자 이상 {max}자 이하여야 합니다.")
        String content,

        LocalDateTime deadline,

        Boolean isOnline,

        Integer locationId,

        List<Long> tagIds
) {}
