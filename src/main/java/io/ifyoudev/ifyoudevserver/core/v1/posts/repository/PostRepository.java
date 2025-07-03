package io.ifyoudev.ifyoudevserver.core.v1.posts.repository;

import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDetailsDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDto;

import java.util.List;

public interface PostRepository {

    Long save(PostDto postDto);

    void savePostDetails(String userUuid, PostDetailsDto postDetailsDto);

    void savePostTags(Long postId, List<Integer> postTagIds);
}
