package io.ifyoudev.ifyoudevserver.posts;

import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDetailDto;
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
    @DisplayName("savePostDetail(..) 성공")
    void savePostDetailSuccess() {
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

        PostDetailDto postDetailDto = new PostDetailDto(
                null,
                postId,
                LocalDateTime.now().plusDays(1),
                true,
                null
        );

        // when
        Long postDetailId = jooqPostRepository.savePostDetail(testUserUuid, postDetailDto);

        // then
        assertThat(postDetailId).isNotNull();
    }

    @Test
    @DisplayName("savePostTags(..) 성공")
    void savePostTagsSuccess() {
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

        List<PostTagsRecord> postTagsRecords = List.of(
                dslContext.newRecord(JPostTags.POST_TAGS, new PostTagDto(1L, "Java")),
                dslContext.newRecord(JPostTags.POST_TAGS, new PostTagDto(2L, "Python")),
                dslContext.newRecord(JPostTags.POST_TAGS, new PostTagDto(3L, "C++")),
                dslContext.newRecord(JPostTags.POST_TAGS, new PostTagDto(5L, "Kotlin"))
        );
        dslContext.batchInsert(postTagsRecords).execute();

        PostsRecord postsRecord = dslContext.newRecord(JPosts.POSTS, postDto);
        postsRecord.insert();

        final Long postId = postsRecord.getPostId();
        final List<Integer> postTagIds = List.of(1, 2, 3, 5);

        // when
        jooqPostRepository.savePostTags(postId, postTagIds);
        List<Integer> savedTagIds = dslContext
                .select(JPostTagMap.POST_TAG_MAP.TAG_ID)
                .from(JPostTagMap.POST_TAG_MAP)
                .where(JPostTagMap.POST_TAG_MAP.POST_ID.eq(postId))
                .fetchInto(Integer.class);

        // then
        Assertions.assertThat(savedTagIds).contains(1, 2, 3, 5);
    }
}
