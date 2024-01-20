package joo.project.my3d.controller;

import joo.project.my3d.dto.response.EmailResponse;
import joo.project.my3d.exception.MailException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.service.EmailService;
import joo.project.my3d.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final UserAccountService userAccountService;

    /**
     * 인증 코드 전송 후 코드와 이메일을 세션에 저장
     */
    @PostMapping("/send_code")
    public ResponseEntity<EmailResponse> sendEmailCertification(@RequestParam String email) {
        validateEmail(email);
        //이메일 중복 체크
        if (userAccountService.isExistsUserEmail(email)) {
            throw new MailException(ErrorCode.ALREADY_EXIST_EMAIL);
        }

        String subject = "[My3D] 이메일 인증";
        String code = String.valueOf(Math.round(Math.random() * 10000));
        emailService.sendEmail(email, subject, code);

        return ResponseEntity.ok(EmailResponse.sendSuccess(email, code));
    }

    @PostMapping("/find_pass")
    public ResponseEntity<EmailResponse> sendEmailFindPass(@RequestParam String email) {
        validateEmail(email);
        //유저의 존재 유무 확인
        if (!userAccountService.isExistsUserEmail(email)) {
            throw new MailException(ErrorCode.NOT_FOUND_EMAIL);
        }
        userAccountService.sendTemporaryPassword(email);
        return ResponseEntity.ok(EmailResponse.sendSuccess(email));
    }

    private void validateEmail(String email) {
        if (invalidEmailFormat(email)) {
            throw new MailException(ErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    private boolean invalidEmailFormat(String email) {
        String pattern = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$";
        return !Pattern.matches(pattern, email);
    }
}
