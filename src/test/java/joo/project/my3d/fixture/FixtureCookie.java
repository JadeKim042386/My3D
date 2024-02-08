package joo.project.my3d.fixture;

import javax.servlet.http.Cookie;

public class FixtureCookie {

    public static Cookie createUserCookie() {
        Cookie cookie = new Cookie("accessToken", "userToken");
        cookie.setPath("/");
        return cookie;
    }

    public static Cookie createAnonymousCookie() {
        Cookie cookie = new Cookie("accessToken", "anonymousToken");
        cookie.setPath("/");
        return cookie;
    }

    public static Cookie createCompanyCookie() {
        Cookie cookie = new Cookie("accessToken", "companyToken");
        cookie.setPath("/");
        return cookie;
    }
}
