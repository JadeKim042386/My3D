package joo.project.my3d.controller;

import joo.project.my3d.dto.security.BoardPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Controller
@RequestMapping("/account")
public class LoginController {
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
    public String type(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            Model model
    ) {
        //OAuth 로그인 후 회원가입
        if (!Objects.isNull(boardPrincipal)) {
            //회원가입이 되어있다면 홈페이지로 이동
            if (boardPrincipal.signUp()) {
                return "redirect:/";
            }
            //authentication 삭제 (로그인 상태가 되지 않도록 함)
            SecurityContextHolder.clearContext();
            //회원가입이 안되어있으면 이메일, 닉네임 정보를 같이 넘김
            model.addAttribute("email", boardPrincipal.email());
            model.addAttribute("nickname", boardPrincipal.nickname());
        }

        return "account/type";
    }

    /**
     * 회원 유형 선택 후
     */
    @GetMapping("/sign_up")
    public String signup() {
        return "account/signup";
    }

    /**
     * 비밀번호 찾기 페이지
     */
    @GetMapping("/find_pass")
    public String findPassword() {
        return "account/find-pass";
    }
}
