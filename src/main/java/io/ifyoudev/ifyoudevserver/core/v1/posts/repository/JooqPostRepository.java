package io.ifyoudev.ifyoudevserver.core.v1.posts.repository;

import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDetailDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.generated.tables.JPostDetails;
import org.jooq.generated.tables.JPostTagMap;
import org.jooq.generated.tables.JPostTags;
import org.jooq.generated.tables.JPosts;
import org.jooq.generated.tables.records.PostDetailsRecord;
import org.jooq.generated.tables.records.PostTagMapRecord;
import org.jooq.generated.tables.records.PostsRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JooqPostRepository implements PostRepository {

    private final DSLContext dslContext;
    private final JPosts POSTS = JPosts.POSTS;
    private final JPostDetails POST_DETAILS = JPostDetails.POST_DETAILS;
    private final JPostTagMap POST_TAG_MAP = JPostTagMap.POST_TAG_MAP;
    private final JPostTags POST_TAGS = JPostTags.POST_TAGS;

    @Override
    public Long save(PostDto postDto) {
        PostsRecord postsRecord = dslContext.newRecord(POSTS, postDto);
        postsRecord.insert();
        return postsRecord.getPostId();
    }

    @Override
    public Long savePostDetail(String userUuid, PostDetailDto postDetailDto) {
        PostDetailsRecord postDetailsRecord = dslContext.newRecord(POST_DETAILS, postDetailDto);
        postDetailsRecord.insert();
        return postDetailsRecord.getPostDetailId();
    }

    @Override
    public void savePostTags(Long postId, List<Long> postTagIds) {
        List<PostTagMapRecord> postTagMapRecords = postTagIds.stream()
                .map(postTagId -> {
                    PostTagMapRecord postTagMapRecord = dslContext.newRecord(POST_TAG_MAP);
                    postTagMapRecord.setPostId(postId);
                    postTagMapRecord.setTagId(postTagId);
                    return postTagMapRecord;
                })
                .toList();
        dslContext.batchInsert(postTagMapRecords).execute();
    }
}
