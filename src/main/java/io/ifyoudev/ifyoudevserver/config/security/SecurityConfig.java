package io.ifyoudev.ifyoudevserver.config.security;

import io.ifyoudev.ifyoudevserver.core.v1.auth.JwtAuthorizationFilter;
import io.ifyoudev.ifyoudevserver.core.v1.auth.JwtTokenVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenVerifier jwtTokenVerifier;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    /**
     * 컨트롤러를 테스트하기 쉽도록 인가 필터는 직접 Bean을 주입합니다.
     */
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(JwtTokenVerifier jwtTokenVerifier) {
        return new JwtAuthorizationFilter(jwtTokenVerifier);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        configureRestApiSecurity(http)
                .authorizeHttpRequests(c -> c
                        .requestMatchers("/**").permitAll()
                );

        http.exceptionHandling(
                c -> c
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
        );

        http.addFilterBefore(jwtAuthorizationFilter(jwtTokenVerifier), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private HttpSecurity configureRestApiSecurity(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }
}
