package joo.project.my3d.dto;

import joo.project.my3d.domain.Address;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;

import java.time.LocalDateTime;

public record UserAccountDto(
        String email,
        String userPassword,
        String nickname,
        String phone,
        Address address,
        UserRole userRole,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static UserAccountDto of(String email, String userPassword, String nickname, String phone, Address address, UserRole userRole, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new UserAccountDto(email, userPassword, nickname, phone, address, userRole, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static UserAccountDto of(String email, String userPassword, String nickname, String phone, Address address, UserRole userRole) {
        return new UserAccountDto(email, userPassword, nickname, phone, address, userRole, null, null, null, null);
    }

    public static UserAccountDto of(String email, String userPassword, String nickname, UserRole userRole) {
        return new UserAccountDto(email, userPassword, nickname, null, null, userRole, null, null, null, null);
    }

    public static UserAccountDto of(String email, String userPassword, String nickname) {
        return new UserAccountDto(email, userPassword, nickname, null, null, null, null, null, null, null);
    }

    public static UserAccountDto from(UserAccount userAccount) {
        return UserAccountDto.of(
                userAccount.getEmail(),
                userAccount.getUserPassword(),
                userAccount.getNickname(),
                userAccount.getPhone(),
                userAccount.getAddress(),
                userAccount.getUserRole()
        );
    }

    public UserAccount toEntity() {
        return UserAccount.of(
                email,
                userPassword,
                nickname,
                phone,
                address,
                userRole
        );
    }
}
