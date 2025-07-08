package io.ifyoudev.ifyoudevserver.core.v1.posts.repository;

import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDetailDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostTagDto;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Long save(PostDto postDto);

    Long savePostDetail(String userUuid, PostDetailDto postDetailDto);

    void savePostTags(Long postId, List<Long> postTagIds);

    Optional<PostDto> findPostDtoByUuid(String postUuid);

    Optional<PostDetailDto> findPostDetailDtoByUuid(String postUuid);

    List<PostTagDto> findAllPostTagDto(String postUuid);
}
