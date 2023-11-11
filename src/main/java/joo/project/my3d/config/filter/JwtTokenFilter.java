package joo.project.my3d.config.filter;

import joo.project.my3d.dto.properties.JwtProperties;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.exception.JwtTokenException;
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
import java.util.NoSuchElementException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

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
        if (header == null) {
            header = getJwtTokenFromCookie(request);
        }

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

    /**
     * header에 Authorization 정보가 존재하지 않을 경우 쿠키로부터 JWT 토큰을 얻는다.
     * @throws NullPointerException JWT 토큰을 가지는 쿠키가 존재하지 않을 경우 발생하는 예외
     */
    private String getJwtTokenFromCookie(HttpServletRequest request) {
        try {
            Optional<Cookie> jwtTokenCookie = CookieUtils.getCookieFromRequest(request, jwtProperties.cookieName());
            return "Bearer " + jwtTokenCookie.get().getValue();
        } catch (RuntimeException e) {
            log.error("JWT 토큰을 찾을 수 없습니다. - {}", new JwtTokenException(ErrorCode.JWT_TOKEN_NOT_FOUND, e));
            return null;
        }
    }
}
