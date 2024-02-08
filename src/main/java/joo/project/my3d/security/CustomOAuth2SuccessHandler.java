package joo.project.my3d.security;

import joo.project.my3d.dto.security.BoardPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        BoardPrincipal principal = (BoardPrincipal) authentication.getPrincipal();

        FlashMap flashMap = new FlashMap();
        flashMap.put("email", principal.email());
        flashMap.put("nickname", principal.nickname());
        flashMap.put("signup", !Objects.isNull(principal.id()));
        new SessionFlashMapManager().saveOutputFlashMap(flashMap, request, response);
        this.getRedirectStrategy().sendRedirect(request, response, "/oauth");
    }
}
