package joo.project.my3d.controller;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.properties.JwtProperties;
import joo.project.my3d.dto.request.BusinessCertificationRequest;
import joo.project.my3d.dto.request.SignUpRequest;
import joo.project.my3d.dto.request.UserLoginRequest;
import joo.project.my3d.dto.response.BusinessCertificationResponse;
import joo.project.my3d.dto.response.SignUpResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.SignUpService;
import joo.project.my3d.service.UserAccountService;
import joo.project.my3d.utils.CookieUtils;
import joo.project.my3d.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class LoginController {

    private final SignUpService signUpService;
    private final UserAccountService userAccountService;
    private final BCryptPasswordEncoder encoder;
    private final JwtProperties jwtProperties;
    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    @GetMapping("/login")
    public String login() {
        return "account/login";
    }

    @PostMapping("/login")
    public String requestLogin(
            UserLoginRequest request,
            HttpServletResponse response
    ) {
        String token = userAccountService.login(request.email(), request.password());
        Cookie cookie = CookieUtils.createCookie(
                jwtProperties.cookieName(),
                token,
                (int) (jwtProperties.expiredTimeMs() / 1000),
                "/"
        );
        response.addCookie(cookie);

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        this.logoutHandler.logout(request, response, authentication);
        Cookie cookie = CookieUtils.createCookie(
                jwtProperties.cookieName(),
                null,
                0,
                "/"
        );
        response.addCookie(cookie);

        return "redirect:/";
    }

    /**
     * 1. OAuth 첫 로그인 후
     * 2. 회원가입
     */
    @GetMapping("/type")
    public String type(@AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        //OAuth 로그인 후 회원가입이 되어있다면 홈페이지로 이동
        if (!Objects.isNull(boardPrincipal) && boardPrincipal.signUp()) {
            return "redirect:/";
        }

        return "account/type";
    }

    /**
     * 회원 유형 선택 후
     */
    @GetMapping("/sign_up")
    public String signup(
            HttpServletRequest request,
            @RequestParam(required = false) UserRole userRole,
            Model model
    ) {
        HttpSession session = request.getSession();
        //회원 유형 선택 페이지에서 선택된 유형 저장
        if (Objects.nonNull(userRole)) {
            session.setAttribute("userRole", userRole);
        }

        //세션에 저장된 정보 전달
        model.addAttribute("oauthLogin", session.getAttribute("oauthLogin"));
        model.addAttribute("email", session.getAttribute("email"));
        //이메일 인증 코드
        model.addAttribute("code", session.getAttribute("emailCode"));
        //이메일 중복 여부
        model.addAttribute("emailError", session.getAttribute("emailError"));

        SignUpResponse response = SignUpResponse.of(
                (UserRole) session.getAttribute("userRole"),
                null,
                (String) session.getAttribute("nickname"),
                null,
                null,
                null,
                null
        );
        model.addAttribute("signUpData", response);

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
            HttpServletRequest request,
            HttpServletResponse response,
            @Validated @ModelAttribute("signUpData") SignUpRequest signUpRequest,
            BindingResult bindingResult,
            Model model
    ) {
        HttpSession session = request.getSession();
        Object email = session.getAttribute("email");

        if (bindingResult.hasErrors()) {
            log.warn("bindingResult={}", bindingResult);
            model.addAttribute("email", email);
            session.removeAttribute("emailCode");
            return "account/signup";
        }

        UserAccountDto userAccountDto = UserAccountDto.from(
                signUpRequest.toEntity((String) email),
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
            HttpServletRequest request,
            Model model
    ) {
        HttpSession session = request.getSession();
        model.addAttribute("email", session.getAttribute("email"));
        model.addAttribute("emailError", session.getAttribute("emailError"));
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
            HttpServletRequest request,
            Model model
    ) {
        HttpSession session = request.getSession();
        BusinessCertificationResponse response = BusinessCertificationResponse.of(
                (String) session.getAttribute("b_no"),
                (String) session.getAttribute("b_stt_cd")
        );
        //수정 요청이 들어오면 b_stt_cd는 존재하면 안되므로 삭제
        session.removeAttribute("b_stt_cd");
        model.addAttribute("certification", response);

        return "account/company";
    }

    /**
     * 사업자 인증 요청<br>
     * [b_stt_cd]<br>
     * 01: 계속사업자<br>
     * 02: 휴업<br>
     * 03: 폐업<br>
     * 04: 존재하지않는 기업
     */
    @PostMapping("/company")
    public String requestBusinessCertification(
            HttpServletRequest request,
            @Validated @ModelAttribute("certification") BusinessCertificationRequest businessCertificationRequest,
            BindingResult bindingResult
    ) {
        HttpSession session = request.getSession();
        session.setAttribute("b_no", businessCertificationRequest.b_no());

        if (bindingResult.hasErrors()) {
            log.warn("bindingResult={}", bindingResult);
            return "/account/company";
        }

        String b_stt_cd = signUpService.businessCertification(businessCertificationRequest.b_no());
        if (b_stt_cd.equals("")) {
            b_stt_cd = "04";
        }
        session.setAttribute("b_stt_cd", b_stt_cd);

        return "redirect:/account/company";
    }
}
