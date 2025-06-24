package io.ifyoudev.ifyoudevserver.core.v1.users;

import io.ifyoudev.ifyoudevserver.core.v1.users.dto.SignUpDto;
import io.ifyoudev.ifyoudevserver.core.v1.users.dto.SignUpSuccessDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> signUp(
            @Valid @RequestBody SignUpDto signUpDto
    ) {
        SignUpSuccessDto signUpSuccessDto = userService.signUp(signUpDto);
        return ResponseEntity.ok(signUpSuccessDto);
    }
}
