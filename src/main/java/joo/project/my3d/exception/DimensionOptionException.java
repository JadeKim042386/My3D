package joo.project.my3d.exception;

import lombok.Getter;

@Getter
public class DimensionOptionException extends RuntimeException {

    private ErrorCode errorCode;


    public DimensionOptionException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public DimensionOptionException(ErrorCode errorCode, Exception causeException) {
        this.errorCode = errorCode;
        super.initCause(causeException);
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
