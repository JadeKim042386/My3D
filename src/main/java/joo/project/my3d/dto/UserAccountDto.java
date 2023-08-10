package joo.project.my3d.dto;

import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;

public record UserAccountDto(
        String userId,
        String userPassword,
        String email,
        String nickname,
        UserRole userRole
) {
    public static UserAccountDto of(String userId, String userPassword, String email, String nickname, UserRole userRole) {
        return new UserAccountDto(userId, userPassword, email, nickname, userRole);
    }

    public static UserAccountDto from(UserAccount userAccount) {
        return UserAccountDto.of(
                userAccount.getUserId(),
                userAccount.getUserPassword(),
                userAccount.getEmail(),
                userAccount.getNickname(),
                userAccount.getUserRole()
        );
    }

    public UserAccount toEntity() {
        return UserAccount.of(
                userId,
                userPassword,
                email,
                nickname,
                userRole
        );
    }
}
