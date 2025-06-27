package io.ifyoudev.ifyoudevserver.core.v1.users;

import io.ifyoudev.ifyoudevserver.core.v1.roles.RoleType;
import io.ifyoudev.ifyoudevserver.core.v1.users.dto.SignUpDto;
import io.ifyoudev.ifyoudevserver.core.v1.users.dto.SignUpSuccessDto;
import io.ifyoudev.ifyoudevserver.core.v1.users.dto.UserCreateDto;
import io.ifyoudev.ifyoudevserver.core.v1.users.exception.business.UserSaveFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUuid(UUID.randomUUID().toString());
        userCreateDto.setEmail(signUpDto.email());
        userCreateDto.setNickname(signUpDto.nickname());
        userCreateDto.setPassword(passwordEncoder.encode(signUpDto.password()));

        try {
            Long userId = userRepository.saveWithReturningId(userCreateDto);
            userRepository.updateUserRoles(userId, List.of(RoleType.USER));
            log.info("사용자 '{}[{}]' signUp 성공", signUpDto.email(), userId);
            return new SignUpSuccessDto(userId, userCreateDto.getUuid());
        } catch (Exception ex) {
            log.error("사용자 '{}' signUp 실패", signUpDto.email(), ex);
            throw new UserSaveFailedException(signUpDto.email());
        }
    }
}
