package joo.project.my3d.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/signin")
public class SignInController {

    /**
     * 로그인 페이지
     */
    @GetMapping
    public String login() {
        return "signin/login";
    }

    /**
     * 비밀번호 찾기 페이지
     */
    @GetMapping("/find_pass")
    public String findPassword() {
        return "signin/find-pass";
    }

    /**
     * 임시 비밀번호 전송 완료 페이지
     */
    @GetMapping("/find_pass_success")
    public String findPasswordSuccess() {
        return "signin/find-pass-success";
    }
}
