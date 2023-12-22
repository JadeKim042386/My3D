package joo.project.my3d.exception;

import lombok.Getter;

@Getter
public class JwtTokenException extends RuntimeException {

    private ErrorCode errorCode;

    public JwtTokenException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public JwtTokenException(ErrorCode errorCode, Exception causeException) {
        this.errorCode = errorCode;
        super.initCause(causeException);
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
