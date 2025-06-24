package io.ifyoudev.ifyoudevserver.core.v1.users.dto;

import io.ifyoudev.ifyoudevserver.core.v1.users.validator.annotation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpDto (
        @Email(message = "이메일 형식을 지켜주세요.")
        @NotBlank(message = "이메일은 필수 항목입니다.")
        @UniqueEmail
        String email,

        @Size(min = 2, max = 15, message = "닉네임의 길이는 2자 이상 15자 이하여야 합니다.")
        @NotBlank(message = "닉네임은 필수 항목입니다.")
        String nickname,

        @Size(min = 8, max = 30, message = "비밀번호의 길이는 8자 이상 30자 이하여야 합니다.")
        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        String password
) {}
