package io.ifyoudev.ifyoudevserver.core.v1.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 범용적으로 쓰이는 DTO입니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {
    private Long postId;
    private String uuid;
    private String title;
    private String content;
    private String summary;
    private Long userId;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}
