package io.ifyoudev.ifyoudevserver.posts.query;

import io.ifyoudev.ifyoudevserver.code.PostMockHelper;
import io.ifyoudev.ifyoudevserver.code.UserMockHelper;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostTagDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostViewDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.repository.query.JooqPostQueryRepository;
import io.ifyoudev.ifyoudevserver.core.v1.users.dto.UserDto;
import org.assertj.core.api.Assertions;
import org.jooq.DSLContext;
import org.jooq.generated.tables.JPostTagMap;
import org.jooq.generated.tables.JPostTags;
import org.jooq.generated.tables.records.PostTagMapRecord;
import org.jooq.generated.tables.records.PostTagsRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JooqTest
@Transactional
@Import(JooqPostQueryRepository.class)
public class JooqPostQueryRepositoryTest {

    @Autowired
    DSLContext dslContext;

    @Autowired
    JooqPostQueryRepository jooqPostQueryRepository;

    UserDto mockUserDto;

    @BeforeEach
    void setUp() {
        this.mockUserDto = UserMockHelper.saveAndGetDefaultMockUserDto(dslContext);
    }

    @Test
    @DisplayName("findPostViewDto(..) 성공")
    void findPostViewDtoSuccess() {
        // given
        PostDto postDto = PostMockHelper.saveAndGetDefaultMockPostDto(mockUserDto.getUserId(), dslContext);

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
        Optional<PostViewDto> postViewDto = jooqPostQueryRepository.findPostViewDto(postDto.getUuid());

        // then
        assertThat(postViewDto).isNotEmpty();
        assertThat(postViewDto.get().getPostId()).isEqualTo(postDto.getPostId());
        assertThat(postViewDto.get().getAuthorDto().getAuthorId()).isEqualTo(mockUserDto.getUserId());
        Assertions.assertThat(postViewDto.get().getPostTagDtos().size()).isEqualTo(4);
    }
}
