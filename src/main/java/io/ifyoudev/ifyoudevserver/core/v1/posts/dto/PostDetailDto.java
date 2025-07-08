package io.ifyoudev.ifyoudevserver.core.v1.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * 범용적으로 쓰이는 DTO 입니다.
 * @see org.jooq.generated.tables.pojos.PostDetails
 */
public class PostDetailDto {
    private Long postDetailId;
    private Long postId;
    private LocalDateTime deadline;
    private Boolean isOnline;
    private Integer locationId;
    private Boolean isCompleted;
}
