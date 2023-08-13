package joo.project.my3d.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    ARTICLE_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "모델 게시글은 카테고리 정보를 포함해야합니다."),
    FILE_CANT_SAVE(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 저장 할 수 없습니다. 파일 경로를 다시 확인해주세요.")
    ;

    private HttpStatus status;
    private String message;
}
