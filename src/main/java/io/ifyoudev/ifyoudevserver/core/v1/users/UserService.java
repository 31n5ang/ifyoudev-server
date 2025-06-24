package io.ifyoudev.ifyoudevserver.core.v1.users;

import io.ifyoudev.ifyoudevserver.core.v1.roles.RoleType;
import io.ifyoudev.ifyoudevserver.core.v1.users.dto.SignUpDto;
import io.ifyoudev.ifyoudevserver.core.v1.users.dto.SignUpSuccessDto;
import io.ifyoudev.ifyoudevserver.core.v1.users.exception.business.UserSaveFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.generated.tables.pojos.Users;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignUpSuccessDto signUp(SignUpDto signUpDto) {
        Users users = new Users();
        users.setUuid(UUID.randomUUID().toString());
        users.setEmail(signUpDto.email());
        users.setNickname(signUpDto.nickname());
        users.setPassword(passwordEncoder.encode(signUpDto.password()));
        users.setCreatedAt(LocalDateTime.now());
        users.setLastModifiedAt(LocalDateTime.now());

        try {
            String userUuid = userRepository.saveWithReturningUuid(users);
            userRepository.updateUserRoles(users.getUserId(), List.of(RoleType.USER));
            log.info("사용자 '{}[{}]' signUp 성공", signUpDto.email(), userUuid);
            return new SignUpSuccessDto(userUuid);
        } catch (Exception ex) {
            log.error("사용자 '{}' signUp 실패", signUpDto.email(), ex);
            throw new UserSaveFailedException(signUpDto.email());
        }
    }
}
