package io.ifyoudev.ifyoudevserver.core.v1.users;

import io.ifyoudev.ifyoudevserver.core.v1.roles.RoleType;
import io.ifyoudev.ifyoudevserver.core.v1.users.dto.UserCreateDto;
import io.ifyoudev.ifyoudevserver.core.v1.users.dto.UserDto;
import io.ifyoudev.ifyoudevserver.core.v1.users.dto.UserWithRolesDto;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Long saveWithReturningId(UserCreateDto userCreateDto);

    void updateUserRoles(Long userId, List<RoleType> roleTypes);

    Optional<UserDto> findOneByEmail(String email);

    Optional<UserDto> findOneByUuid(String uuid);

    Optional<UserWithRolesDto> findOneWithRolesByEmail(String email);

    List<String> findRoleNamesByEmail(String email);

    List<String> findRoleNamesByUuid(String uuid);
}
