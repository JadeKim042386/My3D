package joo.project.my3d.controller;

import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.service.EmailService;
import joo.project.my3d.service.SignUpService;
import joo.project.my3d.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/mail")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final SignUpService signUpService;
    private final UserAccountService userAccountService;
    private final BCryptPasswordEncoder encoder;

    @Value("${admin.email}")
    private String adminEmail;

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

        //이메일 형식 체크
        if (invalidEmailFormat(email)) {
            session.setAttribute("emailError", "format");
            return "redirect:/account/sign_up";
        }

        //이메일 중복 체크
        if (userAccountService.searchUser(email).isPresent()) {
            session.setAttribute("emailError", "duplicated");
            return "redirect:/account/sign_up";
        }
        String subject = "[My3D] 이메일 인증";
        String code = String.valueOf(Math.round(Math.random() * 10000));
        emailService.sendEmail(email, subject, code);
        session.setAttribute("emailCode", code);
        session.removeAttribute("emailError");

        return "redirect:/account/sign_up";
    }

    @PostMapping("/find_pass")
    public String sendEmailFindPass(
            HttpServletRequest request,
            @RequestParam String email
    ) {
        HttpSession session = request.getSession();
        session.setAttribute("email", email);
        //이메일 형식 체크
        if (invalidEmailFormat(email)) {
            session.setAttribute("emailError", "format");
            return "redirect:/account/find_pass";
        }
        if (userAccountService.searchUser(email).isEmpty()) {
            session.setAttribute("emailError", "notFound");
            return "redirect:/account/find_pass";
        }
        session.removeAttribute("emailError");
        String subject = "[My3D] 이메일 임시 비밀번호";
        String code = "{noop}" + String.valueOf(UUID.randomUUID()).split("-")[0];
        emailService.sendEmail(email, subject, code);

        //수정한 AuditiorAware를 위해 Admin 계정을 SecurityContextHolder에 추가
        UserAccountDto userAccountDto = userAccountService.searchUser(adminEmail).get();
        signUpService.setPrincipal(userAccountDto);

        //임시 비밀번호로 변경
        userAccountService.changePassword(email, encoder.encode(code));
        //변경 완료 후 principal 제거
        SecurityContextHolder.clearContext();

        return "redirect:/account/find_pass_success";
    }

    private boolean invalidEmailFormat(String email) {
        String pattern = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$";
        return !Pattern.matches(pattern, email);
    }
}
