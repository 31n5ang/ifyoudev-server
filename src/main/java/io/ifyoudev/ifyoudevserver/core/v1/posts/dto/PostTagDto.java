package io.ifyoudev.ifyoudevserver.core.v1.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 범용적으로 쓰이는 DTO 입니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostTagDto {
    private Long tagId;
    private String name;
}
