package io.ifyoudev.ifyoudevserver.posts;

import io.ifyoudev.ifyoudevserver.core.v1.posts.PostCreateManager;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostCreateDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.exception.PostCreateException;
import io.ifyoudev.ifyoudevserver.core.v1.posts.repository.PostRepository;
import io.ifyoudev.ifyoudevserver.core.v1.users.UserRepository;
import io.ifyoudev.ifyoudevserver.core.v1.users.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class PostCreateManagerTest {

    PostRepository postRepository = Mockito.mock(PostRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);

    @Test
    @DisplayName("init() 초기화 성공, Post 저장은 안된 상태")
    void initTest() {
        // given
        String userUuid = UUID.randomUUID().toString();
        UserDto userDto = new UserDto();
        userDto.setUuid(userUuid);
        userDto.setUserId(1L);
        PostCreateDto postCreateDto = new PostCreateDto(
                "Ifyoudev 프론트엔드 팀원 모집합니다.",
                "같이 열심히 해봐요",
                LocalDateTime.now().plusDays(1),
                false,
                3,
                List.of(1, 2)
        );
        PostCreateManager postCreateManager = new PostCreateManager(postRepository, userRepository);
        when(userRepository.findOneByUuid(eq(userUuid))).thenReturn(Optional.of(userDto));

        // when
        postCreateManager.init(userUuid, postCreateDto);

        // then
        assertThat(postCreateManager.getUserUuid()).isEqualTo(userUuid);
        assertThat(postCreateManager.getPostCreateDto()).isEqualTo(postCreateDto);
        assertThat(postCreateManager.getUserId()).isEqualTo(userDto.getUserId());
        assertThat(postCreateManager.isInitialized()).isTrue();
        assertThat(postCreateManager.isPostIdAcquired()).isFalse();
    }

    @Test
    @DisplayName("init() 후 create() 성공")
    void initAndCreateTest() {
        // given
        String userUuid = UUID.randomUUID().toString();
        UserDto userDto = new UserDto();
        userDto.setUuid(userUuid);
        userDto.setUserId(1L);
        PostCreateDto postCreateDto = new PostCreateDto(
                "Ifyoudev 프론트엔드 팀원 모집합니다.",
                "같이 열심히 해봐요",
                LocalDateTime.now().plusDays(1),
                false,
                3,
                List.of(1, 2)
        );
        Long postId = 1L;
        PostCreateManager postCreateManager = new PostCreateManager(postRepository, userRepository);
        when(userRepository.findOneByUuid(eq(userUuid))).thenReturn(Optional.of(userDto));
        when(postRepository.save(any(PostDto.class))).thenReturn(postId);

        // when
        postCreateManager.init(userUuid, postCreateDto).create();

        // then
        assertThat(postCreateManager.getUserUuid()).isEqualTo(userUuid);
        assertThat(postCreateManager.getPostCreateDto()).isEqualTo(postCreateDto);
        assertThat(postCreateManager.getUserId()).isEqualTo(userDto.getUserId());
        assertThat(postCreateManager.isInitialized()).isTrue();
        assertThat(postCreateManager.isPostIdAcquired()).isTrue();
        assertThat(postCreateManager.getPostId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("init()없이 create()해서 실패")
    void withoutInitAndCreateFailedTest() {
        // given
        PostCreateManager postCreateManager = new PostCreateManager(postRepository, userRepository);

        // when & then
        assertThrows(PostCreateException.class, () -> postCreateManager.create());
    }
}
