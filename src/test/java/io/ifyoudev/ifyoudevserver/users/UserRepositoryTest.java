package io.ifyoudev.ifyoudevserver.users;

import io.ifyoudev.ifyoudevserver.core.v1.roles.RoleType;
import io.ifyoudev.ifyoudevserver.core.v1.users.UserRepository;
import org.jooq.DSLContext;
import org.jooq.generated.tables.JUsers;
import org.jooq.generated.tables.pojos.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    DSLContext dslContext;

    @Autowired
    private UserRepository userRepository;

    Users testUser;

    @BeforeEach
    void setUp() {
        final JUsers USERS = JUsers.USERS;
        testUser = dslContext.insertInto(USERS, USERS.UUID, USERS.EMAIL, USERS.NICKNAME, USERS.PASSWORD)
                .values(UUID.randomUUID().toString(), "test@test.com", "test", "test1234")
                .returning(USERS.fields()) // 삽입 후 user_id 반환
                .fetchOneInto(Users.class);

        System.out.println(testUser.getEmail());
    }

    @Test
    @DisplayName("사용자 Role Update")
    void update() {
        // given
        List<RoleType> roleTypes = List.of(RoleType.USER, RoleType.HELPER);

        // when
        userRepository.updateUserRoles(testUser.getUserId(), roleTypes);

        // then
        List<String> roleNamesByEmail = userRepository.findRoleNamesByEmail(testUser.getEmail());
        System.out.println(roleNamesByEmail);
        assertThat(roleNamesByEmail).contains("ROLE_USER", "ROLE_HELPER");
        assertThat(roleNamesByEmail).hasSize(2);
    }
}
