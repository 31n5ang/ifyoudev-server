package io.ifyoudev.ifyoudevserver.core.v1.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller <-> Service 계층에서 통신하는 DTO 입니다.
 * @see org.jooq.generated.tables.pojos.Posts
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostViewDto {
    // Post 정보
    private Long postId;
    private String postUuid;
    private String title;
    private String content;
    private String summary;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    // 작성자(User) 정보
    private AuthorDto authorDto;

    // Post Details 정보
    private LocalDateTime deadline;
    private Boolean isOnline;
    private Boolean isCompleted;

    // Location 정보
    private String sido;
    private String sigungu;

    // Tag 정보
    private List<PostTagDto> postTagDtos;
}
