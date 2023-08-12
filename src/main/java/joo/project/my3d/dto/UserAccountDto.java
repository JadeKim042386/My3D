package joo.project.my3d.dto;

import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;

import java.time.LocalDateTime;

public record UserAccountDto(
        String userId,
        String userPassword,
        String email,
        String nickname,
        UserRole userRole,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static UserAccountDto of(String userId, String userPassword, String email, String nickname, UserRole userRole, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new UserAccountDto(userId, userPassword, email, nickname, userRole, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static UserAccountDto of(String userId, String userPassword, String email, String nickname, UserRole userRole) {
        return new UserAccountDto(userId, userPassword, email, nickname, userRole, null, null, null, null);
    }

    public static UserAccountDto of(String userId, String userPassword, String email, String nickname) {
        return new UserAccountDto(userId, userPassword, email, nickname, null, null, null, null, null);
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
