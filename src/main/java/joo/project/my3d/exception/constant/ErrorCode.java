package joo.project.my3d.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //Common
    INTERNAL_SERVER_ERROR_CODE(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    FAILED_SAVE(HttpStatus.INTERNAL_SERVER_ERROR, "저장에 실패했습니다."),
    FAILED_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "삭제에 실패했습니다."),
    CONFLICT_DELETE(HttpStatus.CONFLICT, "삭제시 Optimistic Lock 충돌이 일어났습니다."),
    CONFLICT_SAVE(HttpStatus.CONFLICT, "저장시 Optimistic Lock 충돌이 일어났습니다."),
    //Sign Up
    ALREADY_EXIST_EMAIL_OR_NICKNAME(HttpStatus.BAD_REQUEST,"이미 존재하는 이메일 또는 닉네임입니다."),
    ALREADY_EXIST_COMPANY_NAME(HttpStatus.BAD_REQUEST,"이미 존재하는 상호명입니다."),
    //Article
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    NOT_WRITER(HttpStatus.UNAUTHORIZED, "작성자와 요청 유저가 일치하지 않습니다."),
    //File
    FILE_CANT_SAVE(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 저장 할 수 없습니다. 파일 경로를 다시 확인해주세요."),
    FILE_CANT_READ(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 읽을 수 없습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
    FILE_DOWNLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드 실패"),
    //Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    DATA_FOR_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글에 필요한 정보를 찾을 수 없습니다."),
    //Dimension
    DIMENSION_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "치수 옵션을 찾을 수 없습니다."),
    DIMENSION_NOT_FOUND(HttpStatus.NOT_FOUND, "치수를 찾을 수 없습니다."),
    //Alarm
    ALARM_CONNECT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알람을 위한 연결 시도 실패"),
    //Mail
    MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송 실패"),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "이메일 형식이 잘못되었습니다."),
    NOT_FOUND_EMAIL(HttpStatus.NOT_FOUND,"사용자가 존재하지 않는 이메일입니다."),
    //Auth
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    INVALID_USER(HttpStatus.NOT_FOUND, "이메일과 일치하는 유저가 존재하지 않습니다."),
    NOT_EQUAL_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임이 일치하지 않습니다."),
    NOT_FOUND_USER(HttpStatus.FORBIDDEN, "가입되지 않은 유저입니다."),
    NOT_FOUND_COMPANY(HttpStatus.NOT_FOUND, "기업을 찾을 수 없습니다."),
    FAILED_REISSUE(HttpStatus.INTERNAL_SERVER_ERROR, "Access Token을 재발행하는데 실패했습니다."),
    EXCEED_REISSUE(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh Token의 잔여 reissue count가 없습니다."),
    NOT_EQUAL_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh Token이 일치하지 않습니다."),
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "Refresh Token이 없습니다."),
    EXPIRED_TOKEN(HttpStatus.FORBIDDEN, "Token이 만료되었습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
