package joo.project.my3d.config.handler;

import joo.project.my3d.dto.properties.JwtProperties;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.utils.CookieUtils;
import joo.project.my3d.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProperties jwtProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        BoardPrincipal principal = (BoardPrincipal) authentication.getPrincipal();
        String token = JwtTokenUtils.generateToken(
                principal.email(),
                principal.nickname(),
                jwtProperties.secretKey(),
                jwtProperties.expiredTimeMs()
                );
        Cookie cookie = CookieUtils.createCookie(
                jwtProperties.cookieName(),
                token,
                (int) jwtProperties.expiredTimeMs() / 1000,
                "/"
        );
        response.addCookie(cookie);

        getRedirectStrategy().sendRedirect(request, response, "/");
    }
}
