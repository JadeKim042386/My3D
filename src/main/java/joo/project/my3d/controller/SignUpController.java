package joo.project.my3d.controller;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.response.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/signup")
public class SignUpController {

    @Value("${nts.service-key}")
    private String NtsServiceKey;

    /**
     * 회원가입 페이지
     */
    @GetMapping
    public String signUp(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String nickname,
            @RequestParam UserRole userRole,
            Model model) {
        model.addAttribute("signUpData", UserInfo.of(email, nickname, userRole));
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
    public String businessCertification(Model model) {
        model.addAttribute("serviceKey", NtsServiceKey);
        return "signup/company";
    }
}
