package io.ifyoudev.ifyoudevserver.core.v1.posts;

import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostCreateDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostCreateSuccessDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostViewDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.exception.PostNotFoundException;
import io.ifyoudev.ifyoudevserver.core.v1.posts.repository.query.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostCreateManager postCreateManager;
    private final PostQueryRepository postQueryRepository;

    @Transactional
    public PostCreateSuccessDto createPost(String userUuid, PostCreateDto postCreateDto) {
        return postCreateManager.init(userUuid, postCreateDto).create();
    }

    @Transactional(readOnly = true)
    public PostViewDto findPostView(String postUuid) {
        return postQueryRepository.findPostViewDto(postUuid)
                .orElseThrow(() -> new PostNotFoundException("해당 Post[" + postUuid + "]의 PostView를 조회할 수 없습니다."));
    }
}
