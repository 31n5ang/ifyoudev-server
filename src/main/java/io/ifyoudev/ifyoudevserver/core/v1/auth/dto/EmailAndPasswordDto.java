package io.ifyoudev.ifyoudevserver.core.v1.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 인증 요청에 필요한 Dto입니다.
 * @author 31n5ang
 */
public record EmailAndPasswordDto (
    @Email(message = "이메일 주소 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    String email,

    @Length(min = 3, max = 60, message = "비밀번호는 3자 이상 60자 이하여야 합니다.")
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    String password
) {}
