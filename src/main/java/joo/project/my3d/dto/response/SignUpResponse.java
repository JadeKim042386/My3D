package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.UserAccountDto;

import java.util.List;

public record SignUpResponse(
        //OAuth 로그인으로인한 회원가입일때 이메일을 수정할 수 없도록 OAuth 로그인 여부를 뷰에 전달
        Boolean oAuthLogin,
        String email,
        //이메일 인증 요청 후 생성된 인증 코드
        String emailCode,
        //이메일 인증 코드 전송 에러
        String emailError,
        UserRole userRole,
        String nickname,
        //중복 체크를 위핸 낙네임 리스트
        List<String> nicknames,
        //중복 체크를 위핸 기업명 리스트
        List<String> companyNames,
        List<String> validError
) {
    public static SignUpResponse of(boolean oAuthLogin, String email, String emailCode, String emailError, UserRole userRole, String nickname, List<UserAccountDto> userAccountDtos) {
        return new SignUpResponse(
                oAuthLogin,
                email,
                emailCode,
                emailError,
                userRole,
                nickname,
                userAccountDtos.stream()
                        .map(UserAccountDto::nickname)
                        .toList(),
                userAccountDtos.stream()
                        .filter(userAccountDto -> userAccountDto.userRole() == UserRole.COMPANY)
                        .map(UserAccountDto::companyDto)
                        .map(CompanyDto::companyName)
                        .toList(),
                null
        );
    }

    public static SignUpResponse of(List<String> validError) {
        return new SignUpResponse(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                validError
        );
    }
}
