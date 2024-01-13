package joo.project.my3d.dto;

import joo.project.my3d.domain.Address;
import joo.project.my3d.domain.Company;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.UserRefreshToken;
import joo.project.my3d.domain.constant.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 1. BoardPrincipal 로 변환
 */
public record UserAccountDto(
        Long id,
        String email,
        String userPassword,
        String nickname,
        String phone,
        AddressDto addressDto,
        UserRole userRole,
        CompanyDto companyDto,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    /**
     * 모든 필드 주입
     */
    public static UserAccountDto of(Long id, String email, String userPassword, String nickname, String phone, AddressDto address, UserRole userRole, CompanyDto company, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new UserAccountDto(id, email, userPassword, nickname, phone, address, userRole, company, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    /**
     * 생성일시, 생성자, 수정일시, 수정자, 회사 정보 제외 (개인 사용자)
     */
    public static UserAccountDto of(Long id, String email, String userPassword, String nickname, String phone, AddressDto addressDto, UserRole userRole) {
        return UserAccountDto.of(id, email, userPassword, nickname, phone, addressDto, userRole, CompanyDto.of(), null, null, null, null);
    }

    /**
     * boardPrincipal -> DTO
     */
    public static UserAccountDto of(String email, String userPassword, String nickname, String phone, AddressDto addressDto, UserRole userRole) {
        return UserAccountDto.of(null, email, userPassword, nickname, phone, addressDto, userRole, CompanyDto.of(), null, null, null, null);
    }

    /**
     * 계정 정보 수정에 사용
     * 생성일시, 생성자, 수정일시, 수정자, 비밀번호, 가입여부, 회사 정보 제외 (개인 사용자)
     */
    public static UserAccountDto of(String email, String nickname, String phone, AddressDto addressDto) {
        return UserAccountDto.of(null, email, null, nickname, phone, addressDto, null, CompanyDto.of(), null, null, null, null);
    }

    /**
     * 생성일시, 생성자, 수정일시, 수정자, 폰번호, 주소 제외 (기업/기관)
     */
    public static UserAccountDto of(Long id, String email, String userPassword, String nickname, String phone, AddressDto addressDto, UserRole userRole, CompanyDto company) {
        return UserAccountDto.of(id, email, userPassword, nickname, phone, addressDto, userRole, company, null, null, null, null);
    }

    public static UserAccountDto from(UserAccount userAccount) {
        Address address = userAccount.getAddress();
        Company company = userAccount.getCompany();

        return UserAccountDto.of(
                userAccount.getId(),
                userAccount.getEmail(),
                userAccount.getUserPassword(),
                userAccount.getNickname(),
                userAccount.getPhone(),
                Objects.isNull(address) ? AddressDto.of() : AddressDto.from(address),
                userAccount.getUserRole(),
                Objects.isNull(company) ? CompanyDto.of() : CompanyDto.from(company)
        );
    }

    /**
     * 추가로 비밀번호를 인코딩
     */
    public static <T extends PasswordEncoder> UserAccountDto from(UserAccount userAccount, T encoder) {
        Address address = userAccount.getAddress();
        Company company = userAccount.getCompany();

        return UserAccountDto.of(
                userAccount.getId(),
                userAccount.getEmail(),
                encoder.encode(userAccount.getUserPassword()),
                userAccount.getNickname(),
                userAccount.getPhone(),
                Objects.isNull(address) ? AddressDto.of() : AddressDto.from(address),
                userAccount.getUserRole(),
                Objects.isNull(company) ? CompanyDto.of() : CompanyDto.from(company)
        );
    }

    public UserAccount toEntity(String refreshToken) {
        return UserAccount.of(
                email,
                userPassword,
                nickname,
                phone,
                addressDto.toEntity(),
                userRole,
                UserRefreshToken.of(refreshToken),
                companyDto.toEntity()
        );
    }

}
