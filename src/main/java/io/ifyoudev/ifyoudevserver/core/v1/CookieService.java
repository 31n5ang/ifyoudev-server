package io.ifyoudev.ifyoudevserver.core.v1;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    private static final int INF = 10 * 365 * 24 * 60 * 60;
    private static final String REFRESH_TOKEN = "refreshToken";

    public void addRefreshTokenCookie(HttpServletResponse response, String tokenValue) {
        Cookie secureCookie = createSecureCookie(REFRESH_TOKEN, tokenValue, INF);
        response.addCookie(secureCookie);
    }

    private Cookie createSecureCookie(String key, String value, int maxAge) {
        Cookie cookie = createCookie(key, value, maxAge);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }

    private Cookie createCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        return cookie;
    }
}
