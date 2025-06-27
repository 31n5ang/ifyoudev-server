package io.ifyoudev.ifyoudevserver.core.v1.users.dto;

import io.ifyoudev.ifyoudevserver.core.v1.roles.RoleType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Service <-> Repository DTO입니다.
 */
@Getter
@Setter
public class UserCreateDto {
    private String uuid;
    private String email;
    private String password;
    private List<RoleType> role;
    private String nickname;
}
