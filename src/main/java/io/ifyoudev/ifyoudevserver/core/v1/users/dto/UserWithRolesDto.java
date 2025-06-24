package io.ifyoudev.ifyoudevserver.core.v1.users.dto;

import org.jooq.generated.tables.pojos.Users;

import java.util.List;

public record UserWithRolesDto(
        Users users,
        List<String> roleNames
) {}
