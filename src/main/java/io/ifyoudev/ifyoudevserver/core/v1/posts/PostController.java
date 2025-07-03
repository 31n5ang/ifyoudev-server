package io.ifyoudev.ifyoudevserver.core.v1.posts;

import io.ifyoudev.ifyoudevserver.core.v1.auth.AuthUser;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostCreateDto;
import io.ifyoudev.ifyoudevserver.core.v1.posts.dto.PostCreateSuccessDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<?> getPosts() {
        return null;
    }

    @Secured("ROLE_USER")
    @PostMapping
    public ResponseEntity<?> createPost(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody PostCreateDto postCreateDto
    ) {
        PostCreateSuccessDto postCreateSuccessDto = postService.createPost(authUser.getUserUuid(), postCreateDto);
        return ResponseEntity.ok(postCreateSuccessDto);
    }
}
