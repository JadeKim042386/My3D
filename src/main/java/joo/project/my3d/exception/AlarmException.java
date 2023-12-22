package joo.project.my3d.exception;

import lombok.Getter;

@Getter
public class AlarmException extends RuntimeException {

    private ErrorCode errorCode;

    public AlarmException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public AlarmException(ErrorCode errorCode, Exception causeException) {
        this.errorCode = errorCode;
        super.initCause(causeException);
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
