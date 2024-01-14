package joo.project.my3d.controller;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.response.ApiResponse;
import joo.project.my3d.dto.response.EmailResponse;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.service.EmailService;
import joo.project.my3d.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final UserAccountService userAccountService;
    private final BCryptPasswordEncoder encoder;

    @Value("${admin.email}")
    private String adminEmail; //발신자 이메일

    /**
     * 인증 코드 전송 후 코드와 이메일을 세션에 저장
     */
    @PostMapping("/send_code")
    public ApiResponse<EmailResponse> sendEmailCertification(
            @RequestParam String email,
            @RequestParam UserRole userRole
    ) {
        //이메일 형식 체크
        if (invalidEmailFormat(email)) {
            return ApiResponse.invalid(EmailResponse.sendError(email, ErrorCode.INVALID_EMAIL_FORMAT, userRole));
        }

        //이메일 중복 체크
        if (userAccountService.isExistsUserEmail(email)) {
            return ApiResponse.invalid(EmailResponse.sendError(email, ErrorCode.ALREADY_EXIST_EMAIL, userRole));
        }

        String subject = "[My3D] 이메일 인증";
        String code = String.valueOf(Math.round(Math.random() * 10000));
        emailService.sendEmail(email, subject, code);

        return ApiResponse.success(EmailResponse.sendSuccess(email, code, userRole));
    }

    @PostMapping("/find_pass")
    public ApiResponse<EmailResponse> sendEmailFindPass(@RequestParam String email) {
        //이메일 형식 체크
        if (invalidEmailFormat(email)) {
            return ApiResponse.invalid(EmailResponse.sendError(email, ErrorCode.INVALID_EMAIL_FORMAT));
        }
        //유저의 존재 유무 확인
        if (!userAccountService.isExistsUserEmail(email)) {
            return ApiResponse.invalid(EmailResponse.sendError(email, ErrorCode.NOT_FOUND_EMAIL));
        }
        String code = String.valueOf(UUID.randomUUID()).split("-")[0];
        emailService.sendEmail(
                email,
                "[My3D] 이메일 임시 비밀번호",
                code
        );
        userAccountService.changePassword(email, encoder.encode(code));
        return ApiResponse.success(EmailResponse.sendSuccess(email));
    }

    private boolean invalidEmailFormat(String email) {
        String pattern = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$";
        return !Pattern.matches(pattern, email);
    }
}
