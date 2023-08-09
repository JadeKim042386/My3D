package joo.project.my3d.dto;

import joo.project.my3d.domain.constant.UserRole;

public record UserAccountDto(
        String userId,
        String email,
        String nickname,
        UserRole userRole
) {
    public static UserAccountDto of(String userId, String email, String nickname, UserRole userRole) {
        return new UserAccountDto(userId, email, nickname, userRole);
    }
}
