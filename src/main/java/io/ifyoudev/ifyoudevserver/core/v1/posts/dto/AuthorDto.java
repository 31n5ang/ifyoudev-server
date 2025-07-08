package io.ifyoudev.ifyoudevserver.core.v1.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Post의 작성자 정보 DTO 입니다.
 * 보통 Post 정보와 함께 사용됩니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorDto {
    private Long authorId;
    private String authorUuid;
    private String authorEmail;
    private String authorNickname;
}
