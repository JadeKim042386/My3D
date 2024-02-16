package joo.project.my3d.dto.security;

import joo.project.my3d.exception.AuthException;
import joo.project.my3d.exception.constant.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String provider;

    public static OAuthAttributes of(
            String name, String email, String provider, Map<String, Object> attributes, String nameAttributeKey) {
        return new OAuthAttributes(attributes, nameAttributeKey, name, email, provider);
    }

    public static OAuthAttributes of(
            String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        } else if ("kakao".equals(registrationId)) {
            return ofKakao("id", attributes);
        }

        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.of(
                (String) attributes.get("name"),
                (String) attributes.get("email"),
                "Google",
                attributes,
                userNameAttributeName);
    }

    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) Optional.of(attributes.get("response"))
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_REQUEST));

        return OAuthAttributes.of(
                (String) response.get("name"),
                (String) response.get("email"),
                "Naver",
                response,
                userNameAttributeName);
    }

    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) Optional.of(attributes.get("kakao_account"))
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_REQUEST));
        Map<String, Object> account = (Map<String, Object>) Optional.of(response.get("profile"))
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_REQUEST));

        return OAuthAttributes.of(
                (String) account.get("nickname"),
                (String) response.get("email"),
                "Kakao",
                attributes,
                userNameAttributeName);
    }
}
