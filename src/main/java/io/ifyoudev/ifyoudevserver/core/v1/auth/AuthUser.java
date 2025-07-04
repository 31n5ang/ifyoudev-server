package io.ifyoudev.ifyoudevserver.core.v1.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class AuthUser implements UserDetails {

    private final boolean isLoginProcess;

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private String userUuid;

    /**
     * Email, Password, UserUuid, Authorities를 모두 알고있다면, 이 메소드를 사용하세요.
     * 로그인(인증) 시, DB 로부터 실제 암호화된 password를 얻게 되므로, 주의해야 합니다.
     * @see SimpleUserDetailsService
     */
    public static AuthUser create(String email, String password, String userUuid, Collection<? extends GrantedAuthority> authorities) {
        AuthUser authUser = new AuthUser(email, password, authorities);
        authUser.setUserUuid(userUuid);
        return authUser;
    }

    private AuthUser(String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = email;
        this.password = password;
        this.authorities = authorities;
        this.isLoginProcess = false;
    }

    public static AuthUser create(String email, String password, String userUuid,
                                  List<String> authorityNames) {
        AuthUser authUser = new AuthUser(email, password, authorityNames);
        authUser.setUserUuid(userUuid);
        return authUser;
    }

    private AuthUser(String email, String password, List<String> authorityNames) {
        this.username = email;
        this.password = password;
        this.authorities = authorityNames.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        this.isLoginProcess = true;
    }

    /**
     * JWT 토큰 인증 시 발급되는 Authentication 객체입니다.
     * 이 메서드로 만든 AuthUser의 password는 'null'입니다.
     * 추가적으로 userUuid는 변경할 수 없습니다.
     */
    public static AuthUser createWithUserUuid(String userUuid, Collection<? extends GrantedAuthority> authorities) {
        return new AuthUser(userUuid, authorities);
    }

    public static AuthUser createWithUserUuid(String userUuid, List<String> authorityNames) {
        Set<SimpleGrantedAuthority> authorities = authorityNames
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new AuthUser(userUuid, authorities);
    }

    private AuthUser(String userUuid, Collection<? extends GrantedAuthority> authorities) {
        this.userUuid = userUuid;
        this.username = userUuid;
        this.authorities = authorities;
        this.isLoginProcess = false;
    }

    public String getUserUuid() {
        if (userUuid == null) {
            throw new IllegalStateException("'userUuid'가 'null'입니다.");
        }
        return this.userUuid;
    }

    public List<String> getAuthorityNames() {
        return getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    private void setUserUuid(String userUuid) {
        if (isLoginProcess) {
            this.userUuid = userUuid;
        } else {
            throw new IllegalStateException("로그인으로부터 생성된 인증정보가 아니므로, userUuid를 변경할 수 없습니다.");
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
