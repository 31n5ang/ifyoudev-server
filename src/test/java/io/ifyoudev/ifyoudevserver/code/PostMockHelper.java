package io.ifyoudev.ifyoudevserver.code;

import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDto;
import org.jooq.DSLContext;
import org.jooq.generated.tables.JPosts;
import org.jooq.generated.tables.records.PostsRecord;

import java.time.LocalDateTime;
import java.util.UUID;

public class PostMockHelper {
    public static PostDto saveAndGetDefaultMockPostDto(Long userId, DSLContext dslContext) {
        PostDto postDto = new PostDto(
                null,
                UUID.randomUUID().toString(),
                "제목입니다.",
                "본문입니다.",
                "요약입니다.",
                userId,
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        PostsRecord postsRecord = dslContext.newRecord(JPosts.POSTS, postDto);
        postsRecord.insert();
        postDto.setPostId(postsRecord.getPostId());
        return postDto;
    }
}
