package joo.project.my3d.utils;

import javax.servlet.http.Cookie;

public class CookieUtils {

    public static Cookie createCookie(String name, String value, int maxAge, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}
