package io.ifyoudev.ifyoudevserver.posts;

import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDetailsDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostTagDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.repository.JooqPostRepository;
import org.assertj.core.api.Assertions;
import org.jooq.DSLContext;
import org.jooq.generated.tables.JPostTagMap;
import org.jooq.generated.tables.JPostTags;
import org.jooq.generated.tables.JPosts;
import org.jooq.generated.tables.JUsers;
import org.jooq.generated.tables.records.PostTagsRecord;
import org.jooq.generated.tables.records.PostsRecord;
import org.jooq.generated.tables.records.UsersRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JooqTest
@Transactional
@Import(JooqPostRepository.class)
class JooqPostRepositoryTest {

    @Autowired
    JooqPostRepository jooqPostRepository;

    @Autowired
    DSLContext dslContext;

    String testUserUuid;
    Long testUserId;

    @BeforeEach
    void setUp() {
        // 테스트용 User 생성
        final JUsers USERS = JUsers.USERS;
        UsersRecord usersRecord = dslContext.newRecord(USERS);
        usersRecord.setUserId(1L);
        usersRecord.setUuid(UUID.randomUUID().toString());
        usersRecord.setEmail("test@test.com");
        usersRecord.setNickname("test");
        usersRecord.setPassword("my_secret_password");
        usersRecord.setCreatedAt(LocalDateTime.now());
        usersRecord.setLastModifiedAt(LocalDateTime.now());
        usersRecord.insert();

        this.testUserId = usersRecord.getUserId();
        this.testUserUuid = usersRecord.getUuid();
    }

    @Test
    @DisplayName("save(..) 성공")
    void saveSuccess() {
        // given
        PostDto postDto = new PostDto(
                null,
                UUID.randomUUID().toString(),
                "제목입니다.",
                "본문입니다.",
                testUserId,
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // when
        Long postId = jooqPostRepository.save(postDto);

        // then
        assertThat(postId).isNotNull();
    }

    @Test
    @DisplayName("savePostDetails(..) 성공")
    void savePostDetailsSuccess() {
        // given
        PostDto postDto = new PostDto(
                null,
                UUID.randomUUID().toString(),
                "제목입니다.",
                "본문입니다.",
                testUserId,
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        PostsRecord postsRecord = dslContext.newRecord(JPosts.POSTS, postDto);
        postsRecord.insert();

        final Long postId = postsRecord.getPostId();

        PostDetailsDto postDetailsDto = new PostDetailsDto(
                null,
                postId,
                LocalDateTime.now().plusDays(1),
                true,
                null
        );

        // when
        Long postDetailsId = jooqPostRepository.savePostDetails(testUserUuid, postDetailsDto);

        // then
        assertThat(postDetailsId).isNotNull();
    }
}
