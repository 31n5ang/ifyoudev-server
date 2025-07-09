package io.ifyoudev.ifyoudevserver.posts;

import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDetailDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostTagDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.repository.JooqPostRepository;
import io.ifyoudev.ifyoudevserver.code.PostMockHelper;
import org.assertj.core.api.Assertions;
import org.jooq.DSLContext;
import org.jooq.generated.tables.*;
import org.jooq.generated.tables.records.PostTagMapRecord;
import org.jooq.generated.tables.records.PostTagsRecord;
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
import java.util.Optional;
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

    PostDto saveAndGetMockPostDto() {
        return PostMockHelper.saveAndGetDefaultMockPostDto(testUserId, dslContext);
    }

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
                "요약입니다.",
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
        PostDto postDto = saveAndGetMockPostDto();

        final Long postId = postDto.getPostId();

        PostDetailDto postDetailDto = new PostDetailDto(
                null,
                postId,
                LocalDateTime.now().plusDays(1),
                true,
                null,
                false
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
        PostDto postDto = saveAndGetMockPostDto();

        List<PostTagsRecord> postTagsRecords = List.of(
                dslContext.newRecord(JPostTags.POST_TAGS, new PostTagDto(1L, "Java")),
                dslContext.newRecord(JPostTags.POST_TAGS, new PostTagDto(2L, "Python")),
                dslContext.newRecord(JPostTags.POST_TAGS, new PostTagDto(3L, "C++")),
                dslContext.newRecord(JPostTags.POST_TAGS, new PostTagDto(5L, "Kotlin"))
        );
        dslContext.batchInsert(postTagsRecords).execute();


        final Long postId = postDto.getPostId();
        final List<Long> postTagIds = List.of(1L, 2L, 3L, 5L);

        // when
        jooqPostRepository.savePostTags(postId, postTagIds);
        List<Long> savedTagIds = dslContext
                .select(JPostTagMap.POST_TAG_MAP.TAG_ID)
                .from(JPostTagMap.POST_TAG_MAP)
                .where(JPostTagMap.POST_TAG_MAP.POST_ID.eq(postId))
                .fetchInto(Long.class);

        // then
        Assertions.assertThat(savedTagIds).contains(1L, 2L, 3L, 5L);
    }

    @Test
    @DisplayName("findPostDtoByUuid(..) 성공")
    void findPostDtoByUuidSuccess() {
        // given
        PostDto postDto = saveAndGetMockPostDto();

        // when
        Optional<PostDto> optionalPostDtoByUuid = jooqPostRepository.findPostDtoByUuid(postDto.getUuid());

        // then
        assertThat(optionalPostDtoByUuid).isNotEmpty();
        assertThat(optionalPostDtoByUuid.get().getPostId()).isEqualTo(postDto.getPostId());
    }

    @Test
    @DisplayName("findPostDetailDtoByUuid(..) 성공")
    void findPostDetailDtoByUuidSuccess() {
        // given
        PostDto postDto = saveAndGetMockPostDto();

        PostDetailDto postDetailDto = PostDetailDto.builder()
                .postId(postDto.getPostId())
                .deadline(LocalDateTime.now().plusDays(1))
                .isOnline(true)
                .locationId(null)
                .isCompleted(false)
                .build();

        dslContext.newRecord(JPostDetails.POST_DETAILS, postDetailDto).insert();

        // when
        Optional<PostDetailDto> findPostDetailDto = jooqPostRepository.findPostDetailDtoByUuid(postDto.getUuid());

        // then
        assertThat(findPostDetailDto).isNotEmpty();
        assertThat(findPostDetailDto.get().getPostDetailId()).isNotNull();
        assertThat(findPostDetailDto.get().getPostId()).isEqualTo(postDto.getPostId());
    }

    @Test
    @DisplayName("findAllPostTagDto(..) 성공")
    void findAllPostTagDtoSuccess() {
        // given
        PostDto postDto = saveAndGetMockPostDto();

        List<PostTagsRecord> postTagsRecords = List.of(
                dslContext.newRecord(JPostTags.POST_TAGS, new PostTagDto(1L, "Java")),
                dslContext.newRecord(JPostTags.POST_TAGS, new PostTagDto(2L, "Python")),
                dslContext.newRecord(JPostTags.POST_TAGS, new PostTagDto(3L, "C++")),
                dslContext.newRecord(JPostTags.POST_TAGS, new PostTagDto(5L, "Kotlin"))
        );
        dslContext.batchInsert(postTagsRecords).execute();

        List<PostTagMapRecord> postTagMapRecords = List.of(
                dslContext.newRecord(JPostTagMap.POST_TAG_MAP).setPostId(postDto.getPostId()).setTagId(1L),
                dslContext.newRecord(JPostTagMap.POST_TAG_MAP).setPostId(postDto.getPostId()).setTagId(2L),
                dslContext.newRecord(JPostTagMap.POST_TAG_MAP).setPostId(postDto.getPostId()).setTagId(3L),
                dslContext.newRecord(JPostTagMap.POST_TAG_MAP).setPostId(postDto.getPostId()).setTagId(5L)
        );
        dslContext.batchInsert(postTagMapRecords).execute();


        // when
        List<PostTagDto> postTagDtos = jooqPostRepository.findAllPostTagDto(postDto.getUuid());
        List<Long> postTagIds = postTagDtos.stream().map(PostTagDto::getTagId).toList();

        // then
        Assertions.assertThat(postTagIds).contains(1L, 2L, 3L, 5L);
    }
}
