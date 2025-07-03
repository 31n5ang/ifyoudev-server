package io.ifyoudev.ifyoudevserver.core.v1.posts;

import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostCreateDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostCreateSuccessDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDetailsDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.exception.PostCreateException;
import io.ifyoudev.ifyoudevserver.core.v1.posts.repository.PostRepository;
import io.ifyoudev.ifyoudevserver.core.v1.users.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
@RequiredArgsConstructor
@Component
@Scope("prototype")
public class PostCreateManager {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private boolean isInitialized = false;
    private boolean isPostIdAcquired = false;

    private String userUuid = null;
    private Long userId = null;
    private Long postId = null;
    private PostCreateDto postCreateDto = null;

    @Transactional(readOnly = true)
    public PostCreateManager init(String userUuid, PostCreateDto postCreateDto) {
        this.userUuid = userUuid;
        this.postCreateDto = postCreateDto;
        this.userId = userRepository.findOneByUuid(userUuid)
                .orElseThrow(() -> new PostCreateException("올바른 User UUID가 아닙니다.")).getUserId();
        this.isInitialized = true;
        return this;
    }

    @Transactional
    public PostCreateSuccessDto create() {
        if (!isInitialized) {
            throw new PostCreateException("먼저 PostCreateManager의 init(..) 해주세요.");
        }

        if (this.userUuid == null || this.postCreateDto == null) {
            throw new PostCreateException("PostCreateManager의 userUuid 또는 postCreateDto가 null입니다.");
        }

        savePost();
        savePostDetails();
        savePostTags();

        return new PostCreateSuccessDto(postId);
    }

    private void savePost() {
        PostDto postDto = new PostDto();
        postDto.setUserId(userId);
        postDto.setUuid(UUID.randomUUID().toString());
        postDto.setContent(postCreateDto.content());
        postDto.setTitle(postCreateDto.title());
        postDto.setCreatedAt(LocalDateTime.now());
        postDto.setLastModifiedAt(LocalDateTime.now());
        postDto.setIsDeleted(false);

        try {
            Long savePostId = postRepository.save(postDto);
            this.postId = savePostId;
            isPostIdAcquired = true;
        } catch (Exception ex) {
            log.error("savePost() 중 실패", ex);
            throw new PostCreateException("DB에 Post 저장 중 실패했습니다.");
        }
    }

    private void savePostDetails() {
        if (!isPostIdAcquired) {
            throw new PostCreateException("Post ID가 존재하지 않습니다.");
        }

        PostDetailsDto postDetailsDto = new PostDetailsDto();
        postDetailsDto.setDeadline(postCreateDto.deadline());
        postDetailsDto.setLocationId(postCreateDto.locationId());
        postDetailsDto.setIsOnline(postCreateDto.isOnline());

        try {
            postRepository.savePostDetails(userUuid, postDetailsDto);
        } catch (Exception ex) {
            log.error("savePostDetails() 중 실패", ex);
            throw new PostCreateException("DB에 PostDetails 저장 중 실패했습니다.");
        }
    }

    private void savePostTags() {
        if (!isPostIdAcquired) {
            throw new PostCreateException("Post ID가 존재하지 않습니다.");
        }

        if (postCreateDto.tagIds() == null || postCreateDto.tagIds().isEmpty() ) {
            return;
        }

        try {
            postRepository.savePostTags(postId, postCreateDto.tagIds());
        } catch (Exception ex) {
            log.error("savePostTags() 중 실패", ex);
            throw new PostCreateException("DB에 PostTags 저장 중 실패했습니다.");
        }
    }
}
