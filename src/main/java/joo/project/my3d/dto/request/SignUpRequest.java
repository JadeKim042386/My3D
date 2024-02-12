package joo.project.my3d.dto.request;

import joo.project.my3d.domain.Address;
import joo.project.my3d.domain.Company;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.UserRefreshToken;
import joo.project.my3d.domain.constant.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public record SignUpRequest(
        @NotBlank @Email String email,
        @NotNull(message = "회원 유형을 찾을 수 없습니다.") UserRole userRole,
        String companyName,
        @NotBlank(message = "닉네임을 입력해주세요") String nickname,
        @Pattern(
                        regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
                        message = "비밀번호는 영문자,숫자,특수문자 포함 최소 8~20자를 사용하세요.")
                String password,
        @NotBlank(message = "우편번호를 입력해주세요") String zipcode,
        @NotBlank(message = "주소를 입력해주세요") String address,
        @NotBlank(message = "상세 주소를 입력해주세요") String detailAddress) {

    public UserAccount toEntity(String email, String refreshToken, PasswordEncoder encoder) {
        return UserAccount.of(
                email,
                encoder.encode(password),
                nickname,
                null,
                Address.of(zipcode, address, detailAddress),
                userRole,
                UserRefreshToken.of(refreshToken),
                StringUtils.hasText(companyName) ? Company.of(companyName, null) : null);
    }
}
