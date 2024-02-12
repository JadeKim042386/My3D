package joo.project.my3d.dto;

import joo.project.my3d.domain.Address;
import joo.project.my3d.domain.Company;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.UserRefreshToken;
import joo.project.my3d.domain.constant.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

public record UserAccountDto(
        Long id,
        String email,
        String userPassword,
        String nickname,
        String phone,
        AddressDto addressDto,
        UserRole userRole,
        CompanyDto companyDto) {

    /**
     * 모든 필드 주입
     */
    public static UserAccountDto of(
            Long id,
            String email,
            String userPassword,
            String nickname,
            String phone,
            AddressDto addressDto,
            UserRole userRole,
            CompanyDto company) {
        return new UserAccountDto(id, email, userPassword, nickname, phone, addressDto, userRole, company);
    }

    /**
     * 회사 정보 제외 (개인 사용자)
     */
    public static UserAccountDto of(
            Long id,
            String email,
            String userPassword,
            String nickname,
            String phone,
            AddressDto addressDto,
            UserRole userRole) {
        return UserAccountDto.of(id, email, userPassword, nickname, phone, addressDto, userRole, CompanyDto.of());
    }

    /**
     * boardPrincipal -> DTO
     */
    public static UserAccountDto of(
            String email,
            String userPassword,
            String nickname,
            String phone,
            AddressDto addressDto,
            UserRole userRole) {
        return UserAccountDto.of(null, email, userPassword, nickname, phone, addressDto, userRole);
    }

    /**
     * 계정 정보 수정에 사용
     * 비밀번호, 회사 정보 제외 (개인 사용자)
     */
    public static UserAccountDto of(String email, String nickname, String phone, AddressDto addressDto) {
        return UserAccountDto.of(email, null, nickname, phone, addressDto, null);
    }

    /**
     * OAuth 최초 로그인시 임시로 생성하기위해 사용
     */
    public static UserAccountDto of(String email, String userPassword, String nickname, UserRole userRole) {
        return UserAccountDto.of(email, userPassword, nickname, null, AddressDto.of(), userRole);
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
                Objects.isNull(company) ? CompanyDto.of() : CompanyDto.from(company));
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
                Objects.isNull(company) ? CompanyDto.of() : CompanyDto.from(company));
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
                companyDto.toEntity());
    }
}
