package io.ifyoudev.ifyoudevserver.core.v1.posts.repository.query;

import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostViewDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.tables.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JooqPostQueryRepository implements PostQueryRepository{

    private final DSLContext dslContext;

    private final JPosts POSTS = JPosts.POSTS;
    private final JPostDetails POST_DETAILS = JPostDetails.POST_DETAILS;
    private final JPostTags POST_TAGS = JPostTags.POST_TAGS;
    private final JPostTagMap POST_TAG_MAP = JPostTagMap.POST_TAG_MAP;
    private final JUsers USERS = JUsers.USERS;
    private final JLocations LOCATIONS = JLocations.LOCATIONS;

    /**
     * PostView DTO 조회 쿼리입니다.
     */
    @Override
    public Optional<PostViewDto> findPostViewDto(String postUuid) {
        return dslContext
                .select(
                        POSTS.POST_ID,
                        POSTS.UUID,
                        POSTS.TITLE,
                        POSTS.CONTENT,
                        POSTS.SUMMARY,
                        POSTS.CREATED_AT,
                        POSTS.LAST_MODIFIED_AT,
                        USERS.USER_ID.as("authorDto.authorId"),
                        USERS.UUID.as("authorDto.authorUuid"),
                        USERS.EMAIL.as("authorDto.authorEmail"),
                        USERS.NICKNAME.as("authorDto.authorNickname"),
                        POST_DETAILS.DEADLINE,
                        POST_DETAILS.IS_ONLINE,
                        POST_DETAILS.IS_COMPLETED,
                        LOCATIONS.SIDO,
                        LOCATIONS.SIGUNGU,
                        DSL.multiset(
                                dslContext
                                        .select(
                                            POST_TAGS.TAG_ID,
                                            POST_TAGS.NAME
                                        )
                                        .from(POST_TAG_MAP)
                                        .join(POST_TAGS).on(POST_TAG_MAP.TAG_ID.eq(POST_TAGS.TAG_ID))
                                        .where(POST_TAG_MAP.POST_ID.eq(POSTS.POST_ID))
                        ).as("postTagDtos")
                )
                .from(POSTS)
                .join(USERS).on(POSTS.USER_ID.eq(USERS.USER_ID))
                .leftJoin(POST_DETAILS).on(POSTS.POST_ID.eq(POST_DETAILS.POST_ID))
                .leftJoin(LOCATIONS).on(POST_DETAILS.LOCATION_ID.eq(LOCATIONS.LOCATION_ID))
                .leftJoin(POST_TAG_MAP).on(POSTS.POST_ID.eq(POST_TAG_MAP.POST_ID))
                .leftJoin(POST_TAGS).on(POST_TAG_MAP.TAG_ID.eq(POST_TAGS.TAG_ID))
                .where(POSTS.UUID.eq(postUuid))
                .groupBy(
                        POSTS.POST_ID, // All non-aggregated columns must be in GROUP BY
                        POSTS.UUID,
                        POSTS.TITLE,
                        POSTS.CONTENT,
                        POSTS.SUMMARY,
                        POSTS.CREATED_AT,
                        POSTS.LAST_MODIFIED_AT,
                        USERS.USER_ID,
                        USERS.UUID,
                        USERS.EMAIL,
                        USERS.NICKNAME,
                        POST_DETAILS.DEADLINE,
                        POST_DETAILS.IS_ONLINE,
                        POST_DETAILS.IS_COMPLETED,
                        LOCATIONS.LOCATION_ID, // Include location fields in GROUP BY
                        LOCATIONS.SIDO,
                        LOCATIONS.SIGUNGU
                )
                .fetchOptionalInto(PostViewDto.class);
    }
}
