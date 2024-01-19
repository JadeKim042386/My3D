package joo.project.my3d.dto.response;

public record LoginResponse (
    String email,
    String nickname,
    String accessToken,
    String refreshToken
) {
    public static LoginResponse of(String email, String nickname, String accessToken, String refreshToken) {
        return new LoginResponse(email, nickname, accessToken, refreshToken);
    }

    public static LoginResponse of(String email, String nickname) {
        return new LoginResponse(email, nickname, null, null);
    }
}
