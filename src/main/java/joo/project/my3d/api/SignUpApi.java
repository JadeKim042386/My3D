package joo.project.my3d.api;

import joo.project.my3d.dto.request.SignUpRequest;
import joo.project.my3d.dto.response.ExceptionResponse;
import joo.project.my3d.exception.SignUpException;
import joo.project.my3d.exception.ValidatedException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.security.TokenProvider;
import joo.project.my3d.service.CompanyServiceInterface;
import joo.project.my3d.service.UserAccountServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/signup")
@RequiredArgsConstructor
public class SignUpApi {

    private final UserAccountServiceInterface userAccountService;
    private final CompanyServiceInterface companyService;
    private final BCryptPasswordEncoder encoder;
    private final TokenProvider tokenProvider;

    /**
     * 회원가입 요청
     */
    @PostMapping
    public ResponseEntity<?> signup(@Valid SignUpRequest signUpRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("bindingResult={}", bindingResult);
            throw new ValidatedException(
                    ErrorCode.INVALID_REQUEST,
                    ExceptionResponse.fromBindingResult("signup validated error", bindingResult));
        }
        duplicatedEmailOrNicknameOrCompanyNameCheck(
                signUpRequest.email(), signUpRequest.nickname(), signUpRequest.companyName());
        String refreshToken = tokenProvider.generateRefreshToken();
        userAccountService.saveUser(signUpRequest.toEntity(signUpRequest.email(), refreshToken, encoder));

        // 로그인 처리
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userAccountService.login(signUpRequest.email(), signUpRequest.password()));
    }

    /**
     * 이메일, 닉네임, 기업명 중복 체크
     */
    private void duplicatedEmailOrNicknameOrCompanyNameCheck(String email, String nickname, String companyName) {
        if ((StringUtils.hasText(email) || StringUtils.hasText(nickname))
                && userAccountService.isExistsUserEmailOrNickname(email, nickname)) {
            throw new SignUpException(ErrorCode.ALREADY_EXIST_EMAIL_OR_NICKNAME);
        }
        if (StringUtils.hasText(companyName) && companyService.isExistsByCompanyName(companyName)) {
            throw new SignUpException(ErrorCode.ALREADY_EXIST_COMPANY_NAME);
        }
    }
}
