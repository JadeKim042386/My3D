package joo.project.my3d.exception;

import lombok.Getter;

@Getter
public class MailException extends RuntimeException {

    private ErrorCode errorCode;

    public MailException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public MailException(ErrorCode errorCode, Exception causeException) {
        this.errorCode = errorCode;
        super.initCause(causeException);
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
