package joo.project.my3d.dto;

import joo.project.my3d.domain.Address;
import joo.project.my3d.domain.Company;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;

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
     * 계정 정보 수정에 사용
     * 비밀번호, 회사 정보 제외 (개인 사용자)
     */
    public static UserAccountDto of(String email, String nickname, String phone, AddressDto addressDto) {
        return UserAccountDto.of(null, email, null, nickname, phone, addressDto, null);
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
}
