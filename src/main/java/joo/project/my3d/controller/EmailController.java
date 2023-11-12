package joo.project.my3d.controller;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.exception.MailException;
import joo.project.my3d.service.EmailService;
import joo.project.my3d.service.SignUpService;
import joo.project.my3d.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
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
     * 인증 코드 전송 후 코드와 이메일을 세션에 저장
     */
    @PostMapping("/send_code")
    public String sendEmailCertification(
            @RequestParam String email,
            @RequestParam UserRole userRole,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addAttribute("email", email);
        redirectAttributes.addAttribute("userRole", userRole);

        //이메일 형식 체크
        if (invalidEmailFormat(email)) {
            redirectAttributes.addAttribute("emailError", "format");
            return "redirect:/account/sign_up";
        }

        //이메일 중복 체크
        if (userAccountService.searchUser(email).isPresent()) {
            redirectAttributes.addAttribute("emailError", "duplicated");
            return "redirect:/account/sign_up";
        }

        try {
            String subject = "[My3D] 이메일 인증";
            String code = String.valueOf(Math.round(Math.random() * 10000));
            emailService.sendEmail(email, subject, code);
            redirectAttributes.addAttribute("emailCode", code);
        } catch (MailException e) {
            log.error("이메일 전송 실패 - {}", e);
        }

        return "redirect:/account/sign_up";
    }

    @PostMapping("/find_pass")
    public String sendEmailFindPass(
            @RequestParam String email,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addAttribute("email", email);
        //이메일 형식 체크
        if (invalidEmailFormat(email)) {
            redirectAttributes.addAttribute("emailError", "format");
            return "redirect:/account/find_pass";
        }
        if (userAccountService.searchUser(email).isEmpty()) {
            redirectAttributes.addAttribute("emailError", "notFound");
            return "redirect:/account/find_pass";
        }

        try {
            String subject = "[My3D] 이메일 임시 비밀번호";
            String code = String.valueOf(UUID.randomUUID()).split("-")[0];
            emailService.sendEmail(email, subject, code);

            //수정한 AuditiorAware를 위해 Admin 계정을 SecurityContextHolder에 추가
            UserAccountDto userAccountDto = userAccountService.searchUser(adminEmail).get();
            signUpService.setPrincipal(userAccountDto);

            //임시 비밀번호로 변경
            userAccountService.changePassword(email, encoder.encode(code));
            //변경 완료 후 principal 제거
            SecurityContextHolder.clearContext();

            return "redirect:/account/find_pass_success";
        } catch (MailException e) {
            log.error("임시 비밀번호 전송 실패 - {}", e);
            return "account/find-pass";
        }
    }

    private boolean invalidEmailFormat(String email) {
        String pattern = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$";
        return !Pattern.matches(pattern, email);
    }
}
