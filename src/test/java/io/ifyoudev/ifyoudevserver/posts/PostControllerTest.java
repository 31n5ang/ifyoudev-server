package io.ifyoudev.ifyoudevserver.posts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.ifyoudev.ifyoudevserver.config.TestSecurityConfig;
import io.ifyoudev.ifyoudevserver.core.v1.auth.AuthUser;
import io.ifyoudev.ifyoudevserver.core.v1.posts.PostController;
import io.ifyoudev.ifyoudevserver.core.v1.posts.PostService;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = PostController.class)
@Import({TestSecurityConfig.class})
@AutoConfigureRestDocs
@DisplayName("/api/v1/posts")
public class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PostService postService;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() {
        // Jackson 라이브러리는 Java 8 에서 추가된 LocalDateTime을 직렬화, 역직렬화를 못하기 때문에 따로 등록
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("/{postUuid} - 포스트 조회 200 성공")
    void getPostByPostUuidSuccessTest() throws Exception {
        // given
        String postUuid = UUID.randomUUID().toString();
        AuthorDto authorDto = AuthorDto.builder()
                .authorId(1L)
                .authorUuid("테스트 작성자")
                .build();

        List<PostTagDto> postTagDtos = Arrays.asList(
                PostTagDto.builder().tagId(1L).name("개발").build(),
                PostTagDto.builder().tagId(2L).name("IT").build()
        );

        PostViewDto postViewDto = PostViewDto.builder()
                .postId(1L)
                .postUuid(postUuid)
                .title("제목입니다.")
                .content("이것은 포스트의 내용입니다.")
                .summary("이것은 포스트의 요약입니다.")
                .createdAt(LocalDateTime.now().minusDays(1))
                .lastModifiedAt(LocalDateTime.now())
                .authorDto(authorDto)
                .deadline(LocalDateTime.now().plusDays(7))
                .isOnline(true)
                .isCompleted(false)
                .sido("서울특별시")
                .sigungu("금천구")
                .postTagDtos(postTagDtos)
                .build();

        when(postService.findPostView(eq(postUuid))).thenReturn(postViewDto);

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/posts/view/{postUuid}", postUuid)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.postUuid").value(postViewDto.getPostUuid()))
                .andExpect(jsonPath("$.title").value(postViewDto.getTitle()))
                .andDo(print())
                .andDo(document("posts/view/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postUuid").description("조회할 Post의 UUID")
                        )
                ));
    }

    @Test
    @DisplayName("/ - 포스트 작성 200 성공")
    void createPostSuccessTest() throws Exception {
        // given
        String userUuid = UUID.randomUUID().toString();
        PostCreateDto postCreateDto = createMockPostCreateDto("Ifyoudev 프론트엔드 팀원 모집합니다.", "같이 열심히 해봐요");
        Long postId = 1L;

        PostCreateSuccessDto postCreateSuccessDto = new PostCreateSuccessDto(postId);
        when(postService.createPost(eq(userUuid), eq(postCreateDto))).thenReturn(postCreateSuccessDto);

        AuthUser authUser = AuthUser.createWithUserUuid(userUuid, Collections.singletonList("ROLE_USER"));

        // when
        mockMvc.perform(post("/api/v1/posts")
                        .with(user(authUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreateDto)))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.postId").value(postId))
                .andDo(document("posts/create/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("/ - 포스트 작성 401 실패, 인증 없음")
    void createPostAuthenticateFailedTest() throws Exception {
        // given
        String userUuid = UUID.randomUUID().toString();
        PostCreateDto postCreateDto = createMockPostCreateDto("Ifyoudev 프론트엔드 팀원 모집합니다.", "같이 열심히 해봐요");
        Long postId = 1L;

        PostCreateSuccessDto postCreateSuccessDto = new PostCreateSuccessDto(postId);
        when(postService.createPost(eq(userUuid), eq(postCreateDto))).thenReturn(postCreateSuccessDto);

        // when
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreateDto)))
                // then
                 .andExpect(status().isUnauthorized())
                .andDo(print())
                .andDo(document("posts/create/failed/noAuthentication",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("/ - 포스트 작성 400 실패, title, content 형식 올바르지 않음")
    void createPostInvalidTitleAndContentFailedTest() throws Exception {
        // given
        String userUuid = UUID.randomUUID().toString();
        PostCreateDto postCreateDto = createMockPostCreateDto(" ", " ");
        Long postId = 1L;

        PostCreateSuccessDto postCreateSuccessDto = new PostCreateSuccessDto(postId);
        when(postService.createPost(eq(userUuid), eq(postCreateDto))).thenReturn(postCreateSuccessDto);

        AuthUser authUser = AuthUser.createWithUserUuid(userUuid, Collections.singletonList("ROLE_USER"));

        // when
        mockMvc.perform(post("/api/v1/posts")
                        .with(user(authUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreateDto)))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.properties.errors[?(@.field == 'title')]").exists())
                .andExpect(jsonPath("$.properties.errors[?(@.field == 'content')]").exists())
                .andDo(print())
                .andDo(document("posts/create/failed/titleAndContentInvalidArgument",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    private static PostCreateDto createMockPostCreateDto(String title, String content) {
        return new PostCreateDto(
                title,
                content,
                LocalDateTime.now().plusDays(1),
                false,
                3,
                List.of(1L, 2L)
        );
    }
}
