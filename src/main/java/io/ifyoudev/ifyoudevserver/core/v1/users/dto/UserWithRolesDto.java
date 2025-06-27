package io.ifyoudev.ifyoudevserver.core.v1.users.dto;

import java.util.List;

public record UserWithRolesDto(
        UserDto userDto,
        List<String> roleNames
) {}
