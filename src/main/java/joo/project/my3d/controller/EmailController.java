package joo.project.my3d.controller;

import joo.project.my3d.service.EmailService;
import joo.project.my3d.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/mail")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final UserAccountService userAccountService;

    /**
     *  인증 코드 전송 후 코드와 이메일을 세션에 저장
     */
    @PostMapping("/send_code")
    public String sendEmailCertification(
            HttpServletRequest request,
            @RequestParam String email
    ) {
        HttpSession session = request.getSession();
        session.setAttribute("email", email);
        //이메일 중복 체크
        if (userAccountService.searchUser(email).isPresent()) {
            session.setAttribute("duplicatedEmail", true);
            return "redirect:/account/sign_up";
        }
        String subject = "[My3D] 이메일 인증";
        String code = String.valueOf(Math.round(Math.random() * 10000));
        emailService.sendEmail(email, subject, code);
        session.setAttribute("emailCode", code);
        session.setAttribute("duplicatedEmail", false);

        return "redirect:/account/sign_up";
    }
}
