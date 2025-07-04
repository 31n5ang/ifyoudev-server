package io.ifyoudev.ifyoudevserver.core.v1.posts.repository;

import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDetailDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDto;

import java.util.List;

public interface PostRepository {

    Long save(PostDto postDto);

    Long savePostDetail(String userUuid, PostDetailDto postDetailDto);

    Long savePostTags(Long postId, List<Integer> postTagIds);
}
