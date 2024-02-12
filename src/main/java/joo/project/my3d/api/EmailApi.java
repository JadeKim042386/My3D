package joo.project.my3d.api;

import joo.project.my3d.aop.BindingResultHandler;
import joo.project.my3d.dto.request.EmailRequest;
import joo.project.my3d.dto.response.ApiResponse;
import joo.project.my3d.dto.response.EmailResponse;
import joo.project.my3d.exception.MailException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.service.EmailServiceInterface;
import joo.project.my3d.service.UserAccountServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
public class EmailApi {

    private final EmailServiceInterface emailService;
    private final UserAccountServiceInterface userAccountService;

    /**
     * 회원가입을 위해 인증 코드 전송 후 코드와 이메일을 세션에 저장
     */
    @BindingResultHandler(message = "invalid email format")
    @PostMapping("/send_code")
    public ResponseEntity<EmailResponse> sendEmailCertification(
            @Valid EmailRequest emailRequest, BindingResult bindingResult) {
        // 이메일 중복 체크
        if (userAccountService.isExistsUserEmailOrNickname(emailRequest.getEmail())) {
            throw new MailException(ErrorCode.ALREADY_EXIST_EMAIL_OR_NICKNAME);
        }

        String subject = "[My3D] 이메일 인증";
        String code = String.valueOf(Math.round(Math.random() * 10000));
        emailService.sendEmail(emailRequest.getEmail(), subject, code);

        return ResponseEntity.ok(EmailResponse.sendSuccess(emailRequest.getEmail(), code));
    }

    @BindingResultHandler(message = "invalid email format")
    @PostMapping("/find_pass")
    public ResponseEntity<ApiResponse> sendEmailFindPass(
            @Valid EmailRequest emailRequest, BindingResult bindingResult) {
        // 유저의 존재 유무 확인
        if (!userAccountService.isExistsUserEmailOrNickname(emailRequest.getEmail())) {
            throw new MailException(ErrorCode.NOT_FOUND_EMAIL);
        }
        // DB에 비밀번호를 변경해준 후 임시 비밀번호를 메일로 전송
        emailService.sendEmail(
                emailRequest.getEmail(),
                "[My3D] 이메일 임시 비밀번호",
                userAccountService.sendTemporaryPassword(emailRequest.getEmail()));
        return ResponseEntity.ok(ApiResponse.of("Successfully send temporary password."));
    }
}
