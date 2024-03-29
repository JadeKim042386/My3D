package joo.project.my3d.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import joo.project.my3d.domain.UserRefreshToken;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.properties.JwtProperties;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.exception.AuthException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.repository.UserRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_SPEC = "spec";
    private static final String ANONYMOUS = "ANONYMOUS";
    private final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    public String generateAccessToken(String email, String nickname, String spec) {
        Claims claims = Jwts.claims();
        claims.put(KEY_EMAIL, email);
        claims.put(KEY_NICKNAME, nickname);
        claims.put(KEY_SPEC, spec); // "id:role"

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.accessExpiredMs()))
                .signWith(getKey(jwtProperties.secretKey()), SignatureAlgorithm.HS256)
                .compact();
    }

    @Transactional
    public String regenerateAccessToken(String token) throws JsonProcessingException {
        Map<String, String> decoded = decodeExpiredToken(token);
        String spec = getSpec(decoded);
        long userAccountId = Long.parseLong(spec.split(":")[0]);
        // 조회하려는 refresh token은 재발행 횟수 제한보다 적게 재발행이 되어야한다.
        UserRefreshToken refreshToken = userRefreshTokenRepository
                .findByUserAccountIdAndReissueCountLessThan(userAccountId, getReissueLimit())
                .orElseThrow(() -> new AuthException(ErrorCode.EXCEED_REISSUE));
        refreshToken.increaseReissueCount();

        return generateAccessToken(decoded.get(KEY_EMAIL), decoded.get(KEY_NICKNAME), spec);
    }

    public String generateRefreshToken() {
        return Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.refreshExpiredMs()))
                .signWith(getKey(jwtProperties.secretKey()), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 조회하려는 refresh token와 현재 유저의 실제 refresh token의 일치 여부 확인
     */
    @Transactional(readOnly = true)
    public void validateRefreshToken(String refreshToken, String accessToken) throws JsonProcessingException {
        parseOrValidateClaims(refreshToken); // validation
        String spec = getSpec(decodeExpiredToken(accessToken));
        long userAccountId = Long.parseLong(spec.split(":")[0]);
        // 조회하려는 refresh token은 현재 유저의 실제 refresh token과 일치해야한다.
        userRefreshTokenRepository
                .findByUserAccountIdAndReissueCountLessThan(userAccountId, getReissueLimit())
                .filter(token -> token.equalRefreshToken(refreshToken))
                .orElseThrow(() -> new AuthException(ErrorCode.NOT_EQUAL_TOKEN));
    }

    /**
     * 토큰을 파싱하는 과정에서 validation도 같이 진행됨 (empty, expired 등)
     */
    public Claims parseOrValidateClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey(jwtProperties.secretKey()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new AuthException(ErrorCode.EXPIRED_TOKEN, e);
        }
    }

    /**
     * email과 role을 파싱하여 배열 형식으로 반환
     * @return {id, email, nickname, authority}
     */
    public String[] parseSpecification(Claims claims) {
        try {
            String[] spec = claims.get(TokenProvider.KEY_SPEC, String.class).split(":");
            return new String[] {
                spec[0], // id
                claims.get(TokenProvider.KEY_EMAIL, String.class),
                claims.get(TokenProvider.KEY_NICKNAME, String.class),
                spec[1] // authority
            };
        } catch (RequiredTypeException e) {
            return null;
        }
    }

    /**
     * access token의 형식이 잘못되었을 경우 ANONYMOUS로 반환
     */
    public BoardPrincipal getUserDetails(Claims claims) {
        String[] parsed = Optional.ofNullable(claims)
                .map(this::parseSpecification)
                .orElse(new String[] {null, ANONYMOUS, ANONYMOUS, ANONYMOUS});

        return BoardPrincipal.of(Long.parseLong(parsed[0]), parsed[1], parsed[2], UserRole.valueOf(parsed[3]));
    }

    private Map<String, String> decodeExpiredToken(String oldAccessToken) throws JsonProcessingException {
        return objectMapper.readValue(
                new String(Base64.getDecoder().decode(oldAccessToken.split("\\.")[1]), StandardCharsets.UTF_8),
                new TypeReference<Map<String, String>>() {});
    }

    private String getSpec(Map<String, String> decoded) {
        return Optional.of(decoded.get(KEY_SPEC)).orElseThrow(() -> new AuthException(ErrorCode.INVALID_REQUEST));
    }

    private Key getKey(String key) {
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    private long getReissueLimit() {
        return jwtProperties.refreshExpiredMs() / jwtProperties.accessExpiredMs();
    }
}
