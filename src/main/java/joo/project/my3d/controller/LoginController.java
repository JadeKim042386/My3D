package joo.project.my3d.controller;

import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.request.SignUpRequest;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.SignUpService;
import joo.project.my3d.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class LoginController {

    private final SignUpService signUpService;
    private final UserAccountService userAccountService;
    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    @GetMapping("/login")
    public String login() {
        return "account/login";
    }

    @GetMapping("/logout")
    public String logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        this.logoutHandler.logout(request, response, authentication);
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
        Object oauthLogin = session.getAttribute("oauthLogin");
        Object email = session.getAttribute("email");
        Object nickname = session.getAttribute("nickname");
        Object code = session.getAttribute("emailCode"); //이메일 인증 코드
        userRole = (UserRole) session.getAttribute("userRole");
        Object duplicatedEmail = session.getAttribute("duplicatedEmail"); //이메일 중복 여부
        if (Objects.nonNull(oauthLogin)) {
            model.addAttribute("oauthLogin", oauthLogin);
        }
        if (Objects.nonNull(email)) {
            model.addAttribute("email", email);
        }
        if (Objects.nonNull(nickname)) {
            model.addAttribute("nickname", nickname);
        }
        if (Objects.nonNull(code)) {
            model.addAttribute("code", code);
        }
        if (Objects.nonNull(userRole)) {
            model.addAttribute("userRole", userRole);
        }
        if (Objects.nonNull(duplicatedEmail)) {
            model.addAttribute("duplicatedEmail", duplicatedEmail);
        }

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
            SignUpRequest signUpRequest
    ) {
        HttpSession session = request.getSession();
        Object email = session.getAttribute("email");

        UserAccount userAccount = signUpRequest.toEntity((String) email);
        UserAccountDto userAccountDto = UserAccountDto.from(userAccount);

        //auditorAware를 위해 principal을 먼저 등록
        BoardPrincipal principal = BoardPrincipal.from(userAccountDto);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        principal.password(),
                        principal.authorities()
                )
        );

        userAccountService.saveUser(
                    userAccount
        );

        return "redirect:/";
    }

    /**
     * 비밀번호 찾기 페이지
     */
    @GetMapping("/find_pass")
    public String findPassword() {
        return "account/find-pass";
    }

    /**
     * 사업자 인증 페이지
     */
    @GetMapping("/company")
    public String businessCertification(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Object b_stt_cd = session.getAttribute("b_stt_cd");
        Object b_no = session.getAttribute("b_no");
        if (!Objects.isNull(b_stt_cd)) {
            model.addAttribute("b_stt_cd", b_stt_cd);
            //수정 요청이 들어오면 b_stt_cd는 존재하면 안되므로 삭제
            session.removeAttribute("b_stt_cd");
        }
        if (!Objects.isNull(b_no)) {
            model.addAttribute("b_no", b_no);
        }

        return "account/company";
    }

    /**
     * 사업자 인증 요청<br>
     * 04: 사업자 등록번호 길이가 10이 이닌 경우
     */
    @PostMapping("/company")
    public String requestBusinessCertification(
            HttpServletRequest request,
            @RequestParam String b_no
    ) {
        HttpSession session = request.getSession();
        session.setAttribute("b_no", b_no);

        if (b_no.length() != 10) {
            session.setAttribute("b_stt_cd", "04");
            return "redirect:/account/company";
        }

        String b_stt_cd = signUpService.businessCertification(b_no);
        session.setAttribute("b_stt_cd", b_stt_cd);

        return "redirect:/account/company";
    }
}
