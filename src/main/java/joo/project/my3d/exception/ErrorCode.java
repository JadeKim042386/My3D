package joo.project.my3d.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //Common
    FAILED_SAVE(HttpStatus.INTERNAL_SERVER_ERROR, "저장에 실패했습니다."),
    FAILED_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "삭제에 실패했습니다."),
    CONFLICT_DELETE(HttpStatus.CONFLICT, "삭제시 Optimistic Lock 충돌이 일어났습니다."),
    CONFLICT_SAVE(HttpStatus.CONFLICT, "저장시 Optimistic Lock 충돌이 일어났습니다."),
    //Article
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    NOT_WRITER(HttpStatus.UNAUTHORIZED, "작성자와 요청 유저가 일치하지 않습니다."),
    //File
    FILE_CANT_SAVE(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 저장 할 수 없습니다. 파일 경로를 다시 확인해주세요."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
    FILE_DOWNLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드 실패"),
    //Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    DATA_FOR_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글에 필요한 정보를 찾을 수 없습니다."),
    //User
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    INVALID_USER(HttpStatus.UNAUTHORIZED, "이메일과 일치하는 유저가 존재하지 않습니다."),
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "기업을 찾을 수 없습니다."),
    //Dimension
    DIMENSION_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "치수 옵션을 찾을 수 없습니다."),
    DIMENSION_NOT_FOUND(HttpStatus.NOT_FOUND, "치수를 찾을 수 없습니다."),
    //Alarm
    ALARM_CONNECT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알람을 위한 연결 시도 실패"),
    //Mail
    MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송 실패")
    ;

    private HttpStatus status;
    private String message;
}
