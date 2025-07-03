package io.ifyoudev.ifyoudevserver.core.v1.posts;

import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostCreateDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostCreateSuccessDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostCreateManager postCreateManager;

    @Transactional
    public PostCreateSuccessDto createPost(String userUuid, PostCreateDto postCreateDto) {
        return postCreateManager.init(userUuid, postCreateDto).create();
    }
}
