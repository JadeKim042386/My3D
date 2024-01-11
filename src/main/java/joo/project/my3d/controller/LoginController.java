package joo.project.my3d.controller;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.properties.JwtProperties;
import joo.project.my3d.dto.request.SignUpRequest;
import joo.project.my3d.dto.request.UserLoginRequest;
import joo.project.my3d.dto.response.ApiResponse;
import joo.project.my3d.dto.response.BusinessCertificationResponse;
import joo.project.my3d.dto.response.SignUpResponse;
import joo.project.my3d.exception.UserAccountException;
import joo.project.my3d.service.SignUpService;
import joo.project.my3d.service.UserAccountService;
import joo.project.my3d.utils.CookieUtils;
import joo.project.my3d.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class LoginController {

    private final SignUpService signUpService;
    private final UserAccountService userAccountService;
    private final BCryptPasswordEncoder encoder;
    private final JwtProperties jwtProperties;
    private final SecurityContextLogoutHandler logoutHandler;

    @Value("${nts.service-key}")
    private String serviceKey;

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
     * @throws UserAccountException 로그인에 실패할 경우 발생하는 예외
     */
    @PostMapping("/login")
    public ApiResponse<Void> requestLogin(
            UserLoginRequest loginRequest,
            HttpServletResponse response
    ) {
        String token = userAccountService.login(loginRequest.email(), loginRequest.password());

        Cookie cookie = CookieUtils.createCookie(
                jwtProperties.cookieName(),
                token,
                (int) (jwtProperties.expiredTimeMs() / 1000),
                "/"
        );
        response.addCookie(cookie);

        return ApiResponse.success();
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
     * 1. OAuth 첫 로그인 후
     * 2. 회원가입
     * 회원 유형(개인, 기업) 선택 페이지 요청(프론트엔드 작업시 불필요하면 삭제)
     */
    @GetMapping("/type")
    public ApiResponse<Void> type() {

        return ApiResponse.success();
    }

    /**
     * 회원가입을 요청한 유저의 정보 조회
     */
    @GetMapping("/signup")
    public ApiResponse<SignUpResponse> signup(
            HttpServletRequest request,
            @RequestParam UserRole userRole,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String emailError,
            @RequestParam(required = false) String emailCode
    ) {
        //OAuth 첫 로그인시 플랫폼(구글, 카카오, 네이버)의 이메일, 닉네임을 사용
        boolean isOAuthLogin = false;
        String nickname = null;
        if (Objects.nonNull(request.getCookies())){
            Optional<Cookie> jwtTokenCookie = CookieUtils.getCookieFromRequest(request, jwtProperties.cookieName());
            isOAuthLogin = jwtTokenCookie.isPresent();
            if (isOAuthLogin){
                email = JwtTokenUtils.getUserEmail(jwtTokenCookie.get().getValue(), jwtProperties.secretKey());
                nickname = JwtTokenUtils.getUserNickname(jwtTokenCookie.get().getValue(), jwtProperties.secretKey());
            }
        }

        //TODO: 닉네임, 기업명 중복 체크를 위해 findAll을 사용하는 것과 exist 쿼리를 날리는 것의 성능 차이 확인
        return ApiResponse.success(SignUpResponse.of(
                isOAuthLogin,
                email,
                emailCode,
                emailError,
                userRole,
                nickname,
                userAccountService.findAllUser()
        ));
    }

    /**
     * 회원가입 요청
     */
    @PostMapping("/signup")
    public ApiResponse<?> signup(
            HttpServletResponse response,
            @RequestParam String email,
            @Validated SignUpRequest signUpRequest,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            log.warn("bindingResult={}", bindingResult);
            return ApiResponse.invalid(bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList());
        }

        UserAccountDto userAccountDto = UserAccountDto.from(
                signUpRequest.toEntity(email),
                encoder
        );

        //auditorAware를 위해 principal을 먼저 등록
        signUpService.setPrincipal(userAccountDto);
        userAccountService.saveUser(userAccountDto.toEntity());

        //jwt 토큰 추가
        Cookie cookie = CookieUtils.createCookie(
                jwtProperties.cookieName(),
                JwtTokenUtils.generateToken(
                        userAccountDto.email(),
                        userAccountDto.nickname(),
                        jwtProperties.secretKey(),
                        jwtProperties.expiredTimeMs()
                ),
                (int) jwtProperties.expiredTimeMs() / 1000,
                "/"
        );
        response.addCookie(cookie);

        return ApiResponse.success();
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
     * 사업자 인증 페이지
     */
    @GetMapping("/company")
    public ApiResponse<Void> businessCertification() {
        return ApiResponse.success();
    }
}
