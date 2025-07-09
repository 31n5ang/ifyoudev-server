package io.ifyoudev.ifyoudevserver.code;

import io.ifyoudev.ifyoudevserver.core.v1.users.dto.UserDto;
import org.jooq.DSLContext;
import org.jooq.generated.tables.JUsers;
import org.jooq.generated.tables.records.UsersRecord;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserMockHelper {
    public static UserDto saveAndGetDefaultMockUserDto(DSLContext dslContext) {
        final JUsers USERS = JUsers.USERS;
        UsersRecord usersRecord = dslContext.newRecord(USERS);
        usersRecord.setUserId(1L);
        usersRecord.setUuid(UUID.randomUUID().toString());
        usersRecord.setEmail("test@test.com");
        usersRecord.setNickname("test");
        usersRecord.setPassword("my_secret_password");
        usersRecord.setCreatedAt(LocalDateTime.now());
        usersRecord.setLastModifiedAt(LocalDateTime.now());
        usersRecord.insert();
        return usersRecord.into(UserDto.class);
    }
}
