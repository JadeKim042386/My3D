package joo.project.my3d.controller;

import joo.project.my3d.dto.request.SignUpRequest;
import joo.project.my3d.dto.request.UserLoginRequest;
import joo.project.my3d.dto.response.ApiResponse;
import joo.project.my3d.dto.response.LoginResponse;
import joo.project.my3d.exception.AuthException;
import joo.project.my3d.security.TokenProvider;
import joo.project.my3d.service.CompanyService;
import joo.project.my3d.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class LoginController {

    private final UserAccountService userAccountService;
    private final CompanyService companyService;
    private final BCryptPasswordEncoder encoder;
    private final TokenProvider tokenProvider;
    private final SecurityContextLogoutHandler logoutHandler;

    /**
     * 로그인 페이지 요청 (프론트엔드 작업시 불필요하면 삭제)
     */
    @GetMapping("/login")
    public ApiResponse<Void> login() {

        return ApiResponse.success();
    }

    /**
     * 로그인 요청
     * @param loginRequest 로그인 폼에 입력된 이메일, 비밀번호를 담은 DTO
     * @throws AuthException 로그인에 실패할 경우 발생하는 예외
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> requestLogin(UserLoginRequest loginRequest) {

        return ApiResponse.success(
                userAccountService.login(loginRequest.email(), loginRequest.password())
        );
    }

    /**
     * oauth 로그인 후 로그인 처리 또는 oauth 정보 반환
     * @param signup 회원가입 여부
     */
    @GetMapping("/oauth/response")
    public ApiResponse<?> oauthResponse(
            @RequestParam String email,
            @RequestParam String nickname,
            @RequestParam boolean signup
    ) {
        //회원가입이 되어있다면 로그인처리
        if (signup) {
            return ApiResponse.success(userAccountService.oauthLogin(email, nickname));
        }
        //회원가입 안되어있다면 회원 유형 선택 페이지로 redirect
        //TODO: 회원가입이 안되어있다면 email과 nickmame 전달하고 프론트엔드에서 처리하도록 적용
        return ApiResponse.error(String.format("%s:%s", email, nickname));
    }

    /**
     * 로그아웃 요청
     */
    @GetMapping("/logout")
    public ApiResponse<Void> requestLogout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        logoutHandler.logout(request, response, authentication);

        return ApiResponse.success();
    }

    /**
     * 회원 유형(개인, 기업) 선택 페이지 요청(프론트엔드 작업시 불필요하면 삭제)
     * 1. OAuth 첫 로그인 후 -> email, nickname 포함
     * 2. 회원가입 -> 모든 정보가 null
     */
    @GetMapping("/type")
    public ApiResponse<Void> selectType() {
        return ApiResponse.success();
    }

    /**
     * 닉네임, 기업명 중복 체크
     */
    @PostMapping("/signup/duplicatedCheck")
    public ApiResponse<Boolean> duplicatedNicknamesOrCompanyNamesCheck(
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String companyName
    ) {
        if (StringUtils.hasText(nickname) && userAccountService.isExistsUserNickname(nickname)) {
            return ApiResponse.success(true);
        } else if (StringUtils.hasText(companyName) && companyService.isExistsByCompanyName(companyName)) {
            return ApiResponse.success(true);
        }
        return ApiResponse.success(false);
    }

    /**
     * 회원가입 요청
     */
    @PostMapping("/signup")
    public ApiResponse<?> signup(
            @RequestParam String email,
            @Validated SignUpRequest signUpRequest,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            log.warn("bindingResult={}", bindingResult);
            return ApiResponse.invalid(bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList());
        }

        String refreshToken = tokenProvider.generateRefreshToken();
        userAccountService.saveUser(
                signUpRequest.toEntity(email, refreshToken, encoder)
        );

        //로그인 처리
        return ApiResponse.success(
                userAccountService.login(email, signUpRequest.password())
        );
    }

    /**
     * 비밀번호 찾기 페이지 요청(프론트엔드 작업시 불필요하면 삭제)
     */
    @GetMapping("/find_pass")
    public ApiResponse<Void> findPassword() {

        return ApiResponse.success();
    }

    /**
     * 임시 비밀번호 전송 완료 페이지(프론트엔드 작업시 불필요하면 삭제)
     */
    @GetMapping("/find_pass/success")
    public ApiResponse<Void> findPasswordSuccess() {
        return ApiResponse.success();
    }

    /**
     * 사업자 인증 페이지(프론트엔드 작업시 불필요하면 삭제)
     */
    @GetMapping("/company")
    public ApiResponse<Void> businessCertification() {
        return ApiResponse.success();
    }
}
