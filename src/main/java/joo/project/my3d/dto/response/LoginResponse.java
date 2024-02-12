package joo.project.my3d.dto.response;

public record LoginResponse(String accessToken, String refreshToken) {
    public static LoginResponse of(String accessToken, String refreshToken) {
        return new LoginResponse(accessToken, refreshToken);
    }
}
