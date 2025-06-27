package io.ifyoudev.ifyoudevserver.core.v1.users;

import io.ifyoudev.ifyoudevserver.core.v1.roles.RoleType;
import io.ifyoudev.ifyoudevserver.core.v1.users.dto.UserCreateDto;
import io.ifyoudev.ifyoudevserver.core.v1.users.dto.UserWithRolesDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.generated.tables.JRoles;
import org.jooq.generated.tables.JUserRoleMap;
import org.jooq.generated.tables.JUsers;
import org.jooq.generated.tables.pojos.Users;
import org.jooq.generated.tables.records.UserRoleMapRecord;
import org.jooq.generated.tables.records.UsersRecord;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JooqUserRepository implements UserRepository {

    private final DSLContext dslContext;
    private final JUsers USERS = JUsers.USERS;

    @Override
    public Long saveWithReturningId(UserCreateDto userCreateDto) {
        UsersRecord usersRecord = dslContext.newRecord(USERS, userCreateDto);
        usersRecord.setCreatedAt(LocalDateTime.now());
        usersRecord.setLastModifiedAt(LocalDateTime.now());
        usersRecord.insert();
        return usersRecord.getUserId();
    }

    @Override
    public void updateUserRoles(Long userId, List<RoleType> roleTypes) {
        final JRoles ROLES = JRoles.ROLES;
        final JUserRoleMap USER_ROLE = JUserRoleMap.USER_ROLE_MAP;

        List<String> roleNames = RoleType.toDbNames(roleTypes);

        Result<Record1<Long>> roleIds = dslContext
                .select(ROLES.ROLE_ID)
                .from(ROLES)
                .where(ROLES.NAME.in(roleNames))
                .fetch();

        dslContext.deleteFrom(USER_ROLE)
                .where(USER_ROLE.USER_ID.eq(userId));

        List<UserRoleMapRecord> records = roleIds.stream()
                .map(record -> {
                    Long roleId = record.value1();
                    UserRoleMapRecord userRoleMapRecord = dslContext.newRecord(USER_ROLE);
                    userRoleMapRecord.setUserId(userId);
                    userRoleMapRecord.setRoleId(roleId);
                    return userRoleMapRecord;
                }).collect(Collectors.toList());

        dslContext.batchInsert(records).execute();
    }

    @Override
    public Optional<Users> findOneByEmail(String email) {
        return dslContext
                .selectFrom(USERS)
                .where(USERS.EMAIL.eq(email))
                .fetchOptionalInto(Users.class);
    }

    @Override
    public Optional<Users> findOneByUuid(String uuid) {
        return dslContext
                .selectFrom(USERS)
                .where(USERS.UUID.eq(uuid))
                .fetchOptionalInto(Users.class);
    }

    @Override
    public Optional<UserWithRolesDto> findOneWithRolesByEmail(String email) {
        final JRoles ROLES = JRoles.ROLES;
        final JUserRoleMap USER_ROLE = JUserRoleMap.USER_ROLE_MAP;

        return dslContext
                .select(USERS.fields())
                .select(DSL.groupConcat(ROLES.NAME).as("roleNames"))
                .from(USERS)
                .leftJoin(USER_ROLE).on(USER_ROLE.USER_ID.eq(USERS.USER_ID))
                .leftJoin(ROLES).on(ROLES.ROLE_ID.eq(USER_ROLE.ROLE_ID))
                .where(USERS.EMAIL.eq(email))
                .groupBy(USERS.USER_ID)
                .fetchOptional(record -> {
                    Users users = record.into(USERS).into(Users.class);
                    String roleNamesString = record.get("roleNames", String.class);
                    List<String> roleNames = List.of(roleNamesString.split(","));
                    return new UserWithRolesDto(users, roleNames);
                });
    }

    @Override
    public List<String> findRoleNamesByEmail(String email) {
        final JRoles ROLES = JRoles.ROLES;
        final JUserRoleMap USER_ROLE = JUserRoleMap.USER_ROLE_MAP;

        return dslContext
                .select(ROLES.NAME)
                .from(ROLES)
                .join(USER_ROLE).on(USER_ROLE.ROLE_ID.eq(ROLES.ROLE_ID))
                .join(USERS).on(USERS.USER_ID.eq(USER_ROLE.USER_ID))
                .where(USERS.EMAIL.eq(email))
                .fetch(ROLES.NAME);
    }

    @Override
    public List<String> findRoleNamesByUuid(String uuid) {
        final JRoles ROLES = JRoles.ROLES;
        final JUserRoleMap USER_ROLE = JUserRoleMap.USER_ROLE_MAP;

        return dslContext
                .select(ROLES.NAME)
                .from(ROLES)
                .join(USER_ROLE).on(USER_ROLE.ROLE_ID.eq(ROLES.ROLE_ID))
                .join(USERS).on(USERS.USER_ID.eq(USER_ROLE.USER_ID))
                .where(USERS.UUID.eq(uuid))
                .fetch(ROLES.NAME);
    }
}
