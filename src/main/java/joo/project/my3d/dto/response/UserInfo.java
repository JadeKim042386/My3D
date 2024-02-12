package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.UserRole;

public record UserInfo(String email, String nickname, UserRole userRole) {
    public static UserInfo of(String email, String nickname, UserRole userRole) {
        return new UserInfo(email, nickname, userRole);
    }
}
