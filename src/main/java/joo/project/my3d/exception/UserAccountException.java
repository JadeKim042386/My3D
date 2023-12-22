package joo.project.my3d.exception;

import lombok.Getter;

@Getter
public class UserAccountException extends RuntimeException {

    private ErrorCode errorCode;


    public UserAccountException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public UserAccountException(ErrorCode errorCode, Exception causeException) {
        this.errorCode = errorCode;
        super.initCause(causeException);
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
