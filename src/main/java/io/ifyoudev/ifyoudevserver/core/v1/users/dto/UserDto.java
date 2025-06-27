package io.ifyoudev.ifyoudevserver.core.v1.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 범용적으로 쓰이는 DTO입니다.
 * 절대로 password 속성을 외부로 유출시키지 마세요.
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long userId;
    private String uuid;
    private String email;
    @JsonIgnore
    private String password;
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}
