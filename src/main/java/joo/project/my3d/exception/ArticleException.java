package joo.project.my3d.exception;

import joo.project.my3d.exception.constant.ErrorCode;
import lombok.Getter;

@Getter
public class ArticleException extends RuntimeException {

    private ErrorCode errorCode;

    public ArticleException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ArticleException(ErrorCode errorCode, Exception causeException) {
        this.errorCode = errorCode;
        super.initCause(causeException);
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
