package joo.project.my3d.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/signup")
public class SignUpController {

    /**
     * 회원가입 페이지
     */
    @GetMapping
    public String signUp() {
        return "signup/signup";
    }

    /**
     * 회원 유형(개인, 기업) 선택 페이지
     */
    @GetMapping("/type")
    public String selectType() {
        return "signup/type";
    }

    /**
     * 사업자 인증 페이지
     */
    @GetMapping("/company")
    public String businessCertification() {
        return "signup/company";
    }
}
