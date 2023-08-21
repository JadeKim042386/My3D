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
import java.util.Objects;

@Slf4j
public class CustomOAuth2LoginAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!Objects.isNull(authentication)) {
            BoardPrincipal boardPrincipal = (BoardPrincipal) authentication.getPrincipal();
            //회원가입이 되지 않은 사용자는 인증 실패
            if (boardPrincipal.notSignUp()) {
                HttpSession session = request.getSession();
                session.setAttribute("email", boardPrincipal.email());
                session.setAttribute("nickname", boardPrincipal.nickname());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
