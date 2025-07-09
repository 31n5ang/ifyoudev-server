package io.ifyoudev.ifyoudevserver.core.v1.posts.repository.query;

import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostViewDto;

import java.util.Optional;

/**
 * 복잡한 쿼리 혹은 특수한 DTO 조회를 담당합니다.
 */
public interface PostQueryRepository {

    Optional<PostViewDto> findPostViewDto(String postUuid);
}
