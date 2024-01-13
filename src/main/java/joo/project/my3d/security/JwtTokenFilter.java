package joo.project.my3d.security;

import io.jsonwebtoken.Claims;
import joo.project.my3d.exception.AuthException;
import joo.project.my3d.exception.constant.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    public static final String ACCESS_TOKEN_HEADER = HttpHeaders.AUTHORIZATION;
    public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
    public static final String TOKEN_PREFIX = "Bearer";
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = parseBearerToken(request, ACCESS_TOKEN_HEADER);

        try {
            if (accessToken == null) {
                filterChain.doFilter(request, response);
                return;
            }
            setAuthentication(request, tokenProvider.parseOrValidateClaims(accessToken), accessToken);
        } catch (AuthException e) {
            String refreshToken = parseBearerToken(request, REFRESH_TOKEN_HEADER);
            if (ObjectUtils.isEmpty(refreshToken)) {
                //TODO: 예외처리
                request.setAttribute("exception", new AuthException(AuthErrorCode.NOT_FOUND_REFRESH_TOKEN, e));
            } else {
                reissueAccessToken(request, response, refreshToken);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.error("Error occurs during authenticate, {}", e.getMessage());
            //TODO: 예외처리
            request.setAttribute("exception", e);
        }
        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request, String headerName) {
        return Optional.ofNullable(request.getHeader(headerName))
                .filter(token -> !ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX))
                .map(token -> token.substring(TOKEN_PREFIX.length()).trim())
                .orElse(null);
    }

    private void reissueAccessToken(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        try {
            String oldAccessToken = parseBearerToken(request, ACCESS_TOKEN_HEADER);
            tokenProvider.validateRefreshToken(refreshToken, oldAccessToken);
            String newAccessToken = tokenProvider.regenerateAccessToken(oldAccessToken);
            setAuthentication(request, tokenProvider.parseOrValidateClaims(newAccessToken), newAccessToken);
            response.setHeader("New-Access-Token", newAccessToken);
        } catch (Exception e) {
            //TODO: 예외처리
            request.setAttribute("exception", e);
        }
    }

    /**
     * 유저가 존재하지 않을 경우 authentication을 저장하지 않고
     * AnonymousAuthenticationFilter에서 AnonymousAuthenticationToken을 등록한다.
     */
    private void setAuthentication(HttpServletRequest request, Claims claims, String accessToken) {
        User user = tokenProvider.getUserDetails(claims);
        UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(user, accessToken, user.getAuthorities());
        authenticated.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticated);
    }
}
