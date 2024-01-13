package joo.project.my3d.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode {
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    INVALID_USER(HttpStatus.UNAUTHORIZED, "이메일과 일치하는 유저가 존재하지 않습니다."),
    NOT_EQUAL_NICKNAME(HttpStatus.UNAUTHORIZED, "닉네임이 일치하지 않습니다."),
    NOT_FOUND_COMPANY(HttpStatus.NOT_FOUND, "기업을 찾을 수 없습니다."),
    EXCEED_REISSUE(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh Token의 잔여 reissue count가 없습니다."),
    NOT_EQUAL_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh Token이 일치하지 않습니다."),
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "Refresh Token이 없습니다."),
    //Common
    FAILED_SAVE(HttpStatus.INTERNAL_SERVER_ERROR, "저장에 실패했습니다."),
    FAILED_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "삭제에 실패했습니다."),
    CONFLICT_DELETE(HttpStatus.CONFLICT, "삭제시 Optimistic Lock 충돌이 일어났습니다."),
    CONFLICT_SAVE(HttpStatus.CONFLICT, "저장시 Optimistic Lock 충돌이 일어났습니다.")
    ;

    private HttpStatus status;
    private String message;
}
