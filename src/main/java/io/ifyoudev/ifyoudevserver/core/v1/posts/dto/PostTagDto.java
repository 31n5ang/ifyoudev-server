package io.ifyoudev.ifyoudevserver.core.v1.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostTagDto {
    private Long tagId;
    private String name;
}
