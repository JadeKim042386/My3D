package joo.project.my3d.config;

import joo.project.my3d.dto.security.BoardPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
public class CustomOAuth2LoginAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url = String.valueOf(request.getRequestURL());
        //로그인과 회원가입 관련 URL에서만 실행
        if (url.contains("account") || url.contains("/mail/send_code")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!Objects.isNull(authentication)) {
                BoardPrincipal boardPrincipal = (BoardPrincipal) authentication.getPrincipal();
                //회원가입이 되지 않은 사용자는 인증 실패
                if (boardPrincipal.notSignUp()) {
                    HttpSession session = request.getSession();
                    session.setAttribute("email", boardPrincipal.email());
                    session.setAttribute("nickname", boardPrincipal.nickname());
                    session.setAttribute("oauthLogin", true);
                    SecurityContextHolder.clearContext();
                }
            }
        } else {
            //로그인과 회원가입에서 사용되는 모든 세션 attribute 삭제
            List<String> sessionAttrs = List.of(
                    "b_no",
                    "b_stt_cd",
                    "email",
                    "nickname",
                    "emailCode",
                    "userRole",
                    "duplicatedEmail"
            );
            HttpSession session = request.getSession();
            for (String sessionAttr : sessionAttrs) {
                session.removeAttribute(sessionAttr);
            }
        }

        filterChain.doFilter(request, response);
    }
}
