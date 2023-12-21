package joo.project.my3d.controller;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.properties.JwtProperties;
import joo.project.my3d.dto.request.SignUpRequest;
import joo.project.my3d.dto.request.UserLoginRequest;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class LoginController {

    private final SignUpService signUpService;
    private final UserAccountService userAccountService;
    private final BCryptPasswordEncoder encoder;
    private final JwtProperties jwtProperties;

    @Value("${nts.service-key}")
    private String serviceKey;
    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    /**
     * 로그인 페이지 요청
     * @throws MalformedURLException 로그인 이전 페이지의 URL이 잘못되었을 경우 발생하는 예외
     */
    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        try {
            String referer = request.getHeader("Referer"); //이전 URL
            String refererPath = new URL(referer).getPath();
            request.getSession().setAttribute("prevPage", refererPath);
            return "account/login";
        } catch (MalformedURLException e) {
            log.error("로그인 이전 페이지의 URL이 잘못되었습니다; {}", e.getMessage());
            return "/";
        }
    }

    /**
     * 로그인 요청
     * @param loginRequest 로그인 폼에 입력된 이메일, 비밀번호를 담은 DTO
     * @throws UserAccountException 로그인에 실패할 경우 발생하는 예외
     */
    @PostMapping("/login")
    public String requestLogin(
            UserLoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        try {
            String token = userAccountService.login(loginRequest.email(), loginRequest.password());

            Cookie cookie = CookieUtils.createCookie(
                    jwtProperties.cookieName(),
                    token,
                    (int) (jwtProperties.expiredTimeMs() / 1000),
                    "/"
            );
            response.addCookie(cookie);

            return "redirect:" + request.getSession().getAttribute("prevPage");
        } catch (UserAccountException e) {
            log.error("로그인 실패 - {}", e.getMessage());
            model.addAttribute("loginFailedMessage", e.getMessage());
            return "account/login";
        }
    }

    @PostMapping("/logout")
    public String requestLogout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        this.logoutHandler.logout(request, response, authentication);

        return "redirect:/";
    }

    /**
     * 1. OAuth 첫 로그인 후
     * 2. 회원가입
     */
    @GetMapping("/type")
    public String type() {

        return "account/type";
    }

    /**
     * 회원가입 페이지 요청
     */
    @GetMapping("/sign_up")
    public String signup(
            HttpServletRequest request,
            @RequestParam UserRole userRole,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String emailError,
            @RequestParam(required = false) String emailCode,
            Model model
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

        //OAuth 로그인으로인한 회원가입일때 이메일을 수정할 수 없도록 OAuth 로그인 여부를 뷰에 전달
        model.addAttribute("oauthLogin", isOAuthLogin);
        model.addAttribute("email", email);
        //이메일 인증 요청 후 생성된 인증 코드
        model.addAttribute("code", emailCode);
        //이메일 인증 코드 전송 에러
        model.addAttribute("emailError", emailError);

        model.addAttribute("signUpData", SignUpResponse.of(userRole, nickname));

        //닉네임, 기업명 중복 체크를 위해 추가
        List<UserAccountDto> userAccountDtos = userAccountService.findAllUser();
        List<String> nicknames = userAccountDtos.stream()
                .map(UserAccountDto::nickname).toList();
        List<String> companyNames = userAccountDtos.stream()
                .filter(userAccountDto -> userAccountDto.userRole() == UserRole.COMPANY)
                .map(UserAccountDto::companyDto)
                .map(CompanyDto::companyName)
                .toList();
        model.addAttribute("nicknames", nicknames);
        model.addAttribute("companyNames", companyNames);

        return "account/signup";
    }

    @PostMapping("/sign_up")
    public String signup(
            HttpServletResponse response,
            @RequestParam String email,
            @Validated @ModelAttribute("signUpData") SignUpRequest signUpRequest,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            log.warn("bindingResult={}", bindingResult);
            model.addAttribute("email", email);
            return "account/signup";
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

        return "redirect:/";
    }

    /**
     * 비밀번호 찾기 페이지
     */
    @GetMapping("/find_pass")
    public String findPassword(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String emailError,
            Model model
    ) {
        model.addAttribute("email", email);
        model.addAttribute("emailError", emailError);
        return "account/find-pass";
    }

    /**
     * 임시 비밀번호 전송 완료 페이지
     */
    @GetMapping("/find_pass_success")
    public String findPasswordSuccess() {
        return "account/find-pass-success";
    }

    /**
     * 사업자 인증 페이지
     */
    @GetMapping("/company")
    public String businessCertification(
            @RequestParam(required = false) String b_no,
            @RequestParam(required = false) String b_stt_cd,
            Model model
    ) {
        BusinessCertificationResponse response = BusinessCertificationResponse.of(
                b_no,
                b_stt_cd
        );
        model.addAttribute("certification", response);
        model.addAttribute("serviceKey", serviceKey);

        return "account/company";
    }
}
