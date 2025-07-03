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
public class PostDetailsDto {
    private Long postDetailId;
    private Long postId;
    private LocalDateTime deadline;
    private Boolean isOnline;
    private Integer locationId;
}
