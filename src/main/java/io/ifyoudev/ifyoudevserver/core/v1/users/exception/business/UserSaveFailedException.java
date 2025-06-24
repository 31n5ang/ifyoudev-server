package io.ifyoudev.ifyoudevserver.core.v1.users.exception.business;

import io.ifyoudev.ifyoudevserver.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class UserSaveFailedException extends BusinessException {

    private static final URI TYPE = URI.create("https://ifyoudev.io/problems/user-save-failed");
    private static final String TITLE = "User Save Failed";

    public UserSaveFailedException(String email) {
        super(
                "사용자 '" + email + "' 저장 중에 실패했습니다.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                TYPE,
                TITLE
        );
    }
}
