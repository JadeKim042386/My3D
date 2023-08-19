package joo.project.my3d.dto;

import joo.project.my3d.domain.Address;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;

import java.time.LocalDateTime;

/**
 * 1. BoardPrincipal 로 변환
 */
public record UserAccountDto(
        String email,
        String userPassword,
        String nickname,
        String phone,
        Address address,
        boolean signUp,
        UserRole userRole,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    /**
     * 모든 필드 주입
     */
    public static UserAccountDto of(String email, String userPassword, String nickname, String phone, Address address, boolean signUp, UserRole userRole, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new UserAccountDto(email, userPassword, nickname, phone, address, signUp, userRole, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    /**
     *  생성일시, 생성자, 수정일시, 수정자 제외
     */
    public static UserAccountDto of(String email, String userPassword, String nickname, String phone, Address address, boolean signUp, UserRole userRole) {
        return new UserAccountDto(email, userPassword, nickname, phone, address, signUp, userRole, null, null, null, null);
    }

    /**
     *  생성일시, 생성자, 수정일시, 수정자, 폰번호, 주소 제외
     */
    public static UserAccountDto of(String email, String userPassword, String nickname, boolean signUp, UserRole userRole) {
        return new UserAccountDto(email, userPassword, nickname, null, null, signUp, userRole, null, null, null, null);
    }

    /**
     *  BoardPrincipal 을 DTO 로 변환시 사용 <br>
     *  생성일시, 생성자, 수정일시, 수정자, 폰번호, 주소, 권한 제외
     */
    public static UserAccountDto of(String email, String userPassword, String nickname, boolean signUp) {
        return new UserAccountDto(email, userPassword, nickname, null, null, signUp, null, null, null, null, null);
    }

    public static UserAccountDto from(UserAccount userAccount) {
        return UserAccountDto.of(
                userAccount.getEmail(),
                userAccount.getUserPassword(),
                userAccount.getNickname(),
                userAccount.getPhone(),
                userAccount.getAddress(),
                userAccount.isSignUp(),
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
                signUp,
                userRole
        );
    }
}
