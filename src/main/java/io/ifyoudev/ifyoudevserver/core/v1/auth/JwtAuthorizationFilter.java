package io.ifyoudev.ifyoudevserver.core.v1.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenVerifier jwtTokenVerifier;

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = extractAccessToken(request);

        if (!StringUtils.hasText(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        DecodedJwtToken decodedJwtToken = verifyAndDecode(accessToken);

        if (decodedJwtToken == null) {
            filterChain.doFilter(request, response);
            return;
        }


        setAuthentication(decodedJwtToken.getUserUuid(), decodedJwtToken.getAuthorities());

        filterChain.doFilter(request, response);
    }

    private String extractAccessToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        return extractAccessTokenFromHeader(authorizationHeader);
    }

    private String extractAccessTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER)) {
            return null;
        }
        return authorizationHeader.substring(BEARER.length());
    }

    private DecodedJwtToken verifyAndDecode(String accessToken) {
        try {
            return jwtTokenVerifier.verifyAndDecode(accessToken);
        } catch (Exception ex) {
            return null;
        }
    }

    private void setAuthentication(String userUuid, List<GrantedAuthority> authorities) {
        try {
            AuthUser authUser = AuthUser.createWithUserUuid(userUuid, authorities);
            Authentication authentication = new UsernamePasswordAuthenticationToken(authUser, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ex) {
            log.debug("JWT Authorization Failed", ex);
        }
    }
}
