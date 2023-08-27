package joo.project.my3d.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

public class CookieUtils {

    public static Cookie createCookie(String name, String value, int maxAge, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    public static Cookie getCookieFromRequest(HttpServletRequest request, String name) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
