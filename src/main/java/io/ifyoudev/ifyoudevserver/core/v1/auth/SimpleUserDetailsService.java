package io.ifyoudev.ifyoudevserver.core.v1.auth;

import io.ifyoudev.ifyoudevserver.core.v1.auth.exception.EmailNotFoundException;
import io.ifyoudev.ifyoudevserver.core.v1.users.dto.UserWithRolesDto;
import io.ifyoudev.ifyoudevserver.core.v1.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("로그인 시도 '{}'", email);

        UserWithRolesDto userWithRoles = userRepository.findOneWithRolesByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(email + " 이메일이 존재하지 않습니다."));

        return AuthUser.create(
                userWithRoles.users().getEmail(),
                userWithRoles.users().getPassword(),
                userWithRoles.users().getUuid(),
                userWithRoles.roleNames()
        );
    }
}
