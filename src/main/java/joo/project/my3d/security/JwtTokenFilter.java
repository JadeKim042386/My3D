package joo.project.my3d.security;

import io.jsonwebtoken.Claims;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    public static final String ACCESS_TOKEN_HEADER = HttpHeaders.AUTHORIZATION;
    public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
    public static final String TOKEN_PREFIX = "Bearer";
    private static final String ACCESS_TOKEN_COOKIE = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = parseToken(request, true);
        try {
            if (!StringUtils.hasText(accessToken)) {
                filterChain.doFilter(request, response);
                return;
            }
            setAuthentication(request, tokenProvider.parseOrValidateClaims(accessToken), accessToken);
        } catch (AuthException e) {
            String refreshToken = parseToken(request, false);
            if (!StringUtils.hasText(refreshToken)) {
                deleteToken(response);
            } else {
                reissueAccessToken(request, response, refreshToken);
            }
        } catch (Exception e) {
            log.error("Error occurs during authenticate, {}", e.getMessage());
            deleteToken(response);
        }
        filterChain.doFilter(request, response);
    }

    private String parseToken(HttpServletRequest request, boolean isAccessToken) {
        String token;
        //헤더에 토큰이 없을 경우 쿠키에서 토큰을 파싱
        if (isAccessToken) {
            token = parseBearerToken(request, ACCESS_TOKEN_HEADER);
            if (!StringUtils.hasText(token)) {
                token = parseCookieToken(request, ACCESS_TOKEN_COOKIE);
            }
        } else {
            token = parseBearerToken(request, REFRESH_TOKEN_HEADER);
            if (!StringUtils.hasText(token)) {
                token = parseCookieToken(request, REFRESH_TOKEN_COOKIE);
            }
        }
        return token;
    }

    private String parseCookieToken(HttpServletRequest request, String cookieName) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private String parseBearerToken(HttpServletRequest request, String headerName) {
        return Optional.ofNullable(request.getHeader(headerName))
                .filter(token -> !ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX))
                .map(token -> token.substring(TOKEN_PREFIX.length()).trim())
                .orElse(null);
    }

    private void reissueAccessToken(HttpServletRequest request, HttpServletResponse response, String refreshToken) throws IOException {
        try {
            String oldAccessToken = parseToken(request, true);
            tokenProvider.validateRefreshToken(refreshToken, oldAccessToken);
            String newAccessToken = tokenProvider.regenerateAccessToken(oldAccessToken);
            setAuthentication(request, tokenProvider.parseOrValidateClaims(newAccessToken), newAccessToken);
            //response.setHeader("New-Access-Token", newAccessToken);
            Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE, newAccessToken);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            deleteToken(response);
        }
    }

    /**
     * 유저가 존재하지 않을 경우 authentication을 저장하지 않고
     * AnonymousAuthenticationFilter에서 AnonymousAuthenticationToken을 등록한다.
     */
    private void setAuthentication(HttpServletRequest request, Claims claims, String accessToken) {
        BoardPrincipal boardPrincipal = tokenProvider.getUserDetails(claims);
        UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(boardPrincipal, accessToken, boardPrincipal.getAuthorities());
        authenticated.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticated);
    }

    /**
     * 토큰이 만료되었거나 유효하지 않을 경우 클라이언트에 있는 토큰을 삭제
     */
    private void deleteToken(HttpServletResponse response) throws IOException {
        Cookie acceessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE, "");
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE, "");
        acceessTokenCookie.setMaxAge(-1);
        refreshTokenCookie.setMaxAge(-1);
        response.addCookie(acceessTokenCookie);
        response.addCookie(refreshTokenCookie);
        response.sendRedirect("/signin");
    }
}
