package joo.project.my3d.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailErrorCode {
    INVALID_EMAIL_FORMAT("이메일 형식이 잘못되었습니다."),
    ALREADY_EXIST_EMAIL("이미 존재하는 이메일입니다."),
    NOT_FOUND_EMAIL("사용자가 존재하지 않는 이메일입니다.")
    ;
    private String message;
}
