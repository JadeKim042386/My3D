package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.UserRole;

public record SignUpData(
        String email,
        String nickname,
        UserRole userRole
) {
    public static SignUpData of(String email, String nickname, UserRole userRole) {
        return new SignUpData(email, nickname, userRole);
    }
}
