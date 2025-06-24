package io.ifyoudev.ifyoudevserver.core.v1.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AuthUser extends User {

    private String userUuid;

    private final boolean isLoginProcess;

    /**
     * Email, Password, UserUuid, Authorities를 모두 알고있다면, 이 메소드를 사용하세요.
     *
     */
    public static AuthUser create(String email, String password, String userUuid, Collection<? extends GrantedAuthority> authorities) {
        AuthUser authUser = new AuthUser(email, password, authorities);
        authUser.setUserUuid(userUuid);
        return authUser;
    }

    private AuthUser(String email, String password, Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
        this.userUuid = null;
        this.isLoginProcess = true;
    }

    public static AuthUser create(String email, String password, String userUuid,
                                  List<String> authorityNames) {
        AuthUser authUser = new AuthUser(email, password, authorityNames);
        authUser.setUserUuid(userUuid);
        return authUser;
    }

    private AuthUser(String email, String password, List<String> authorityNames) {
        super(email, password, authorityNames.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));
        this.userUuid = null;
        this.isLoginProcess = true;
    }

    /**
     * 로그인(인증) 시 발급되는 Authentication 객체입니다.
     * 이 메서드로 만든 AuthUser의 userUuid가 'null' 이므로 따로 설정해주어야 합니다.
     */
    public static AuthUser createWithEmailAndPassword(String email, String password, Collection<?
            extends GrantedAuthority> authorities) {
        return new AuthUser(email, password, authorities);
    }

    public static AuthUser createWithEmailAndPassword(String email, String password, List<String> authorityNames) {
        Set<SimpleGrantedAuthority> authorities = authorityNames
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        return new AuthUser(email, password, authorities);
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
        super(userUuid, "N/A", authorities);
        this.userUuid = userUuid;
        this.isLoginProcess = false;
    }

    public String getUserUuid() {
        if (userUuid == null) {
            throw new IllegalStateException("'userUuid'가 'null'입니다.");
        }
        return this.userUuid;
    }

    public void setUserUuid(String userUuid) {
        if (this.isLoginProcess) {
            this.userUuid = userUuid;
        } else {
            throw new IllegalStateException("JWT 토큰 인증을 위한 AuthUser는 'userUuid'를 변경할 수 없습니다.");
        }
    }

    public List<String> getAuthorityNames() {
        return super.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    public Set<String> getAuthorityNamesWithSet() {
        return super.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }
}
