package joo.project.my3d.exception;

import joo.project.my3d.exception.constant.AuthErrorCode;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    private AuthErrorCode errorCode;


    public AuthException(AuthErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public AuthException(AuthErrorCode errorCode, Exception causeException) {
        this.errorCode = errorCode;
        super.initCause(causeException);
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
