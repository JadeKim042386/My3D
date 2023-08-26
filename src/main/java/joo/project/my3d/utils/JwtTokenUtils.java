package joo.project.my3d.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtTokenUtils {

    /**
     * 토큰에서 이메일 정보 추출
     */
    public static String getUserEmail(String token, String key) {
        return extracClaims(token, key).get("email", String.class);
    }

    /**
     * 토큰에서 닉네임 정보 추출
     */
    public static String getUserNickname(String token, String key) {
        return extracClaims(token, key).get("nickname", String.class);
    }

    /**
     * 토큰 만료 여부 확인
     */
    public static boolean isExpired(String token, String key) {
        Date expiredDate = extracClaims(token, key).getExpiration();
        return expiredDate.before(new Date());
    }

    /**
     * JWT 토큰 생성
     */
    public static String generateToken(String email, String nickname, String key, long expiredTimeMs) {
        Claims claims = Jwts.claims();
        claims.put("email", email);
        claims.put("nickname", nickname);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs))
                .signWith(getKey(key), SignatureAlgorithm.HS256)
                .compact();
    }

    private static Claims extracClaims(String token, String key) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey(key))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private static Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
