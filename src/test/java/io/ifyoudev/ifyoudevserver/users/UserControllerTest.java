package io.ifyoudev.ifyoudevserver.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ifyoudev.ifyoudevserver.core.v1.users.UserController;
import io.ifyoudev.ifyoudevserver.core.v1.users.UserRepository;
import io.ifyoudev.ifyoudevserver.core.v1.users.UserService;
import io.ifyoudev.ifyoudevserver.core.v1.users.dto.SignUpDto;
import io.ifyoudev.ifyoudevserver.core.v1.users.dto.SignUpSuccessDto;
import io.ifyoudev.ifyoudevserver.core.v1.users.validator.UniqueEmailValidator;
import org.jooq.generated.tables.pojos.Users;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = UserController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
@Import({UniqueEmailValidator.class})
@AutoConfigureRestDocs
@DisplayName("/api/v1/users")
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    UserService userService;

    @MockitoBean
    UserRepository userRepository;

    @Test
    @DisplayName("/ - 회원가입 성공")
    void signUp() throws Exception {
        // given
        final String email = "test@test.com";
        final String password = "test1234";
        final String nickname = "test";
        final String uuid = UUID.randomUUID().toString();

        SignUpDto signUpDto = new SignUpDto(email, nickname, password);

        when(userService.signUp(signUpDto)).thenReturn(new SignUpSuccessDto(uuid));
        when(userRepository.findOneByEmail(email)).thenReturn(Optional.empty());

        // when: request
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(signUpDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userUuid").value(uuid))

                // then: response
                .andDo(print())
                .andDo(document("users/signUp/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("/ - 회원가입 실패, 이메일과 닉네임 및 비밀번호 형식이 잘못됨")
    void signUpFailedValidation() throws Exception {
        // given
        final String invalidEmail = "test@";
        final String invalidPassword = "test123";
        final String invalidNickname = "a";
        final String uuid = UUID.randomUUID().toString();

        SignUpDto signUpDto = new SignUpDto(invalidEmail, invalidNickname, invalidPassword);

        when(userService.signUp(signUpDto)).thenReturn(new SignUpSuccessDto(uuid));
        when(userRepository.findOneByEmail(signUpDto.email())).thenReturn(Optional.empty());

        // when: request
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(signUpDto)))

                // then: response
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.properties.errors[?(@.field == 'email')]").exists())
                .andExpect(jsonPath("$.properties.errors[?(@.field == 'password')]").exists())
                .andExpect(jsonPath("$.properties.errors[?(@.field == 'nickname')]").exists())
                .andDo(print())
                .andDo(document("users/signUp/fail/invalidArguments",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("/ - 회원가입 실패, 이미 존재하는 이메일")
    void signUpFailedDuplicatedEmail() throws Exception {
        // given
        final String invalidEmail = "test@test.com";
        final String invalidPassword = "test1234";
        final String invalidNickname = "test";
        final String uuid = UUID.randomUUID().toString();

        SignUpDto signUpDto = new SignUpDto(invalidEmail, invalidNickname, invalidPassword);

        when(userService.signUp(signUpDto)).thenReturn(new SignUpSuccessDto(uuid));
        when(userRepository.findOneByEmail(signUpDto.email())).thenReturn(Optional.of(new Users()));

        // when: request
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(signUpDto)))

                // then: response
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.properties.errors[?(@.field == 'email')]").exists())
                .andExpect(jsonPath("$.properties.errors[?(@.message == '이미 사용 중인 이메일입니다.')]").exists())
                .andDo(print())
                .andDo(document("users/signUp/fail/duplicatedEmail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }
}
