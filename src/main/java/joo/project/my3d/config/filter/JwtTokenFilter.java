package joo.project.my3d.config.filter;

import joo.project.my3d.dto.properties.JwtProperties;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.UserAccountService;
import joo.project.my3d.utils.CookieUtils;
import joo.project.my3d.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserAccountService userAccountService;
    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String token;

        //회원가입관련 URL일 경우 생략
        String url = request.getRequestURL().toString();
        if (url.startsWith("/account") || url.startsWith("/mail")) {
            filterChain.doFilter(request, response);
            return;
        }
        //쿠키에서 JWT 토큰을 가져옴 (이후 프론트엔드에서 토큰을 관리할 경우 이 부분을 수정)
        header = getJwtTokenFromCookie(request, header);

        try {
            if(header == null || !header.startsWith("Bearer ")) {
                log.error("Error occurs while getting  fil header, header is null or invalid {}", request.getRequestURL());
                filterChain.doFilter(request, response);
                return;
            } else {
                token = header.split(" ")[1].trim();
            }

            //토큰 만료 체크
            if (JwtTokenUtils.isExpired(token, jwtProperties.secretKey())) {
                log.error("Key is expired");
                filterChain.doFilter(request, response);
                return;
            }

            String email = JwtTokenUtils.getUserEmail(token, jwtProperties.secretKey());

            BoardPrincipal principal = userAccountService.getUserPrincipal(email);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            log.error("Error occurs while validating, {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtTokenFromCookie(HttpServletRequest request, String header) {
        if (header == null) {
            if (request.getCookies() == null) {
                log.error("Error occurs while getting header, header is null or invalid {}", request.getRequestURL());
                return null;
            }
            Cookie jwtTokenCookie = CookieUtils.getCookieFromRequest(request, jwtProperties.cookieName());
            if (jwtTokenCookie == null) {
                log.error("Error occurs while getting header, jwtToken is null or invalid {}", request.getRequestURL());
                return null;
            }

            header = "Bearer " + jwtTokenCookie.getValue();
        }
        return header;
    }
}
