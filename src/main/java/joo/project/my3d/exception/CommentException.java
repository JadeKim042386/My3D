package joo.project.my3d.exception;

import lombok.Getter;

@Getter
public class CommentException extends RuntimeException {

    private ErrorCode errorCode;


    public CommentException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public CommentException(ErrorCode errorCode, Exception causeException) {
        this.errorCode = errorCode;
        super.initCause(causeException);
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
