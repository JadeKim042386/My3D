package joo.project.my3d.dto.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param cookieName 쿠키에 저장될때 사용되는 키 값
 * @param secretKey 토큰을 생성할때 사용되는 키 값
 * @param expiredTimeMs 토큰 만료 시간 (ms)
 */
@ConfigurationProperties("jwt.token")
public record JwtProperties(
        String cookieName,
        String secretKey,
        long expiredTimeMs
) {}
