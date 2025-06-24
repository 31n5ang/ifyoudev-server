package io.ifyoudev.ifyoudevserver.core.v1.roles;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    USER("ROLE_USER", "USER", "사용자"),
    ADMIN("ROLE_ADMIN", "ADMIN", "관리자"),
    HELPER("ROLE_HELPER", "HELPER", "헬퍼");

    private final String dbName;
    private final String name;
    private final String description;

    public static List<String> toDbNames(List<RoleType> roleTypes) {
        return roleTypes.stream()
                .map(roleType -> roleType.getDbName())
                .collect(Collectors.toList());
    }
}
