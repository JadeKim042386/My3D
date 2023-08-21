package joo.project.my3d.controller;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Objects;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class LoginController {

    private final SignUpService signUpService;
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
            @RequestParam(required = false) UserRole userRole
    ) {
        HttpSession session = request.getSession();

        session.removeAttribute("b_no");
        return "account/signup";
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
