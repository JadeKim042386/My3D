package joo.project.my3d.exception;

import joo.project.my3d.exception.constant.ErrorCode;
import lombok.Getter;

@Getter
public abstract class CustomException extends RuntimeException{
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, Throwable cause) {
        this.errorCode = errorCode;
        initCause(cause);
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
