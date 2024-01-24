package joo.project.my3d.service.impl;

import joo.project.my3d.exception.MailException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.service.EmailServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService implements EmailServiceInterface {

    private final JavaMailSender javaMailSender;

    /**
     * @throws MailException 이메일 전송 실패 예외
     */
    @Async
    @Override
    public void sendEmail(String to, String subject, String text) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text);
            javaMailSender.send(mimeMessage);
            log.info("이메일 전송 완료");
        } catch (MailAuthenticationException | MailSendException | MessagingException e) {
            throw new MailException(ErrorCode.MAIL_SEND_FAIL, e);
        }
    }
}
